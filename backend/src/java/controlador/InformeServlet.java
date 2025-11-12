package controlador;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import Conexion.Conexion; // Asegúrate de que esta clase de conexión sea correcta

@WebServlet("/api/informes/*") // Usamos /* para manejar sub-rutas como /inventario, /guardar, /historico
public class InformeServlet extends HttpServlet {

    // Método auxiliar para obtener el ID de usuario de la sesión
    private Integer getUserIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Integer) session.getAttribute("idUsuario");
        }
        return null;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        Integer idUsuario = getUserIdFromSession(request);
        if (idUsuario == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"mensaje\": \"Usuario no autenticado o sesión expirada.\", \"estado\": \"error\"}");
            return;
        }

        String pathInfo = request.getPathInfo(); // Obtener la parte de la URL después de /api/informes

        try {
            conn = Conexion.getConnection();

            if ("/inventario".equals(pathInfo)) {
                // --- GET: Obtener el informe de inventario actual ---
                handleGetInventarioReport(request, response, conn, out);
            } else if ("/historico".equals(pathInfo)) {
                // --- GET: Obtener la lista de informes históricos o un informe específico ---
                String reportIdStr = request.getParameter("id"); // Parámetro para un informe específico
                if (reportIdStr != null && !reportIdStr.isEmpty()) {
                    handleGetSpecificHistoricalReport(request, response, conn, out, Integer.parseInt(reportIdStr));
                } else {
                    handleGetHistoricalReportsList(request, response, conn, out);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"mensaje\": \"Ruta de informe no encontrada.\", \"estado\": \"error\"}");
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"mensaje\": \"Error de base de datos: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"mensaje\": \"ID de informe inválido.\", \"estado\": \"error\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"mensaje\": \"Error interno del servidor: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.flush();
        }
    }

    // --- Lógica para obtener el informe de inventario actual ---
    private void handleGetInventarioReport(HttpServletRequest request, HttpServletResponse response, Connection conn, PrintWriter out)
            throws SQLException, IOException {
        String filterStatus = request.getParameter("filterStatus"); // "Asignado", "Disponible", "En Mantenimiento", "all"

        JSONArray reportArray = new JSONArray();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        // 1. Obtener todos los equipos/periféricos de la tabla 'equipos_perifericos'
        StringBuilder sqlItems = new StringBuilder("SELECT n_inventario, n_serie, tipo, clase, marca, ram, disco, procesador, estado FROM equipos_perifericos WHERE 1=1 ");
        if (filterStatus != null && !filterStatus.equalsIgnoreCase("all")) {
            sqlItems.append("AND estado = ? "); // Filtrar por el estado directamente de equipos_perifericos
        }
        sqlItems.append("ORDER BY clase, tipo, marca");

        stmt = conn.prepareStatement(sqlItems.toString());
        if (filterStatus != null && !filterStatus.equalsIgnoreCase("all")) {
            stmt.setString(1, filterStatus);
        }
        rs = stmt.executeQuery();

        JSONArray rawItems = new JSONArray();
        while (rs.next()) {
            JSONObject item = new JSONObject();
            item.put("n_inventario", rs.getString("n_inventario"));
            item.put("n_serie", rs.getString("n_serie"));
            item.put("tipo", rs.getString("tipo"));
            item.put("clase", rs.getString("clase"));
            item.put("marca", rs.getString("marca"));
            item.put("ram", rs.getString("ram"));
            item.put("disco", rs.getString("disco"));
            item.put("procesador", rs.getString("procesador"));
            item.put("estado", rs.getString("estado")); // Estado actual del equipo/periférico
            rawItems.put(item);
        }
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();

        // 2. Obtener la última asignación de cada empleado de la tabla 'actas'
        Map<String, JSONObject> latestAssignments = new HashMap<>(); // Key: n_inventario, Value: {asignadoA, fechaAsignacion (long)}

        String sqlActas = "SELECT a.n_inventario, a.cedula, a.fecha, e.nombre " +
                          "FROM actas a JOIN empleados e ON a.cedula = e.cedula ORDER BY a.fecha DESC";
        stmt = conn.prepareStatement(sqlActas);
        rs = stmt.executeQuery();

        while (rs.next()) {
            String inventariosInActa = rs.getString("n_inventario");
            String[] individualInventarios = inventariosInActa.split(",");
            String assigneeName = rs.getString("nombre");
            Timestamp fechaActa = rs.getTimestamp("fecha");

            for (String inv : individualInventarios) {
                String trimmedInv = inv.trim();
                if (!latestAssignments.containsKey(trimmedInv) || fechaActa.after(new Timestamp(latestAssignments.get(trimmedInv).getLong("fechaAsignacion")))) {
                     JSONObject assigneeInfo = new JSONObject();
                     assigneeInfo.put("asignadoA", assigneeName);
                     assigneeInfo.put("fechaAsignacion", fechaActa.getTime()); // Guardar como milisegundos (long)
                     latestAssignments.put(trimmedInv, assigneeInfo);
                }
            }
        }
        if (rs != null) rs.close();
        if (stmt != null) stmt.close();


        // 3. Combinar los datos de equipos_perifericos con la información de asignación
        for (int i = 0; i < rawItems.length(); i++) {
            JSONObject item = rawItems.getJSONObject(i);
            String nInventario = item.getString("n_inventario");
            String estadoItem = item.getString("estado"); // Estado del ítem en equipos_perifericos

            item.put("id", nInventario); // Usamos n_inventario como el ID para el reporte
            item.put("categoria", item.getString("clase")); // 'clase' de la DB es 'categoria' en el reporte
            item.put("serial", item.getString("n_serie")); // 'n_serie' de la DB es 'serial' en el reporte
            item.put("estadoAsignacion", estadoItem); // Usamos el estado directamente de equipos_perifericos

            // Inicializar asignadoA, fechaAsignacion, fechaDevolucion
            item.put("asignadoA", "N/A");
            item.put("fechaAsignacion", JSONObject.NULL);
            item.put("fechaDevolucion", JSONObject.NULL); // No tenemos fecha de devolución directa en actas

            // Si el estado del ítem es 'ASIGNADO' o 'PENDIENTE', intentamos encontrar al asignado
            if ("ASIGNADO".equalsIgnoreCase(estadoItem) || "PENDIENTE".equalsIgnoreCase(estadoItem)) {
                JSONObject assigneeInfo = latestAssignments.get(nInventario);
                if (assigneeInfo != null) {
                    item.put("asignadoA", assigneeInfo.getString("asignadoA"));
                    item.put("fechaAsignacion", assigneeInfo.getLong("fechaAsignacion"));
                }
            }

            reportArray.put(item);
        }

        out.print(reportArray.toString());
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // --- Lógica para obtener la lista de informes históricos ---
    private void handleGetHistoricalReportsList(HttpServletRequest request, HttpServletResponse response, Connection conn, PrintWriter out)
            throws SQLException, IOException {
        JSONArray historicalReportsArray = new JSONArray();
        PreparedStatement stmt = null;
        ResultSet rs = null;

        // MODIFICACIÓN: Unir con la tabla 'usuarios' para obtener el nombre del generador
        String sql = "SELECT ig.id, ig.fecha_generacion, ig.id_usuario_generador, ig.estado_filtro, u.nombre AS nombre_usuario_generador " +
                     "FROM informes_generados ig JOIN usuarios u ON ig.id_usuario_generador = u.id " +
                     "ORDER BY ig.fecha_generacion DESC";
        stmt = conn.prepareStatement(sql);
        rs = stmt.executeQuery();

        while (rs.next()) {
            JSONObject reportSummary = new JSONObject();
            reportSummary.put("id", rs.getInt("id"));
            reportSummary.put("fechaGeneracion", rs.getTimestamp("fecha_generacion"));
            reportSummary.put("idUsuarioGenerador", rs.getInt("id_usuario_generador"));
            reportSummary.put("nombreUsuarioGenerador", rs.getString("nombre_usuario_generador")); // NUEVO: Nombre del usuario
            reportSummary.put("estadoFiltro", rs.getString("estado_filtro"));
            historicalReportsArray.put(reportSummary);
        }

        out.print(historicalReportsArray.toString());
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // --- Lógica para obtener un informe histórico específico ---
    private void handleGetSpecificHistoricalReport(HttpServletRequest request, HttpServletResponse response, Connection conn, PrintWriter out, int reportId)
            throws SQLException, IOException {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        String sql = "SELECT reporte_json FROM informes_generados WHERE id = ?";
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, reportId);
        rs = stmt.executeQuery();

        if (rs.next()) {
            String reportJson = rs.getString("reporte_json");
            try {
                new JSONArray(reportJson);
            } catch (org.json.JSONException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"mensaje\": \"Formato de informe histórico inesperado.\", \"estado\": \"error\"}");
                e.printStackTrace();
                return;
            }
            out.print(reportJson);
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.print("{\"mensaje\": \"Informe histórico no encontrado.\", \"estado\": \"error\"}");
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Connection conn = null;
        PreparedStatement stmt = null;

        Integer idUsuario = getUserIdFromSession(request);
        if (idUsuario == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"mensaje\": \"Usuario no autenticado o sesión expirada.\", \"estado\": \"error\"}");
            return;
        }

        String pathInfo = request.getPathInfo();

        try {
            conn = Conexion.getConnection();

            if ("/guardar".equals(pathInfo)) {
                // --- POST: Guardar un informe generado ---
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = request.getReader().readLine()) != null) {
                    sb.append(line);
                }
                String requestBody = sb.toString();

                JSONObject jsonRequest = new JSONObject(requestBody);
                String estadoFiltro = jsonRequest.optString("estadoFiltro", "all");
                String reporteJson = jsonRequest.getString("reporteJson");

                String sql = "INSERT INTO informes_generados (fecha_generacion, id_usuario_generador, estado_filtro, reporte_json) VALUES (?, ?, ?, ?)";
                stmt = conn.prepareStatement(sql);
                stmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                stmt.setInt(2, idUsuario);
                stmt.setString(3, estadoFiltro);
                stmt.setString(4, reporteJson);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    response.setStatus(HttpServletResponse.SC_CREATED);
                    out.print("{\"mensaje\": \"Informe guardado con éxito\", \"estado\": \"ok\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.print("{\"mensaje\": \"No se pudo guardar el informe.\", \"estado\": \"error\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"mensaje\": \"Ruta de informe no encontrada para POST.\", \"estado\": \"error\"}");
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"mensaje\": \"Error de base de datos al guardar informe: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } catch (org.json.JSONException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"mensaje\": \"Formato de solicitud JSON inválido: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"mensaje\": \"Error interno del servidor: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } finally {
                if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
                if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            out.flush();
        }
    }
}

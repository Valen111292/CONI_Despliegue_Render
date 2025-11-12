package controlador;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import Conexion.Conexion;

@WebServlet("/api/solicitudes")
public class ListarSolicitudesServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        JSONArray solicitudesArray = new JSONArray();

        try {
            HttpSession session = request.getSession(false);
            Integer idUsuario = null;
            String rolAutenticacion = null;
            String cargoEmpleado = null;

            if (session != null) {
                idUsuario = (Integer) session.getAttribute("idUsuario");
                rolAutenticacion = (String) session.getAttribute("rolAutenticacion");
                cargoEmpleado = (String) session.getAttribute("cargoEmpleado");
            }
            if (idUsuario == null || rolAutenticacion == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"mensaje\": \"Usuario no autenticado o sesión expirada, o no tiene cargo asignado.\", \"estado\": \"error\"}");
                return;
            }

            conn = Conexion.getConnection();

            // --- Parámetros de ordenamiento y filtrado ---
            String sortBy = request.getParameter("sortBy");
            String order = request.getParameter("order");
            String filterPriority = request.getParameter("filterPriority");
            String searchQuery = request.getParameter("search");

            StringBuilder sqlBuilder = new StringBuilder("SELECT id, tipo_solicitud, descripcion, alta_prioridad, fecha_solicitud, estado, id_usuario FROM solicitudes_compra");

            // Condiciones WHERE
            sqlBuilder.append(" WHERE 1=1"); // Cláusula siempre verdadera para facilitar la adición de ANDs

            // Lógica corregida para filtrar por id_usuario
            // Solo se filtra por id_usuario si el rol NO es "usuario" Y el cargo NO es "Otro"
            if (!("usuario".equalsIgnoreCase(rolAutenticacion) && "Otro".equalsIgnoreCase(cargoEmpleado))) {
                sqlBuilder.append(" AND id_usuario = ?");
            }

            if (filterPriority != null && !filterPriority.equalsIgnoreCase("all")) {
                sqlBuilder.append(" AND alta_prioridad = ?");
            }

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                // Búsqueda por palabras clave en descripción o tipo_solicitud
                // Asegúrate de que tu base de datos soporta LIKE para búsqueda insensible a mayúsculas/minúsculas
                // Si usas MySQL/SQL Server, podrías necesitar LIKE o LOWER()
                sqlBuilder.append(" AND (descripcion LIKE ? OR tipo_solicitud LIKE ?)");
            }

            // Ordenamiento
            if (sortBy != null && !sortBy.trim().isEmpty()) {
                sqlBuilder.append(" ORDER BY "); // Añadido espacio aquí
                switch (sortBy.toLowerCase()) {
                    case "fecha":
                        sqlBuilder.append("fecha_solicitud");
                        break;
                    case "estado":
                        sqlBuilder.append("estado");
                        break;
                    case "prioridad":
                        sqlBuilder.append("alta_prioridad");
                        break;
                    case "tipo":
                        sqlBuilder.append("tipo_solicitud");
                        break;
                    default:
                        sqlBuilder.append("fecha_solicitud"); // Orden por defecto si sortBy es inválido
                }
                if (order != null && order.equalsIgnoreCase("desc")) {
                    sqlBuilder.append(" DESC"); // Añadido espacio aquí
                } else {
                    sqlBuilder.append(" ASC"); // Por defecto ascendente
                }
            } else {
                sqlBuilder.append(" ORDER BY fecha_solicitud DESC"); // Orden por defecto si no se especifica, añadido espacio
            }

            String sql = sqlBuilder.toString();
            stmt = conn.prepareStatement(sql);
            
            int paramIndex = 1;
            // Lógica corregida para establecer el parámetro id_usuario
            // Solo se establece el parámetro si el rol NO es "usuario" Y el cargo NO es "Otro"
            if (!("usuario".equalsIgnoreCase(rolAutenticacion) && "Otro".equalsIgnoreCase(cargoEmpleado))) {
                stmt.setInt(paramIndex++, idUsuario);
            }
            
            if (filterPriority != null && !filterPriority.equalsIgnoreCase("all")) {
                stmt.setBoolean(paramIndex++, Boolean.parseBoolean(filterPriority));
            }
            
            if (searchQuery != null && !searchQuery.trim().isEmpty()){
                stmt.setString(paramIndex++, "%" + searchQuery + "%");
                stmt.setString(paramIndex++, "%" + searchQuery + "%");
            }
            
            rs = stmt.executeQuery();

            while (rs.next()) {
                JSONObject solicitudJson = new JSONObject();
                solicitudJson.put("id", rs.getInt("id"));
                solicitudJson.put("tipoSolicitud", rs.getString("tipo_solicitud"));
                solicitudJson.put("descripcion", rs.getString("descripcion"));
                solicitudJson.put("altaPrioridad", rs.getBoolean("alta_prioridad"));
                solicitudJson.put("fechaSolicitud", rs.getTimestamp("fecha_solicitud"));
                solicitudJson.put("estado", rs.getString("estado"));
                solicitudJson.put("idUsuario", rs.getInt("id_usuario"));
                solicitudesArray.put(solicitudJson);
            }

            out.print(solicitudesArray.toString());
            response.setStatus(HttpServletResponse.SC_OK);

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"mensaje\": \"Error de base de datos al listar solicitudes: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"mensaje\": \"Error interno del servidor al listar solicitudes: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.flush();
        }
    }
}

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

@WebServlet("/listar-solicitudes-compra")
public class ListarSolicitudesServlet extends HttpServlet {

    // -------------------------------
    // CORS GENERAL
    // -------------------------------
    private void configurarCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "https://coni-frontend.onrender.com");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCORS(response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCORS(response);

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
                out.print("{\"mensaje\": \"Usuario no autenticado o sesión expirada.\", \"estado\": \"error\"}");
                return;
            }

            conn = Conexion.getConnection();

            // Parámetros
            String sortBy = request.getParameter("sortBy");
            String order = request.getParameter("order");
            String filterPriority = request.getParameter("filterPriority");
            String searchQuery = request.getParameter("search");

            StringBuilder sql = new StringBuilder("SELECT id, tipo_solicitud, descripcion, alta_prioridad, fecha_solicitud, estado, id_usuario FROM solicitudes_compra WHERE 1=1 ");

            boolean filtrarPorUsuario = !("usuario".equalsIgnoreCase(rolAutenticacion)
                    && "Otro".equalsIgnoreCase(cargoEmpleado));

            if (filtrarPorUsuario) {
                sql.append(" AND id_usuario = ?");
            }

            if (filterPriority != null && !filterPriority.equalsIgnoreCase("all")) {
                sql.append(" AND alta_prioridad = ?");
            }

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                sql.append(" AND (descripcion LIKE ? OR tipo_solicitud LIKE ?)");
            }

            // Orden
            String columnaOrden;
            sortBy = sortBy != null ? sortBy.toLowerCase() : "fecha";

            switch (sortBy) {
                case "estado":
                    columnaOrden = "estado";
                    break;
                case "prioridad":
                    columnaOrden = "alta_prioridad";
                    break;
                case "tipo":
                    columnaOrden = "tipo_solicitud";
                    break;
                default:
                    columnaOrden = "fecha_solicitud";
            }

            String ordenFinal = "ASC";
            if ("desc".equalsIgnoreCase(order)) ordenFinal = "DESC";

            sql.append(" ORDER BY ").append(columnaOrden).append(" ").append(ordenFinal);

            stmt = conn.prepareStatement(sql.toString());

            int index = 1;

            if (filtrarPorUsuario) {
                stmt.setInt(index++, idUsuario);
            }

            if (filterPriority != null && !filterPriority.equalsIgnoreCase("all")) {
                stmt.setBoolean(index++, Boolean.parseBoolean(filterPriority));
            }

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                stmt.setString(index++, "%" + searchQuery + "%");
                stmt.setString(index++, "%" + searchQuery + "%");
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                JSONObject json = new JSONObject();
                json.put("id", rs.getInt("id"));
                json.put("tipoSolicitud", rs.getString("tipo_solicitud"));
                json.put("descripcion", rs.getString("descripcion"));
                json.put("altaPrioridad", rs.getBoolean("alta_prioridad"));
                json.put("fechaSolicitud", rs.getTimestamp("fecha_solicitud"));
                json.put("estado", rs.getString("estado"));
                json.put("idUsuario", rs.getInt("id_usuario"));
                solicitudesArray.put(json);
            }

            response.setStatus(HttpServletResponse.SC_OK);
            out.print(solicitudesArray.toString());

        } catch (SQLException e) {
            response.setStatus(500);
            out.print("{\"mensaje\": \"Error SQL: " + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"mensaje\": \"Error interno: " + e.getMessage() + "\"}");
        }
    }
}

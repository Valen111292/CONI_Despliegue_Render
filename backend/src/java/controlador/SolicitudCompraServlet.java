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
import org.json.JSONObject;
import Conexion.Conexion;

@WebServlet("/solicitudes-compra")
public class SolicitudCompraServlet extends HttpServlet {

    // ==============================
    // CORS UNIVERSAL
    // ==============================
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

    private int getRequestId(HttpServletRequest request) {
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && pathInfo.length() > 1) {
            try {
                return Integer.parseInt(pathInfo.substring(1));
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    // ==============================
    // PUT
    // ==============================
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCORS(response);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        Connection conn = null;
        PreparedStatement stmt = null;
        int solicitudId = getRequestId(request);

        if (solicitudId == -1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"mensaje\": \"ID de solicitud inválido o no proporcionado.\", \"estado\": \"error\"}");
            return;
        }

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

            // Leer JSON
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            JSONObject jsonRequest = new JSONObject(sb.toString());

            String nuevoEstado = jsonRequest.optString("estado", null);
            String tipoSolicitud = jsonRequest.optString("tipoSolicitud", null);
            String descripcion = jsonRequest.optString("descripcion", null);
            Boolean altaPrioridad = jsonRequest.has("altaPrioridad") ? jsonRequest.getBoolean("altaPrioridad") : null;

            conn = Conexion.getConnection();

            // Lógica de actualización de estado
            if (nuevoEstado != null && "Usuario".equalsIgnoreCase(rolAutenticacion)
                    && "Otro".equalsIgnoreCase(cargoEmpleado)) {

                String sql = "UPDATE solicitudes_compra SET estado = ? WHERE id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, nuevoEstado);
                stmt.setInt(2, solicitudId);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    out.print("{\"mensaje\": \"Estado de solicitud actualizado con éxito\", \"estado\": \"ok\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"mensaje\": \"Solicitud no encontrada o no se pudo actualizar.\", \"estado\": \"error\"}");
                }
                return;
            }

            // Edición normal
            String checkSql = "SELECT estado, id_usuario FROM solicitudes_compra WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, solicitudId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String estadoActual = rs.getString("estado");
                int propietario = rs.getInt("id_usuario");

                if ("Pendiente".equalsIgnoreCase(estadoActual) && propietario == idUsuario) {
                    String updateSql = "UPDATE solicitudes_compra SET tipo_solicitud = COALESCE(?, tipo_solicitud), "
                            + "descripcion = COALESCE(?, descripcion), alta_prioridad = COALESCE(?, alta_prioridad) "
                            + "WHERE id = ?";
                    PreparedStatement up = conn.prepareStatement(updateSql);

                    up.setString(1, tipoSolicitud != null ? tipoSolicitud : null);
                    up.setString(2, descripcion != null ? descripcion : null);
                    if (altaPrioridad != null) {
                        up.setBoolean(3, altaPrioridad);
                    } else {
                        up.setNull(3, java.sql.Types.BOOLEAN);
                    }
                    up.setInt(4, solicitudId);

                    int rows = up.executeUpdate();
                    if (rows > 0) {
                        out.print("{\"mensaje\": \"Solicitud actualizada con éxito\", \"estado\": \"ok\"}");
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.print("{\"mensaje\": \"No se pudo actualizar la solicitud.\", \"estado\": \"error\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"mensaje\": \"No puede editar esta solicitud.\", \"estado\": \"error\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"mensaje\": \"Solicitud no encontrada.\", \"estado\": \"error\"}");
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"mensaje\": \"Error interno: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        }
    }

    // ==============================
    // DELETE
    // ==============================
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCORS(response);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        Connection conn = null;
        PreparedStatement stmt = null;
        int solicitudId = getRequestId(request);

        if (solicitudId == -1) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"mensaje\": \"ID inválido.\", \"estado\": \"error\"}");
            return;
        }

        try {
            HttpSession session = request.getSession(false);

            Integer idUsuario = null;
            String rol = null;
            if (session != null) {
                idUsuario = (Integer) session.getAttribute("idUsuario");
                rol = (String) session.getAttribute("rolAutenticacion");
            }

            if (idUsuario == null || rol == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"mensaje\": \"No autenticado.\", \"estado\": \"error\"}");
                return;
            }

            conn = Conexion.getConnection();

            String checkSql = "SELECT estado, id_usuario FROM solicitudes_compra WHERE id = ?";
            PreparedStatement chk = conn.prepareStatement(checkSql);
            chk.setInt(1, solicitudId);
            ResultSet rs = chk.executeQuery();

            if (rs.next()) {
                String estado = rs.getString("estado");
                int propietario = rs.getInt("id_usuario");

                if ("Pendiente".equalsIgnoreCase(estado) && propietario == idUsuario) {
                    String delSql = "DELETE FROM solicitudes_compra WHERE id = ?";
                    stmt = conn.prepareStatement(delSql);
                    stmt.setInt(1, solicitudId);

                    int rows = stmt.executeUpdate();
                    if (rows > 0) {
                        out.print("{\"mensaje\": \"Solicitud eliminada\", \"estado\": \"ok\"}");
                    } else {
                        response.setStatus(500);
                        out.print("{\"mensaje\": \"No se eliminó.\", \"estado\":\"error\"}");
                    }
                } else {
                    response.setStatus(403);
                    out.print("{\"mensaje\": \"No puede eliminar.\", \"estado\":\"error\"}");
                }

            } else {
                response.setStatus(404);
                out.print("{\"mensaje\": \"Solicitud no encontrada.\", \"estado\":\"error\"}");
            }

        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"mensaje\": \"Error interno: " + e.getMessage() + "\"}");
        }
    }

    // ==============================
    // POST
    // ==============================
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCORS(response);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            HttpSession session = request.getSession(false);
            Integer idUsuario = null;

            if (session != null) {
                idUsuario = (Integer) session.getAttribute("idUsuario");
            }

            if (idUsuario == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"mensaje\": \"Usuario no autenticado.\", \"estado\": \"error\"}");
                return;
            }

            // Leer JSON
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            JSONObject reqJson = new JSONObject(sb.toString());

            String tipoSolicitud = reqJson.getString("tipoSolicitud");
            String descripcion = reqJson.getString("descripcion");
            boolean altaPrioridad = reqJson.getBoolean("altaPrioridad");

            conn = Conexion.getConnection();
            String sql = "INSERT INTO solicitudes_compra (tipo_solicitud, descripcion, alta_prioridad, fecha_solicitud, estado, id_usuario) "
                    + "VALUES (?, ?, ?, NOW(), ?, ?)";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, tipoSolicitud);
            stmt.setString(2, descripcion);
            stmt.setBoolean(3, altaPrioridad);
            stmt.setString(4, "Pendiente");
            stmt.setInt(5, idUsuario);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                out.print("{\"mensaje\": \"Solicitud registrada\", \"estado\": \"ok\"}");
            } else {
                response.setStatus(500);
                out.print("{\"mensaje\": \"No se pudo registrar.\", \"estado\": \"error\"}");
            }

        } catch (Exception e) {
            response.setStatus(500);
            out.print("{\"mensaje\": \"Error interno: " + e.getMessage() + "\"}");
        }
    }
}

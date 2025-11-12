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

@WebServlet("/api/solicitudes-compra")
public class SolicitudCompraServlet extends HttpServlet {

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

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
            String cargoEmpleado = null; // Se usa para los permisos de edicion de estado de las solicitudes
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

            // Leer los datos del JSON de la solicitud
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            String jsonString = sb.toString();
            JSONObject jsonRequest = new JSONObject(jsonString);

            String nuevoEstado = jsonRequest.optString("estado", null);
            String tipoSolicitud = jsonRequest.optString("tipoSolicitud", null);
            String descripcion = jsonRequest.optString("descripcion", null);
            Boolean altaPrioridad = jsonRequest.has("altaPrioridad") ? jsonRequest.getBoolean("altaPrioridad") : null;

            conn = Conexion.getConnection();

            // Logica para actualizar estado (solo si el rol es "usuario" y el cargoEmpleado es "Otro")
            if (nuevoEstado != null && "Usuario".equalsIgnoreCase(rolAutenticacion) && "Otro".equalsIgnoreCase(cargoEmpleado)) {
                String sql = "UPDATE solicitudes_compra SET estado = ? WHERE id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, nuevoEstado);
                stmt.setInt(2, solicitudId);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    out.print("{\"mensaje\": \"Estado de solicitud actualizado con éxito\", \"estado\": \"ok\"}");
                    response.setStatus(HttpServletResponse.SC_OK);
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"mensaje\": \"Solicitud no encontrada o no se pudo actualizar el estado.\", \"estado\": \"error\"}");
                }
            } 
// Logica para editar (solo si el estado es pendiente y el usuario es el dueño)
            else if (tipoSolicitud != null || descripcion != null || altaPrioridad != null) {
                String checkSql = "SELECT estado, id_usuario FROM solicitudes_compra WHERE id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setInt(1, solicitudId);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    String estadoActual = rs.getString("estado");
                    int propietarioId = rs.getInt("id_usuario");

                    // Permitir edición solo si está Pendiente y el usuario es el propietario
                    if ("Pendiente".equalsIgnoreCase(estadoActual) && propietarioId == idUsuario) {
                        String updateSql = "UPDATE solicitudes_compra SET tipo_solicitud = COALESCE(?, tipo_solicitud), descripcion = COALESCE(?, descripcion), alta_prioridad = COALESCE(?, alta_prioridad) WHERE id = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);

                        if (tipoSolicitud != null) {
                            updateStmt.setString(1, tipoSolicitud);
                        } else {
                            updateStmt.setNull(1, java.sql.Types.VARCHAR);
                        }
                        if (descripcion != null) {
                            updateStmt.setString(2, descripcion);
                        } else {
                            updateStmt.setNull(2, java.sql.Types.LONGVARCHAR);
                        }
                        if (altaPrioridad != null) {
                            updateStmt.setBoolean(3, altaPrioridad);
                        } else {
                            updateStmt.setNull(3, java.sql.Types.BOOLEAN);
                        }
                        updateStmt.setInt(4, solicitudId);

                        int rowsAffected = updateStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            out.print("{\"mensaje\": \"Solicitud actualizada con éxito\", \"estado\": \"ok\"}");
                            response.setStatus(HttpServletResponse.SC_OK);
                        } else {
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            out.print("{\"mensaje\": \"No se pudo actualizar la solicitud.\", \"estado\": \"error\"}");
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        out.print("{\"mensaje\": \"No se puede editar la solicitud. Estado no es Pendiente o no es el propietario.\", \"estado\": \"error\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("{\"mensaje\": \"Solicitud no encontrada.\", \"estado\": \"error\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print("{\"mensaje\": \"No se proporcionaron datos válidos para actualizar o el cargo no permite la acción.\", \"estado\": \"error\"}");
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"mensaje\": \"Error de base de datos al actualizar solicitud: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } catch (org.json.JSONException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"mensaje\": \"Formato de solicitud inválido: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"mensaje\": \"Error interno del servidor: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } finally {
            try {
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

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
            String rolAutenticacion = (String) session.getAttribute("rolAutenticacion");
            String cargoEmpleado = null; // ¡NUEVO! Usaremos este para los permisos

            if (session != null) {
                idUsuario = (Integer) session.getAttribute("idUsuario");
                rolAutenticacion = (String) session.getAttribute("rolAutenticacion");
                cargoEmpleado = (String) session.getAttribute("cargoEmpleado"); // Obtener el cargo
            }

            if (idUsuario == null || rolAutenticacion == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                out.print("{\"mensaje\": \"Usuario no autenticado o sesión expirada, o no tiene cargo asignado.\", \"estado\": \"error\"}");
                return;
            }

            conn = Conexion.getConnection();

            String checkSql = "SELECT estado, id_usuario FROM solicitudes_compra WHERE id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, solicitudId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String estadoActual = rs.getString("estado");
                int propietarioId = rs.getInt("id_usuario");

                // Solo permitir eliminar si está Pendiente y el usuario es el propietario
                if ("Pendiente".equalsIgnoreCase(estadoActual) && propietarioId == idUsuario) {
                    String deleteSql = "DELETE FROM solicitudes_compra WHERE id = ?";
                    stmt = conn.prepareStatement(deleteSql);
                    stmt.setInt(1, solicitudId);

                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected > 0) {
                        out.print("{\"mensaje\": \"Solicitud eliminada con éxito\", \"estado\": \"ok\"}");
                        response.setStatus(HttpServletResponse.SC_OK);
                    } else {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.print("{\"mensaje\": \"No se pudo eliminar la solicitud.\", \"estado\": \"error\"}");
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    out.print("{\"mensaje\": \"No se puede eliminar la solicitud. Estado no es Pendiente o no es el propietario.\", \"estado\": \"error\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"mensaje\": \"Solicitud no encontrada.\", \"estado\": \"error\"}");
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"mensaje\": \"Error de base de datos al eliminar solicitud: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"mensaje\": \"Error interno del servidor: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } finally {
            try {
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
    
    // Dentro de SolicitudCompraServlet.java

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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
                out.print("{\"mensaje\": \"Usuario no autenticado o sesión expirada.\", \"estado\": \"error\"}");
                return;
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }
            String jsonString = sb.toString();
            JSONObject jsonRequest = new JSONObject(jsonString);

            String tipoSolicitud = jsonRequest.getString("tipoSolicitud");
            String descripcion = jsonRequest.getString("descripcion");
            boolean altaPrioridad = jsonRequest.getBoolean("altaPrioridad");

            conn = Conexion.getConnection();
            String sql = "INSERT INTO solicitudes_compra (tipo_solicitud, descripcion, alta_prioridad, fecha_solicitud, estado, id_usuario) VALUES (?, ?, ?, NOW(), ?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, tipoSolicitud);
            stmt.setString(2, descripcion);
            stmt.setBoolean(3, altaPrioridad);
            stmt.setString(4, "Pendiente"); // Estado inicial
            stmt.setInt(5, idUsuario);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                response.setStatus(HttpServletResponse.SC_CREATED); // 201 Created
                out.print("{\"mensaje\": \"Solicitud de compra registrada con éxito\", \"estado\": \"ok\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"mensaje\": \"No se pudo registrar la solicitud de compra.\", \"estado\": \"error\"}");
            }

        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"mensaje\": \"Error de base de datos al registrar solicitud: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } catch (org.json.JSONException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"mensaje\": \"Formato de solicitud inválido: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"mensaje\": \"Error interno del servidor: " + e.getMessage() + "\", \"estado\": \"error\"}");
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            out.flush();
        }
    }
    
}

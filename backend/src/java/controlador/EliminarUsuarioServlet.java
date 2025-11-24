package controlador;

import dao.UsuarioDAO;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

@WebServlet("/usuarios/eliminar")
public class EliminarUsuarioServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private void configurarCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "https://coni-frontend.onrender.com");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        configurarCORS(response);
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        configurarCORS(response);
        response.setContentType("application/json");

        String idParam = request.getParameter("id");

        if (idParam == null) {
            response.setStatus(400);
            response.getWriter().write("{\"error\":\"Falta parámetro ID\"}");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            boolean eliminado = usuarioDAO.eliminarUsuario(id);

            if (eliminado) {
                response.getWriter().write("{\"mensaje\":\"Usuario eliminado correctamente\"}");
            } else {
                response.setStatus(404);
                response.getWriter().write("{\"error\":\"Usuario no encontrado\"}");
            }

        } catch (NumberFormatException e) {
            response.setStatus(400);
            response.getWriter().write("{\"error\":\"ID inválido\"}");
        }
    }
}

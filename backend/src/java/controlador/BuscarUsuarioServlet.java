package controlador;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import modelo.Usuario;
import dao.UsuarioDAO;
import com.google.gson.Gson;

@WebServlet("/api/usuarios/cedula")
public class BuscarUsuarioServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    private void configurarCORS(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "https://coni-frontend.onrender.com");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarCORS(resp);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String cedula = request.getParameter("cedula");

        if (cedula == null || cedula.isEmpty()) {
            response.setStatus(400);
            response.getWriter().write("{\"error\":\"cedula no proporcionada\"}");
            return;
        }

        try {
            Usuario usuario = usuarioDAO.buscarPorCedula(cedula);
            if (usuario != null) {
                response.getWriter().write(new Gson().toJson(usuario));
            } else {
                response.setStatus(404);
                response.getWriter().write("{\"error\":\"Usuario no encontrado\"}");
            }
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"error\":\"Error en servidor\"}");
        }
    }
}

package controlador;

import com.google.gson.Gson;
import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import modelo.Usuario;
import dao.UsuarioDAO;

@WebServlet("/api/usuarios/modificar")
public class ModificarUsuarioServlet extends HttpServlet {
    
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Gson gson = new Gson();

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
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        configurarCORS(response);
        response.setContentType("application/json");

        StringBuilder sb = new StringBuilder();
        request.getReader().lines().forEach(sb::append);

        try {
            Usuario usuario = gson.fromJson(sb.toString(), Usuario.class);

            if (usuarioDAO.modificar(usuario)) {
                response.getWriter().write("{\"mensaje\":\"Usuario modificado correctamente\"}");
            } else {
                response.setStatus(404);
                response.getWriter().write("{\"error\":\"No se encontró el usuario\"}");
            }

        } catch (Exception e) {
            response.setStatus(400);
            response.getWriter().write("{\"error\":\"Datos inválidos\"}");
        }
    }
}

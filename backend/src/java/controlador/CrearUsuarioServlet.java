package controlador;

import dao.UsuarioDAO;
import dao.EmpleadoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import modelo.Usuario;
import com.google.gson.Gson;
import java.io.BufferedReader;

@WebServlet("/CrearUsuarioServlet")
public class CrearUsuarioServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        configurarCORS(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        StringBuilder sb = new StringBuilder();
        request.getReader().lines().forEach(sb::append);

        Usuario nuevoUsuario;
        try {
            nuevoUsuario = gson.fromJson(sb.toString(), Usuario.class);
        } catch (Exception e) {
            response.setStatus(400);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"JSON inválido\"}");
            return;
        }

        try {
            if (!empleadoDAO.existeEmpleadoPorCedula(nuevoUsuario.getCedula())) {
                response.setStatus(404);
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Empleado no existe\"}");
                return;
            }

            if (usuarioDAO.existeUsuarioConCedula(nuevoUsuario.getCedula())) {
                response.setStatus(409);
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Usuario ya existe\"}");
                return;
            }

            if (usuarioDAO.insertar(nuevoUsuario)) {
                response.setStatus(201);
                response.getWriter().write("{\"status\":\"ok\",\"message\":\"Usuario creado\"}");
            } else {
                response.setStatus(500);
                response.getWriter().write("{\"status\":\"error\",\"message\":\"Error al crear\"}");
            }

        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("{\"status\":\"error\",\"message\":\"Error interno\"}");
        }
    }
}

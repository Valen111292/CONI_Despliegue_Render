/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import modelo.Usuario;
import dao.UsuarioDAO;
import com.google.gson.Gson;

@WebServlet("/api/usuarios/cedula")
public class BuscarUsuarioServlet extends HttpServlet {
    
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String cedula = request.getParameter("cedula");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (cedula == null || cedula.trim().isEmpty()){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"cedula no proporcionada\"}");
            return;
        }
        
        try {
        Usuario usuario = usuarioDAO.buscarPorCedula(cedula);
        if (usuario != null){
        String json = new Gson().toJson(usuario);
        response.getWriter().write(json);
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"Usuario no encontrado\"}");
        }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Error en el servidor\"}");
        }
    }
}

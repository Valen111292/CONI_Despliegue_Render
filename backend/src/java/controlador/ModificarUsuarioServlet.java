/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import modelo.Usuario;
import dao.UsuarioDAO;

/**
 *
 * @author ansap
 */
@WebServlet("/api/usuarios/modificar")
public class ModificarUsuarioServlet extends HttpServlet {
    
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        StringBuilder jsonBuffer = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) !=null){
                jsonBuffer.append(line);
            }
        }
    try {
        Usuario usuario = gson.fromJson(jsonBuffer.toString(), Usuario.class);
        boolean modificado = usuarioDAO.modificar(usuario);
        if (modificado){
            response.getWriter().write("{\"mensaje\":\"Usuario modificado correctamente\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"error\":\"no se encontró el usuario para modificar}");
        }
    } catch (Exception e){
        e.printStackTrace();
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"error\":\"Datos inválidos\"}");
    }
}
}
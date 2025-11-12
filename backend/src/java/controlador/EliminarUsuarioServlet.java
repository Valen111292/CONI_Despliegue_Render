/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import dao.UsuarioDAO;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

/**
 *
 * @author ansap
 */
@WebServlet("/api/usuarios/eliminar")
public class EliminarUsuarioServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("id");

        if (idParam != null) {
            try {
                int id = Integer.parseInt(idParam);

                HttpSession session = request.getSession(false);
                if (session != null) {
                    Object usuarioActualObj = session.getAttribute("UsuarioLogueado");
                    if (usuarioActualObj instanceof modelo.Usuario usuarioActual) {
                        if (usuarioActual.getId() == id) {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"error\":\"No puedes eliminar tu propio usuario\"}");
                            return;
                        } 
                    }
                }

                boolean eliminado = usuarioDAO.eliminarUsuario(id);

                if (eliminado) {
                    response.getWriter().write("{\"mensaje\":\"Usuario eliminado correctamente\"}");
                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("{\"error\":\"Usuario no encontrado\"}");
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("{\"error\":\"ID inválido\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\":\"Falta el parámetro ID\"}");
        }
    } 

}

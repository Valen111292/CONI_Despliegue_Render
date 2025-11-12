/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controlador;

import java.io.IOException;
import jakarta.servlet.http.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.Usuario;
import dao.UsuarioDAO;

/**
 *
 * @author ansap
 */
@WebServlet("/CambiarPasswordServlet")
public class CambiarPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String actual = request.getParameter("actual");
        String nueva = request.getParameter("nueva");
        String confirmar = request.getParameter("confirmar");

        HttpSession session = request.getSession();
        Usuario usuarios = (Usuario) session.getAttribute("UsuarioLogueado");

        if (usuarios == null) {
            response.sendRedirect("sesion.jsp");
            return;
        }

        UsuarioDAO dao = new UsuarioDAO();
        if (!nueva.equals(confirmar)) {
            request.setAttribute("mensaje", "La nueva contraseña no coincide con la confirmación.");
        } else if (!usuarios.getPassword().equals(actual)) {
            request.setAttribute("mensaje", "Intente nuevamente, la contraseña actual es incorrecta.");
        } else {

            boolean actualizado = dao.cambiarContraseña(usuarios.getId(), nueva);
            if (actualizado) {
                usuarios.setPassword(nueva);
                session.setAttribute("usuarios", usuarios);
                request.setAttribute("mensaje", "Contraseña actualizada con exito.");
            } else {
                request.setAttribute("mensaje", "Error al actualizar la contraseña.");
            }
        }
        request.getRequestDispatcher("cambiarPassword.jsp").forward(request, response);
    }
}

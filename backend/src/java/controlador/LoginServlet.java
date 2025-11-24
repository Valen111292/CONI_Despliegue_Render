package controlador;

import dao.UsuarioDAO;
import modelo.Usuario;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔥 FIX CORS PARA PERMITIR COOKIE DE SESIÓN DESDE FRONTEND EN RENDER
        response.setHeader("Access-Control-Allow-Origin", "https://coni-frontend.onrender.com");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        JSONObject jsonResponse = new JSONObject();

        try {
            // Leer JSON recibido
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = request.getReader().readLine()) != null) {
                sb.append(line);
            }

            String jsonString = sb.toString();
            JSONObject jsonRequest = new JSONObject(jsonString);

            String username = jsonRequest.getString("username");
            String password = jsonRequest.getString("password");

            UsuarioDAO dao = new UsuarioDAO();
            Usuario usuario = dao.validar(username, password);

            if (usuario != null) {

                // CREACIÓN DE SESIÓN
                HttpSession session = request.getSession(true);
                session.setAttribute("idUsuario", usuario.getId());
                session.setAttribute("rolAutenticacion", usuario.getRol());
                session.setAttribute("cargoEmpleado", usuario.getCargoEmpleado());
                session.setAttribute("username", usuario.getUsername());
                session.setMaxInactiveInterval(3600); // 1 hora

                // 🔥 FIX CRÍTICO PARA CHROME + RENDER:
                // Obliga a que el navegador ACEPTE la cookie JSESSIONID cross-domain
                response.setHeader(
                    "Set-Cookie",
                    "JSESSIONID=" + session.getId() +
                    "; Path=/; HttpOnly; Secure; SameSite=None"
                );

                jsonResponse.put("success", true);
                jsonResponse.put("message", "Login exitoso");

                JSONObject userData = new JSONObject();
                userData.put("id", usuario.getId());
                userData.put("rolAutenticacion", usuario.getRol());
                userData.put("cargoEmpleado", usuario.getCargoEmpleado());
                userData.put("username", usuario.getUsername());
                jsonResponse.put("user", userData);

                response.setStatus(HttpServletResponse.SC_OK);

            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Usuario, contraseña o rol incorrectos.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }

        } catch (org.json.JSONException e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Formato JSON inválido.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            e.printStackTrace();

        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Error interno: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();

        } finally {
            out.print(jsonResponse.toString());
            out.flush();
        }
    }
}

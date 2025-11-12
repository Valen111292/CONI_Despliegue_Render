package controlador;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import modelo.EmpleadoVO;
import dao.EmpleadoDAO;
import java.util.List;

/**
 *
 * @author ansap
 */
@WebServlet("/EmpleadoServlet")
public class EmpleadoServlet extends HttpServlet {

    private EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String cedula = request.getParameter("cedula");

        if (cedula != null && !cedula.isEmpty()) {
            // Obtener un solo empleado
            EmpleadoVO empleado = empleadoDAO.obtenerEmpleadoPorCedula(cedula);
            if (empleado != null) {
                out.print(gson.toJson(empleado));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"error\":\"Empleado no encontrado\"}");
            }
        } else {
            // Obtener la lista de todos los empleados
            List<EmpleadoVO> empleados = empleadoDAO.listarEmpleados();
            // Si la lista está vacia no es un error, simplemente no hay empleados para mostrar
            // Siempre se devuelve un array JSON, aunque esté vacío
            out.print(gson.toJson(empleados));
        }

        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        // Asegúrate de que el JSON no esté vacío si esperas un objeto EmpleadoVO
        // Esto puede causar problemas si la acción viene solo como un parámetro y no en el body.
        EmpleadoVO empleado = null;
        try {
            BufferedReader reader = request.getReader();
            empleado = gson.fromJson(reader, EmpleadoVO.class);
        } catch (com.google.gson.JsonSyntaxException e) {
            // Si el body es vacío o no es un JSON válido, 'empleado' será null.
            // Esto puede ocurrir si el frontend envía una petición POST vacía o con solo parámetros URL.
            System.err.println("Error al parsear JSON en doPost: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("Formato de datos inválido. Asegúrese de enviar un JSON valido.");
            return;
        } catch (IOException e) {
            System.err.println("Error de I/O al leer el cuerpo de la solicitud en doPost: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal server error
            out.print("Error interno del servidor al procesar su solicitud.");
            return;
        }

        if (empleado == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("Datos de empleado no proporcionados.");
            return;
        }

        boolean registrado = empleadoDAO.registrarEmpleado(empleado);

        if (registrado) {
            response.setStatus(HttpServletResponse.SC_OK); // 200 OK
            out.print("Empleado registrado correctamente");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("Error al registrar el empleado");
        }
        out.flush();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        EmpleadoVO empleado = null;

        try {
            BufferedReader reader = request.getReader();
            empleado = gson.fromJson(reader, EmpleadoVO.class);
        } catch (com.google.gson.JsonSyntaxException e) {
            System.err.println("Error al parsear JSON en doPut: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("Formato de datos inválido para la actualizacion.");
            return;
        } catch (IOException e) {
            System.err.println("Error de I/O al leer el cuerpo de la solicitud en doPut: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("Error interno del servidor al procesar su solicitud.");
            return;
        }

        if (empleado == null || empleado.getCedula() == null || empleado.getCedula().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("Datos de empleado incompletos o inválidos para la actualización (cedula requerida).");
            return;
        }

        boolean actualizado = empleadoDAO.actualizarEmpleado(empleado);
        if (actualizado) {
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("Empleado actualizado correctamente");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("Error al actualizar el empleado.");
        }
        out.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain;charset=UTF-8");
        String cedula = request.getParameter("cedula");
        PrintWriter out = response.getWriter();

        // Lógica para evitar autoborrado (Paso 6)
        HttpSession session = request.getSession(false);
        // Suponiendo que guardas la cédula del usuario logueado en la sesión
        String cedulaUsuarioLogueado = null;

        if (session != null && session.getAttribute("cedulaUsuario") != null) {
            cedulaUsuarioLogueado = (String) session.getAttribute("cedulaUsuario");
        }

        if (cedula == null || cedula.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("Falta el parámetro cédula para eliminar.");
            return;
        }

        if (cedulaUsuarioLogueado != null && cedula.equals(cedulaUsuarioLogueado)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print("Error: no puedes eliminar tu propio registro de empleado.");
            return;
        }

        boolean eliminado = empleadoDAO.eliminarEmpleado(cedula);

        if (eliminado) {
            response.setStatus(HttpServletResponse.SC_OK);
            out.print("Empleado eliminado correctamente.");
        } else {
            // Podría ser 404 si el empleado no existe o 500 si hubo un error en la DB
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("Error al eliminar el empleado. Es posible que no exista o haya un error en la base de datos.");
        }
        out.flush();
    }
}

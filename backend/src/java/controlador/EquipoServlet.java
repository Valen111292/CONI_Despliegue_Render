package controlador;

import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import dao.EquipoDAO;
import modelo.EquipoVO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/EquipoServlet")
public class EquipoServlet extends HttpServlet {

    private final EquipoDAO dao = new EquipoDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            BufferedReader reader = request.getReader();
            EquipoVO equipo = gson.fromJson(reader, EquipoVO.class);
            equipo.setClase(equipo.getClase().toUpperCase());

            // Validar serie duplicada antes de insertar
            if (dao.verificarSerieExiste(equipo.getN_serie())) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"El número de serie ya está registrado\"}");
                return;
            }

            boolean ok = dao.insertarEquipo(equipo);
            if (ok) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(equipo)); // devolver datos con inventario generado
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"message\":\"Error al registrar equipo\"}");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String serie = request.getParameter("verificarSerie");
        String accion = request.getParameter("accion");
        String estado = request.getParameter("estado");
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        if (serie != null) {
            try {
                boolean existe = dao.verificarSerieExiste(serie);
                response.getWriter().write("{\"existe\": " + existe + "}");
            } catch (SQLException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"message\":\"" + e.getMessage() + "\"}");
            }
        } else if ("listar".equals(accion)) {
        try {
            List<EquipoVO> lista;
            if (estado != null && !estado.isEmpty()) {
                //  FILTRAR por estado
                lista = dao.listarEquiposPorEstado(estado);
            } else {
                lista = dao.listarEquipos();
            }
            out.write(gson.toJson(lista));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }
    else {
        out.write("{\"message\":\"Parámetro inválido\"}");
    }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String inventario = request.getParameter("n_inventario");

        try {
            boolean eliminado = dao.eliminarEquipo(inventario);
            if (eliminado) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\":\"Equipo eliminado correctamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\":\"Equipo no encontrado\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            BufferedReader reader = request.getReader();
            EquipoVO equipo = gson.fromJson(reader, EquipoVO.class);

            boolean actualizado = dao.actualizarEquipo(equipo);
            if (actualizado) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"Equipo actualizado correctamente\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"message\":\"No se encontró el equipo para actualizar\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\":\"" + e.getMessage() + "\"}");
        }
    }

}

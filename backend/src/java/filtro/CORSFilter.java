package filtro;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CORSFilter implements Filter {

    // Lista blanca simple (puedes ampliar)
    private static final String ALLOWED_ORIGIN = "http://localhost:3001";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Si quieres en dev aceptar el Origin que venga (con credentials debe ser exacto)
        String origin = req.getHeader("Origin");
        if (ALLOWED_ORIGIN.equals(origin)) {
            res.setHeader("Access-Control-Allow-Origin", origin);
        } else {
            // Para desarrollo puedes comentar la siguiente línea y permitir cualquier origen:
            // res.setHeader("Access-Control-Allow-Origin", "*");
            res.setHeader("Access-Control-Allow-Origin", ALLOWED_ORIGIN);
        }

        res.setHeader("Vary", "Origin"); // recomendable si variamos el origen
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        // permite las cabeceras que el navegador envía en preflight (incluye Content-Type)
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Accept, Cookie");
        // habilita envio de cookies/credenciales
        res.setHeader("Access-Control-Allow-Credentials", "true");
        // opcional: cuanto tiempo cachear la respuesta preflight (en segundos)
        res.setHeader("Access-Control-Max-Age", "3600");

        // Responder inmediatamente a OPTIONS (preflight)
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }
}

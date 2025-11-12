package filtro;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")  // Intercepta todas las URLs
public class CORSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // Origen permitido, cámbialo si tienes otro frontend
        String allowedOrigin = "http://localhost:3001"; // ¡Usar HTTPS y la URL desplegada!

        // Añadir headers CORS comunes a TODAS las respuestas
        response.setHeader("Access-Control-Allow-Origin", allowedOrigin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.setHeader("Vary", "Origin"); // Para que los proxies no cacheen mal
        response.setHeader("Access-Control-Max-Age", "3600"); 

        // Responder directamente OPTIONS (preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().flush(); 
            return;  // No seguir con el chain, solo responder preflight
        }

        // Para los demás métodos, continuar con la cadena de filtros/servlets
        chain.doFilter(req, res);
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
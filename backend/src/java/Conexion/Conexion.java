package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    public static Connection getConnection() throws SQLException {
        
        // 1. OBTENER VARIABLES DE ENTORNO DE RENDER
        // Render nos da el host de la DB, el nombre, usuario y contraseña
        String DB_HOST = System.getenv("DB_HOST");
        String DB_NAME = System.getenv("DB_NAME");
        String DB_USER = System.getenv("DB_USER");
        String DB_PASSWORD = System.getenv("DB_PASSWORD");
        
        // 2. CONSTRUIR LA URL DE CONEXIÓN DE POSTGRESQL (puerto 5432 estándar)
        // Usamos las variables para construir la URL dinámicamente
        String URL = "jdbc:postgresql://" + DB_HOST + ":5432/" + DB_NAME;

        try {
            // 3. CARGAR EL DRIVER DE POSTGRESQL (debes tener el .jar en el proyecto)
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            // Si falta el driver, lanzamos un error claro.
            throw new SQLException("Error al cargar el Driver de PostgreSQL. Asegúrate de incluir el JAR en el proyecto.", e);
        }
        
        // 4. ESTABLECER LA CONEXIÓN
        // Usamos el usuario y contraseña proporcionados por Render.
        return DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
    }
}
package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // URL y credenciales de la base de datos en Render
    private static final String URL = "jdbc:postgresql://dpg-d49umn9e2q1c73dsdrug-a.oregon-postgres.render.com/coni";
private static final String USER = "coni_user";
private static final String PASSWORD = "LeCaaRPYQG9G57Gsfdl3VSmeWG9GCr98";


    // Obtener la conexión
    public static Connection getConnection() throws SQLException {
        try {
            // Cargar el driver de PostgreSQL
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Error al cargar el Driver de PostgreSQL", e);
        }

        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a PostgreSQL ✅");
            return conn;
        } catch (SQLException e) {
            System.err.println("Error al conectar con PostgreSQL ❌: " + e.getMessage());
            throw e;
        }
    }
}

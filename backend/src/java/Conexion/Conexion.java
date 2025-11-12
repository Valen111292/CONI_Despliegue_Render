/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // CAMBIOS CLAVE:
    // 1. HOST: Usar 'database' (el nombre del servicio Docker) en lugar de 'localhost'.
    // 2. DB NAME: Usar 'coni' (el nuevo nombre de la base de datos).
    // 3. PASSWORD: Usar 'root_pass' (la contraseña de Docker Compose).
    private static final String URL = "jdbc:mysql://database:3306/coni";
    private static final String user = "root";
    private static final String password = "root_pass"; // Asegura que la contraseña sea la de Docker

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Este error puede ocurrir si el JAR del driver MySQL no está incluido en el WAR
            throw new SQLException("Error al cargar el Driver MySQL. Asegúrate de que el JAR del driver está incluido en el proyecto.", e);
        }
        return DriverManager.getConnection(URL, user, password);
    }
}
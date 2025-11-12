/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

import java.sql.Connection;
import Conexion.Conexion;

public class testConexion {
    public static void main(String[] args) {
        try {
            Connection con = Conexion.getConnection();
            if (con != null) {
                System.out.println("✅ Conexión exitosa a la base de datos.");
            } else {
                System.out.println("❌ No se pudo conectar.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

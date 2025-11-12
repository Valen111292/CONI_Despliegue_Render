package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import Conexion.Conexion;
import modelo.EmpleadoVO;
import java.util.List;
import java.util.ArrayList;


public class EmpleadoDAO {

    public boolean registrarEmpleado(EmpleadoVO emp) {
        boolean registrado = false;

        try (Connection conn = Conexion.getConnection()) {

            String idGenerado = emp.getId_empleado();
            // Si el ID ya viene (ej. de una re-asignación en el futuro), úsalo.
            // De lo contrario, generarlo aquí.
            if(idGenerado == null || idGenerado.isEmpty()){
                String prefijo = obtenerPrefijoCargo(emp.getCargo());
                int consecutivo = obtenerSiguienteConsecutivo(conn, emp.getCargo(), prefijo); // Pasa la conexión
                idGenerado = prefijo + String.format("%02d", consecutivo);
            }
            emp.setId_empleado(idGenerado);

            String sql = "INSERT INTO empleados (id_empleado, nombre, cedula, email, cargo) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, emp.getId_empleado());
            ps.setString(2, emp.getNombre());
            ps.setString(3, emp.getCedula());
            ps.setString(4, emp.getEmail());
            ps.setString(5, emp.getCargo());

            registrado = ps.executeUpdate() > 0;

        } catch (SQLException e) {
            // Manejo específico para duplicidad de cédula o id_empleado si son UNIQUE
            if (e.getSQLState().startsWith("23")) { // SQLState para integridad de datos (duplicidad)
                System.err.println("Error SQL: La cédula o ID de empleado ya existe. " + e.getMessage());
                // Podrías lanzar una excepción personalizada aquí o devolver un código de error específico
            }else{
                e.printStackTrace();
                System.err.println("Error SQL al registrar empleado: " + e.getMessage());
            }
            }
            return registrado;
}
            

    private String obtenerPrefijoCargo(String cargo) {
        switch (cargo.toLowerCase()) {
            case "auxiliar de logistica":
                return "LOG";
            case "aprendiz":
                return "APR";
            case "ejecutivo(a) de ventas":
                return "VEN";
            case "tesorero":
                return "TES";
            case "gerente de distribuciones":
                return "GER";
            default:
                return "EMP";
        }
    }

private int obtenerSiguienteConsecutivo(Connection conn, String cargo, String prefijo) throws SQLException {
        // Obtenemos el último consecutivo para el prefijo dado
        // Buscamos el ID con el prefijo y el número más alto
        String sql = "SELECT id_empleado FROM empleados WHERE id_empleado LIKE ? ORDER BY id_empleado DESC LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefijo + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String ultimoId = rs.getString("id_empleado");
                    // Extraer el número del ID (ej. de "LOG05" extraemos "05")
                    String numeroStr = ultimoId.substring(prefijo.length());
                    try {
                        int ultimoConsecutivo = Integer.parseInt(numeroStr);
                        return ultimoConsecutivo + 1;
                    } catch (NumberFormatException e) {
                        // Si el formato del número es inválido, empezar desde 1
                        System.err.println("Advertencia: Formato de consecutivo inválido en ID " + ultimoId + ". Reiniciando consecutivo para prefijo " + prefijo);
                        return 1;
                    }
                }
            }
        }
        return 1; // Si no hay IDs con ese prefijo, empezamos en 1
    }

    public List<EmpleadoVO> listarEmpleados() {
        List<EmpleadoVO> lista = new ArrayList<>();
        String sql = "SELECT id_empleado, nombre, cedula, email, cargo FROM empleados";

        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                EmpleadoVO emp = new EmpleadoVO();
                emp.setId_empleado(rs.getString("id_empleado"));
                emp.setNombre(rs.getString("nombre"));
                emp.setCedula(rs.getString("cedula"));
                emp.setEmail(rs.getString("email"));
                emp.setCargo(rs.getString("cargo"));
                lista.add(emp);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Manejo de errores
            System.err.println("Error SQL al listar empleado: " + e.getMessage());
        }
        return lista;
    }

    public EmpleadoVO obtenerEmpleadoPorCedula(String cedula) {
        EmpleadoVO emp = null;
        String sql = "SELECT id_empleado, nombre, cedula, email, cargo FROM empleados WHERE cedula = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    emp = new EmpleadoVO();
                    emp.setId_empleado(rs.getString("id_empleado"));
                    emp.setNombre(rs.getString("nombre"));
                    emp.setCedula(rs.getString("cedula"));
                    emp.setEmail(rs.getString("email"));
                    emp.setCargo(rs.getString("cargo"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error SQL al obtener empleado por cédula: " + e.getMessage());
        }
        return emp;
    }

    public boolean actualizarEmpleado(EmpleadoVO emp) {
        boolean actualizado = false;
        String sql = "UPDATE empleados SET nombre = ?, email = ?, cargo = ?, id_empleado = ? WHERE cedula = ?"; // Se añade id_empleado a la actualización

        try (Connection conn = Conexion.getConnection()) {
            // 1. Obtener el empleado existente por su cédula para comparar el cargo
            EmpleadoVO empleadoExistente = obtenerEmpleadoPorCedula(emp.getCedula());

            if (empleadoExistente == null) {
                System.err.println("Error: Empleado con cédula " + emp.getCedula() + " no encontrado para actualizar.");
                return false; // No se puede actualizar si no existe
            }

            String nuevoIdEmpleado = empleadoExistente.getId_empleado(); // Por defecto, mantiene el ID actual

            // 2. Verificar si el cargo ha cambiado
            if (!empleadoExistente.getCargo().equalsIgnoreCase(emp.getCargo())) {
                System.out.println("El cargo ha cambiado de " + empleadoExistente.getCargo() + " a " + emp.getCargo());
                // El cargo ha cambiado, recalcular el id_empleado
                String nuevoPrefijo = obtenerPrefijoCargo(emp.getCargo());
                int nuevoConsecutivo = obtenerSiguienteConsecutivo(conn, emp.getCargo(), nuevoPrefijo);
                nuevoIdEmpleado = nuevoPrefijo + String.format("%02d", nuevoConsecutivo);
                System.out.println("Nuevo ID de empleado generado: " + nuevoIdEmpleado);
            } else {
                System.out.println("El cargo no ha cambiado. Manteniendo ID: " + empleadoExistente.getId_empleado());
            }

            // 3. Ejecutar la actualización con el nuevo (o el mismo) id_empleado
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, emp.getNombre());
                ps.setString(2, emp.getEmail());
                ps.setString(3, emp.getCargo()); // Se actualiza el cargo
                ps.setString(4, nuevoIdEmpleado); // Se actualiza el id_empleado (recalculado o el mismo)
                ps.setString(5, emp.getCedula());

                actualizado = ps.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error SQL al actualizar empleado: " + e.getMessage());
        }
        return actualizado;
    }

    public boolean eliminarEmpleado(String cedula) {
        boolean eliminado = false;
        String sql = "DELETE FROM empleados WHERE cedula = ?";

        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cedula);
            eliminado = ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error SQL al eliminar empleado: " + e.getMessage());
        }
        return eliminado;
    }
    
    // Método para verificar si un empleado existe por su cédula (útil para la validación de usuario)
    public boolean existeEmpleadoPorCedula(String cedula) {
        String sql = "SELECT COUNT(*) FROM empleados WHERE cedula = ?";
        try (Connection conn = Conexion.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error SQL al verificar existencia de empleado por cédula: " + e.getMessage());
        }
        return false;
    }

}

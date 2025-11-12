package dao;

import modelo.Usuario;
import Conexion.Conexion;
import java.sql.Connection;
import java.sql.*;

public class UsuarioDAO {

    private Connection conn;

    public UsuarioDAO() {
    }

    public Usuario validar(String username, String password) {
        Usuario usuario = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        PreparedStatement psEmpleado = null; // Para la tabla empleados
        ResultSet rsEmpleado = null; // Para la tabla empleados.

        try {
            conn = Conexion.getConnection();
            String sql = "SELECT id, nombre, cedula, rol, username, email, password FROM usuarios WHERE username = ? AND password = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            if (rs.next()) {
                usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setCedula(rs.getString("cedula"));
                usuario.setRol(rs.getString("rol")); // Rol de autenticacion (administrador, usuario)
                usuario.setUsername(rs.getString("username"));
                usuario.setEmail(rs.getString("email"));
                usuario.setPassword(rs.getString("password"));

                // LOGICA PARA OBTENER EL CARGO DEL EMPLEADO POR CEDULA
                String cedulaUsuario = usuario.getCedula();
                if (cedulaUsuario != null && !cedulaUsuario.isEmpty()) {
                    String sqlEmpleado = "SELECT cargo FROM empleados WHERE cedula =?";
                    psEmpleado = conn.prepareStatement(sqlEmpleado);
                    psEmpleado.setString(1, cedulaUsuario);
                    rsEmpleado = psEmpleado.executeQuery();

                    if (rsEmpleado.next()) {
                        usuario.setCargoEmpleado(rsEmpleado.getString("cargo"));
                    } else {
                        // SI EL USUARIO EXISTE PERO NO ESTÁ EN LA TABLA DE EMPLEADOS
                        System.out.println("Advertencia: usuario con cedula" + cedulaUsuario + " no encontrado en la tabla de empleados");
                        usuario.setCargoEmpleado(null);
                    }
                } else {
                    System.out.println("Advertencia: usuario " + username + " no tiene cedula registrada");
                    usuario.setCargoEmpleado(null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error de SQL en UsuarioDAO validar: " + e.getMessage());
        } finally {
            try {
                if (rsEmpleado != null) rsEmpleado.close();
                if (psEmpleado != null) psEmpleado.close();
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch(SQLException e){
                e.printStackTrace();
            }
        }
        return usuario;
    }

    public boolean insertar(Usuario usuarios) {
        boolean registrado = false;
        String sql = "INSERT INTO usuarios (nombre, cedula, rol, username, email, password) VALUES (?, ?, ?, ?, ?, ?)"; // Asegúrate que los nombres de las columnas coincidan con tu DB

        try (Connection conn = Conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuarios.getNombre());
            ps.setString(2, usuarios.getCedula());
            ps.setString(3, usuarios.getRol());
            ps.setString(4, usuarios.getUsername());
            ps.setString(5, usuarios.getEmail());
            ps.setString(6, usuarios.getPassword());

            int filasAfectadas = ps.executeUpdate();
            registrado = filasAfectadas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error SQL al insertar usuario: " + e.getMessage());
        }
        return registrado;
    }

    public Usuario buscarPorCedula(String cedula) throws Exception {
        Usuario usuarios = null;
        System.out.println(">>> buscarPorCedula, cédula recibida: " + cedula);
        String sql = "SELECT * FROM usuarios WHERE cedula = ?";
        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cedula);
            ResultSet rs = ps.executeQuery();
            System.out.println(">>> SQL ejecutado: " + ps);

            if (rs.next()) {
                usuarios = new Usuario();
                usuarios.setId(rs.getInt("id"));
                usuarios.setNombre(rs.getString("nombre"));
                usuarios.setCedula(rs.getString("cedula"));
                usuarios.setRol(rs.getString("rol"));
                usuarios.setUsername(rs.getString("username"));
                usuarios.setPassword(rs.getString("password"));
                usuarios.setEmail(rs.getString("email"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public boolean modificar(Usuario usuarios) {
        String sql = "UPDATE usuarios SET nombre = ?, cedula = ?, rol = ?, username = ?, email = ?, password = ? WHERE id = ?";

        try (Connection con = Conexion.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, usuarios.getNombre());
            stmt.setString(2, usuarios.getCedula());
            stmt.setString(3, usuarios.getRol());
            stmt.setString(4, usuarios.getUsername());
            stmt.setString(5, usuarios.getEmail());
            stmt.setString(6, usuarios.getPassword());
            stmt.setInt(7, usuarios.getId());

            int filas = stmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cambiarContraseña(int id, String nueva) {
        String sql = "UPDATE usuarios SET password = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, nueva);
            stmt.setInt(2, id);

            int filas = stmt.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeUsuarioConCedula(String cedula) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE cedula = ?";
        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Si el conteo es mayor a 0, la cédula ya existe
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error SQL al verificar existencia de cédula: " + e.getMessage());
        }
        return false; // Por defecto, si hay un error o no se encuentra, asumimos que no existe para evitar bloqueos
    }

}

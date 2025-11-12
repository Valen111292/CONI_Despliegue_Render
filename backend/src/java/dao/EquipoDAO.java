// src/java/modelo/EquipoDAO.java
package dao;

import Conexion.Conexion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.EquipoVO;

public class EquipoDAO {

    public boolean insertarEquipo(EquipoVO equipo) throws SQLException {
        String nuevoInventario = generarNumeroInventario(equipo.getClase());
        equipo.setN_inventario(nuevoInventario);

        String sql = "INSERT INTO equipos_perifericos "
                + "(n_inventario, n_serie, tipo, clase, marca, ram, disco, procesador, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, equipo.getN_inventario());
            ps.setString(2, equipo.getN_serie());
            ps.setString(3, equipo.getTipo());
            ps.setString(4, equipo.getClase());
            ps.setString(5, equipo.getMarca());
            ps.setString(6, equipo.getRam());
            ps.setString(7, equipo.getDisco());
            ps.setString(8, equipo.getProcesador());
            ps.setString(9, equipo.getEstado());

            int filas = ps.executeUpdate();
            return filas > 0;
        }
    }

    public String generarNumeroInventario(String clase) throws SQLException {
        String prefijo = clase.equalsIgnoreCase("periferico") ? "PER" : "EQ";
        int longitudPrefijo = prefijo.length();
        
        String query = "SELECT MAX(CAST(SUBSTRING(n_inventario, " + (longitudPrefijo + 1) + ") AS UNSIGNED)) AS max_num " +
                   "FROM equipos_perifericos WHERE n_inventario LIKE ?";

        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, prefijo + "%");
            
            ResultSet rs = ps.executeQuery();
            int siguiente = 1;
            if (rs.next()) {
                siguiente = rs.getInt("max_num") + 1;
            }
            // Asegurar formato como EQ01, EQ02...
            return String.format("%s%02d", prefijo, siguiente);
        }

    }

    public boolean verificarSerieExiste(String serie) throws SQLException {
        String sql = "SELECT COUNT(*) FROM equipos_perifericos WHERE n_serie = ?";
        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, serie);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<EquipoVO> listarEquipos() throws SQLException {
        List<EquipoVO> lista = new ArrayList<>();
        String sql = "SELECT * FROM equipos_perifericos";

        try (Connection con = Conexion.getConnection(); PreparedStatement stmt = con.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                EquipoVO equipo = new EquipoVO();
                equipo.setN_inventario(rs.getString("n_inventario"));
                equipo.setN_serie(rs.getString("n_serie"));
                equipo.setClase(rs.getString("clase"));
                equipo.setTipo(rs.getString("tipo"));
                equipo.setMarca(rs.getString("marca"));
                equipo.setEstado(rs.getString("estado"));
                equipo.setRam(rs.getString("ram"));
                equipo.setDisco(rs.getString("disco"));
                equipo.setProcesador(rs.getString("procesador"));
                lista.add(equipo);
            }
        }

        return lista;
    }
    
    public List<EquipoVO> listarEquiposPorEstado(String estado) throws SQLException {
    List<EquipoVO> lista = new ArrayList<>();
    String sql = "SELECT * FROM equipos_perifericos WHERE estado = ?";
    try (Connection con = Conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, estado.toUpperCase());
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                EquipoVO e = new EquipoVO();
                e.setN_inventario(rs.getString("n_inventario"));
                e.setN_serie(rs.getString("n_serie"));
                e.setClase(rs.getString("clase"));
                e.setTipo(rs.getString("tipo"));
                e.setMarca(rs.getString("marca"));
                e.setRam(rs.getString("ram"));
                e.setDisco(rs.getString("disco"));
                e.setProcesador(rs.getString("procesador"));
                e.setEstado(rs.getString("estado"));
                lista.add(e);
            }
        }
    }
    return lista;
}


    public boolean eliminarEquipo(String n_inventario) throws SQLException {
        String sql = "DELETE FROM equipos_perifericos WHERE n_inventario = ?";
        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, n_inventario);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarEquipo(EquipoVO equipo) throws SQLException {
        String sql = "UPDATE equipos_perifericos SET n_serie=?, clase=?, tipo=?, marca=?, ram=?, disco=?, procesador=?, estado=? WHERE n_inventario=?";
        try (Connection conn = Conexion.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, equipo.getN_serie().toUpperCase());
            stmt.setString(2, equipo.getClase().toUpperCase());
            stmt.setString(3, equipo.getTipo().toUpperCase());
            stmt.setString(4, equipo.getMarca().toUpperCase());
            stmt.setString(5, equipo.getRam().toUpperCase());
            stmt.setString(6, equipo.getDisco().toUpperCase());
            stmt.setString(7, equipo.getProcesador().toUpperCase());
            stmt.setString(8, equipo.getEstado().toUpperCase());
            stmt.setString(9, equipo.getN_inventario());

            return stmt.executeUpdate() > 0;
        }
    }

}

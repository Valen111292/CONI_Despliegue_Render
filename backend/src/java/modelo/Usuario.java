package modelo;

import java.sql.Timestamp;

public class Usuario {

    private int id;
    private String nombre;
    private String cedula;
    private String rol;
    private String cargoEmpleado; // Campo para Almacenar el cargo del Empleado
    private String username;
    private String email;
    private String password;
    private String tokenRecuperacion;
    private Timestamp tokenExpiracion;

    public Usuario() {
    }

    public Usuario(String nombre, String cedula, String rol, String username, String email, String password) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.rol = rol;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    
    public String getCargoEmpleado() {
        return cargoEmpleado;
    }

    public void setCargoEmpleado(String cargoEmpleado) {
        this.cargoEmpleado = cargoEmpleado;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTokenRecuperacion() {
        return tokenRecuperacion;
    }

    public void setTokenRecuperacion(String tokenRecuperacion) {
        this.tokenRecuperacion = tokenRecuperacion;
    }

    public Timestamp getTokenExpiracion() {
        return tokenExpiracion;
    }

    public void setTokenExpiracion(Timestamp tokenExpiracion) {
        this.tokenExpiracion = tokenExpiracion;
    }

}

package modelo;

public class EmpleadoVO {

    private String id_empleado;
    private String nombre;
    private String cedula;
    private String email;
    private String cargo;

    public EmpleadoVO() {
    }

    public EmpleadoVO(String id_empleado, String nombre, String cedula, String email, String cargo) {
        this.id_empleado = id_empleado;
        this.nombre = nombre;
        this.cedula = cedula;
        this.email = email;
        this.cargo = cargo;
    }

    public String getId_empleado() {
        return id_empleado;
    }

    public void setId_empleado(String id_empleado) {
        this.id_empleado = id_empleado;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
    
}

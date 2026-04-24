package modelo;

import java.io.Serializable;

/**
 * Clase base para todos los usuarios del sistema.
 * Administrador, Instructor y Estudiante heredan de esta clase.
 */
public abstract class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    protected String codigo;
    protected String nombre;
    protected String fechaNacimiento;
    protected String genero;
    protected String contrasena;
    protected String rol; // "ADMINISTRADOR", "INSTRUCTOR", "ESTUDIANTE"

    public Usuario(String codigo, String nombre, String fechaNacimiento,
                   String genero, String contrasena, String rol) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    // Getters
    public String getCodigo()          { return codigo; }
    public String getNombre()          { return nombre; }
    public String getFechaNacimiento() { return fechaNacimiento; }
    public String getGenero()          { return genero; }
    public String getContrasena()      { return contrasena; }
    public String getRol()             { return rol; }

    // Setters
    public void setNombre(String nombre)       { this.nombre = nombre; }
    public void setContrasena(String c)        { this.contrasena = c; }
    public void setFechaNacimiento(String f)   { this.fechaNacimiento = f; }
    public void setGenero(String g)            { this.genero = g; }

    /** Método polimórfico que cada rol implementa para describirse */
    public abstract String getDescripcion();

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | " + rol;
    }
}

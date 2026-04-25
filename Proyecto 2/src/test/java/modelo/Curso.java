package modelo;

import java.io.Serializable;

/**
 * Representa un curso en la plataforma académica.
 */
public class Curso implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nombre;
    private String descripcion;
    private int creditos;
    private String seccion;

    public Curso(String codigo, String nombre, String descripcion,
                 int creditos, String seccion) {
        this.codigo     = codigo;
        this.nombre     = nombre;
        this.descripcion = descripcion;
        this.creditos   = creditos;
        this.seccion    = seccion;
    }

    // Getters
    public String getCodigo()      { return codigo; }
    public String getNombre()      { return nombre; }
    public String getDescripcion() { return descripcion; }
    public int    getCreditos()    { return creditos; }
    public String getSeccion()     { return seccion; }

    // Setters (solo campos editables)
    public void setNombre(String nombre)           { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setCreditos(int creditos)          { this.creditos = creditos; }
    public void setSeccion(String seccion)         { this.seccion = seccion; }

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | Créditos: " + creditos + " | Sección: " + seccion;
    }
}

package modelo;

import java.io.Serializable;

/**
 * Representa una calificación registrada para un estudiante
 * en una sección determinada.
 */
public class Nota implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codigoCurso;
    private String codigoSeccion;
    private String codigoEstudiante;
    private String etiqueta;       // nombre de la actividad (ej: "Parcial 1")
    private double ponderacion;    // porcentaje (0-100)
    private double nota;           // valor (0-100)
    private String fechaRegistro;  // YYYY-MM-DD

    public Nota(String codigoCurso, String codigoSeccion, String codigoEstudiante,
                String etiqueta, double ponderacion, double nota, String fechaRegistro) {
        this.codigoCurso      = codigoCurso;
        this.codigoSeccion    = codigoSeccion;
        this.codigoEstudiante = codigoEstudiante;
        this.etiqueta         = etiqueta;
        this.ponderacion      = ponderacion;
        this.nota             = nota;
        this.fechaRegistro    = fechaRegistro;
    }

    // Getters
    public String getCodignoCurso()      { return codigoCurso; }
    public String getCodigoSeccion()     { return codigoSeccion; }
    public String getCodigoEstudiante()  { return codigoEstudiante; }
    public String getEtiqueta()          { return etiqueta; }
    public double getPonderacion()       { return ponderacion; }
    public double getNota()              { return nota; }
    public String getFechaRegistro()     { return fechaRegistro; }

    // Setters editables
    public void setPonderacion(double p) { this.ponderacion = p; }
    public void setNota(double n)        { this.nota = n; }
    public void setEtiqueta(String e)    { this.etiqueta = e; }

    @Override
    public String toString() {
        return etiqueta + " | Pond: " + ponderacion + "% | Nota: " + nota +
               " | Fecha: " + fechaRegistro;
    }
}

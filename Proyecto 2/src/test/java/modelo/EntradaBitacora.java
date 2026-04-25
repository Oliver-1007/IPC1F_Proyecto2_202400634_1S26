package modelo;

import java.io.Serializable;

/**
 * Representa una entrada en la bitácora del sistema.
 */
public class EntradaBitacora implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fechaHora;
    private String tipoUsuario;
    private String codigoUsuario;
    private String operacion;
    private String estado;       // "EXITOSA" o "FALLIDA"
    private String descripcion;

    public EntradaBitacora(String fechaHora, String tipoUsuario, String codigoUsuario,
                            String operacion, String estado, String descripcion) {
        this.fechaHora     = fechaHora;
        this.tipoUsuario   = tipoUsuario;
        this.codigoUsuario = codigoUsuario;
        this.operacion     = operacion;
        this.estado        = estado;
        this.descripcion   = descripcion;
    }

    public String getFechaHora()     { return fechaHora; }
    public String getTipoUsuario()   { return tipoUsuario; }
    public String getCodigoUsuario() { return codigoUsuario; }
    public String getOperacion()     { return operacion; }
    public String getEstado()        { return estado; }
    public String getDescripcion()   { return descripcion; }

    @Override
    public String toString() {
        return "[" + fechaHora + "] | " + tipoUsuario + " | " + codigoUsuario +
               " | " + operacion + " | " + estado + " | " + descripcion;
    }
}

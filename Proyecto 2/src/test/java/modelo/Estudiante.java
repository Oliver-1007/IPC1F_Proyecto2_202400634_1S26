package modelo;

/**
 * Clase que representa a un Estudiante del sistema.
 * Hereda de Usuario.
 */
public class Estudiante extends Usuario {
    private static final long serialVersionUID = 1L;

    private static final int MAX_INSCRIPCIONES = 20;
    private String[] seccionesInscritas;
    private int totalInscritas;

    public Estudiante(String codigo, String nombre, String fechaNacimiento,
                      String genero, String contrasena) {
        super(codigo, nombre, fechaNacimiento, genero, contrasena, "ESTUDIANTE");
        this.seccionesInscritas = new String[MAX_INSCRIPCIONES];
        this.totalInscritas = 0;
    }

    /** Inscribe al estudiante en una sección */
    public boolean inscribirSeccion(String codigoSeccion) {
        if (totalInscritas >= MAX_INSCRIPCIONES) return false;
        if (estaInscrito(codigoSeccion)) return false;
        seccionesInscritas[totalInscritas++] = codigoSeccion;
        return true;
    }

    /** Verifica si ya está inscrito en una sección */
    public boolean estaInscrito(String codigoSeccion) {
        for (int i = 0; i < totalInscritas; i++) {
            if (seccionesInscritas[i] != null && seccionesInscritas[i].equals(codigoSeccion)) {
                return true;
            }
        }
        return false;
    }

    /** Desasigna al estudiante de una sección */
    public boolean desasignarSeccion(String codigoSeccion) {
        for (int i = 0; i < totalInscritas; i++) {
            if (seccionesInscritas[i] != null && seccionesInscritas[i].equals(codigoSeccion)) {
                for (int j = i; j < totalInscritas - 1; j++) {
                    seccionesInscritas[j] = seccionesInscritas[j + 1];
                }
                seccionesInscritas[--totalInscritas] = null;
                return true;
            }
        }
        return false;
    }

    public String[] getSeccionesInscritas() { return seccionesInscritas; }
    public int getTotalInscritas()           { return totalInscritas; }

    @Override
    public String getDescripcion() {
        return "Estudiante con " + totalInscritas + " secciones inscritas.";
    }
}

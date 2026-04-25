package modelo;

/**
 * Clase que representa a un Instructor del sistema.
 * Hereda de Usuario.
 */
public class Instructor extends Usuario {
    private static final long serialVersionUID = 1L;

    private int seccionesAsignadas;
    // Arreglo de códigos de secciones asignadas (sin ArrayList)
    private String[] secciones;
    private int totalSecciones;
    private static final int MAX_SECCIONES = 50;

    public Instructor(String codigo, String nombre, String fechaNacimiento,
                      String genero, String contrasena) {
        super(codigo, nombre, fechaNacimiento, genero, contrasena, "INSTRUCTOR");
        this.seccionesAsignadas = 0;
        this.secciones = new String[MAX_SECCIONES];
        this.totalSecciones = 0;
    }

    /** Asigna una sección al instructor */
    public boolean agregarSeccion(String codigoSeccion) {
        if (totalSecciones >= MAX_SECCIONES) return false;
        secciones[totalSecciones++] = codigoSeccion;
        seccionesAsignadas++;
        return true;
    }

    /** Verifica si el instructor tiene asignada una sección */
    public boolean tieneSeccion(String codigoSeccion) {
        for (int i = 0; i < totalSecciones; i++) {
            if (secciones[i] != null && secciones[i].equals(codigoSeccion)) {
                return true;
            }
        }
        return false;
    }

    /** Elimina una sección del instructor */
    public boolean eliminarSeccion(String codigoSeccion) {
        for (int i = 0; i < totalSecciones; i++) {
            if (secciones[i] != null && secciones[i].equals(codigoSeccion)) {
                // Desplazar
                for (int j = i; j < totalSecciones - 1; j++) {
                    secciones[j] = secciones[j + 1];
                }
                secciones[--totalSecciones] = null;
                seccionesAsignadas--;
                return true;
            }
        }
        return false;
    }

    public String[] getSecciones() { return secciones; }
    public int getTotalSecciones() { return totalSecciones; }
    public int getSeccionesAsignadas() { return seccionesAsignadas; }

    @Override
    public String getDescripcion() {
        return "Instructor con " + seccionesAsignadas + " secciones asignadas.";
    }
}

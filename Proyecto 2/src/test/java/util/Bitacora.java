package util;

import modelo.EntradaBitacora;
import modelo.SistemaDatos;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utilitario centralizado para registrar eventos en la bitácora.
 */
public class Bitacora {

    private static SistemaDatos datos;

    public static void setDatos(SistemaDatos d) {
        datos = d;
    }

    /**
     * Registra un evento en la bitácora del sistema.
     * @param tipoUsuario  "ADMINISTRADOR" | "INSTRUCTOR" | "ESTUDIANTE"
     * @param codigoUsuario código del usuario que realizó la acción
     * @param operacion     nombre de la operación (ej: "CREAR_ESTUDIANTE")
     * @param estado        "EXITOSA" | "FALLIDA"
     * @param descripcion   descripción detallada del resultado
     */
    public static void registrar(String tipoUsuario, String codigoUsuario,
                                  String operacion, String estado, String descripcion) {
        if (datos == null) return;
        String fechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
        EntradaBitacora entrada = new EntradaBitacora(
                fechaHora, tipoUsuario, codigoUsuario, operacion, estado, descripcion);
        datos.registrarEvento(entrada);
    }

    public static String getFechaHoraActual() {
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());
    }

    public static String getFechaActual() {
        return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    public static String getTimestampArchivo() {
        return new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
    }
}

package util;

import modelo.SistemaDatos;
import java.io.*;

/**
 * Utilidad para guardar y recuperar el estado del sistema mediante serialización.
 */
public class Serializacion {

    private static final String ARCHIVO = "datos_sistema.ser";

    /** Guarda el sistema de datos en disco */
    public static void guardar(SistemaDatos datos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(ARCHIVO))) {
            oos.writeObject(datos);
        } catch (IOException e) {
            System.err.println("[Serialización] Error al guardar: " + e.getMessage());
        }
    }

    /** Carga el sistema de datos desde disco; si no existe retorna uno nuevo */
    public static SistemaDatos cargar() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            return new SistemaDatos();
        }
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(archivo))) {
            return (SistemaDatos) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Serialización] Error al cargar: " + e.getMessage());
            return new SistemaDatos();
        }
    }
}

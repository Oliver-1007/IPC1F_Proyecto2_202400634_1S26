package util;

import modelo.*;
import java.io.*;

/**
 * Exporta datos del sistema a archivos CSV.
 * No usa librerías externas ni colecciones prohibidas.
 */
public class ExportadorCSV {

    /**
     * Exporta todas las notas de un estudiante a CSV.
     */
    public static String exportarNotasEstudiante(SistemaDatos datos,
                                                  String codigoEst,
                                                  String rutaDestino) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDestino))) {
            pw.println("Seccion,Etiqueta,Ponderacion,Nota,Fecha,Promedio,Estado");
            for (Nota n : datos.getTodasNotas()) {
                if (n.getCodigoEstudiante().equals(codigoEst)) {
                    double prom  = datos.calcularPromedio(codigoEst, n.getCodigoSeccion());
                    String estado = prom >= 61 ? "Aprobado" : "Reprobado";
                    pw.println(n.getCodigoSeccion() + "," + n.getEtiqueta() + "," +
                               n.getPonderacion() + "," + n.getNota() + "," +
                               n.getFechaRegistro() + "," +
                               String.format("%.2f", prom) + "," + estado);
                }
            }
            return "OK";
        } catch (IOException e) {
            return "Error al exportar: " + e.getMessage();
        }
    }

    /**
     * Exporta notas de una sección completa a CSV.
     */
    public static String exportarNotasSeccion(SistemaDatos datos,
                                               String codigoSeccion,
                                               String rutaDestino) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDestino))) {
            pw.println("Estudiante,Etiqueta,Ponderacion,Nota,Fecha,Promedio,Estado");
            for (Nota n : datos.getTodasNotas()) {
                if (n.getCodigoSeccion().equals(codigoSeccion)) {
                    double prom  = datos.calcularPromedio(n.getCodigoEstudiante(), codigoSeccion);
                    String estado = prom >= 61 ? "Aprobado" : "Reprobado";
                    pw.println(n.getCodigoEstudiante() + "," + n.getEtiqueta() + "," +
                               n.getPonderacion() + "," + n.getNota() + "," +
                               n.getFechaRegistro() + "," +
                               String.format("%.2f", prom) + "," + estado);
                }
            }
            return "OK";
        } catch (IOException e) {
            return "Error al exportar: " + e.getMessage();
        }
    }

    /**
     * Exporta la bitácora completa a CSV.
     */
    public static String exportarBitacora(SistemaDatos datos, String rutaDestino) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDestino))) {
            pw.println("FechaHora,TipoUsuario,Codigo,Operacion,Estado,Descripcion");
            for (EntradaBitacora e : datos.getTodaBitacora()) {
                pw.println(e.getFechaHora() + "," + e.getTipoUsuario() + "," +
                           e.getCodigoUsuario() + "," + e.getOperacion() + "," +
                           e.getEstado() + "," + e.getDescripcion());
            }
            return "OK";
        } catch (IOException e) {
            return "Error al exportar: " + e.getMessage();
        }
    }

    /**
     * Exporta lista de instructores a CSV.
     */
    public static String exportarInstructores(SistemaDatos datos, String rutaDestino) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDestino))) {
            pw.println("Codigo,Nombre,FechaNacimiento,Genero,SeccionesAsignadas");
            for (Usuario u : datos.getUsuariosPorRol("INSTRUCTOR")) {
                Instructor ins = (Instructor) u;
                pw.println(ins.getCodigo() + "," + ins.getNombre() + "," +
                           ins.getFechaNacimiento() + "," + ins.getGenero() + "," +
                           ins.getTotalSecciones());
            }
            return "OK";
        } catch (IOException e) {
            return "Error al exportar: " + e.getMessage();
        }
    }

    /**
     * Exporta lista de estudiantes a CSV.
     */
    public static String exportarEstudiantes(SistemaDatos datos, String rutaDestino) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDestino))) {
            pw.println("Codigo,Nombre,FechaNacimiento,Genero,SeccionesInscritas");
            for (Usuario u : datos.getUsuariosPorRol("ESTUDIANTE")) {
                Estudiante est = (Estudiante) u;
                pw.println(est.getCodigo() + "," + est.getNombre() + "," +
                           est.getFechaNacimiento() + "," + est.getGenero() + "," +
                           est.getTotalInscritas());
            }
            return "OK";
        } catch (IOException e) {
            return "Error al exportar: " + e.getMessage();
        }
    }
}

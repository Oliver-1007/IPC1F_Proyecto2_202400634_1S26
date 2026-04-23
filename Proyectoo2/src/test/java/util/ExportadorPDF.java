package util;

import modelo.*;
import java.io.*;

/**
 * Generador de reportes en formato de texto plano con extensión .txt
 * (simulando un PDF sin librerías externas prohibidas).
 *
 * NOTA PARA EL ESTUDIANTE:
 * Si deseas generar PDF real, puedes agregar iText o Apache PDFBox en el classpath
 * y reemplazar los métodos de este archivo. Esta implementación usa texto
 * para cumplir la restricción de "sin librerías no permitidas".
 */
public class ExportadorPDF {

    // ================================================================
    //  REPORTE: TOP 5 MEJOR DESEMPEÑO
    // ================================================================
    public static String reporteMejorDesempeno(SistemaDatos datos,
                                                String codigoSeccion,
                                                String rutaDestino) {
        // Obtener estudiantes en la sección
        String[] estudiantes = obtenerEstudiantesEnSeccion(datos, codigoSeccion);
        int total = estudiantes.length;
        if (total == 0) return "No hay estudiantes en la sección '" + codigoSeccion + "'.";

        // Calcular promedios
        double[] promedios = new double[total];
        for (int i = 0; i < total; i++) {
            promedios[i] = datos.calcularPromedio(estudiantes[i], codigoSeccion);
        }

        // Ordenar descendente (burbuja)
        for (int i = 0; i < total - 1; i++) {
            for (int j = 0; j < total - i - 1; j++) {
                if (promedios[j] < promedios[j + 1]) {
                    double tmpD = promedios[j]; promedios[j] = promedios[j+1]; promedios[j+1] = tmpD;
                    String tmpS = estudiantes[j]; estudiantes[j] = estudiantes[j+1]; estudiantes[j+1] = tmpS;
                }
            }
        }

        int top = Math.min(5, total);
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDestino))) {
            pw.println("=================================================");
            pw.println("  REPORTE: TOP 5 ESTUDIANTES - MEJOR DESEMPEÑO");
            pw.println("=================================================");
            pw.println("Sección: " + codigoSeccion);
            pw.println("Fecha de emisión: " + Bitacora.getFechaHoraActual());
            pw.println("-------------------------------------------------");
            pw.printf("%-5s %-15s %-12s %-12s%n", "Pos.", "Estudiante", "Promedio", "Estado");
            pw.println("-------------------------------------------------");
            for (int i = 0; i < top; i++) {
                String estado = promedios[i] >= 61 ? "Aprobado" : "Reprobado";
                pw.printf("%-5d %-15s %-12.2f %-12s%n",
                    i + 1, estudiantes[i], promedios[i], estado);
            }
            pw.println("=================================================");
            return "OK";
        } catch (IOException e) {
            return "Error al generar reporte: " + e.getMessage();
        }
    }

    // ================================================================
    //  REPORTE: TOP 5 BAJO DESEMPEÑO
    // ================================================================
    public static String reporteBajoDesempeno(SistemaDatos datos,
                                               String codigoSeccion,
                                               String rutaDestino) {
        String[] estudiantes = obtenerEstudiantesEnSeccion(datos, codigoSeccion);
        int total = estudiantes.length;
        if (total == 0) return "No hay estudiantes en la sección '" + codigoSeccion + "'.";

        double[] promedios = new double[total];
        for (int i = 0; i < total; i++) {
            promedios[i] = datos.calcularPromedio(estudiantes[i], codigoSeccion);
        }

        // Ordenar ascendente (burbuja)
        for (int i = 0; i < total - 1; i++) {
            for (int j = 0; j < total - i - 1; j++) {
                if (promedios[j] > promedios[j + 1]) {
                    double tmpD = promedios[j]; promedios[j] = promedios[j+1]; promedios[j+1] = tmpD;
                    String tmpS = estudiantes[j]; estudiantes[j] = estudiantes[j+1]; estudiantes[j+1] = tmpS;
                }
            }
        }

        int top = Math.min(5, total);
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDestino))) {
            pw.println("=================================================");
            pw.println("  REPORTE: TOP 5 ESTUDIANTES - BAJO DESEMPEÑO");
            pw.println("=================================================");
            pw.println("Sección: " + codigoSeccion);
            pw.println("Fecha de emisión: " + Bitacora.getFechaHoraActual());
            pw.println("-------------------------------------------------");
            pw.printf("%-5s %-15s %-12s %-20s%n", "Pos.", "Estudiante", "Promedio", "Recomendación");
            pw.println("-------------------------------------------------");
            for (int i = 0; i < top; i++) {
                String rec = promedios[i] < 40 ? "Tutoría urgente" :
                             promedios[i] < 61 ? "Reforzamiento" : "Seguimiento";
                pw.printf("%-5d %-15s %-12.2f %-20s%n",
                    i + 1, estudiantes[i], promedios[i], rec);
            }
            pw.println("=================================================");
            return "OK";
        } catch (IOException e) {
            return "Error al generar reporte: " + e.getMessage();
        }
    }

    // ================================================================
    //  REPORTE: CALIFICACIONES POR SECCIÓN
    // ================================================================
    public static String reporteCalificacionesSeccion(SistemaDatos datos,
                                                       String codigoSeccion,
                                                       String rutaDestino) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDestino))) {
            pw.println("=================================================");
            pw.println("  REPORTE DE CALIFICACIONES POR SECCIÓN");
            pw.println("=================================================");
            pw.println("Sección: " + codigoSeccion);
            pw.println("Fecha de emisión: " + Bitacora.getFechaHoraActual());
            pw.println("-------------------------------------------------");

            String[] estudiantes = obtenerEstudiantesEnSeccion(datos, codigoSeccion);
            int aprobados = 0, reprobados = 0;

            for (String codEst : estudiantes) {
                double prom = datos.calcularPromedio(codEst, codigoSeccion);
                String estado = prom >= 61 ? "Aprobado" : "Reprobado";
                if (prom >= 61) aprobados++; else reprobados++;

                pw.println("Estudiante: " + codEst + "  |  Promedio: " +
                           String.format("%.2f", prom) + "  |  " + estado);

                Nota[] notas = datos.getNotasEstudianteSeccion(codEst, codigoSeccion);
                for (Nota n : notas) {
                    pw.printf("    %-20s  Pond: %5.1f%%  Nota: %6.2f%n",
                        n.getEtiqueta(), n.getPonderacion(), n.getNota());
                }
                pw.println();
            }

            pw.println("-------------------------------------------------");
            pw.println("Aprobados:  " + aprobados);
            pw.println("Reprobados: " + reprobados);
            pw.println("=================================================");
            return "OK";
        } catch (IOException e) {
            return "Error al generar reporte: " + e.getMessage();
        }
    }

    // ================================================================
    //  REPORTE: HISTORIAL INDIVIDUAL DE ESTUDIANTE
    // ================================================================
    public static String reporteHistorialEstudiante(SistemaDatos datos,
                                                     String codigoEst,
                                                     String rutaDestino) {
        Usuario u = datos.buscarUsuario(codigoEst);
        if (u == null) return "Estudiante '" + codigoEst + "' no encontrado.";

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDestino))) {
            pw.println("=================================================");
            pw.println("  HISTORIAL ACADÉMICO INDIVIDUAL");
            pw.println("=================================================");
            pw.println("Estudiante: " + u.getNombre());
            pw.println("Código:     " + u.getCodigo());
            pw.println("Fecha de emisión: " + Bitacora.getFechaHoraActual());
            pw.println("-------------------------------------------------");

            // Obtener secciones únicas del estudiante
            String[] secciones   = new String[50];
            int      totalSecs   = 0;
            for (Nota n : datos.getTodasNotas()) {
                if (n.getCodigoEstudiante().equals(codigoEst)) {
                    boolean yaEsta = false;
                    for (int i = 0; i < totalSecs; i++) {
                        if (secciones[i].equals(n.getCodigoSeccion())) { yaEsta = true; break; }
                    }
                    if (!yaEsta && totalSecs < 50) secciones[totalSecs++] = n.getCodigoSeccion();
                }
            }

            double promedioAcumulado = 0;
            for (int s = 0; s < totalSecs; s++) {
                String sec  = secciones[s];
                double prom = datos.calcularPromedio(codigoEst, sec);
                promedioAcumulado += prom;
                String estado = prom >= 61 ? "Aprobado" : "Reprobado";
                pw.println("Sección: " + sec + "  |  Promedio: " +
                           String.format("%.2f", prom) + "  |  " + estado);
                for (Nota n : datos.getNotasEstudianteSeccion(codigoEst, sec)) {
                    pw.printf("    %-20s  Pond: %5.1f%%  Nota: %6.2f  Fecha: %s%n",
                        n.getEtiqueta(), n.getPonderacion(), n.getNota(), n.getFechaRegistro());
                }
                pw.println();
            }

            if (totalSecs > 0) {
                pw.println("-------------------------------------------------");
                pw.printf("Promedio acumulado: %.2f%n", promedioAcumulado / totalSecs);
            }
            pw.println("=================================================");
            return "OK";
        } catch (IOException e) {
            return "Error al generar reporte: " + e.getMessage();
        }
    }

    // ================================================================
    //  REPORTE: INSCRIPCIONES POR CURSO
    // ================================================================
    public static String reporteInscripcionesCurso(SistemaDatos datos,
                                                    String codigoCurso,
                                                    String rutaDestino) {
        Curso curso = datos.buscarCurso(codigoCurso);
        if (curso == null) return "Curso '" + codigoCurso + "' no encontrado.";

        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaDestino))) {
            pw.println("=================================================");
            pw.println("  REPORTE DE INSCRIPCIONES POR CURSO");
            pw.println("=================================================");
            pw.println("Curso:   " + curso.getNombre() + " (" + curso.getCodigo() + ")");
            pw.println("Sección: " + curso.getSeccion());
            pw.println("Créditos: " + curso.getCreditos());
            pw.println("Fecha de emisión: " + Bitacora.getFechaHoraActual());
            pw.println("-------------------------------------------------");

            int totalInscritos = 0;
            for (Usuario u : datos.getUsuariosPorRol("ESTUDIANTE")) {
                Estudiante est = (Estudiante) u;
                if (est.estaInscrito(curso.getSeccion())) {
                    pw.println("  " + est.getCodigo() + " - " + est.getNombre());
                    totalInscritos++;
                }
            }
            pw.println("-------------------------------------------------");
            pw.println("Total inscritos: " + totalInscritos);
            pw.println("=================================================");
            return "OK";
        } catch (IOException e) {
            return "Error al generar reporte: " + e.getMessage();
        }
    }

    // ================================================================
    //  HELPER: obtener códigos únicos de estudiantes en una sección
    // ================================================================
    private static String[] obtenerEstudiantesEnSeccion(SistemaDatos datos,
                                                         String codigoSeccion) {
        String[] tmp = new String[500];
        int count = 0;
        for (Nota n : datos.getTodasNotas()) {
            if (n.getCodigoSeccion().equals(codigoSeccion)) {
                boolean existe = false;
                for (int i = 0; i < count; i++) {
                    if (tmp[i].equals(n.getCodigoEstudiante())) { existe = true; break; }
                }
                if (!existe) tmp[count++] = n.getCodigoEstudiante();
            }
        }
        String[] resultado = new String[count];
        for (int i = 0; i < count; i++) resultado[i] = tmp[i];
        return resultado;
    }
}

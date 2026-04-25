package modelo;

import java.io.Serializable;

/**
 * Almacén central de datos del sistema.
 * Se serializa/deserializa para persistencia entre sesiones.
 * Usa arreglos en lugar de colecciones.
 */
public class SistemaDatos implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final int MAX_USUARIOS    = 500;
    private static final int MAX_CURSOS      = 200;
    private static final int MAX_NOTAS       = 5000;
    private static final int MAX_BITACORA    = 10000;

    // Arreglos de entidades
    private Usuario[]        usuarios;
    private int              totalUsuarios;

    private Curso[]          cursos;
    private int              totalCursos;

    private Nota[]           notas;
    private int              totalNotas;

    private EntradaBitacora[] bitacora;
    private int               totalBitacora;

    // Contador de sesiones activas (usado por hilos)
    private int sesionesActivas;

    public SistemaDatos() {
        usuarios   = new Usuario[MAX_USUARIOS];
        cursos     = new Curso[MAX_CURSOS];
        notas      = new Nota[MAX_NOTAS];
        bitacora   = new EntradaBitacora[MAX_BITACORA];
        totalUsuarios  = 0;
        totalCursos    = 0;
        totalNotas     = 0;
        totalBitacora  = 0;
        sesionesActivas = 0;

        // Admin 
        agregarUsuario(new Administrador("admin", "Administrador",
                "01/01/1990", "M", "IPC1F"));
    }

    // ===================== USUARIOS =====================
    public boolean agregarUsuario(Usuario u) {
        if (totalUsuarios >= MAX_USUARIOS) return false;
        if (buscarUsuario(u.getCodigo()) != null) return false;
        usuarios[totalUsuarios++] = u;
        return true;
    }

    public Usuario buscarUsuario(String codigo) {
        for (int i = 0; i < totalUsuarios; i++) {
            if (usuarios[i] != null && usuarios[i].getCodigo().equals(codigo)) {
                return usuarios[i];
            }
        }
        return null;
    }

    public boolean eliminarUsuario(String codigo) {
        for (int i = 0; i < totalUsuarios; i++) {
            if (usuarios[i] != null && usuarios[i].getCodigo().equals(codigo)) {
                for (int j = i; j < totalUsuarios - 1; j++) {
                    usuarios[j] = usuarios[j + 1];
                }
                usuarios[--totalUsuarios] = null;
                return true;
            }
        }
        return false;
    }

    /** Devuelve copia del arreglo con solo los usuarios del rol indicado */
    public Usuario[] getUsuariosPorRol(String rol) {
        int count = 0;
        for (int i = 0; i < totalUsuarios; i++) {
            if (usuarios[i] != null && usuarios[i].getRol().equals(rol)) count++;
        }
        Usuario[] resultado = new Usuario[count];
        int idx = 0;
        for (int i = 0; i < totalUsuarios; i++) {
            if (usuarios[i] != null && usuarios[i].getRol().equals(rol)) {
                resultado[idx++] = usuarios[i];
            }
        }
        return resultado;
    }

    public Usuario[] getTodosUsuarios() {
        Usuario[] copia = new Usuario[totalUsuarios];
        for (int i = 0; i < totalUsuarios; i++) copia[i] = usuarios[i];
        return copia;
    }

    public int getTotalUsuarios()   { return totalUsuarios; }

    // ===================== CURSOS =====================
    public boolean agregarCurso(Curso c) {
        if (totalCursos >= MAX_CURSOS) return false;
        if (buscarCurso(c.getCodigo()) != null) return false;
        cursos[totalCursos++] = c;
        return true;
    }

    public Curso buscarCurso(String codigo) {
        for (int i = 0; i < totalCursos; i++) {
            if (cursos[i] != null && cursos[i].getCodigo().equals(codigo)) {
                return cursos[i];
            }
        }
        return null;
    }

    public boolean eliminarCurso(String codigo) {
        for (int i = 0; i < totalCursos; i++) {
            if (cursos[i] != null && cursos[i].getCodigo().equals(codigo)) {
                for (int j = i; j < totalCursos - 1; j++) {
                    cursos[j] = cursos[j + 1];
                }
                cursos[--totalCursos] = null;
                return true;
            }
        }
        return false;
    }

    public Curso[] getTodosCursos() {
        Curso[] copia = new Curso[totalCursos];
        for (int i = 0; i < totalCursos; i++) copia[i] = cursos[i];
        return copia;
    }

    public int getTotalCursos() { return totalCursos; }

    // ===================== NOTAS =====================
    public boolean agregarNota(Nota n) {
        if (totalNotas >= MAX_NOTAS) return false;
        notas[totalNotas++] = n;
        return true;
    }

    /** Devuelve todas las notas de un estudiante en una sección */
    public Nota[] getNotasEstudianteSeccion(String codigoEst, String codigoSec) {
        int count = 0;
        for (int i = 0; i < totalNotas; i++) {
            if (notas[i] != null &&
                notas[i].getCodigoEstudiante().equals(codigoEst) &&
                notas[i].getCodigoSeccion().equals(codigoSec)) count++;
        }
        Nota[] resultado = new Nota[count];
        int idx = 0;
        for (int i = 0; i < totalNotas; i++) {
            if (notas[i] != null &&
                notas[i].getCodigoEstudiante().equals(codigoEst) &&
                notas[i].getCodigoSeccion().equals(codigoSec)) {
                resultado[idx++] = notas[i];
            }
        }
        return resultado;
    }

    /** Calcula promedio ponderado de un estudiante en una sección */
    public double calcularPromedio(String codigoEst, String codigoSec) {
        Nota[] ns = getNotasEstudianteSeccion(codigoEst, codigoSec);
        if (ns.length == 0) return 0.0;
        double sumaPond = 0, sumaNotaPond = 0;
        for (Nota n : ns) {
            sumaPond     += n.getPonderacion();
            sumaNotaPond += n.getNota() * n.getPonderacion();
        }
        if (sumaPond == 0) return 0.0;
        return sumaNotaPond / sumaPond;
    }

    public boolean eliminarNota(String codigoEst, String codigoSec, String etiqueta) {
        for (int i = 0; i < totalNotas; i++) {
            if (notas[i] != null &&
                notas[i].getCodigoEstudiante().equals(codigoEst) &&
                notas[i].getCodigoSeccion().equals(codigoSec) &&
                notas[i].getEtiqueta().equals(etiqueta)) {
                for (int j = i; j < totalNotas - 1; j++) {
                    notas[j] = notas[j + 1];
                }
                notas[--totalNotas] = null;
                return true;
            }
        }
        return false;
    }

    public Nota[] getTodasNotas() {
        Nota[] copia = new Nota[totalNotas];
        for (int i = 0; i < totalNotas; i++) copia[i] = notas[i];
        return copia;
    }

    public int getTotalNotas() { return totalNotas; }

    // ===================== BITÁCORA =====================
    public void registrarEvento(EntradaBitacora entrada) {
        if (totalBitacora < MAX_BITACORA) {
            bitacora[totalBitacora++] = entrada;
        }
    }

    public EntradaBitacora[] getTodaBitacora() {
        EntradaBitacora[] copia = new EntradaBitacora[totalBitacora];
        for (int i = 0; i < totalBitacora; i++) copia[i] = bitacora[i];
        return copia;
    }

    public int getTotalBitacora() { return totalBitacora; }

    // ===================== SESIONES =====================
    public void incrementarSesiones() { sesionesActivas++; }
    public void decrementarSesiones() { if (sesionesActivas > 0) sesionesActivas--; }
    public int  getSesionesActivas()  { return sesionesActivas; }
}

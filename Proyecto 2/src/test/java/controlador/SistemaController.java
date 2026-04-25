package controlador;

import modelo.*;
import util.Serializacion;
import util.Bitacora;

/**
 * Controlador principal del sistema.
 * Conecta la Vista con el Modelo. Gestiona autenticación, CRUD y lógica de negocio.
 */
public class SistemaController {

    private SistemaDatos datos;
    private Usuario usuarioActual;

    public SistemaController() {
        datos = Serializacion.cargar();
        Bitacora.setDatos(datos);
    }

    // ===================== AUTENTICACIÓN =====================

    /**
     * Intenta autenticar un usuario.
     * @return Usuario autenticado, o null si falla.
     */
    public Usuario login(String codigo, String contrasena) {
        Usuario u = datos.buscarUsuario(codigo);
        if (u != null && u.getContrasena().equals(contrasena)) {
            usuarioActual = u;
            datos.incrementarSesiones();
            Bitacora.registrar(u.getRol(), codigo, "LOGIN", "EXITOSA",
                    "Inicio de sesión correcto");
            guardar();
            return u;
        }
        Bitacora.registrar("DESCONOCIDO", codigo, "LOGIN", "FALLIDA",
                "Credenciales incorrectas");
        guardar();
        return null;
    }

    public void logout() {
        if (usuarioActual != null) {
            datos.decrementarSesiones();
            Bitacora.registrar(usuarioActual.getRol(), usuarioActual.getCodigo(),
                    "LOGOUT", "EXITOSA", "Cierre de sesión");
            guardar();
            usuarioActual = null;
        }
    }

    public Usuario getUsuarioActual() { return usuarioActual; }
    public SistemaDatos getDatos()    { return datos; }

    // ===================== INSTRUCTORES =====================

    public String crearInstructor(String codigo, String nombre, String fechaNac,
                                   String genero, String contrasena) {
        if (codigo.isEmpty() || nombre.isEmpty() || contrasena.isEmpty())
            return "Todos los campos son obligatorios.";
        if (datos.buscarUsuario(codigo) != null)
            return "El código '" + codigo + "' ya existe en el sistema.";
        Instructor inst = new Instructor(codigo, nombre, fechaNac, genero, contrasena);
        datos.agregarUsuario(inst);
        Bitacora.registrar("ADMINISTRADOR", usuarioActual.getCodigo(),
                "CREAR_INSTRUCTOR", "EXITOSA", "Instructor " + codigo + " creado.");
        guardar();
        return "OK";
    }

    public String actualizarInstructor(String codigo, String nuevoNombre, String nuevaContrasena) {
        Usuario u = datos.buscarUsuario(codigo);
        if (u == null) return "Instructor '" + codigo + "' no encontrado.";
        if (!u.getRol().equals("INSTRUCTOR")) return "El código no corresponde a un instructor.";
        if (!nuevoNombre.isEmpty())    u.setNombre(nuevoNombre);
        if (!nuevaContrasena.isEmpty()) u.setContrasena(nuevaContrasena);
        Bitacora.registrar("ADMINISTRADOR", usuarioActual.getCodigo(),
                "ACTUALIZAR_INSTRUCTOR", "EXITOSA", "Instructor " + codigo + " actualizado.");
        guardar();
        return "OK";
    }

    public String eliminarInstructor(String codigo) {
        Usuario u = datos.buscarUsuario(codigo);
        if (u == null) return "Instructor '" + codigo + "' no encontrado.";
        if (!u.getRol().equals("INSTRUCTOR")) return "El código no corresponde a un instructor.";
        datos.eliminarUsuario(codigo);
        Bitacora.registrar("ADMINISTRADOR", usuarioActual.getCodigo(),
                "ELIMINAR_INSTRUCTOR", "EXITOSA", "Instructor " + codigo + " eliminado.");
        guardar();
        return "OK";
    }

    // ===================== ESTUDIANTES =====================

    public String crearEstudiante(String codigo, String nombre, String fechaNac,
                                   String genero, String contrasena) {
        if (codigo.isEmpty() || nombre.isEmpty() || contrasena.isEmpty())
            return "Todos los campos son obligatorios.";
        if (datos.buscarUsuario(codigo) != null)
            return "El código '" + codigo + "' ya existe.";
        Estudiante est = new Estudiante(codigo, nombre, fechaNac, genero, contrasena);
        datos.agregarUsuario(est);
        Bitacora.registrar("ADMINISTRADOR", usuarioActual.getCodigo(),
                "CREAR_ESTUDIANTE", "EXITOSA", "Estudiante " + codigo + " creado.");
        guardar();
        return "OK";
    }

    public String actualizarEstudiante(String codigo, String nuevoNombre, String nuevaContrasena) {
        Usuario u = datos.buscarUsuario(codigo);
        if (u == null) return "Estudiante '" + codigo + "' no encontrado.";
        if (!u.getRol().equals("ESTUDIANTE")) return "El código no corresponde a un estudiante.";
        if (!nuevoNombre.isEmpty())    u.setNombre(nuevoNombre);
        if (!nuevaContrasena.isEmpty()) u.setContrasena(nuevaContrasena);
        Bitacora.registrar("ADMINISTRADOR", usuarioActual.getCodigo(),
                "ACTUALIZAR_ESTUDIANTE", "EXITOSA", "Estudiante " + codigo + " actualizado.");
        guardar();
        return "OK";
    }

    public String eliminarEstudiante(String codigo) {
        Usuario u = datos.buscarUsuario(codigo);
        if (u == null) return "Estudiante '" + codigo + "' no encontrado.";
        if (!u.getRol().equals("ESTUDIANTE")) return "El código no corresponde a un estudiante.";
        datos.eliminarUsuario(codigo);
        Bitacora.registrar("ADMINISTRADOR", usuarioActual.getCodigo(),
                "ELIMINAR_ESTUDIANTE", "EXITOSA", "Estudiante " + codigo + " eliminado.");
        guardar();
        return "OK";
    }

    // ===================== CURSOS =====================

    public String crearCurso(String codigo, String nombre, String descripcion,
                              String creditosStr, String seccion) {
        if (codigo.isEmpty() || nombre.isEmpty())
            return "Código y nombre son obligatorios.";
        if (datos.buscarCurso(codigo) != null)
            return "El código de curso '" + codigo + "' ya existe.";
        int creditos;
        try { creditos = Integer.parseInt(creditosStr); }
        catch (NumberFormatException e) { return "Créditos debe ser un número entero."; }
        Curso c = new Curso(codigo, nombre, descripcion, creditos, seccion);
        datos.agregarCurso(c);
        Bitacora.registrar("ADMINISTRADOR", usuarioActual.getCodigo(),
                "CREAR_CURSO", "EXITOSA", "Curso " + codigo + " creado.");
        guardar();
        return "OK";
    }

    public String actualizarCurso(String codigo, String nombre, String descripcion,
                                   String creditosStr, String seccion) {
        Curso c = datos.buscarCurso(codigo);
        if (c == null) return "Curso '" + codigo + "' no encontrado.";
        if (!nombre.isEmpty()) c.setNombre(nombre);
        if (!descripcion.isEmpty()) c.setDescripcion(descripcion);
        if (!creditosStr.isEmpty()) {
            try { c.setCreditos(Integer.parseInt(creditosStr)); }
            catch (NumberFormatException e) { return "Créditos inválido."; }
        }
        if (!seccion.isEmpty()) c.setSeccion(seccion);
        Bitacora.registrar("ADMINISTRADOR", usuarioActual.getCodigo(),
                "ACTUALIZAR_CURSO", "EXITOSA", "Curso " + codigo + " actualizado.");
        guardar();
        return "OK";
    }

    public String eliminarCurso(String codigo) {
        Curso c = datos.buscarCurso(codigo);
        if (c == null) return "Curso '" + codigo + "' no encontrado.";
        datos.eliminarCurso(codigo);
        Bitacora.registrar("ADMINISTRADOR", usuarioActual.getCodigo(),
                "ELIMINAR_CURSO", "EXITOSA", "Curso " + codigo + " eliminado.");
        guardar();
        return "OK";
    }

    // ===================== NOTAS =====================

    public String crearNota(String codigoCurso, String codigoSeccion, String codigoEstudiante,
                             String etiqueta, String ponderacionStr, String notaStr,
                             String fecha, String codigoInstructor) {
        // Validar curso
        if (datos.buscarCurso(codigoCurso) == null)
            return "Curso '" + codigoCurso + "' no existe.";
        // Validar instructor asignado a sección
        Usuario inst = datos.buscarUsuario(codigoInstructor);
        if (inst == null || !inst.getRol().equals("INSTRUCTOR"))
            return "Instructor no válido.";
        if (!((Instructor) inst).tieneSeccion(codigoSeccion))
            return "El instructor no tiene asignada la sección '" + codigoSeccion + "'.";
        // Validar estudiante inscrito
        Usuario est = datos.buscarUsuario(codigoEstudiante);
        if (est == null || !est.getRol().equals("ESTUDIANTE"))
            return "Estudiante '" + codigoEstudiante + "' no encontrado.";
        if (!((Estudiante) est).estaInscrito(codigoSeccion))
            return "Estudiante no está inscrito en la sección '" + codigoSeccion + "'.";
        // Parsear valores
        double ponderacion, notaVal;
        try {
            ponderacion = Double.parseDouble(ponderacionStr);
            notaVal     = Double.parseDouble(notaStr);
        } catch (NumberFormatException e) {
            return "Ponderación y Nota deben ser valores numéricos.";
        }
        if (notaVal < 0 || notaVal > 100) return "Nota debe estar entre 0 y 100.";
        if (ponderacion <= 0)             return "Ponderación debe ser mayor que 0.";

        Nota n = new Nota(codigoCurso, codigoSeccion, codigoEstudiante,
                etiqueta, ponderacion, notaVal, fecha);
        datos.agregarNota(n);
        Bitacora.registrar("INSTRUCTOR", codigoInstructor, "CREAR_NOTA", "EXITOSA",
                "Nota " + etiqueta + " para " + codigoEstudiante + " en " + codigoSeccion);
        guardar();
        return "OK";
    }

    public String eliminarNota(String codigoEst, String codigoSec, String etiqueta) {
        boolean ok = datos.eliminarNota(codigoEst, codigoSec, etiqueta);
        if (!ok) return "Nota no encontrada.";
        Bitacora.registrar(usuarioActual.getRol(), usuarioActual.getCodigo(),
                "ELIMINAR_NOTA", "EXITOSA",
                "Nota '" + etiqueta + "' de " + codigoEst + " en " + codigoSec + " eliminada.");
        guardar();
        return "OK";
    }

    // ===================== INSCRIPCIONES =====================

    public String inscribirEstudiante(String codigoEst, String codigoSec) {
        Usuario u = datos.buscarUsuario(codigoEst);
        if (u == null || !u.getRol().equals("ESTUDIANTE"))
            return "Estudiante '" + codigoEst + "' no encontrado.";
        Estudiante est = (Estudiante) u;
        if (est.estaInscrito(codigoSec))
            return "Estudiante ya está inscrito en '" + codigoSec + "'.";
        est.inscribirSeccion(codigoSec);
        Bitacora.registrar("ESTUDIANTE", codigoEst, "INSCRIBIR_SECCION", "EXITOSA",
                "Inscripción en " + codigoSec);
        guardar();
        return "OK";
    }

    public String desasignarEstudiante(String codigoEst, String codigoSec) {
        Usuario u = datos.buscarUsuario(codigoEst);
        if (u == null || !u.getRol().equals("ESTUDIANTE"))
            return "Estudiante '" + codigoEst + "' no encontrado.";
        Estudiante est = (Estudiante) u;
        // No desasignar si tiene notas
        Nota[] ns = datos.getNotasEstudianteSeccion(codigoEst, codigoSec);
        if (ns.length > 0)
            return "No se puede desasignar: el estudiante tiene notas registradas en esta sección.";
        if (!est.desasignarSeccion(codigoSec))
            return "Estudiante no estaba inscrito en '" + codigoSec + "'.";
        Bitacora.registrar("ESTUDIANTE", codigoEst, "DESASIGNAR_SECCION", "EXITOSA",
                "Desasignado de " + codigoSec);
        guardar();
        return "OK";
    }

    // ===================== ASIGNACIÓN INSTRUCTOR =====================

    public String asignarSeccionInstructor(String codigoInst, String codigoSec) {
        Usuario u = datos.buscarUsuario(codigoInst);
        if (u == null || !u.getRol().equals("INSTRUCTOR"))
            return "Instructor '" + codigoInst + "' no encontrado.";
        ((Instructor) u).agregarSeccion(codigoSec);
        Bitacora.registrar("ADMINISTRADOR", usuarioActual.getCodigo(),
                "ASIGNAR_SECCION_INSTRUCTOR", "EXITOSA",
                "Sección " + codigoSec + " asignada a instructor " + codigoInst);
        guardar();
        return "OK";
    }

    // ===================== CSV =====================

    /**
     * Carga masiva de instructores desde CSV.
     * Formato: Código,Nombre,FechaNacimiento,Género,Contraseña
     */
    public String[] cargarInstructoresCSV(String contenido) {
        String[] lineas = contenido.split("\\n");
        int ok = 0, errores = 0;
        StringBuilder sb = new StringBuilder();
        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;
            String[] campos = linea.split(",");
            if (campos.length < 5) { errores++; sb.append("Fila inválida: ").append(linea).append("\n"); continue; }
            String res = crearInstructor(campos[0].trim(), campos[1].trim(),
                    campos[2].trim(), campos[3].trim(), campos[4].trim());
            if ("OK".equals(res)) ok++;
            else { errores++; sb.append(res).append("\n"); }
        }
        return new String[]{"Cargados: " + ok + " | Errores: " + errores, sb.toString()};
    }

    /**
     * Carga masiva de estudiantes desde CSV.
     * Formato: Código,Nombre,FechaNacimiento,Género,Contraseña
     */
    public String[] cargarEstudiantesCSV(String contenido) {
        String[] lineas = contenido.split("\\n");
        int ok = 0, errores = 0;
        StringBuilder sb = new StringBuilder();
        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;
            String[] campos = linea.split(",");
            if (campos.length < 5) { errores++; sb.append("Fila inválida: ").append(linea).append("\n"); continue; }
            String res = crearEstudiante(campos[0].trim(), campos[1].trim(),
                    campos[2].trim(), campos[3].trim(), campos[4].trim());
            if ("OK".equals(res)) ok++;
            else { errores++; sb.append(res).append("\n"); }
        }
        return new String[]{"Cargados: " + ok + " | Errores: " + errores, sb.toString()};
    }

    /**
     * Carga masiva de cursos desde CSV.
     * Formato: Código,NombreCurso,Descripcion,Creditos,Seccion
     */
    public String[] cargarCursosCSV(String contenido) {
        String[] lineas = contenido.split("\\n");
        int ok = 0, errores = 0;
        StringBuilder sb = new StringBuilder();
        for (String linea : lineas) {
            linea = linea.trim();
            if (linea.isEmpty()) continue;
            String[] campos = linea.split(",");
            if (campos.length < 5) { errores++; sb.append("Fila inválida: ").append(linea).append("\n"); continue; }
            String res = crearCurso(campos[0].trim(), campos[1].trim(),
                    campos[2].trim(), campos[3].trim(), campos[4].trim());
            if ("OK".equals(res)) ok++;
            else { errores++; sb.append(res).append("\n"); }
        }
        return new String[]{"Cargados: " + ok + " | Errores: " + errores, sb.toString()};
    }

    // ===================== PERSISTENCIA =====================
    private void guardar() {
        Serializacion.guardar(datos);
    }
}

package modelo;

/**
 * Clase que representa al Administrador del sistema.
 * Hereda de Usuario.
 */
public class Administrador extends Usuario {
    private static final long serialVersionUID = 1L;

    public Administrador(String codigo, String nombre, String fechaNacimiento,
                         String genero, String contrasena) {
        super(codigo, nombre, fechaNacimiento, genero, contrasena, "ADMINISTRADOR");
    }

    @Override
    public String getDescripcion() {
        return "Administrador del sistema Sancarlista Academy. Gestiona instructores, " +
               "estudiantes, cursos y secciones.";
    }
}

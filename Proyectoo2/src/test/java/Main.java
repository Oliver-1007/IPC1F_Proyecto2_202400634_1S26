import controlador.SistemaController;
import vista.LoginFrame;

import javax.swing.*;

/**
 * Punto de entrada principal de la aplicación Sancarlista Academy.
 * Inicializa el controlador y lanza la ventana de login.
 *
 * Credenciales por defecto del Administrador:
 *   Código:     admin
 *   Contraseña: IPC1A   (cambiar "A" por la sección del curso)
 */
public class Main {

    public static void main(String[] args) {
        // Usar Look & Feel nativo del sistema operativo
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Si falla, usar el L&F por defecto de Swing
        }

        // Iniciar en el hilo de despacho de eventos de Swing (EDT)
        SwingUtilities.invokeLater(() -> {
            SistemaController controlador = new SistemaController();
            LoginFrame loginFrame = new LoginFrame(controlador);
            loginFrame.setVisible(true);
        });
    }
}

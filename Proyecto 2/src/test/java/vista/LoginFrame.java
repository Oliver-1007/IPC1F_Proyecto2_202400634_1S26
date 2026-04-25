package vista;

import controlador.SistemaController;
import modelo.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Pantalla de inicio de sesión del sistema Sancarlista Academy.
 */
public class LoginFrame extends JFrame {

    private SistemaController controlador;

    private JTextField  txtCodigo;
    private JPasswordField txtContrasena;
    private JButton     btnIngresar;
    private JLabel      lblMensaje;

    public LoginFrame(SistemaController controlador) {
        this.controlador = controlador;
        inicializarUI();
    }

    private void inicializarUI() {
        setTitle("Sancarlista Academy — Inicio de Sesión");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con borde
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Título
        JLabel lblTitulo = new JLabel("Sancarlista Academy", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        JLabel lblSub = new JLabel("Universidad San Carlos de Guatemala", SwingConstants.CENTER);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 11));
        gbc.gridy = 1;
        panel.add(lblSub, gbc);

        // Separador
        gbc.gridy = 2;
        panel.add(new JSeparator(), gbc);

        // Código
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Código:"), gbc);
        txtCodigo = new JTextField(15);
        gbc.gridx = 1;
        panel.add(txtCodigo, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Contraseña:"), gbc);
        txtContrasena = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(txtContrasena, gbc);

        // Botón
        btnIngresar = new JButton("Ingresar");
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        panel.add(btnIngresar, gbc);

        // Mensaje de error
        lblMensaje = new JLabel("", SwingConstants.CENTER);
        lblMensaje.setForeground(Color.RED);
        gbc.gridy = 6;
        panel.add(lblMensaje, gbc);

        add(panel);

        // Acción del botón
        btnIngresar.addActionListener(e -> intentarLogin());

        // Enter en contraseña también hace login
        txtContrasena.addActionListener(e -> intentarLogin());
    }

    private void intentarLogin() {
        String codigo    = txtCodigo.getText().trim();
        String contrasena = new String(txtContrasena.getPassword()).trim();

        if (codigo.isEmpty() || contrasena.isEmpty()) {
            lblMensaje.setText("Ingrese código y contraseña.");
            return;
        }

        Usuario usuario = controlador.login(codigo, contrasena);
        if (usuario == null) {
            lblMensaje.setText("Credenciales incorrectas. Intente de nuevo.");
            txtContrasena.setText("");
            return;
        }

        // Redirigir según rol
        this.setVisible(false);
        switch (usuario.getRol()) {
            case "ADMINISTRADOR":
                new AdminFrame(controlador).setVisible(true);
                break;
            case "INSTRUCTOR":
                new InstructorFrame(controlador).setVisible(true);
                break;
            case "ESTUDIANTE":
                new EstudianteFrame(controlador).setVisible(true);
                break;
        }
        this.dispose();
    }
}

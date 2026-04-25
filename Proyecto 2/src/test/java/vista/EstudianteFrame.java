package vista;

import controlador.SistemaController;
import modelo.*;
import util.Bitacora;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * Ventana principal del Estudiante.
 * Permite inscribirse a secciones, ver calificaciones y ver su perfil.
 */
public class EstudianteFrame extends JFrame {

    private SistemaController ctrl;
    private JLabel lblStatus;
    private volatile boolean hilosActivos = true;

    public EstudianteFrame(SistemaController ctrl) {
        this.ctrl = ctrl;
        inicializarUI();
        iniciarHilos();
    }

    private void inicializarUI() {
        Estudiante est = (Estudiante) ctrl.getUsuarioActual();
        setTitle("Sancarlista Academy — Estudiante: " + est.getNombre());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(820, 550);
        setLocationRelativeTo(null);

        // Menú
        JMenuBar mb = new JMenuBar();
        JMenu mSistema = new JMenu("Sistema");
        JMenuItem miCerrar = new JMenuItem("Cerrar Sesión");
        JMenuItem miSalir  = new JMenuItem("Salir");
        mSistema.add(miCerrar); mSistema.addSeparator(); mSistema.add(miSalir);
        mb.add(mSistema);
        JMenu mAcerca = new JMenu("Acerca de");
        JMenuItem miDev = new JMenuItem("Ver datos del desarrollador");
        mAcerca.add(miDev); mb.add(mAcerca);
        setJMenuBar(mb);

        miCerrar.addActionListener(e -> cerrarSesion());
        miSalir.addActionListener(e -> salir());
        miDev.addActionListener(e -> mostrarDatosDesarrollador());

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) { salir(); }
        });

        // Status bar
        lblStatus = new JLabel(" Cargando...");
        lblStatus.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(lblStatus);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Mis Cursos",         construirPanelMisCursos());
        tabs.addTab("Inscribir Sección",  construirPanelInscripcion());
        tabs.addTab("Calificaciones",     construirPanelCalificaciones());
        tabs.addTab("Mi Perfil",          construirPanelPerfil());

        add(tabs, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    // ================================================================
    //  PANEL MIS CURSOS
    // ================================================================
    private JPanel construirPanelMisCursos() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"Sección", "Promedio", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(model);

        Runnable cargar = () -> {
            model.setRowCount(0);
            Estudiante est = (Estudiante) ctrl.getUsuarioActual();
            for (int i = 0; i < est.getTotalInscritas(); i++) {
                String sec = est.getSeccionesInscritas()[i];
                if (sec == null) continue;
                double prom = ctrl.getDatos().calcularPromedio(est.getCodigo(), sec);
                String estado = prom >= 61 ? "Aprobado" : "Reprobado";
                model.addRow(new Object[]{sec, String.format("%.2f", prom), estado});
            }
        };
        cargar.run();

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargar.run());

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(btnRefrescar, BorderLayout.SOUTH);
        return panel;
    }

    // ================================================================
    //  PANEL INSCRIPCION
    // ================================================================
    private JPanel construirPanelInscripcion() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Cursos disponibles
        String[] colsCursos = {"Código", "Nombre", "Descripción", "Créditos", "Sección"};
        DefaultTableModel modelCursos = new DefaultTableModel(colsCursos, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tablaCursos = new JTable(modelCursos);

        for (Curso c : ctrl.getDatos().getTodosCursos()) {
            modelCursos.addRow(new Object[]{
                c.getCodigo(), c.getNombre(), c.getDescripcion(),
                c.getCreditos(), c.getSeccion()
            });
        }

        // Formulario de inscripción
        JPanel frm = new JPanel(new FlowLayout());
        frm.setBorder(BorderFactory.createTitledBorder("Inscribir / Desasignar Sección"));
        JTextField fSeccion = new JTextField(12);
        JButton btnInscribir  = new JButton("Inscribir");
        JButton btnDesasignar = new JButton("Desasignar");
        frm.add(new JLabel("Código Sección:"));
        frm.add(fSeccion);
        frm.add(btnInscribir);
        frm.add(btnDesasignar);

        // Al seleccionar un curso en la tabla, llenar el campo
        tablaCursos.getSelectionModel().addListSelectionListener(e -> {
            int row = tablaCursos.getSelectedRow();
            if (row >= 0) {
                fSeccion.setText((String) modelCursos.getValueAt(row, 4));
            }
        });

        btnInscribir.addActionListener(e -> {
            String res = ctrl.inscribirEstudiante(
                ctrl.getUsuarioActual().getCodigo(), fSeccion.getText().trim());
            if ("OK".equals(res)) JOptionPane.showMessageDialog(this, "Inscripción exitosa.");
            else JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
        });

        btnDesasignar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desasignarse de la sección '" + fSeccion.getText() + "'?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String res = ctrl.desasignarEstudiante(
                    ctrl.getUsuarioActual().getCodigo(), fSeccion.getText().trim());
                if ("OK".equals(res)) JOptionPane.showMessageDialog(this, "Desasignado correctamente.");
                else JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JScrollPane(tablaCursos), BorderLayout.CENTER);
        panel.add(frm, BorderLayout.SOUTH);
        return panel;
    }

    // ================================================================
    //  PANEL CALIFICACIONES
    // ================================================================
    private JPanel construirPanelCalificaciones() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtroPanel.add(new JLabel("Filtrar por sección:"));
        JTextField fFiltro = new JTextField(12);
        JButton btnBuscar  = new JButton("Buscar");
        JButton btnTodas   = new JButton("Ver Todas");
        filtroPanel.add(fFiltro); filtroPanel.add(btnBuscar); filtroPanel.add(btnTodas);

        String[] cols = {"Sección", "Etiqueta", "Ponderación%", "Nota", "Fecha", "Promedio Sección", "Estado"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(model);

        Runnable cargarTodas = () -> {
            model.setRowCount(0);
            String codigoEst = ctrl.getUsuarioActual().getCodigo();
            for (Nota n : ctrl.getDatos().getTodasNotas()) {
                if (n.getCodigoEstudiante().equals(codigoEst)) {
                    double prom  = ctrl.getDatos().calcularPromedio(codigoEst, n.getCodigoSeccion());
                    String estado = prom >= 61 ? "Aprobado" : "Reprobado";
                    model.addRow(new Object[]{
                        n.getCodigoSeccion(), n.getEtiqueta(), n.getPonderacion(),
                        n.getNota(), n.getFechaRegistro(),
                        String.format("%.2f", prom), estado
                    });
                }
            }
        };
        cargarTodas.run();

        btnBuscar.addActionListener(e -> {
            model.setRowCount(0);
            String codigoEst = ctrl.getUsuarioActual().getCodigo();
            String filtro    = fFiltro.getText().trim();
            for (Nota n : ctrl.getDatos().getTodasNotas()) {
                if (n.getCodigoEstudiante().equals(codigoEst) &&
                    n.getCodigoSeccion().contains(filtro)) {
                    double prom  = ctrl.getDatos().calcularPromedio(codigoEst, n.getCodigoSeccion());
                    String estado = prom >= 61 ? "Aprobado" : "Reprobado";
                    model.addRow(new Object[]{
                        n.getCodigoSeccion(), n.getEtiqueta(), n.getPonderacion(),
                        n.getNota(), n.getFechaRegistro(),
                        String.format("%.2f", prom), estado
                    });
                }
            }
        });

        btnTodas.addActionListener(e -> cargarTodas.run());

        panel.add(filtroPanel,          BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    // ================================================================
    //  PANEL PERFIL
    // ================================================================
    private JPanel construirPanelPerfil() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        Estudiante est = (Estudiante) ctrl.getUsuarioActual();

        JPanel info = new JPanel(new GridBagLayout());
        info.setBorder(BorderFactory.createTitledBorder("Mi Perfil"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5); g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fNombre  = new JTextField(est.getNombre(), 20);
        JTextField fFecha   = new JTextField(est.getFechaNacimiento(), 12);
        JTextField fGenero  = new JTextField(est.getGenero(), 6);
        JPasswordField fPassActual = new JPasswordField(12);
        JPasswordField fPassNueva  = new JPasswordField(12);

        addRow(info, g, 0, "Código:",             new JLabel(est.getCodigo()));
        addRow(info, g, 1, "Nombre:",              fNombre);
        addRow(info, g, 2, "Fecha Nacimiento:",    fFecha);
        addRow(info, g, 3, "Género:",              fGenero);
        addRow(info, g, 4, "Contraseña Actual:",   fPassActual);
        addRow(info, g, 5, "Nueva Contraseña:",    fPassNueva);

        JButton btnGuardar = new JButton("Guardar Cambios");
        g.gridx = 0; g.gridy = 6; g.gridwidth = 2;
        info.add(btnGuardar, g);

        btnGuardar.addActionListener(e -> {
            String passActual = new String(fPassActual.getPassword());
            String passNueva  = new String(fPassNueva.getPassword());
            if (!passActual.isEmpty() && !est.getContrasena().equals(passActual)) {
                JOptionPane.showMessageDialog(this, "Contraseña actual incorrecta.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            est.setNombre(fNombre.getText());
            est.setFechaNacimiento(fFecha.getText());
            est.setGenero(fGenero.getText());
            if (!passNueva.isEmpty()) est.setContrasena(passNueva);
            util.Serializacion.guardar(ctrl.getDatos());
            JOptionPane.showMessageDialog(this, "Perfil actualizado exitosamente.");
        });

        panel.add(info, BorderLayout.NORTH);
        return panel;
    }

    // ================================================================
    //  HILOS
    // ================================================================
    private void iniciarHilos() {
        Thread hilo = new Thread(() -> {
            int pendientes = 0;
            while (hilosActivos) {
                pendientes = (pendientes + 1) % 5; // simulación
                final int p = pendientes;
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText(
                        "[Thread-Inscripciones] Inscripciones Pendientes: " + p +
                        " - Procesando... " + Bitacora.getFechaHoraActual()
                    );
                });
                try { Thread.sleep(8000); } catch (InterruptedException ex) { break; }
            }
        });
        hilo.setDaemon(true);
        hilo.start();
    }

    private void cerrarSesion() {
        hilosActivos = false;
        ctrl.logout();
        dispose();
        new LoginFrame(ctrl).setVisible(true);
    }

    private void salir() {
        int c = JOptionPane.showConfirmDialog(this, "¿Desea salir?", "Salir", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) { hilosActivos = false; ctrl.logout(); System.exit(0); }
    }

    private void mostrarDatosDesarrollador() {
        JOptionPane.showMessageDialog(this,
            "Nombre: Oliver Jorge Raxún Morales\nCarné: 202400634\nSección: F\n" +
            "Curso: Introducción a la Programación y Computación 1\n" +
            "Universidad San Carlos de Guatemala",
            "Datos del Desarrollador", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addRow(JPanel panel, GridBagConstraints g, int row, String label, JComponent field) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        panel.add(new JLabel(label), g);
        g.gridx = 1;
        panel.add(field, g);
    }
}

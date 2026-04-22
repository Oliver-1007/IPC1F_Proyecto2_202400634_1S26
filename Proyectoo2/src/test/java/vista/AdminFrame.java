package vista;

import controlador.SistemaController;
import modelo.*;
import util.Bitacora;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Ventana principal del Administrador.
 * Contiene pestañas para: Instructores, Estudiantes, Cursos, Reportes, Bitácora.
 */
public class AdminFrame extends JFrame {

    private SistemaController ctrl;
    private JLabel lblSesion;

    public AdminFrame(SistemaController ctrl) {
        this.ctrl = ctrl;
        inicializarUI();
        iniciarHilos();
    }

    private void inicializarUI() {
        setTitle("Sancarlista Academy — Administrador: " + ctrl.getUsuarioActual().getNombre());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);

        // Menú superior
        JMenuBar menuBar = new JMenuBar();
        JMenu menuSistema = new JMenu("Sistema");
        JMenuItem miCerrarSesion = new JMenuItem("Cerrar Sesión");
        JMenuItem miSalir        = new JMenuItem("Salir");
        menuSistema.add(miCerrarSesion);
        menuSistema.addSeparator();
        menuSistema.add(miSalir);
        menuBar.add(menuSistema);
        JMenu menuReportes = new JMenu("Reportes");
        JMenuItem miReportes = new JMenuItem("Generar Reportes");
        menuReportes.add(miReportes);
        menuBar.add(menuReportes);

        JMenu menuAyuda = new JMenu("Acerca de");
        JMenuItem miAcerca = new JMenuItem("Ver datos del desarrollador");
        menuAyuda.add(miAcerca);
        menuBar.add(menuAyuda);
        setJMenuBar(menuBar);

        // Panel inferior con info de sesión e hilos
        lblSesion = new JLabel(" [Thread-Sesiones] Cargando...", SwingConstants.LEFT);
        lblSesion.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(lblSesion);

        // Pestañas principales
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Instructores",  construirPanelInstructores());
        tabs.addTab("Estudiantes",   construirPanelEstudiantes());
        tabs.addTab("Cursos",        construirPanelCursos());
        tabs.addTab("Inscripciones", construirPanelInscripciones());
        tabs.addTab("Bitácora",      construirPanelBitacora());

        add(tabs, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        // Acciones menú
        miCerrarSesion.addActionListener(e -> cerrarSesion());
        miSalir.addActionListener(e -> salir());
        miAcerca.addActionListener(e -> mostrarDatosDesarrollador());
        miReportes.addActionListener(e -> new ReportesFrame(ctrl).setVisible(true));

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { salir(); }
        });
    }

    // ================================================================
    //  PANEL INSTRUCTORES
    // ================================================================
    private JPanel construirPanelInstructores() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Tabla
        String[] cols = {"Código", "Nombre", "Fecha Nac.", "Género", "Secciones"};
        DefaultTableModel modelTabla = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelTabla);
        JScrollPane scroll = new JScrollPane(tabla);

        // Formulario
        JPanel frmPanel = new JPanel(new GridBagLayout());
        frmPanel.setBorder(BorderFactory.createTitledBorder("Datos del Instructor"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4); g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fCodigo  = new JTextField(12);
        JTextField fNombre  = new JTextField(20);
        JTextField fFecha   = new JTextField(12);
        JTextField fGenero  = new JTextField(6);
        JPasswordField fPass = new JPasswordField(12);

        addRow(frmPanel, g, 0, "Código:",       fCodigo);
        addRow(frmPanel, g, 1, "Nombre:",        fNombre);
        addRow(frmPanel, g, 2, "Fecha Nac.:",    fFecha);
        addRow(frmPanel, g, 3, "Género (M/F):",  fGenero);
        addRow(frmPanel, g, 4, "Contraseña:",    fPass);

        // Botones
        JButton btnCrear    = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnCSV      = new JButton("Cargar CSV");
        JButton btnRefrescar = new JButton("Refrescar");

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(btnCrear); btnPanel.add(btnActualizar);
        btnPanel.add(btnEliminar); btnPanel.add(btnCSV); btnPanel.add(btnRefrescar);

        // Cargar tabla
        Runnable cargarTabla = () -> {
            modelTabla.setRowCount(0);
            for (Usuario u : ctrl.getDatos().getUsuariosPorRol("INSTRUCTOR")) {
                Instructor ins = (Instructor) u;
                modelTabla.addRow(new Object[]{
                    ins.getCodigo(), ins.getNombre(), ins.getFechaNacimiento(),
                    ins.getGenero(), ins.getTotalSecciones()
                });
            }
        };
        cargarTabla.run();

        // Seleccionar fila → llenar formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row >= 0) {
                fCodigo.setText((String) modelTabla.getValueAt(row, 0));
                fNombre.setText((String) modelTabla.getValueAt(row, 1));
                fFecha.setText((String) modelTabla.getValueAt(row, 2));
                fGenero.setText((String) modelTabla.getValueAt(row, 3));
            }
        });

        btnCrear.addActionListener(e -> {
            String res = ctrl.crearInstructor(
                fCodigo.getText(), fNombre.getText(), fFecha.getText(),
                fGenero.getText(), new String(fPass.getPassword()));
            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "Instructor creado exitosamente.");
                limpiarCampos(fCodigo, fNombre, fFecha, fGenero); fPass.setText("");
                cargarTabla.run();
            } else {
                JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnActualizar.addActionListener(e -> {
            String res = ctrl.actualizarInstructor(
                fCodigo.getText(), fNombre.getText(), new String(fPass.getPassword()));
            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "Instructor actualizado.");
                cargarTabla.run();
            } else {
                JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar instructor '" + fCodigo.getText() + "'?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String res = ctrl.eliminarInstructor(fCodigo.getText());
                if ("OK".equals(res)) {
                    JOptionPane.showMessageDialog(this, "Instructor eliminado.");
                    limpiarCampos(fCodigo, fNombre, fFecha, fGenero); fPass.setText("");
                    cargarTabla.run();
                } else {
                    JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCSV.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String contenido = leerArchivo(fc.getSelectedFile());
                    String[] resultado = ctrl.cargarInstructoresCSV(contenido);
                    JOptionPane.showMessageDialog(this,
                        resultado[0] + (resultado[1].isEmpty() ? "" : "\n\nDetalles:\n" + resultado[1]));
                    cargarTabla.run();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error leyendo archivo: " + ex.getMessage());
                }
            }
        });

        btnRefrescar.addActionListener(e -> cargarTabla.run());

        panel.add(frmPanel,  BorderLayout.NORTH);
        panel.add(scroll,    BorderLayout.CENTER);
        panel.add(btnPanel,  BorderLayout.SOUTH);
        return panel;
    }

    // ================================================================
    //  PANEL ESTUDIANTES
    // ================================================================
    private JPanel construirPanelEstudiantes() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"Código", "Nombre", "Fecha Nac.", "Género", "Inscritos"};
        DefaultTableModel modelTabla = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelTabla);

        JPanel frmPanel = new JPanel(new GridBagLayout());
        frmPanel.setBorder(BorderFactory.createTitledBorder("Datos del Estudiante"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4); g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fCodigo  = new JTextField(12);
        JTextField fNombre  = new JTextField(20);
        JTextField fFecha   = new JTextField(12);
        JTextField fGenero  = new JTextField(6);
        JPasswordField fPass = new JPasswordField(12);

        addRow(frmPanel, g, 0, "Código:",       fCodigo);
        addRow(frmPanel, g, 1, "Nombre:",        fNombre);
        addRow(frmPanel, g, 2, "Fecha Nac.:",    fFecha);
        addRow(frmPanel, g, 3, "Género (M/F):",  fGenero);
        addRow(frmPanel, g, 4, "Contraseña:",    fPass);

        JButton btnCrear     = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar  = new JButton("Eliminar");
        JButton btnCSV       = new JButton("Cargar CSV");
        JButton btnRefrescar = new JButton("Refrescar");
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(btnCrear); btnPanel.add(btnActualizar);
        btnPanel.add(btnEliminar); btnPanel.add(btnCSV); btnPanel.add(btnRefrescar);

        Runnable cargarTabla = () -> {
            modelTabla.setRowCount(0);
            for (Usuario u : ctrl.getDatos().getUsuariosPorRol("ESTUDIANTE")) {
                Estudiante est = (Estudiante) u;
                modelTabla.addRow(new Object[]{
                    est.getCodigo(), est.getNombre(), est.getFechaNacimiento(),
                    est.getGenero(), est.getTotalInscritas()
                });
            }
        };
        cargarTabla.run();

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row >= 0) {
                fCodigo.setText((String) modelTabla.getValueAt(row, 0));
                fNombre.setText((String) modelTabla.getValueAt(row, 1));
                fFecha.setText((String) modelTabla.getValueAt(row, 2));
                fGenero.setText((String) modelTabla.getValueAt(row, 3));
            }
        });

        btnCrear.addActionListener(e -> {
            String res = ctrl.crearEstudiante(
                fCodigo.getText(), fNombre.getText(), fFecha.getText(),
                fGenero.getText(), new String(fPass.getPassword()));
            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "Estudiante creado exitosamente.");
                limpiarCampos(fCodigo, fNombre, fFecha, fGenero); fPass.setText("");
                cargarTabla.run();
            } else JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
        });

        btnActualizar.addActionListener(e -> {
            String res = ctrl.actualizarEstudiante(
                fCodigo.getText(), fNombre.getText(), new String(fPass.getPassword()));
            if ("OK".equals(res)) { JOptionPane.showMessageDialog(this, "Estudiante actualizado."); cargarTabla.run(); }
            else JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
        });

        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar estudiante '" + fCodigo.getText() + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String res = ctrl.eliminarEstudiante(fCodigo.getText());
                if ("OK".equals(res)) { JOptionPane.showMessageDialog(this, "Estudiante eliminado."); cargarTabla.run(); }
                else JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCSV.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String[] r = ctrl.cargarEstudiantesCSV(leerArchivo(fc.getSelectedFile()));
                    JOptionPane.showMessageDialog(this, r[0]);
                    cargarTabla.run();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        btnRefrescar.addActionListener(e -> cargarTabla.run());

        panel.add(frmPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ================================================================
    //  PANEL CURSOS
    // ================================================================
    private JPanel construirPanelCursos() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"Código", "Nombre", "Descripción", "Créditos", "Sección"};
        DefaultTableModel modelTabla = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(modelTabla);

        JPanel frmPanel = new JPanel(new GridBagLayout());
        frmPanel.setBorder(BorderFactory.createTitledBorder("Datos del Curso"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4); g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fCodigo  = new JTextField(10);
        JTextField fNombre  = new JTextField(20);
        JTextField fDesc    = new JTextField(25);
        JTextField fCred    = new JTextField(5);
        JTextField fSeccion = new JTextField(10);

        addRow(frmPanel, g, 0, "Código:",      fCodigo);
        addRow(frmPanel, g, 1, "Nombre:",       fNombre);
        addRow(frmPanel, g, 2, "Descripción:",  fDesc);
        addRow(frmPanel, g, 3, "Créditos:",     fCred);
        addRow(frmPanel, g, 4, "Sección:",      fSeccion);

        JButton btnCrear     = new JButton("Crear");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar  = new JButton("Eliminar");
        JButton btnCSV       = new JButton("Cargar CSV");
        JButton btnRefrescar = new JButton("Refrescar");
        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(btnCrear); btnPanel.add(btnActualizar);
        btnPanel.add(btnEliminar); btnPanel.add(btnCSV); btnPanel.add(btnRefrescar);

        Runnable cargarTabla = () -> {
            modelTabla.setRowCount(0);
            for (Curso c : ctrl.getDatos().getTodosCursos()) {
                modelTabla.addRow(new Object[]{
                    c.getCodigo(), c.getNombre(), c.getDescripcion(),
                    c.getCreditos(), c.getSeccion()
                });
            }
        };
        cargarTabla.run();

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row >= 0) {
                fCodigo.setText((String) modelTabla.getValueAt(row, 0));
                fNombre.setText((String) modelTabla.getValueAt(row, 1));
                fDesc.setText((String) modelTabla.getValueAt(row, 2));
                fCred.setText(modelTabla.getValueAt(row, 3).toString());
                fSeccion.setText((String) modelTabla.getValueAt(row, 4));
            }
        });

        btnCrear.addActionListener(e -> {
            String res = ctrl.crearCurso(fCodigo.getText(), fNombre.getText(),
                fDesc.getText(), fCred.getText(), fSeccion.getText());
            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "Curso creado.");
                limpiarCampos(fCodigo, fNombre, fDesc, fCred, fSeccion);
                cargarTabla.run();
            } else JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
        });

        btnActualizar.addActionListener(e -> {
            String res = ctrl.actualizarCurso(fCodigo.getText(), fNombre.getText(),
                fDesc.getText(), fCred.getText(), fSeccion.getText());
            if ("OK".equals(res)) { JOptionPane.showMessageDialog(this, "Curso actualizado."); cargarTabla.run(); }
            else JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
        });

        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar curso '" + fCodigo.getText() + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String res = ctrl.eliminarCurso(fCodigo.getText());
                if ("OK".equals(res)) { JOptionPane.showMessageDialog(this, "Curso eliminado."); cargarTabla.run(); }
                else JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCSV.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    String[] r = ctrl.cargarCursosCSV(leerArchivo(fc.getSelectedFile()));
                    JOptionPane.showMessageDialog(this, r[0]);
                    cargarTabla.run();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        });

        btnRefrescar.addActionListener(e -> cargarTabla.run());

        panel.add(frmPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ================================================================
    //  PANEL INSCRIPCIONES (asignar secciones a instructores)
    // ================================================================
    private JPanel construirPanelInscripciones() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JPanel frmPanel = new JPanel(new GridBagLayout());
        frmPanel.setBorder(BorderFactory.createTitledBorder("Asignar Sección a Instructor"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4,4,4,4); g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fInstructor = new JTextField(12);
        JTextField fSeccion    = new JTextField(12);

        addRow(frmPanel, g, 0, "Código Instructor:", fInstructor);
        addRow(frmPanel, g, 1, "Código Sección:",    fSeccion);

        JButton btnAsignar = new JButton("Asignar Sección a Instructor");
        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        frmPanel.add(btnAsignar, g);

        btnAsignar.addActionListener(e -> {
            String res = ctrl.asignarSeccionInstructor(fInstructor.getText().trim(), fSeccion.getText().trim());
            if ("OK".equals(res)) JOptionPane.showMessageDialog(this, "Sección asignada correctamente.");
            else JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
        });

        // Panel inscripción estudiante
        JPanel frmEst = new JPanel(new GridBagLayout());
        frmEst.setBorder(BorderFactory.createTitledBorder("Inscribir / Desasignar Estudiante"));
        GridBagConstraints g2 = new GridBagConstraints();
        g2.insets = new Insets(4,4,4,4); g2.fill = GridBagConstraints.HORIZONTAL;

        JTextField fEstCod  = new JTextField(12);
        JTextField fSecCod  = new JTextField(12);
        addRow(frmEst, g2, 0, "Código Estudiante:", fEstCod);
        addRow(frmEst, g2, 1, "Código Sección:",    fSecCod);

        JButton btnInscribir   = new JButton("Inscribir");
        JButton btnDesasignar  = new JButton("Desasignar");
        g2.gridx = 0; g2.gridy = 2; g2.gridwidth = 1;
        frmEst.add(btnInscribir, g2);
        g2.gridx = 1;
        frmEst.add(btnDesasignar, g2);

        btnInscribir.addActionListener(e -> {
            String res = ctrl.inscribirEstudiante(fEstCod.getText().trim(), fSecCod.getText().trim());
            if ("OK".equals(res)) JOptionPane.showMessageDialog(this, "Estudiante inscrito.");
            else JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
        });

        btnDesasignar.addActionListener(e -> {
            String res = ctrl.desasignarEstudiante(fEstCod.getText().trim(), fSecCod.getText().trim());
            if ("OK".equals(res)) JOptionPane.showMessageDialog(this, "Estudiante desasignado.");
            else JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
        });

        JPanel top = new JPanel(new GridLayout(1, 2, 10, 0));
        top.add(frmPanel);
        top.add(frmEst);

        panel.add(top, BorderLayout.NORTH);
        return panel;
    }

    // ================================================================
    //  PANEL BITÁCORA
    // ================================================================
    private JPanel construirPanelBitacora() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        String[] cols = {"Fecha/Hora", "Tipo Usuario", "Código", "Operación", "Estado", "Descripción"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(model);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnExportar  = new JButton("Exportar CSV");

        Runnable cargar = () -> {
            model.setRowCount(0);
            for (EntradaBitacora e : ctrl.getDatos().getTodaBitacora()) {
                model.addRow(new Object[]{
                    e.getFechaHora(), e.getTipoUsuario(), e.getCodigoUsuario(),
                    e.getOperacion(), e.getEstado(), e.getDescripcion()
                });
            }
        };
        cargar.run();

        btnRefrescar.addActionListener(e -> cargar.run());

        btnExportar.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(Bitacora.getTimestampArchivo() + "_Bitacora.csv"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (PrintWriter pw = new PrintWriter(new FileWriter(fc.getSelectedFile()))) {
                    pw.println("FechaHora,TipoUsuario,Codigo,Operacion,Estado,Descripcion");
                    for (EntradaBitacora en : ctrl.getDatos().getTodaBitacora()) {
                        pw.println(en.getFechaHora() + "," + en.getTipoUsuario() + "," +
                            en.getCodigoUsuario() + "," + en.getOperacion() + "," +
                            en.getEstado() + "," + en.getDescripcion());
                    }
                    JOptionPane.showMessageDialog(this, "Bitácora exportada.");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage());
                }
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.add(btnRefrescar);
        btnPanel.add(btnExportar);

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ================================================================
    //  HILOS DE FONDO
    // ================================================================
    private volatile boolean hilosActivos = true;

    private void iniciarHilos() {
        // Hilo 1: Monitor de sesiones activas
        Thread hiloSesiones = new Thread(() -> {
            while (hilosActivos) {
                SwingUtilities.invokeLater(() -> {
                    lblSesion.setText(
                        "[Thread-Sesiones] Usuarios Activos: " +
                        ctrl.getDatos().getSesionesActivas() +
                        " — Última actividad: " + Bitacora.getFechaHoraActual() +
                        "   |   [Thread-Stats] Cursos: " + ctrl.getDatos().getTotalCursos() +
                        " | Estudiantes: " + ctrl.getDatos().getUsuariosPorRol("ESTUDIANTE").length +
                        " | Notas: " + ctrl.getDatos().getTotalNotas()
                    );
                });
                try { Thread.sleep(10000); } catch (InterruptedException e) { break; }
            }
        });
        hiloSesiones.setDaemon(true);
        hiloSesiones.start();
    }

    // ================================================================
    //  HELPERS
    // ================================================================
    private void cerrarSesion() {
        hilosActivos = false;
        ctrl.logout();
        dispose();
        new LoginFrame(ctrl).setVisible(true);
    }

    private void salir() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "¿Desea salir del sistema?", "Salir", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            hilosActivos = false;
            ctrl.logout();
            System.exit(0);
        }
    }

    private void mostrarDatosDesarrollador() {
        JOptionPane.showMessageDialog(this,
            "Nombre: Oliver Jorge Raxtún Morales\nCarné: 202400634\nSección: F\n" +
            "Curso: Introducción a la Programación y Computación 1\n" +
            "Universidad San Carlos de Guatemala",
            "Datos del Desarrollador", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addRow(JPanel panel, GridBagConstraints g, int row,
                        String label, JComponent field) {
        g.gridx = 0; g.gridy = row; g.gridwidth = 1;
        panel.add(new JLabel(label), g);
        g.gridx = 1;
        panel.add(field, g);
    }

    private void limpiarCampos(JTextField... campos) {
        for (JTextField f : campos) f.setText("");
    }

    private String leerArchivo(File archivo) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new FileReader(archivo));
        String linea;
        while ((linea = br.readLine()) != null) {
            sb.append(linea).append("\n");
        }
        br.close();
        return sb.toString();
    }
}

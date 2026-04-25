package vista;

import controlador.SistemaController;
import modelo.*;
import util.Bitacora;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.io.*;

/**
 * Ventana principal del Instructor.
 * Permite gestionar notas de sus secciones asignadas.
 */
public class InstructorFrame extends JFrame {

    private SistemaController ctrl;
    private JLabel lblStatus;
    private volatile boolean hilosActivos = true;

    public InstructorFrame(SistemaController ctrl) {
        this.ctrl = ctrl;
        inicializarUI();
        iniciarHilos();
    }

    private void inicializarUI() {
        Instructor inst = (Instructor) ctrl.getUsuarioActual();
        setTitle("Sancarlista Academy — Instructor: " + inst.getNombre());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(860, 580);
        setLocationRelativeTo(null);

        // Menú
        JMenuBar mb = new JMenuBar();
        JMenu mSistema = new JMenu("Sistema");
        JMenuItem miCerrar = new JMenuItem("Cerrar Sesión");
        JMenuItem miSalir  = new JMenuItem("Salir");
        mSistema.add(miCerrar); mSistema.addSeparator(); mSistema.add(miSalir);
        mb.add(mSistema);
        JMenu mReportes = new JMenu("Reportes");
        JMenuItem miRep = new JMenuItem("Generar Reportes");
        mReportes.add(miRep); mb.add(mReportes);
        JMenu mAcerca = new JMenu("Acerca de");
        JMenuItem miDev = new JMenuItem("Ver datos del desarrollador");
        mAcerca.add(miDev); mb.add(mAcerca);
        setJMenuBar(mb);

        miCerrar.addActionListener(e -> cerrarSesion());
        miSalir.addActionListener(e -> salir());
        miDev.addActionListener(e -> mostrarDatosDesarrollador());
        miRep.addActionListener(e -> new ReportesFrame(ctrl).setVisible(true));

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) { salir(); }
        });

        // Status bar
        lblStatus = new JLabel(" Cargando hilos...");
        lblStatus.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.add(lblStatus);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Gestión de Notas", construirPanelNotas());
        tabs.addTab("Mis Secciones",    construirPanelSecciones());

        add(tabs, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    // ================================================================
    //  PANEL NOTAS
    // ================================================================
    private JPanel construirPanelNotas() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        // Formulario
        JPanel frm = new JPanel(new GridBagLayout());
        frm.setBorder(BorderFactory.createTitledBorder("Registrar / Editar Nota"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3,3,3,3); g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fCurso    = new JTextField(10);
        JTextField fSeccion  = new JTextField(10);
        JTextField fEst      = new JTextField(10);
        JTextField fEtiqueta = new JTextField(15);
        JTextField fPond     = new JTextField(6);
        JTextField fNota     = new JTextField(6);
        JTextField fFecha    = new JTextField(12);
        fFecha.setText(Bitacora.getFechaActual());

        addRow(frm, g, 0, "Código Curso:",      fCurso);
        addRow(frm, g, 1, "Código Sección:",     fSeccion);
        addRow(frm, g, 2, "Código Estudiante:",  fEst);
        addRow(frm, g, 3, "Etiqueta/Actividad:", fEtiqueta);
        addRow(frm, g, 4, "Ponderación (%):",    fPond);
        addRow(frm, g, 5, "Nota (0-100):",       fNota);
        addRow(frm, g, 6, "Fecha (YYYY-MM-DD):", fFecha);

        JButton btnGuardar  = new JButton("Guardar Nota");
        JButton btnEliminar = new JButton("Eliminar Nota");
        g.gridx = 0; g.gridy = 7; g.gridwidth = 1;
        frm.add(btnGuardar, g);
        g.gridx = 1;
        frm.add(btnEliminar, g);

        // Tabla de notas
        String[] cols = {"Curso", "Sección", "Estudiante", "Etiqueta", "Pond%", "Nota", "Fecha", "Promedio"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = new JTable(model);

        // Filtros
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtros.add(new JLabel("Filtrar sección:"));
        JTextField fFiltro = new JTextField(10);
        JButton btnFiltrar = new JButton("Buscar");
        JButton btnTodos   = new JButton("Ver Todos");
        filtros.add(fFiltro); filtros.add(btnFiltrar); filtros.add(btnTodos);

        Runnable cargarTodas = () -> {
            model.setRowCount(0);
            Instructor inst = (Instructor) ctrl.getUsuarioActual();
            for (Nota n : ctrl.getDatos().getTodasNotas()) {
                if (inst.tieneSeccion(n.getCodigoSeccion())) {
                    double prom = ctrl.getDatos().calcularPromedio(
                        n.getCodigoEstudiante(), n.getCodigoSeccion());
                    model.addRow(new Object[]{
                        n.getCodignoCurso(), n.getCodigoSeccion(), n.getCodigoEstudiante(),
                        n.getEtiqueta(), n.getPonderacion(), n.getNota(),
                        n.getFechaRegistro(), String.format("%.2f", prom)
                    });
                }
            }
        };
        cargarTodas.run();

        // Seleccionar fila → llenar formulario
        tabla.getSelectionModel().addListSelectionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row >= 0) {
                fCurso.setText((String) model.getValueAt(row, 0));
                fSeccion.setText((String) model.getValueAt(row, 1));
                fEst.setText((String) model.getValueAt(row, 2));
                fEtiqueta.setText((String) model.getValueAt(row, 3));
                fPond.setText(model.getValueAt(row, 4).toString());
                fNota.setText(model.getValueAt(row, 5).toString());
                fFecha.setText((String) model.getValueAt(row, 6));
            }
        });

        btnGuardar.addActionListener(e -> {
            String res = ctrl.crearNota(
                fCurso.getText().trim(), fSeccion.getText().trim(), fEst.getText().trim(),
                fEtiqueta.getText().trim(), fPond.getText().trim(), fNota.getText().trim(),
                fFecha.getText().trim(), ctrl.getUsuarioActual().getCodigo());
            if ("OK".equals(res)) {
                JOptionPane.showMessageDialog(this, "Nota registrada exitosamente.");
                cargarTodas.run();
            } else JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
        });

        btnEliminar.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar nota '" + fEtiqueta.getText() + "' del estudiante '" + fEst.getText() + "'?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String res = ctrl.eliminarNota(fEst.getText().trim(),
                    fSeccion.getText().trim(), fEtiqueta.getText().trim());
                if ("OK".equals(res)) { JOptionPane.showMessageDialog(this, "Nota eliminada."); cargarTodas.run(); }
                else JOptionPane.showMessageDialog(this, res, "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnFiltrar.addActionListener(e -> {
            String filtro = fFiltro.getText().trim();
            model.setRowCount(0);
            Instructor inst = (Instructor) ctrl.getUsuarioActual();
            for (Nota n : ctrl.getDatos().getTodasNotas()) {
                if (inst.tieneSeccion(n.getCodigoSeccion()) &&
                    n.getCodigoSeccion().contains(filtro)) {
                    double prom = ctrl.getDatos().calcularPromedio(
                        n.getCodigoEstudiante(), n.getCodigoSeccion());
                    model.addRow(new Object[]{
                        n.getCodignoCurso(), n.getCodigoSeccion(), n.getCodigoEstudiante(),
                        n.getEtiqueta(), n.getPonderacion(), n.getNota(),
                        n.getFechaRegistro(), String.format("%.2f", prom)
                    });
                }
            }
        });

        btnTodos.addActionListener(e -> cargarTodas.run());

        panel.add(frm, BorderLayout.NORTH);
        panel.add(filtros, BorderLayout.CENTER);
        panel.add(new JScrollPane(tabla), BorderLayout.SOUTH);
        ((JScrollPane)panel.getComponent(2)).setPreferredSize(new Dimension(800, 250));
        return panel;
    }

    // ================================================================
    //  PANEL MIS SECCIONES
    // ================================================================
    private JPanel construirPanelSecciones() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));

        Instructor inst = (Instructor) ctrl.getUsuarioActual();
        StringBuilder sb = new StringBuilder("Secciones asignadas al instructor " + inst.getCodigo() + ":\n\n");
        for (int i = 0; i < inst.getTotalSecciones(); i++) {
            sb.append("  - ").append(inst.getSecciones()[i]).append("\n");
        }
        if (inst.getTotalSecciones() == 0) sb.append("  (Sin secciones asignadas aún)");
        area.setText(sb.toString());

        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    // ================================================================
    //  HILOS
    // ================================================================
    private void iniciarHilos() {
        Thread hilo = new Thread(() -> {
            while (hilosActivos) {
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText("[Thread-Stats] Cursos: " + ctrl.getDatos().getTotalCursos() +
                        " | Notas registradas: " + ctrl.getDatos().getTotalNotas() +
                        " | " + Bitacora.getFechaHoraActual());
                });
                try { Thread.sleep(15000); } catch (InterruptedException e) { break; }
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
            "Nombre: Oliver Jorge Raxtún Morales\nCarné: 202400634\nSección: F\n" +
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

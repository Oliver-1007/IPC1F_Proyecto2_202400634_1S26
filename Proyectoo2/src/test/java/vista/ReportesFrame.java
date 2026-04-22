package vista;

import controlador.SistemaController;
import util.Bitacora;
import util.ExportadorCSV;
import util.ExportadorPDF;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Ventana de generación de reportes académicos.
 * Accesible desde el menú del Administrador o del Instructor.
 */
public class ReportesFrame extends JFrame {

    private SistemaController ctrl;

    public ReportesFrame(SistemaController ctrl) {
        this.ctrl = ctrl;
        inicializarUI();
    }

    private void inicializarUI() {
        setTitle("Sancarlista Academy — Generación de Reportes");
        setSize(620, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Desempeño",       construirPanelDesempeno());
        tabs.addTab("Sección",         construirPanelSeccion());
        tabs.addTab("Estudiante",      construirPanelEstudiante());
        tabs.addTab("Inscripciones",   construirPanelInscripciones());
        tabs.addTab("Exportar datos",  construirPanelExportar());
        add(tabs, BorderLayout.CENTER);
    }

    // ================================================================
    //  PANEL DESEMPEÑO (Mejor / Peor)
    // ================================================================
    private JPanel construirPanelDesempeno() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6); g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fSeccion = new JTextField(15);

        g.gridx = 0; g.gridy = 0; panel.add(new JLabel("Código de Sección:"), g);
        g.gridx = 1; panel.add(fSeccion, g);

        JButton btnMejor = new JButton("Reporte Top 5 Mejor Desempeño");
        JButton btnPeor  = new JButton("Reporte Top 5 Bajo Desempeño");

        g.gridx = 0; g.gridy = 1; g.gridwidth = 2;
        panel.add(btnMejor, g);
        g.gridy = 2;
        panel.add(btnPeor, g);

        JTextArea log = new JTextArea(8, 40);
        log.setEditable(false);
        log.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.gridy = 3;
        panel.add(new JScrollPane(log), g);

        btnMejor.addActionListener(e -> {
            String sec = fSeccion.getText().trim();
            if (sec.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese código de sección."); return; }
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(Bitacora.getTimestampArchivo() + "_MejorDesempeno.txt"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String res = ExportadorPDF.reporteMejorDesempeno(
                    ctrl.getDatos(), sec, fc.getSelectedFile().getAbsolutePath());
                log.setText(res.equals("OK") ? "Reporte generado: " + fc.getSelectedFile().getName() : res);
                Bitacora.registrar(ctrl.getUsuarioActual().getRol(),
                    ctrl.getUsuarioActual().getCodigo(), "GENERAR_REPORTE", "EXITOSA",
                    "Reporte MejorDesempeno sección " + sec);
            }
        });

        btnPeor.addActionListener(e -> {
            String sec = fSeccion.getText().trim();
            if (sec.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese código de sección."); return; }
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(Bitacora.getTimestampArchivo() + "_BajoDesempeno.txt"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String res = ExportadorPDF.reporteBajoDesempeno(
                    ctrl.getDatos(), sec, fc.getSelectedFile().getAbsolutePath());
                log.setText(res.equals("OK") ? "Reporte generado: " + fc.getSelectedFile().getName() : res);
                Bitacora.registrar(ctrl.getUsuarioActual().getRol(),
                    ctrl.getUsuarioActual().getCodigo(), "GENERAR_REPORTE", "EXITOSA",
                    "Reporte BajoDesempeno sección " + sec);
            }
        });

        return panel;
    }

    // ================================================================
    //  PANEL SECCIÓN
    // ================================================================
    private JPanel construirPanelSeccion() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6); g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fSeccion = new JTextField(15);
        g.gridx = 0; g.gridy = 0; panel.add(new JLabel("Código de Sección:"), g);
        g.gridx = 1; panel.add(fSeccion, g);

        JButton btnReporte = new JButton("Generar Reporte de Calificaciones de Sección");
        JButton btnCSV     = new JButton("Exportar Notas de Sección a CSV");

        g.gridx = 0; g.gridy = 1; g.gridwidth = 2;
        panel.add(btnReporte, g);
        g.gridy = 2;
        panel.add(btnCSV, g);

        JTextArea log = new JTextArea(8, 40);
        log.setEditable(false);
        log.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.gridy = 3;
        panel.add(new JScrollPane(log), g);

        btnReporte.addActionListener(e -> {
            String sec = fSeccion.getText().trim();
            if (sec.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese código de sección."); return; }
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(Bitacora.getTimestampArchivo() + "_Calificaciones_" + sec + ".txt"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String res = ExportadorPDF.reporteCalificacionesSeccion(
                    ctrl.getDatos(), sec, fc.getSelectedFile().getAbsolutePath());
                log.setText(res.equals("OK") ? "Reporte generado: " + fc.getSelectedFile().getName() : res);
                Bitacora.registrar(ctrl.getUsuarioActual().getRol(),
                    ctrl.getUsuarioActual().getCodigo(), "GENERAR_REPORTE", "EXITOSA",
                    "Reporte CalificacionesSeccion " + sec);
            }
        });

        btnCSV.addActionListener(e -> {
            String sec = fSeccion.getText().trim();
            if (sec.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese código de sección."); return; }
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(Bitacora.getTimestampArchivo() + "_NotasSeccion_" + sec + ".csv"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String res = ExportadorCSV.exportarNotasSeccion(
                    ctrl.getDatos(), sec, fc.getSelectedFile().getAbsolutePath());
                log.setText(res.equals("OK") ? "CSV generado: " + fc.getSelectedFile().getName() : res);
            }
        });

        return panel;
    }

    // ================================================================
    //  PANEL ESTUDIANTE
    // ================================================================
    private JPanel construirPanelEstudiante() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6); g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fEst = new JTextField(15);
        g.gridx = 0; g.gridy = 0; panel.add(new JLabel("Código Estudiante:"), g);
        g.gridx = 1; panel.add(fEst, g);

        JButton btnHistorial = new JButton("Generar Historial Individual");
        JButton btnCSV       = new JButton("Exportar Notas a CSV");

        g.gridx = 0; g.gridy = 1; g.gridwidth = 2;
        panel.add(btnHistorial, g);
        g.gridy = 2;
        panel.add(btnCSV, g);

        JTextArea log = new JTextArea(8, 40);
        log.setEditable(false);
        log.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.gridy = 3;
        panel.add(new JScrollPane(log), g);

        btnHistorial.addActionListener(e -> {
            String cod = fEst.getText().trim();
            if (cod.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese código de estudiante."); return; }
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(Bitacora.getTimestampArchivo() + "_Historial_" + cod + ".txt"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String res = ExportadorPDF.reporteHistorialEstudiante(
                    ctrl.getDatos(), cod, fc.getSelectedFile().getAbsolutePath());
                log.setText(res.equals("OK") ? "Historial generado: " + fc.getSelectedFile().getName() : res);
                Bitacora.registrar(ctrl.getUsuarioActual().getRol(),
                    ctrl.getUsuarioActual().getCodigo(), "GENERAR_REPORTE", "EXITOSA",
                    "Historial estudiante " + cod);
            }
        });

        btnCSV.addActionListener(e -> {
            String cod = fEst.getText().trim();
            if (cod.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese código de estudiante."); return; }
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(Bitacora.getTimestampArchivo() + "_Notas_" + cod + ".csv"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String res = ExportadorCSV.exportarNotasEstudiante(
                    ctrl.getDatos(), cod, fc.getSelectedFile().getAbsolutePath());
                log.setText(res.equals("OK") ? "CSV generado: " + fc.getSelectedFile().getName() : res);
            }
        });

        return panel;
    }

    // ================================================================
    //  PANEL INSCRIPCIONES
    // ================================================================
    private JPanel construirPanelInscripciones() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6,6,6,6); g.fill = GridBagConstraints.HORIZONTAL;

        JTextField fCurso = new JTextField(15);
        g.gridx = 0; g.gridy = 0; panel.add(new JLabel("Código Curso:"), g);
        g.gridx = 1; panel.add(fCurso, g);

        JButton btnReporte = new JButton("Generar Reporte de Inscripciones");
        g.gridx = 0; g.gridy = 1; g.gridwidth = 2;
        panel.add(btnReporte, g);

        JTextArea log = new JTextArea(8, 40);
        log.setEditable(false);
        log.setFont(new Font("Monospaced", Font.PLAIN, 11));
        g.gridy = 2;
        panel.add(new JScrollPane(log), g);

        btnReporte.addActionListener(e -> {
            String cod = fCurso.getText().trim();
            if (cod.isEmpty()) { JOptionPane.showMessageDialog(this, "Ingrese código de curso."); return; }
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(Bitacora.getTimestampArchivo() + "_Inscripciones_" + cod + ".txt"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String res = ExportadorPDF.reporteInscripcionesCurso(
                    ctrl.getDatos(), cod, fc.getSelectedFile().getAbsolutePath());
                log.setText(res.equals("OK") ? "Reporte generado: " + fc.getSelectedFile().getName() : res);
                Bitacora.registrar(ctrl.getUsuarioActual().getRol(),
                    ctrl.getUsuarioActual().getCodigo(), "GENERAR_REPORTE", "EXITOSA",
                    "Reporte Inscripciones curso " + cod);
            }
        });

        return panel;
    }

    // ================================================================
    //  PANEL EXPORTAR DATOS GENERALES
    // ================================================================
    private JPanel construirPanelExportar() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JButton btnExpInst = new JButton("Exportar lista de Instructores (CSV)");
        JButton btnExpEst  = new JButton("Exportar lista de Estudiantes (CSV)");
        JButton btnExpBit  = new JButton("Exportar Bitácora (CSV)");

        panel.add(new JLabel("Exportaciones rápidas:", SwingConstants.CENTER));
        panel.add(btnExpInst);
        panel.add(btnExpEst);
        panel.add(btnExpBit);
        panel.add(new JLabel(""));

        btnExpInst.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(Bitacora.getTimestampArchivo() + "_Instructores.csv"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String res = ExportadorCSV.exportarInstructores(ctrl.getDatos(), fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, res.equals("OK") ? "Exportado exitosamente." : res);
            }
        });

        btnExpEst.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(Bitacora.getTimestampArchivo() + "_Estudiantes.csv"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String res = ExportadorCSV.exportarEstudiantes(ctrl.getDatos(), fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, res.equals("OK") ? "Exportado exitosamente." : res);
            }
        });

        btnExpBit.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(Bitacora.getTimestampArchivo() + "_Bitacora.csv"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                String res = ExportadorCSV.exportarBitacora(ctrl.getDatos(), fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this, res.equals("OK") ? "Bitácora exportada." : res);
            }
        });

        return panel;
    }
}

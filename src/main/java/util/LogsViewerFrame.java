/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package util;
import org.apache.poi.xssf.usermodel.*; // Para Excel
import org.apache.pdfbox.pdmodel.*;
import enlaces.CConexion;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
/**
 *
 * @author frixi
 */
public class LogsViewerFrame extends javax.swing.JFrame {
    
    private JTable tablaLogs;

    public LogsViewerFrame() {
        super("Logs de Acciones - Solo Admin");
        initUI();
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        cargarLogs();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Historial de Acciones (Ordenado por mAs reciente)", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Usuario", "Acción", "Detalle", "Fecha/Hora"}, 0);
        tablaLogs = new JTable(model);
        tablaLogs.setAutoCreateRowSorter(true);
        JScrollPane scroll = new JScrollPane(tablaLogs);
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout());
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        bottom.add(btnCerrar);

        JButton btnExportarExcel = new JButton("Exportar Excel");
        btnExportarExcel.addActionListener(e -> exportarExcel());
        bottom.add(btnExportarExcel);

        JButton btnExportarPDF = new JButton("Exportar PDF");
        btnExportarPDF.addActionListener(e -> exportarPDF());
        bottom.add(btnExportarPDF);

        add(bottom, BorderLayout.SOUTH);
    }

    private void cargarLogs() {
        DefaultTableModel model = (DefaultTableModel) tablaLogs.getModel();
        model.setRowCount(0);

        CConexion con = new CConexion();
        try (Connection conn = con.establecerConexion();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM TablaLogs ORDER BY ID DESC")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("ID"),
                    rs.getString("Usuario"),
                    rs.getString("Accion"),
                    rs.getString("Detalle"),
                    rs.getTimestamp("FechaHora")
                });
            }
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No hay logs aún. ¡Haz algunas acciones para probar!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar logs: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    private void exportarExcel() {
        if (tablaLogs.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay logs para exportar.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("logs.xlsx"));
        int opcion = chooser.showSaveDialog(this);
        if (opcion != JFileChooser.APPROVE_OPTION) return;

        File outFile = chooser.getSelectedFile();

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Logs");
            int rownum = 0;
            
            DefaultTableModel model = (DefaultTableModel) tablaLogs.getModel();
            
            XSSFRow header = sheet.createRow(rownum++);
            for (int i = 0; i < model.getColumnCount(); i++) {
                header.createCell(i).setCellValue(model.getColumnName(i));
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                XSSFRow row = sheet.createRow(rownum++);
                for (int j = 0; j < model.getColumnCount(); j++) {
                    Object valor = model.getValueAt(i, j);
                    row.createCell(j).setCellValue(valor != null ? valor.toString() : "");
                }
            }

            
            for (int i = 0; i < model.getColumnCount(); i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream out = new FileOutputStream(outFile)) {
                workbook.write(out);
            }
            JOptionPane.showMessageDialog(this, "Excel exportado: " + outFile.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al crear Excel: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

private void exportarPDF() {
    if (tablaLogs.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "No hay logs para exportar.", "Info", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    JFileChooser chooser = new JFileChooser();
    chooser.setSelectedFile(new File("logs.pdf"));
    int opcion = chooser.showSaveDialog(this);
    if (opcion != JFileChooser.APPROVE_OPTION) return;

    File outFile = chooser.getSelectedFile();

    try (PDDocument doc = new PDDocument()) {
        PDPage page = new PDPage(PDRectangle.LETTER);  
        doc.addPage(page);

        PDPageContentStream content = new PDPageContentStream(doc, page);

        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 16);  
        content.newLineAtOffset(50, 700);
        content.showText("LISTA DE LOGS DE ACCIONES");
        content.endText();

        float y = 660;
        content.setFont(PDType1Font.HELVETICA, 8);  

        DefaultTableModel model = (DefaultTableModel) tablaLogs.getModel();

        content.beginText();
        content.newLineAtOffset(50, y);
        String headers = "";
        for (int j = 0; j < model.getColumnCount(); j++) {
            headers += model.getColumnName(j) + " | ";  
        }
        content.showText(headers);
        content.endText();
        y -= 15;

        for (int i = 0; i < model.getRowCount(); i++) {
            if (y < 100) {  
                content.close();
                page = new PDPage(PDRectangle.LETTER);
                doc.addPage(page);
                content = new PDPageContentStream(doc, page);
                y = 700;
            }
            content.beginText();
            content.newLineAtOffset(50, y);
            String linea = "";
            for (int j = 0; j < model.getColumnCount(); j++) {
                Object valor = model.getValueAt(i, j);
                String texto = (valor != null ? valor.toString() : "");
                if (j == 3 && texto.length() > 80) {  
                    texto = texto.substring(0, 80) + "...";
                }
                linea += texto + " | ";
            }
            content.showText(linea);
            content.endText();
            y -= 12;  
        }

        content.close();
        doc.save(outFile);
        JOptionPane.showMessageDialog(this, "PDF exportado (con Detalle truncado si es largo): " + outFile.getAbsolutePath());
    } catch (IOException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error al crear PDF: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new LogsViewerFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

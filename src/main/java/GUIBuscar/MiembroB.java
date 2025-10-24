/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUIBuscar;

import GUIEntrada.Menu;
import GUIEntrada.MiembroE;
import enlaces.CConexion;
import enlaces.MiembroCC;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author stefa
 */
public class MiembroB extends javax.swing.JFrame {
Integer xMouse,yMouse;
    
    public MiembroB() {
        initComponents();
        mostrarTodos();  
    }
    
    public void mostrarTodos() {
        String sql = "SELECT * FROM TablaMiembros ORDER BY id ASC";  
        CConexion con = new CConexion();
        Connection conexion = con.establecerConexion();
        DefaultTableModel model = (DefaultTableModel) tablaMiembros.getModel();
        Object[] datos = new Object[9];

        try {
            if (conexion == null) {
                JOptionPane.showMessageDialog(null, "Error de conexion a la BD. Verifique que este abierta.");
                return;
            }

            Statement st = conexion.createStatement();
            ResultSet rs = st.executeQuery(sql);

            model.setRowCount(0);

            int filasCargadas = 0;
            while (rs.next()) {
                datos[0] = rs.getObject(1);
                datos[1] = rs.getObject(2);
                datos[2] = rs.getObject(3);
                datos[3] = rs.getObject(4);
                datos[4] = rs.getObject(5);
                datos[5] = rs.getObject(6);
                datos[6] = rs.getObject(7);
                datos[7] = rs.getObject(8);
                datos[8] = rs.getObject(9);
                model.addRow(datos);
                filasCargadas++;
            }

            if (filasCargadas == 0) {
                JOptionPane.showMessageDialog(null, "No hay miembros registrados aun.");
            }

            rs.close();
            st.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar datos: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void buscarDato() {
        String dniInput = JOptionPane.showInputDialog("Insertar DNI del miembro");
        if (dniInput == null || dniInput.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "DNI cancelado o vacio. No se realizo la busqueda.");
            return;
        }

        int dni;
        try {
            dni = Integer.parseInt(dniInput.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "DNI invalido: debe ser un numero de 8 digitos.");
            return;
        }

        String sql = "SELECT * FROM TablaMiembros WHERE dni = ?";
        CConexion con = new CConexion();
        Connection conexion = con.establecerConexion();
        DefaultTableModel model = (DefaultTableModel) tablaMiembros.getModel();
        Object[] datos = new Object[9];

        try {
            if (conexion == null) {
                JOptionPane.showMessageDialog(null, "Error de conexion a la BD. Verifique que este abierta.");
                return;
            }

            PreparedStatement pstmt = conexion.prepareStatement(sql);
            pstmt.setInt(1, dni);
            ResultSet rs = pstmt.executeQuery();

            model.setRowCount(0);

            int filasEncontradas = 0;
            while (rs.next()) {
                datos[0] = rs.getObject(1);
                datos[1] = rs.getObject(2);
                datos[2] = rs.getObject(3);
                datos[3] = rs.getObject(4);
                datos[4] = rs.getObject(5);
                datos[5] = rs.getObject(6);
                datos[6] = rs.getObject(7);
                datos[7] = rs.getObject(8);
                datos[8] = rs.getObject(9);
                model.addRow(datos);
                filasEncontradas++;
            }

            if (filasEncontradas == 0) {
                JOptionPane.showMessageDialog(null, "No se encontro ningun miembro con DNI: " + dni);
            } else {
                JOptionPane.showMessageDialog(null, "Se encontro " + filasEncontradas + " resultado(s).");
            }

            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error en la busqueda: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conexion != null) conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void eliminarUsuario() {
        int filaSeleccionada = tablaMiembros.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "elija su eliminacion");
            return;
        }
        int id = (int) tablaMiembros.getValueAt(filaSeleccionada, 0);
        MiembroCC miembroDAO = new MiembroCC();
        boolean eliminado = miembroDAO.eliminarMiembro(id);
        if (eliminado) {
            DefaultTableModel model = (DefaultTableModel) tablaMiembros.getModel();
            model.removeRow(filaSeleccionada);
            JOptionPane.showMessageDialog(null, " eliminado correctamente");
        } else {
            JOptionPane.showMessageDialog(null, "no se puede eliminar ");
        }
    }
    
    private void actualizarDato() {
        int filaSeleccionada = tablaMiembros.getSelectedRow();
        int columnaSeleccionada = tablaMiembros.getSelectedColumn();

        if (filaSeleccionada == -1 || columnaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "por favor selecciona una celda para actualizar");
            return;
        }

        String nuevoValor = tablaMiembros.getValueAt(filaSeleccionada, columnaSeleccionada).toString();
        String columna = tablaMiembros.getColumnName(columnaSeleccionada);
        int id = (int) tablaMiembros.getValueAt(filaSeleccionada, 0);
        if (columna.equalsIgnoreCase("dni")) {
            MiembroCC miembroDAO = new MiembroCC();
            if (miembroDAO.existeDNI(nuevoValor)) {
                JOptionPane.showMessageDialog(null, "el dni " + nuevoValor + " ya está registrado");
                return;
            }
        }

        MiembroCC miembroDAO = new MiembroCC();
        boolean actualizado = miembroDAO.actualizarDato(id, columna, nuevoValor);

        if (actualizado) {
            JOptionPane.showMessageDialog(null, "dato actualizado correctamente");
        } else {
            JOptionPane.showMessageDialog(null, "error al actualizar el dato");
        }
    }
    
    
    
    private void exportarExcel() {
    if (tablaMiembros.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "No hay miembros para exportar.", "Info", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    JFileChooser chooser = new JFileChooser();
    chooser.setSelectedFile(new File("miembros.xlsx"));
    int opcion = chooser.showSaveDialog(this);
    if (opcion != JFileChooser.APPROVE_OPTION) return;

    File outFile = chooser.getSelectedFile();

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
        XSSFSheet sheet = workbook.createSheet("Miembros");
        int rownum = 0;
        
        XSSFRow header = sheet.createRow(rownum++);
        String[] headers = {"ID", "DNI", "Nombre", "Apellido", "Edad", "Deporte", "Membresia", "Tiempo", "Mensualidad"};
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        DefaultTableModel model = (DefaultTableModel) tablaMiembros.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            XSSFRow row = sheet.createRow(rownum++);
            for (int j = 0; j < model.getColumnCount(); j++) {
                Object valor = model.getValueAt(i, j);
                row.createCell(j).setCellValue(valor != null ? valor.toString() : "");
            }
        }

        for (int i = 0; i < 9; i++) {
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
    if (tablaMiembros.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "No hay miembros para exportar.", "Info", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    JFileChooser chooser = new JFileChooser();
    chooser.setSelectedFile(new File("miembros.pdf"));
    int opcion = chooser.showSaveDialog(this);
    if (opcion != JFileChooser.APPROVE_OPTION) return;

    File outFile = chooser.getSelectedFile();

    try (PDDocument doc = new PDDocument()) {
        PDPage page = new PDPage(PDRectangle.LETTER);
        doc.addPage(page);

        PDPageContentStream content = new PDPageContentStream(doc, page);

        content.beginText();
        content.setFont(PDType1Font.HELVETICA_BOLD, 18);
        content.newLineAtOffset(50, 700);
        content.showText("LISTA DE MIEMBROS");
        content.endText();

        float y = 660;
        content.setFont(PDType1Font.HELVETICA, 10); 

        content.beginText();
        content.newLineAtOffset(50, y);
        String headers = "ID;DNI;Nombre;Apellido;Edad;Deporte;Membresía;Tiempo;Mensualidad";
        content.showText(headers);
        content.endText();
        y -= 15;

        DefaultTableModel model = (DefaultTableModel) tablaMiembros.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (y < 80) { 
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
                linea += (valor != null ? valor.toString() : "") + ";";
            }
            content.showText(linea);
            content.endText();
            y -= 12; 
        }

        content.close();
        doc.save(outFile);
        JOptionPane.showMessageDialog(this, "PDF exportado: " + outFile.getAbsolutePath());
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

        bg = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaMiembros = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        btnExportarExcel = new javax.swing.JButton();
        btnExportarPDF = new javax.swing.JButton();
        head = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setUndecorated(true);
        setResizable(false);

        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBackground(new java.awt.Color(207, 0, 21));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Club Dema");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icono.png"))); // NOI18N

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/rotated_result_image.png"))); // NOI18N

        jLabel14.setBackground(new java.awt.Color(204, 204, 204));
        jLabel14.setForeground(new java.awt.Color(204, 204, 204));
        jLabel14.setText("Menu");
        jLabel14.setToolTipText("");
        jLabel14.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel14MouseClicked(evt);
            }
        });

        jLabel15.setBackground(new java.awt.Color(255, 255, 255));
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Mostrar");
        jLabel15.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel15MouseClicked(evt);
            }
        });

        jLabel16.setBackground(new java.awt.Color(204, 204, 204));
        jLabel16.setForeground(new java.awt.Color(204, 204, 204));
        jLabel16.setText("Registrar");
        jLabel16.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel16.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel16MouseClicked(evt);
            }
        });

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/registro (3).png"))); // NOI18N
        jLabel17.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel17.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel17MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16)
                            .addComponent(jLabel14)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel17)))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel3)))
                .addGap(49, 49, 49)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15)))
                .addContainerGap(139, Short.MAX_VALUE))
        );

        bg.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 410));

        jPanel2.setBackground(new java.awt.Color(237, 234, 230));

        tablaMiembros.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "DNI", "Nombre", "Apellido", "Edad", "Deporte", "Membresia", "Tiempo", "Mensualidad"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tablaMiembros);

        jLabel1.setFont(new java.awt.Font("Sitka Small", 1, 18)); // NOI18N
        jLabel1.setText("Busqueda de Miembros");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(69, 69, 69)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 675, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(264, 264, 264)
                        .addComponent(jLabel1)))
                .addContainerGap(76, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27))
        );

        bg.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 820, 330));

        jPanel3.setBackground(new java.awt.Color(197, 161, 115));

        jButton4.setBackground(new java.awt.Color(219, 183, 136));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton4.setText("Actualizar Dato");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(219, 183, 136));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.setText("Buscar por DNI");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(219, 183, 136));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton3.setText("Eliminar Miembro");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        btnExportarExcel.setBackground(new java.awt.Color(219, 183, 136));
        btnExportarExcel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnExportarExcel.setText("Excel");
        btnExportarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarExcelActionPerformed(evt);
            }
        });

        btnExportarPDF.setBackground(new java.awt.Color(219, 183, 136));
        btnExportarPDF.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnExportarPDF.setText("PDF");
        btnExportarPDF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarPDFActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(95, 95, 95)
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addComponent(jButton3)
                .addGap(26, 26, 26)
                .addComponent(jButton4)
                .addGap(34, 34, 34)
                .addComponent(btnExportarExcel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnExportarPDF)
                .addContainerGap(129, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(32, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton2)
                    .addComponent(jButton3)
                    .addComponent(btnExportarExcel)
                    .addComponent(btnExportarPDF))
                .addGap(25, 25, 25))
        );

        bg.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 330, 820, 80));

        head.setBackground(new java.awt.Color(207, 0, 21));
        head.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                headMouseDragged(evt);
            }
        });
        head.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                headMousePressed(evt);
            }
        });

        javax.swing.GroupLayout headLayout = new javax.swing.GroupLayout(head);
        head.setLayout(headLayout);
        headLayout.setHorizontalGroup(
            headLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1230, Short.MAX_VALUE)
        );
        headLayout.setVerticalGroup(
            headLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 37, Short.MAX_VALUE)
        );

        bg.add(head, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1230, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, 1015, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        buscarDato();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        eliminarUsuario();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        actualizarDato();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jLabel14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseClicked
        // TODO add your handling code here:
        dispose();
        Menu menu= new Menu();
        menu.setVisible(true);
        menu.setLocationRelativeTo(null);
    }//GEN-LAST:event_jLabel14MouseClicked

    private void jLabel15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel15MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel15MouseClicked

    private void jLabel16MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel16MouseClicked
        // TODO add your handling code here:
        dispose();
        MiembroE miembro= new MiembroE();
        miembro.setVisible(true);
        miembro.setLocationRelativeTo(null);
    }//GEN-LAST:event_jLabel16MouseClicked

    private void jLabel17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jLabel17MouseClicked

    private void headMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_headMouseDragged
        // TODO add your handling code here:
        Integer x=evt.getXOnScreen();
        Integer y=evt.getYOnScreen();
        this.setLocation(x-xMouse, y-yMouse);
    }//GEN-LAST:event_headMouseDragged

    private void headMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_headMousePressed
        // TODO add your handling code here:
        xMouse=evt.getX();
        yMouse=evt.getY();
    }//GEN-LAST:event_headMousePressed

    private void btnExportarExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarExcelActionPerformed
        // TODO add your handling code here:BOTON EXCEL
        exportarExcel();
    }//GEN-LAST:event_btnExportarExcelActionPerformed

    private void btnExportarPDFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnExportarPDFActionPerformed
        // TODO add your handling code here: BOTON PDF
        exportarPDF();
    }//GEN-LAST:event_btnExportarPDFActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MiembroB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MiembroB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MiembroB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MiembroB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MiembroB().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bg;
    private javax.swing.JButton btnExportarExcel;
    private javax.swing.JButton btnExportarPDF;
    private javax.swing.JPanel head;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tablaMiembros;
    // End of variables declaration//GEN-END:variables
}

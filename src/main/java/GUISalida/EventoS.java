/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUISalida;
import util.Sesion;
import org.apache.poi.xssf.usermodel.*; 
import org.apache.pdfbox.pdmodel.*;     
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import GUIEntrada.EventoE;
import GUIEntrada.Menu;
import enlaces.CConexion;
import enlaces.EventoCC;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import enlaces.LogCC;
import enlaces.MiembroCC;
import util.Sesion;
/**
 *
 * @author stefa
 */
public class EventoS extends javax.swing.JFrame {
DefaultTableModel model = new DefaultTableModel();
Integer xMouse,yMouse;
    

    public EventoS() {
        initComponents();
        mostrar(tablaEventos);
        ocultarBotonEliminarTodoSiNoAdmin();
    }
    
    private void ocultarBotonEliminarTodoSiNoAdmin() {
        if (!"admin".equals(Sesion.getRolActual())) {
            btnEliminarTodo.setVisible(false);  
        }
    }
    
    
    
    
    public void mostrar(JTable tabla) {
    String sql = "SELECT e.ID, e.Ocasión, e.Deporte, e.horaInicio, e.horaFin, e.fecha,e.reservacion, e.Área, m.nombre, m.apellido " +
                 "FROM TablaEvento e " +
                 "JOIN TablaMiembros m ON e.reservacion = m.dni";
    Statement st;
    Connection conexion = new CConexion().establecerConexion();
    DefaultTableModel model = (DefaultTableModel) tabla.getModel();
    model.setColumnIdentifiers(new Object[]{"ID", "Ocasión", "Deporte", "Hora Inicio", "Hora Fin", "Fecha", "Área", "Reservacion (DNI)","Nombre", "Apellido"});

    try {
        st = conexion.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getInt("ID"),
                rs.getString("Ocasión"),
                rs.getString("deporte"),
                rs.getTime("horaInicio"),
                rs.getTime("horaFin"),
                rs.getDate("fecha"),
                rs.getString("Área"),
                rs.getString("reservacion"),
                rs.getString("nombre"),
                rs.getString("apellido")
            });
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "error al cargar datos: " + e.getMessage());
    }
}


    
        private void eliminarEventos() {
    int filaSeleccionada = tablaEventos.getSelectedRow();
    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(null, "Elija a quien desea eliminar");
        return;
    }
    int id=(int) tablaEventos.getValueAt(filaSeleccionada, 0);
    
    EventoCC miembroDAO = new EventoCC();
    boolean eliminado = miembroDAO.eliminarEvento(id);
    
    if (eliminado) {
        DefaultTableModel model = (DefaultTableModel) tablaEventos.getModel();
        model.removeRow(filaSeleccionada);
        
        LogCC logDAO = new LogCC();
        logDAO.insertarLog("Eliminación Evento", "ID: " + id);
        
        JOptionPane.showMessageDialog(null, " eliminado correctamente");
    } else {
        JOptionPane.showMessageDialog(null, "no se puede eliminar ");
    }
}

private void eliminarTodo() {
        int confirmacion = JOptionPane.showConfirmDialog(null, 
            "se eliminara todo registro", 
            " confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            EventoCC miembroDAO = new EventoCC();
            boolean eliminado = miembroDAO.eliminarTodos();
            
            if (eliminado) {
                DefaultTableModel model = (DefaultTableModel) tablaEventos.getModel();
                model.setRowCount(0); 
                
                LogCC logDAO = new LogCC();
                logDAO.insertarLog("Eliminación Total Eventos", "Se borraron todos los registros");
                
                JOptionPane.showMessageDialog(null, "se elimino todo de la tabla");
            } else {
                JOptionPane.showMessageDialog(null, "no se pudo eliminar todo");
            }
        }
    }

private void actualizarDatos() {
    int filaSeleccionada = tablaEventos.getSelectedRow();
    int columnaSeleccionada = tablaEventos.getSelectedColumn();

    if (filaSeleccionada == -1 || columnaSeleccionada == -1) {
        JOptionPane.showMessageDialog(null, "elija el campo a actualizar");
        return;
    }

    String nuevoValor = tablaEventos.getValueAt(filaSeleccionada, columnaSeleccionada).toString();
    String columna = tablaEventos.getColumnName(columnaSeleccionada); 
    int id = (int) tablaEventos.getValueAt(filaSeleccionada, 0);

    if (columna.equalsIgnoreCase("reservacion")) {  
        MiembroCC miembroDAO = new MiembroCC();  
        if (!miembroDAO.existeDNI(nuevoValor)) {  
            JOptionPane.showMessageDialog(null, "El DNI '" + nuevoValor + "' no existe en miembros. No se puede reservar.");
            return;  
        }
    }

    EventoCC eventoDAO = new EventoCC();  
    boolean actualizado = eventoDAO.actualizarDato(id, columna, nuevoValor);

    if (actualizado) {
        LogCC logDAO = new LogCC();
        logDAO.insertarLog("Actualizacion Evento", "ID: " + id + ", Campo: " + columna + ", Nuevo valor: " + nuevoValor);
        
        JOptionPane.showMessageDialog(null, "dato actualizado correctamente");
    } else {
        JOptionPane.showMessageDialog(null, "error al actualizar, verifique");
    }
}    

private void exportarExcel() {
    if (tablaEventos.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "No hay eventos para exportar.", "Info", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    JFileChooser chooser = new JFileChooser();
    chooser.setSelectedFile(new File("eventos.xlsx"));
    int opcion = chooser.showSaveDialog(this);
    if (opcion != JFileChooser.APPROVE_OPTION) return;

    File outFile = chooser.getSelectedFile();

    try (XSSFWorkbook workbook = new XSSFWorkbook()) {
        XSSFSheet sheet = workbook.createSheet("Eventos");
        int rownum = 0;
        
        DefaultTableModel model = (DefaultTableModel) tablaEventos.getModel();
        
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
    if (tablaEventos.getRowCount() == 0) {
        JOptionPane.showMessageDialog(this, "No hay eventos para exportar.", "Info", JOptionPane.INFORMATION_MESSAGE);
        return;
    }

    JFileChooser chooser = new JFileChooser();
    chooser.setSelectedFile(new File("eventos.pdf"));
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
        content.showText("LISTA DE EVENTOS");
        content.endText();

        float y = 660;
        content.setFont(PDType1Font.HELVETICA, 8); // Fuente aún más pequeña para 10 columnas

        DefaultTableModel model = (DefaultTableModel) tablaEventos.getModel();

        //  
        content.beginText();
        content.newLineAtOffset(50, y);
        String headers = "";
        for (int j = 0; j < model.getColumnCount(); j++) {
            headers += model.getColumnName(j) + ";";
        }
        content.showText(headers);
        content.endText();
        y -= 15;

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
            y -= 10; 
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
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaEventos = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        btnEliminarTodo = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        btnExportarExcel = new javax.swing.JButton();
        btnExportarPDF = new javax.swing.JButton();
        head = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tablaEventos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Ocasión", "Deporte", "Horario Inicio", "Horario Final", "Fecha", "Área", "Reservacion(DNI)", "Nombre", "Apellido"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, false, false, true, true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tablaEventos);

        bg.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 80, 1004, 260));

        jPanel1.setBackground(new java.awt.Color(207, 0, 21));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Manchester");

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
                .addContainerGap(18, Short.MAX_VALUE))
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
                .addContainerGap(169, Short.MAX_VALUE))
        );

        bg.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 440));

        jPanel2.setBackground(new java.awt.Color(237, 234, 230));

        jLabel1.setFont(new java.awt.Font("Sitka Small", 1, 18)); // NOI18N
        jLabel1.setText("Tabla Eventos");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(429, 429, 429)
                .addComponent(jLabel1)
                .addContainerGap(503, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel1)
                .addContainerGap(300, Short.MAX_VALUE))
        );

        bg.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 1070, 360));

        jPanel3.setBackground(new java.awt.Color(197, 161, 115));

        jButton2.setBackground(new java.awt.Color(219, 183, 136));
        jButton2.setText("Eliminar Evento");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        btnEliminarTodo.setBackground(new java.awt.Color(219, 183, 136));
        btnEliminarTodo.setText("Eliminar Todo");
        btnEliminarTodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarTodoActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(219, 183, 136));
        jButton4.setText("Actualizar Dato");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        btnExportarExcel.setBackground(new java.awt.Color(219, 183, 136));
        btnExportarExcel.setText("Excel");
        btnExportarExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExportarExcelActionPerformed(evt);
            }
        });

        btnExportarPDF.setBackground(new java.awt.Color(219, 183, 136));
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
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(118, 118, 118)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEliminarTodo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addGap(127, 127, 127)
                .addComponent(btnExportarExcel)
                .addGap(18, 18, 18)
                .addComponent(btnExportarPDF)
                .addContainerGap(311, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(btnEliminarTodo)
                    .addComponent(jButton4)
                    .addComponent(btnExportarExcel)
                    .addComponent(btnExportarPDF))
                .addContainerGap(30, Short.MAX_VALUE))
        );

        bg.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 360, 1070, 80));

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
            .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, 435, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here: eliminar
     eliminarEventos();
        
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void btnEliminarTodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarTodoActionPerformed
        // TODO add your handling code here:eliminar todo
        eliminarTodo();
        
    }//GEN-LAST:event_btnEliminarTodoActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        actualizarDatos();
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
        EventoE evento= new EventoE();
        evento.setVisible(true);
        evento.setLocationRelativeTo(null);
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
        // TODO add your handling code here: BOTON EXCEL
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
            java.util.logging.Logger.getLogger(EventoS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(EventoS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(EventoS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(EventoS.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new EventoS().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bg;
    private javax.swing.JButton btnEliminarTodo;
    private javax.swing.JButton btnExportarExcel;
    private javax.swing.JButton btnExportarPDF;
    private javax.swing.JPanel head;
    private javax.swing.JButton jButton2;
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
    private javax.swing.JTable tablaEventos;
    // End of variables declaration//GEN-END:variables
}

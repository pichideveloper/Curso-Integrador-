/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUIEntrada;

import GUISalida.MiembroS;
import enlaces.CConexion;
import enlaces.MiembroCC;
import java.sql.Connection;
import javax.swing.JOptionPane;
import enlaces.LogCC;
import javax.swing.DefaultComboBoxModel;
import util.Sesion;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author stefa
 */
public class MiembroE extends javax.swing.JFrame {
CConexion  conec= new CConexion();
Connection conectar = conec.establecerConexion();
Integer xMouse,yMouse;
    

    public MiembroE() {
    initComponents();
    String[] deportes = {"Selecciona un deporte...", "Futbol", "Basquet", "Tenis", "Natacion", "Voleibol", "Gimnasia"};
    cmbDeporte.setModel(new DefaultComboBoxModel<>(deportes));  
    cmbDeporte.setSelectedIndex(0);  
    ((AbstractDocument) txtnombre.getDocument()).setDocumentFilter(new FiltroTexto("letras", 30));
    ((AbstractDocument) txtapellido.getDocument()).setDocumentFilter(new FiltroTexto("letras", 30));
    ((AbstractDocument) txtedad.getDocument()).setDocumentFilter(new FiltroTexto("numeros", 3));  
    ((AbstractDocument) txtanos.getDocument()).setDocumentFilter(new FiltroTexto("numeros", 2));  
    ((AbstractDocument) txtdni.getDocument()).setDocumentFilter(new FiltroTexto("numeros", 8));  
}
  
    private void limpiarCampos() {
    txtnombre.setText("");
    txtapellido.setText("");
    txtdni.setText("");
    txtedad.setText("");
    txtanos.setText("");
    txtmembre.setSelectedIndex(0);
    cmbDeporte.setSelectedIndex(0);  
}
    
    private void guardarDatos() {
    try {
        String nombre = txtnombre.getText().trim();
        String apellido = txtapellido.getText().trim();
        String deporte = (String) cmbDeporte.getSelectedItem();   
        String membresia = (String) txtmembre.getSelectedItem();
        int dni;
        int edad;
        int tiempo;
        double mensualidad = 0;
        if (nombre.isEmpty() || apellido.isEmpty() || deporte == null || deporte.equals("Selecciona un deporte...") || membresia == null) {
            JOptionPane.showMessageDialog(null, "Por favor, completa todos los campos correctamente (elige un deporte)");
            return; 
        }
        
        try {
            edad = Integer.parseInt(txtedad.getText().trim());
            tiempo = Integer.parseInt(txtanos.getText().trim());
            if (tiempo < 1 || tiempo > 30) {
                JOptionPane.showMessageDialog(null, "Años como miembro debe ser entre 1 y 30");
                return;
            }
            dni = Integer.parseInt(txtdni.getText().trim());
            if (txtdni.getText().trim().length() != 8) {  
                JOptionPane.showMessageDialog(null, "El DNI debe tener exactamente 8 digitos.");
                return;
            }
            if (edad < 18 || edad > 120) {
                JOptionPane.showMessageDialog(null, "La edad debe ser mayor o igual a 18 y menor o igual a 120 años.");
                txtedad.setText("");  
                txtedad.requestFocus();  
                return;
            }
            switch (membresia){
                case "Bronce" -> {mensualidad=200;}
                case "Plata" -> {mensualidad=300;}
                case "Oro" -> {mensualidad=500;}
                case "Platino" -> {mensualidad=800;}
            }
            if (tiempo>=5 & tiempo<10) {
                mensualidad= mensualidad*0.90;
            }
            if (tiempo>=10 & tiempo<20) {
                mensualidad= mensualidad*0.85;
            }if(tiempo >= 20){
                mensualidad=mensualidad*0.75;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "por favor ingresa valores numericos validos");
            return; 
        }

        MiembroCC miembroDAO = new MiembroCC();
        
        if (miembroDAO.existeDNI(txtdni.getText().trim())) {
            JOptionPane.showMessageDialog(this, "El dni " + txtdni.getText() + " ha sido previamente registrado ");
            return; 
        }  
        
        miembroDAO.agregarMiembro(dni,nombre, apellido, edad, deporte, membresia, tiempo,mensualidad);
        
        LogCC logDAO = new LogCC();
        logDAO.insertarLog("Registro Miembro", "DNI: " + dni + ", Nombre: " + nombre + " " + apellido + ", Edad: " + edad + ", Deporte: " + deporte + ", Membresía: " + membresia + ", Tiempo: " + tiempo + ", Mensualidad: " + mensualidad);
        
        JOptionPane.showMessageDialog(this,"miembro ingresado correctamente");
        limpiarCampos();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "error" + e.getMessage());
        e.printStackTrace();
    }
}
    
   private void verificarDNI() {
    String dni = txtdni.getText().trim();

    if (dni.isEmpty()) {
        JOptionPane.showMessageDialog(this, "ingrese un dni valido ");
        return;
    }

    MiembroCC miembroDAO = new MiembroCC();
    if (miembroDAO.existeDNI(dni)) {
        JOptionPane.showMessageDialog(this, "este dni:" + dni + " ya está registrado");
    } else {
        JOptionPane.showMessageDialog(this, "dni disponible");
    }
} 
 
private class FiltroTexto extends DocumentFilter {
    private String tipo;  
    private int maxLong;  

    public FiltroTexto(String tipo, int maxLong) {
        this.tipo = tipo;
        this.maxLong = maxLong;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string == null) return;

        String nuevoTexto = fb.getDocument().getText(0, offset) + string + fb.getDocument().getText(offset, fb.getDocument().getLength() - offset);
        if (nuevoTexto.length() <= maxLong && esValido(string)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text == null) {
            super.replace(fb, offset, length, text, attrs);
            return;
        }

        String nuevoTexto = fb.getDocument().getText(0, offset) + text + fb.getDocument().getText(offset + length, fb.getDocument().getLength() - offset - length);
        if (nuevoTexto.length() <= maxLong && esValido(text)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    private boolean esValido(String input) {
        if (tipo.equals("letras")) {
            return input.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*");  
        } else if (tipo.equals("numeros")) {
            return input.matches("[0-9]*");  
        }
        return false;
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
        jLabel7 = new javax.swing.JLabel();
        txtnombre = new javax.swing.JTextField();
        txtedad = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtapellido = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtmembre = new javax.swing.JComboBox<>();
        txtanos = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtdni = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel11 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel12 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        cmbDeporte = new javax.swing.JComboBox<>();
        head = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setUndecorated(true);
        setResizable(false);

        bg.setBackground(new java.awt.Color(237, 234, 230));
        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel7.setText("Deporte");
        bg.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 390, -1, -1));

        txtnombre.setBorder(null);
        txtnombre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtnombreActionPerformed(evt);
            }
        });
        bg.add(txtnombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 100, 240, 20));

        txtedad.setBorder(null);
        bg.add(txtedad, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 250, 240, 20));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Tipo de Membresia");
        bg.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 290, -1, -1));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Nombre");
        bg.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 80, 60, -1));

        txtapellido.setBorder(null);
        bg.add(txtapellido, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 150, 240, 20));

        jLabel9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel9.setText("Años como Miembro");
        bg.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 340, -1, -1));

        txtmembre.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Bronce", "Plata", "Oro", "Platino" }));
        txtmembre.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtmembreActionPerformed(evt);
            }
        });
        bg.add(txtmembre, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 310, 240, -1));

        txtanos.setBorder(null);
        bg.add(txtanos, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 360, 240, 20));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel5.setText("Membresía ");
        bg.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 290, -1, -1));

        txtdni.setBorder(null);
        bg.add(txtdni, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 200, 240, 20));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel6.setText("Edad");
        bg.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 230, -1, -1));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("DNI");
        bg.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 180, -1, -1));

        jPanel2.setBackground(new java.awt.Color(197, 161, 115));

        jButton1.setBackground(new java.awt.Color(219, 183, 136));
        jButton1.setText("Verificar DNI");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(219, 183, 136));
        jButton5.setText("Ingresar Datos");
        jButton5.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 191, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addGap(53, 53, 53))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton5))
                .addContainerGap())
        );

        bg.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 450, 450, 50));

        jPanel1.setBackground(new java.awt.Color(207, 0, 21));

        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Manchester");

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icono.png"))); // NOI18N

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/result_image_centered.png"))); // NOI18N

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

        jLabel15.setBackground(new java.awt.Color(204, 204, 204));
        jLabel15.setForeground(new java.awt.Color(204, 204, 204));
        jLabel15.setText("Mostrar");
        jLabel15.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel15MouseClicked(evt);
            }
        });

        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Registrar");

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
                        .addGap(20, 20, 20)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
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
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel3)))
                .addGap(49, 49, 49)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(26, 26, 26)
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel15)))
                .addContainerGap(244, Short.MAX_VALUE))
        );

        bg.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 500));

        jSeparator1.setBackground(new java.awt.Color(153, 153, 153));
        jSeparator1.setForeground(new java.awt.Color(153, 153, 153));
        bg.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 280, 400, 10));

        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel11.setText("Apellido");
        bg.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 130, -1, -1));

        jSeparator2.setBackground(new java.awt.Color(153, 153, 153));
        jSeparator2.setForeground(new java.awt.Color(153, 153, 153));
        bg.add(jSeparator2, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 70, 400, 10));

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel12.setText("Miembro");
        bg.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 80, -1, -1));

        jPanel3.setBackground(new java.awt.Color(237, 234, 230));
        jPanel3.setForeground(new java.awt.Color(237, 234, 230));

        jLabel13.setBackground(new java.awt.Color(0, 0, 0));
        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setText("Menu de Registro de Miembro");

        cmbDeporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbDeporteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(jLabel13))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(180, 180, 180)
                        .addComponent(cmbDeporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(96, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 348, Short.MAX_VALUE)
                .addComponent(cmbDeporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );

        bg.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 450, 450));

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
            .addGap(0, 651, Short.MAX_VALUE)
        );
        headLayout.setVerticalGroup(
            headLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 37, Short.MAX_VALUE)
        );

        bg.add(head, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, 646, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtmembreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtmembreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtmembreActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        
        guardarDatos();
      
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here: VERIFIACION DNI
        
        
        verificarDNI();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void txtnombreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtnombreActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtnombreActionPerformed

    private void jLabel14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseClicked
        // TODO add your handling code here:
        dispose();
        Menu menu= new Menu();
        menu.setVisible(true);
        menu.setLocationRelativeTo(null);
    }//GEN-LAST:event_jLabel14MouseClicked

    private void jLabel15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel15MouseClicked
        // TODO add your handling code here:
        dispose();
        MiembroS mi= new MiembroS();
        mi.setVisible(true);
        mi.setLocationRelativeTo(null);
    }//GEN-LAST:event_jLabel15MouseClicked

    private void headMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_headMousePressed
        // TODO add your handling code here:
        xMouse=evt.getX();
        yMouse=evt.getY();
    }//GEN-LAST:event_headMousePressed

    private void headMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_headMouseDragged
        // TODO add your handling code here:
        Integer x=evt.getXOnScreen();
        Integer y=evt.getYOnScreen();
        this.setLocation(x-xMouse, y-yMouse);
        
    }//GEN-LAST:event_headMouseDragged

    private void jLabel17MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel17MouseClicked
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jLabel17MouseClicked

    private void cmbDeporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDeporteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbDeporteActionPerformed

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
            java.util.logging.Logger.getLogger(MiembroE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MiembroE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MiembroE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MiembroE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MiembroE().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bg;
    private javax.swing.JComboBox<String> cmbDeporte;
    private javax.swing.JPanel head;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField txtanos;
    private javax.swing.JTextField txtapellido;
    private javax.swing.JTextField txtdni;
    private javax.swing.JTextField txtedad;
    private javax.swing.JComboBox<String> txtmembre;
    private javax.swing.JTextField txtnombre;
    // End of variables declaration//GEN-END:variables
}

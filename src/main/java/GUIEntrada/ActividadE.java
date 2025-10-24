/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package GUIEntrada;

import GUISalida.ActividadS;
import enlaces.ActividadCC;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import enlaces.LogCC;
import util.Sesion;
import javax.swing.JOptionPane;
import javax.swing.DefaultComboBoxModel;
import java.util.Map;
import java.util.HashMap;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
/**
 *
 * @author joaquindd
 */
public class ActividadE extends javax.swing.JFrame {
    
    Integer xMouse,yMouse;
    public ActividadE() {
    initComponents();

    cmbArea.setModel(new DefaultComboBoxModel<>());
    cmbArea.setSelectedIndex(-1);  
    ((AbstractDocument) txtocasion.getDocument()).setDocumentFilter(new FiltroTexto("letras", 25));  
    ((AbstractDocument) txtreservacion.getDocument()).setDocumentFilter(new FiltroTexto("numeros", 8));  
    
}
    private void limpiarCampos() {
    txtocasion.setText("");

    lblDeporte.setText("Deporte: ");  
    cmbArea.setModel(new DefaultComboBoxModel<>());  
    cmbArea.setSelectedIndex(-1);
    txtreservacion.setText("");
    calenderfecha.setDate(null); 
    txtinicio.clear(); 
    txtfinal.clear();
    nombre1.setText("Nombre: ");
    apellido1.setText("Apellido: ");
}
    
    
    private void poblarAreasPorDeporte(String deporte) {
    Map<String, String[]> areasPorDeporte = new HashMap<>();
    areasPorDeporte.put("Futbol", new String[]{"Selecciona un area...", "Campo de Futbol 11", "Losa Deportiva", "Campo Sintetico"});
    areasPorDeporte.put("Voleibol", new String[]{"Selecciona un area...", "Pista de Voleibol", "Coliseo Norte", "Coliseo Sur"});  
    areasPorDeporte.put("Basquet", new String[]{"Selecciona un area...", "Pista de Basquet Norte", "Pista de Basquet Sur"});  
    areasPorDeporte.put("Tenis", new String[]{"Selecciona un area...", "Campo de Tenis Profesional", "Campo de Tenis Amateur"});
    areasPorDeporte.put("Natacion", new String[]{"Selecciona un area...", "Piscina Olimpica", "Alberca Norte", "Alberca Sur"});
    areasPorDeporte.put("Gimnasia", new String[]{"Selecciona un area...", "Sala de Gimnasia 1", "Sala de Gimnasia 2"});

    String[] areas = areasPorDeporte.getOrDefault(deporte, new String[]{"Selecciona un area...", "Area no disponible para este deporte"});
    cmbArea.setModel(new DefaultComboBoxModel<>(areas));
    cmbArea.setSelectedIndex(0);  
}
    
    private String normalizarDeporte(String deporte) {
    if (deporte == null || deporte.isEmpty()) {
        return "";
    }
    String normalizado = deporte
        .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
        .replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U")
        .trim();
    if (normalizado.length() > 0) {
        normalizado = normalizado.substring(0, 1).toUpperCase() + normalizado.substring(1).toLowerCase();
    }
    return normalizado;
}

    private void guardarDatos() {
    try {
        String ocasion = txtocasion.getText().trim();
        String area = (String) cmbArea.getSelectedItem();  
        String dni = txtreservacion.getText().trim();
        String deporte = lblDeporte.getText().replace("Deporte: ", "").trim();  
        
        if (dni.length() != 8 || !dni.matches("[0-9]{8}")) {  
            JOptionPane.showMessageDialog(null, "El DNI debe tener exactamente 8 digitos numericos.");
            txtreservacion.setText("");  
            txtreservacion.requestFocus();  
            return;
        }
        Date fechaSeleccionada = calenderfecha.getDate();
        if (fechaSeleccionada == null) {
            JOptionPane.showMessageDialog(null, "Ingrese una fecha valida");
            return;
        }
  
        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = simple.format(fechaSeleccionada);
        LocalTime horaInicio = txtinicio.getTime();
        LocalTime horaFin = txtfinal.getTime();

        if (horaInicio == null || horaFin == null) {
            JOptionPane.showMessageDialog(null, "Elija horas válidas");
            return;
        }      
        if (horaInicio.isAfter(horaFin) || horaInicio.equals(horaFin)) {
            JOptionPane.showMessageDialog(null, "La hora inicial debe ser antes de la final");
            return; 
        }
        if (ocasion.isEmpty() || deporte.isEmpty() || dni.isEmpty() || area == null || area.equals("Selecciona un área...")) {
            JOptionPane.showMessageDialog(null, "Complete todos los campos correctamente (elige un área)");
            return;
        }
        //
        ActividadCC actividadDAO = new ActividadCC();
        //
        String[] datosEntrenador = actividadDAO.buscarEntenadorPorDNI(dni);
        
        if (datosEntrenador == null) {
            JOptionPane.showMessageDialog(null, "Ese DNI no esta en la base de datos");
            return;
        }
        if (actividadDAO.verificarConflictoHorario(fecha, horaInicio.toString(), horaFin.toString())) {
            JOptionPane.showMessageDialog(null, "Ese horario ya ha sido reservado");
            return;
        }
        actividadDAO.agregarActividad(ocasion, deporte, horaInicio.toString(), horaFin.toString(), fecha, area, dni);  
        
        LogCC logDAO = new LogCC();
        logDAO.insertarLog("Registro Actividad", "DNI Entrenador: " + dni + ", Ocasión: " + ocasion + ", Deporte: " + deporte + ", Fecha: " + fecha + ", Hora Inicio: " + horaInicio.toString() + ", Hora Fin: " + horaFin.toString() + ", Área: " + area);
        
        JOptionPane.showMessageDialog(null, "Actividad ingresada correctamente");
        limpiarCampos();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        e.printStackTrace();  
    }
}

    
    
    private void checkEntrenador() {
    String dni = txtreservacion.getText().trim(); 

    if (dni.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Ingrese su DNI");
        return;
    }
    
    ActividadCC actividadDAO = new ActividadCC();
    String[] datosEntrenador = actividadDAO.buscarEntenadorPorDNI(dni);

    if (datosEntrenador != null && datosEntrenador.length >= 4) {
        txtreservacion.setText(datosEntrenador[2]);  
        nombre1.setText("Nombre: " + datosEntrenador[0]);  
        apellido1.setText("Apellido: " + datosEntrenador[1]);  
        
        String deporteOriginal = datosEntrenador[3];  
        if (deporteOriginal != null && !deporteOriginal.isEmpty()) {
            lblDeporte.setText("Deporte: " + deporteOriginal);  
            String deporteNormalizado = normalizarDeporte(deporteOriginal);  
            System.out.println("DEBUG: Deporte original: '" + deporteOriginal + "', normalizado: '" + deporteNormalizado + "'");  
            poblarAreasPorDeporte(deporteNormalizado);  
        } else {
            lblDeporte.setText("Deporte: No disponible");
        }
    } else {
        JOptionPane.showMessageDialog(null, "No se encontro ningun entrenador asociado a este DNI");
        lblDeporte.setText("Deporte: ");  
        cmbArea.setModel(new DefaultComboBoxModel<>());  
        nombre1.setText("Nombre: ");
        apellido1.setText("Apellido: ");
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
        jButton9 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        calenderfecha = new com.toedter.calendar.JDateChooser();
        jLabel26 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        txtinicio = new com.github.lgooddatepicker.components.TimePicker();
        txtfinal = new com.github.lgooddatepicker.components.TimePicker();
        jLabel27 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        txtocasion = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        apellido1 = new javax.swing.JLabel();
        nombre1 = new javax.swing.JLabel();
        txtreservacion = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        lblDeporte = new javax.swing.JLabel();
        cmbArea = new javax.swing.JComboBox<>();
        head = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(421, 255));
        setUndecorated(true);
        setResizable(false);

        bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton9.setBackground(new java.awt.Color(219, 183, 136));
        jButton9.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton9.setText("Ingresar Datos");
        jButton9.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });
        bg.add(jButton9, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 500, -1, -1));

        jButton2.setText("Verificar DNI");
        jButton2.setBackground(new java.awt.Color(219, 183, 136));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        bg.add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 500, -1, -1));

        jPanel1.setBackground(new java.awt.Color(207, 0, 21));

        jLabel3.setText("Club Dema");
        jLabel3.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/icono.png"))); // NOI18N

        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/result_image_centered.png"))); // NOI18N

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

        jLabel16.setText("Registrar");
        jLabel16.setForeground(new java.awt.Color(255, 255, 255));

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
                        .addContainerGap()
                        .addComponent(jLabel17))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16)
                            .addComponent(jLabel14))))
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
                .addContainerGap(284, Short.MAX_VALUE))
        );

        bg.add(jPanel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 200, 540));

        jPanel2.setBackground(new java.awt.Color(237, 234, 230));

        jLabel18.setText("Menu de Registro de Actividad");
        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N

        calenderfecha.setBackground(new java.awt.Color(255, 255, 255));

        jLabel26.setText("Fecha");
        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jLabel24.setText("Area");
        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jLabel27.setText("Hora inicio");
        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jLabel23.setText("Hora Final");
        jLabel23.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jLabel22.setText("Deporte");
        jLabel22.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        txtocasion.setBorder(null);
        txtocasion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtocasionActionPerformed(evt);
            }
        });

        jLabel20.setText("Ocasion");
        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jLabel5.setText("Datos de la Actividad");
        jLabel5.setFont(new java.awt.Font("Sitka Heading", 1, 14)); // NOI18N

        jSeparator1.setBackground(new java.awt.Color(153, 153, 153));
        jSeparator1.setForeground(new java.awt.Color(153, 153, 153));

        apellido1.setText("Apellido: ");

        nombre1.setText("Nombre: ");

        txtreservacion.setBorder(null);
        txtreservacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtreservacionActionPerformed(evt);
            }
        });

        jLabel25.setText("DNI");
        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N

        jLabel1.setText("Datos del Entrenador:");
        jLabel1.setFont(new java.awt.Font("Sitka Heading", 1, 14)); // NOI18N

        jSeparator2.setBackground(new java.awt.Color(153, 153, 153));
        jSeparator2.setForeground(new java.awt.Color(153, 153, 153));

        lblDeporte.setText("Deporte Designado");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(txtocasion, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel22)
                            .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(calenderfecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel26)
                                    .addComponent(txtinicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addGap(47, 47, 47)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtfinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cmbArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel24)))))
                            .addComponent(lblDeporte, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(56, 56, 56)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel25)
                                    .addComponent(txtreservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(nombre1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(apellido1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 160, Short.MAX_VALUE))))
                            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel18)
                .addGap(91, 91, 91))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtreservacion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(nombre1)
                .addGap(18, 18, 18)
                .addComponent(apellido1)
                .addGap(54, 54, 54)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtocasion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel22)
                        .addGap(9, 9, 9)
                        .addComponent(lblDeporte, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27)
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtinicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtfinal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel24, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(9, 9, 9)
                        .addComponent(calenderfecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cmbArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        bg.add(jPanel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 0, 450, 480));

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
            .addGap(0, 650, Short.MAX_VALUE)
        );
        headLayout.setVerticalGroup(
            headLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 37, Short.MAX_VALUE)
        );

        bg.add(head, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 650, -1));

        jPanel3.setBackground(new java.awt.Color(197, 161, 115));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 450, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 60, Short.MAX_VALUE)
        );

        bg.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 480, 450, 60));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, 643, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(bg, javax.swing.GroupLayout.PREFERRED_SIZE, 538, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtocasionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtocasionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtocasionActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here: ingresa los datos
        guardarDatos();
        
        
        
        
        
    }//GEN-LAST:event_jButton9ActionPerformed

    private void txtreservacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtreservacionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtreservacionActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here: checkea los datos   
        checkEntrenador();
        
        
        
        
    }//GEN-LAST:event_jButton2ActionPerformed

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
        ActividadS mi= new ActividadS();
        mi.setVisible(true);
        mi.setLocationRelativeTo(null);
    }//GEN-LAST:event_jLabel15MouseClicked

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
            java.util.logging.Logger.getLogger(ActividadE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ActividadE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ActividadE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ActividadE.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ActividadE().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel apellido1;
    private javax.swing.JPanel bg;
    private com.toedter.calendar.JDateChooser calenderfecha;
    private javax.swing.JComboBox<String> cmbArea;
    private javax.swing.JPanel head;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lblDeporte;
    private javax.swing.JLabel nombre1;
    private com.github.lgooddatepicker.components.TimePicker txtfinal;
    private com.github.lgooddatepicker.components.TimePicker txtinicio;
    private javax.swing.JTextField txtocasion;
    private javax.swing.JTextField txtreservacion;
    // End of variables declaration//GEN-END:variables
}

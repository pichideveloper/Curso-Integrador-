/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enlaces;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author frixi
 */
public class CConexion {
    Connection  conectar =  null ;
   String usuario = "root" ;
   String contraseña ="apolitopichi";
   String  bd = "BD_Club";
   String ip = "localhost";
   String puerto = "3306";
   
   String cadena = "jdbc:mysql://"+ip+":"+puerto+"/"+bd ;
   
   public  Connection  establecerConexion  () {
       try {
           Class.forName("com.mysql.cj.jdbc.Driver");

           conectar = DriverManager.getConnection(cadena,usuario,contraseña);           
       }catch(Exception e) {
           JOptionPane.showMessageDialog(null,"no se conecto a la base de datos, error"+e.toString());
       }
       return conectar;
}
  public void cerrarConexion() {
        try {
            if (conectar != null && !conectar.isClosed()) {
                conectar.close();
                JOptionPane.showMessageDialog(null, "Conexión cerrada.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error sql: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

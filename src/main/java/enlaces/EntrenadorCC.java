/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enlaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

/**
 *
 * @author frixi
 */
public class EntrenadorCC {
    private CConexion conexion;
    public EntrenadorCC() {
        conexion = new CConexion();
    }
    public void agregarEntrenador(int dni,String nombre, String apellido, int edad, String deporte, double sueldo, int tiempo,double sueldo_Final) {
    String sql = "INSERT INTO TablaEntrenadores (dni ,nombre, apellido, edad, deporte, sueldo, tiempo,sueldo_Final) VALUES (?,?, ?, ?, ?, ?, ?, ?)";
    Connection conn = conexion.establecerConexion();

    try (PreparedStatement statement = conn.prepareStatement(sql)) {
        statement.setInt(1, dni);
        statement.setString(2, nombre);
        statement.setString(3, apellido);
        statement.setInt(4, edad);
        statement.setString(5, deporte);
        statement.setDouble(6, sueldo); 
        statement.setInt(7, tiempo);
        statement.setDouble(8, sueldo_Final);

        statement.executeUpdate();

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al agregar miembro: " + e.getMessage());
  
    } 
    
   
}
    public boolean eliminarEntrenador(int id) {
    String sql = "DELETE FROM TablaEntrenadores WHERE ID = ?";

    CConexion con = new CConexion();
    Connection ELIMINAR1 = con.establecerConexion();

    try (PreparedStatement pst = ELIMINAR1.prepareStatement(sql)) {
        pst.setInt(1, id);
        int filasAfectadas = pst.executeUpdate(); 
        return filasAfectadas > 0; 
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "error al eliminar" + e.getMessage());
        return false; 
    }
}

    
    public boolean actualizarDato(int id_entrenador, String columna, String nuevoValor) {
    String sql = "UPDATE TablaEntrenadores SET " + columna + " = ? WHERE ID = ?";
    CConexion con = new CConexion();
    Connection conexionACTUALIZAR = con.establecerConexion();

    try (PreparedStatement pst = conexionACTUALIZAR.prepareStatement(sql)) {
        pst.setString(1, nuevoValor);
        pst.setInt(2, id_entrenador);

        int filasAfectadas = pst.executeUpdate();
        return filasAfectadas > 0; 
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar dato: " + e.getMessage());
        return false;
    }
}
  public boolean existeDNI(String dni) {
    String sql = "SELECT COUNT(*) AS total FROM TablaEntrenadores WHERE dni = ?";
    Connection conn = conexion.establecerConexion();

    try (PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, dni);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt("total") > 0; 
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al verificar DNI: " + e.getMessage());
    }
    return false;
}   
    
    public boolean eliminarTodos() {
    String sql = "DELETE FROM TablaEntrenadores";
    try (Connection conn = conexion.establecerConexion();  
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected > 0;  
    } catch (SQLException e) {
        System.out.println("Error al eliminar todos: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}
}

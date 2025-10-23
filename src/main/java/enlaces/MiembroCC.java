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
public class MiembroCC {
    private CConexion conexion;

    public MiembroCC() {
        conexion = new CConexion();
    }
   

    public void agregarMiembro(int dni,String nombre, String apellido, int edad, String deporte, String membresia, int tiempo,double mensualidad) {
        
        
    String sql = "INSERT INTO TablaMiembros (dni,nombre, apellido, edad, deporte, membresia, tiempo,mensualidad) VALUES (?,? , ?, ?, ?, ?, ?, ?)";
    Connection conn = conexion.establecerConexion();

    try (PreparedStatement statement = conn.prepareStatement(sql)) {
        statement.setInt(1, dni);
        statement.setString(2, nombre);
        statement.setString(3, apellido);
        statement.setInt(4, edad);
        statement.setString(5, deporte);
        statement.setString(6, membresia.toLowerCase()); 
        statement.setInt(7, tiempo);
        statement.setDouble(8, mensualidad);

        statement.executeUpdate();
    } catch (SQLException e) {
        if (e.getMessage().contains("duplicado")) { 
            JOptionPane.showMessageDialog(null, "dni registrado con anterioridad");
        } else {
            JOptionPane.showMessageDialog(null, "error al agregar miembro: " + e.getMessage());
        }
    } 
}
    
public boolean eliminarMiembro(int id_miembro) {
    String sql = "DELETE FROM TablaMiembros WHERE ID = ?";

    CConexion con = new CConexion();
    Connection ELIMINAR1 = con.establecerConexion();

    try (PreparedStatement pst = ELIMINAR1.prepareStatement(sql)) {
        pst.setInt(1, id_miembro);
        int filasAfectadas = pst.executeUpdate(); 
        return filasAfectadas > 0; 
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "error al eliminar" + e.getMessage());
        return false; 
    }
}

 public boolean actualizarDato(int id_miembro, String columna, String nuevoValor) {
    String sql = "UPDATE TablaMiembros SET " + columna + " = ? WHERE ID = ?";
    CConexion con = new CConexion();
    Connection conexionACTUALIZAR = con.establecerConexion();

    try (PreparedStatement pst = conexionACTUALIZAR.prepareStatement(sql)) {
        pst.setString(1, nuevoValor);
        pst.setInt(2, id_miembro);

        int filasAfectadas = pst.executeUpdate();
        return filasAfectadas > 0; 
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "error al actualizar dato: " + e.getMessage());
        return false;
    }
} 
 public boolean existeDNI(String dni) {
    String sql = "SELECT COUNT(*) AS total FROM TablaMiembros WHERE dni = ?";
    Connection conn = conexion.establecerConexion();

    try (PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, dni);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            return rs.getInt("total") > 0; 
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "error al verificar dni: " + e.getMessage());
    }
    return false;
}
 
public boolean eliminarTodos() {
    String sql = "DELETE FROM TablaMiembros";
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

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
   
public boolean agregarMiembro(int dni, String nombre, String apellido, int edad, String deporte, String membresia, int tiempo, double mensualidad) {
        String sql = "INSERT INTO TablaMiembros (dni, nombre, apellido, edad, deporte, membresia, tiempo, mensualidad) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = new CConexion().establecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, dni);
            pstmt.setString(2, nombre);
            pstmt.setString(3, apellido);
            pstmt.setInt(4, edad);
            pstmt.setString(5, deporte);
            pstmt.setString(6, membresia);
            pstmt.setInt(7, tiempo);
            pstmt.setDouble(8, mensualidad);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println(" Error al agregar miembro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

public boolean eliminarMiembro(int id) {
        String sql = "DELETE FROM TablaMiembros WHERE id = ?";
        try (Connection conn = new CConexion().establecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println(" Error al eliminar miembro: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


 public boolean actualizarDato(int id, String columna, String nuevoValor) {
        String[] columnasPermitidas = {"dni", "nombre", "apellido", "edad", "deporte", "membresia", "tiempo", "mensualidad"};
        boolean columnaValida = false;

        for (String c : columnasPermitidas) {
            if (c.equalsIgnoreCase(columna)) {
                columnaValida = true;
                break;
            }
        }

        if (!columnaValida) {
            System.out.println("️ Columna no válida: " + columna);
            return false;
        }

        String sql = "UPDATE TablaMiembros SET " + columna + " = ? WHERE id = ?";
        try (Connection conn = new CConexion().establecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuevoValor);
            pstmt.setInt(2, id);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println(" Error al actualizar dato: " + e.getMessage());
            e.printStackTrace();
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

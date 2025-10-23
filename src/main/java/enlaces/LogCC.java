/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enlaces;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import util.Sesion;
/**
 *
 * @author frixi
 */
public class LogCC {
    private CConexion conexion;

    public LogCC() {
        this.conexion = new CConexion();
    }

    public void insertarLog(String accion, String detalle) {
        if (!Sesion.estaLogueado()) {
            return;  
        }
        String usuario = Sesion.getUsuarioActual();
        String sql = "INSERT INTO TablaLogs (Usuario, Accion, Detalle) VALUES (?, ?, ?)";
        try (Connection conn = conexion.establecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario);
            pstmt.setString(2, accion);
            pstmt.setString(3, detalle != null ? detalle : "");
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error al insertar log: " + e.getMessage());  
        }
    }
    /*
    public static void main(String[] args) {
    Sesion.setSesion("admin", "admin");  // Simula login
    LogCC logDAO = new LogCC();
    logDAO.insertarLog("Prueba", "Solo para test");
    System.out.println("Log insertado! Chequea BD.");
*/
}


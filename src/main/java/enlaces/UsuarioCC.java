/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enlaces;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author frixi
 */
public class UsuarioCC {
    private CConexion conexion;

    public UsuarioCC() {
        this.conexion = new CConexion();
    }

    public String validarUsuario(String usuario, String contraseña) {
        String sql = "SELECT Rol FROM TablaUsuarios WHERE Usuario = ? AND Contraseña = ?";
        try (Connection conn = conexion.establecerConexion();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, usuario);
            pstmt.setString(2, contraseña);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("Rol");  
            }
        } catch (SQLException e) {
            System.out.println("Error en validarUsuario: " + e.getMessage());  
        }
        return null;  
    }
}

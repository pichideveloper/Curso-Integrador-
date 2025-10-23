/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enlaces;

import javax.swing.SwingUtilities;
import util.LogsViewerFrame;
import util.Sesion;

/**
 *
 * @author frixi
 */
public class apptesteo {
    
    /*
    public static void main(String[] args) {
    UsuarioCC usuarioDAO = new UsuarioCC();
    String rol = usuarioDAO.validarUsuario("admin", "admin123");
    System.out.println("Rol: " + rol);  // Debe imprimir "admin"
}
    */
    
    /*
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Sesion.setSesion("admin", "admin");  // Simula admin
            new LogsViewerFrame().setVisible(true);
        });
    }
*/
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Sesion.setSesion("admin", "admin");
            new LogsViewerFrame().setVisible(true);
        });
    }
}

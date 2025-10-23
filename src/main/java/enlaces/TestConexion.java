/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enlaces;

/**
 *
 * @author frixi
 */
public class TestConexion {
    public static void main(String[] args) {
        CConexion conexion = new CConexion();
        if (conexion.establecerConexion() != null) {
            System.out.println("Conexión establecida con éxito.");
        } else {
            System.out.println("Error al conectar con la base de datos.");
        }
    }
}

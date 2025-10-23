/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

/**
 *
 * @author frixi
 */
public class Sesion {
    private static String usuarioActual = null;
    private static String rolActual = null;

    public static void setSesion(String usuario, String rol) {
        usuarioActual = usuario;
        rolActual = rol;
    }

    public static String getUsuarioActual() {
        return usuarioActual;
    }

    public static String getRolActual() {
        return rolActual;
    }

    public static void cerrarSesion() {
        usuarioActual = null;
        rolActual = null;
    }

    public static boolean estaLogueado() {
        return usuarioActual != null;
    }
}

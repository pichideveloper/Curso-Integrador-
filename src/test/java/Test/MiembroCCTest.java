/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Test;

import enlaces.MiembroCC;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author frixi
 */

public class MiembroCCTest {
    private MiembroCC dao;

    @BeforeEach
    void setUp() {
        dao = new MiembroCC();
        
        dao.eliminarMiembro(99999999);
        dao.eliminarMiembro(74185296);
        dao.eliminarMiembro(11111111);
    }

    @AfterEach
    void tearDown() {
        dao.eliminarMiembro(99999999);
        dao.eliminarMiembro(74185296);
        dao.eliminarMiembro(11111111);
    }

    @Test
    void testAgregarMiembro_Exito() {
        System.out.println("🧪 Probando agregar miembro...");
        int dni = (int) (Math.random() * 90000000) + 10000000; 
        boolean result = dao.agregarMiembro(
                dni, "Carlos", "Ramirez", 29, "futbol", "oro", 6, 150.00
        );
        assertTrue(result, "El miembro deberia agregarse correctamente");
    }

    @Test
    void testAgregarMiembro_DuplicadoDNI() {
        System.out.println(" Probando agregar miembro duplicado...");
        dao.agregarMiembro(99999999, "Pedro", "López", 30, "tenis", "plata", 3, 200.00);
        boolean result = dao.agregarMiembro(99999999, "Pedro", "Duplicado", 30, "tenis", "plata", 3, 200.00);
        assertFalse(result, "No deberia poder agregarse un DNI duplicado");
    }

    @Test
    void testActualizarDato_Exito() {
        System.out.println(" Probando actualizar miembro...");
        dao.agregarMiembro(11111111, "Luis", "Perez", 28, "futbol", "bronce", 2, 100.00);
        boolean result = dao.actualizarDato(11111111, "nombre", "LuisEditado");
        assertTrue(result, "El nombre deberia haberse actualizado correctamente");
    }

    @Test
    void testEliminarMiembro_Exito() {
        System.out.println(" Probando eliminar miembro...");
        dao.agregarMiembro(99999999, "Ana", "Torres", 32, "basquet", "plata", 4, 250.00);
        boolean eliminado = dao.eliminarMiembro(99999999);
        assertTrue(eliminado, "El miembro deberia haberse eliminado correctamente");
    }

    @Test
    void testEliminarMiembro_NoExiste() {
        System.out.println("Probando eliminar miembro inexistente...");
        boolean eliminado = dao.eliminarMiembro(123456789);
        assertFalse(eliminado, "No deberia eliminar un miembro que no existe");
    }
}

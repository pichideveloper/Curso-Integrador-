/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package test;

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
    }

    @AfterEach
    void tearDown() {
        
        dao.eliminarMiembro(99999999);  
    }

    @Test
    void testAgregarMiembro_Exito() {
        boolean result = dao.agregarMiembro(12345678, "Test Juan", "Pérez", 25, "Futbol", "Oro", 5, 500.0);
        assertTrue(result);  
        assertTrue(dao.existeDNI("12345678"));  
    }

    @Test
    void testAgregarMiembro_DuplicadoDNI() {
        dao.agregarMiembro(87654321, "Test Ana", "García", 30, "Tenis", "Plata", 3, 300.0);
        boolean result = dao.agregarMiembro(87654321, "Duplicado", "Error", 30, "Tenis", "Plata", 3, 300.0);  // Mismo DNI
        assertFalse(result);  
    }

    @Test
    void testExisteDNI_NoExiste() {
        assertFalse(dao.existeDNI("99999999"));  
    }

    @Test
    void testEliminarMiembro_Exito() {
        dao.agregarMiembro(55555555, "Test Eliminar", "User", 28, "Futbol", "Bronce", 2, 200.0);
        boolean result = dao.eliminarMiembro(55555555);
        assertTrue(result);  
        assertFalse(dao.existeDNI("55555555"));  
    }

    @Test
    void testActualizarDato_Exito() {
        dao.agregarMiembro(11111111, "Test Update", "User", 25, "Futbol", "Oro", 5, 500.0);
        boolean result = dao.actualizarDato(11111111, "nombre", "Updated Name");
        assertTrue(result);  
    }

    @Test
    void testEliminarTodos_Exito() {
        dao.agregarMiembro(77777777, "Test All", "Delete", 30, "Basquet", "Plata", 4, 300.0);
        boolean result = dao.eliminarTodos();
        assertTrue(result);  
    }
}

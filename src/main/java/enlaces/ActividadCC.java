/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enlaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author frixi
 */
public class ActividadCC {
    private CConexion conexion;

    public ActividadCC() {
        conexion = new CConexion();
    }
    public void agregarActividad(String Ocasión, String deporte, String horaInicio, String horaFin, String fecha, String Área, String dni) {
    String sql = "INSERT INTO TablaActividad (Ocasión, deporte, horaInicio, horaFin, fecha, Área, reservacion, nombre, apellido) VALUES (?,?,?,?,?,?,?,?,?)";

    String sqlEntrenador = "SELECT nombre, apellido FROM TablaEntrenadores WHERE dni = ?";
    Connection conn = conexion.establecerConexion();

    try {
        
        String nombre = null, apellido = null;
        try (PreparedStatement pst = conn.prepareStatement(sqlEntrenador)) {
            pst.setString(1, dni);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                nombre = rs.getString("nombre");
                apellido = rs.getString("apellido");
            } else {
                JOptionPane.showMessageDialog(null, "no se encontro entrenador con ese dni: " + dni);
                return;
            }
        }

      
        try (PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setString(1, Ocasión);
            statement.setString(2, deporte);
            statement.setString(3, horaInicio);
            statement.setString(4, horaFin);
            statement.setString(5, fecha);
            statement.setString(6, Área);
            statement.setString(7, dni);
            statement.setString(8, nombre);
            statement.setString(9, apellido); 
            statement.executeUpdate();
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al agregar actividad: " + e.getMessage());
    }
}

    
    public boolean eliminarActividad(int id) {
    String sql = "DELETE FROM TablaActividad WHERE ID = ?";

 Connection conn = conexion.establecerConexion();

    try (PreparedStatement statement = conn.prepareStatement(sql)) {
        statement.setInt(1, id);
        

        int filasAfectadas = statement.executeUpdate(); 
        if (filasAfectadas > 0) {
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "no se encontró un evento con ese id ");
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "error al eliminar" + e.getMessage());
        
    }
    return false; 
    }
    
    
    
    
    public boolean eliminarTodos() {
    String sql = "DELETE FROM TablaActividad";
    CConexion con = new CConexion();
    Connection eliminarTODO = con.establecerConexion();
    
    try (PreparedStatement pst = eliminarTODO.prepareStatement(sql)) {
        int filasAfectadas = pst.executeUpdate();
        return filasAfectadas > 0; 
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "error al eliminar todos " + e.getMessage());
        return false;
    }
}
    
    
 public boolean actualizarDato(int id, String columna, String nuevoValor) {
    List<String> columnasNoEditables = Arrays.asList("id", "nombre", "apellido", "reservacion");
    if (columnasNoEditables.contains(columna)) {
        JOptionPane.showMessageDialog(null, "no se puede editar la columna: " + columna);
        return false;
    }

    String sql = "UPDATE TablaActividad SET " + columna + " = ? WHERE id = ?";
    Connection conn = conexion.establecerConexion();

    try (PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, nuevoValor);
        pst.setInt(2, id);
        return pst.executeUpdate() > 0;
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al actualizar: " + e.getMessage());
    }
    return false;
}
    
    
    
    
    public String[] buscarEntenadorPorDNI(String dni) {
    String sql = "SELECT nombre, apellido, dni, deporte FROM TablaEntrenadores WHERE dni = ?";  
    Connection conn = conexion.establecerConexion();

    try (PreparedStatement pst = conn.prepareStatement(sql)) {
        pst.setString(1, dni); 
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            return new String[]{
                rs.getString("nombre"),   
                rs.getString("apellido"),  
                rs.getString("dni"),       
                rs.getString("deporte")    
            };
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "Error al buscar ese entrenador: " + e.getMessage());
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "El DNI debe ser un valor válido");
    }

    return null; 
}
public boolean verificarFechaUnica(String fecha) {
    String sql = "SELECT COUNT(*) FROM TablaActividad WHERE fecha = ?";
    Connection conn = conexion.establecerConexion();

    try (PreparedStatement statement = conn.prepareStatement(sql)) {
        statement.setString(1, fecha);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0; 
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "error al verificar fecha: " + e.getMessage());
    }
    return false;
}

public boolean verificarConflictoHorario(String fecha, String horaInicio, String horaFin) {
    String sql = "SELECT COUNT(*) FROM TablaActividad " +
                 "WHERE fecha = ? " +
                 "AND (horaInicio < ? AND horaFin > ?)";
    Connection conn = conexion.establecerConexion();

    try (PreparedStatement statement = conn.prepareStatement(sql)) {
        statement.setString(1, fecha);
        statement.setString(2, horaFin);
        statement.setString(3, horaInicio);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0; 
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(null, "error al verificar choques de hora: " + e.getMessage());
    }
    return false;
}
    
}

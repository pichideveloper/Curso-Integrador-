/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.projectofinalpoo;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.SwingUtilities;
import util.LoginFrame;


/**
 *
 * @author stefa
 */
public class ProjectoFinalPOO {

    public static void main(String[] args) {

        /*
        Menu menucito= new Menu();
        menucito.setVisible(true);
        menucito.setLocationRelativeTo(null);    

         */
        FlatLightLaf.install();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));

        
    }
}

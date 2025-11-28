/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.projectofinalpoo;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.SwingUtilities;
import util.LoginFrame;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 *
 * @author stefa
 */
public class ProjectoFinalPOO {
private static final Logger logger = LogManager.getLogger(ProjectoFinalPOO.class);

    public static void main(String[] args) {

        /*
        Menu menucito= new Menu();
        menucito.setVisible(true);
        menucito.setLocationRelativeTo(null);    

         */
        FlatLightLaf.install();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        logger.info("El sistema ha iniciado correctamente.");

        
    }
}

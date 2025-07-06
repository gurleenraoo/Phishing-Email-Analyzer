package ui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.BorderLayout;

/*
 * The SplashScreen class displays a temporary splash screen for the application.
 * When an instance of SplashScreen is created, it opens a JFrame containing an image
 * loaded . 
 */

public class SplashScreen {
    /* 
    * Constructs a new SplashScreen and displays it immediately. 
    * EFFECTS: displays a splash screen with an image
    */

    public SplashScreen() {
        displayScreen();
    }

    /*
    * Creates and displays a splash screen.
    * MODIFIES: this
    * EFFECTS: creates and displays a JFrame containing a JLabel with an image,
    *          waits for 6 seconds, then disposes the frame
    */

    private void displayScreen() {
        JFrame splashFrame = new JFrame();
        JLabel splashLabel = new JLabel(new ImageIcon("data/SplashScreen.jpg"));
        splashFrame.getContentPane().add(splashLabel, BorderLayout.CENTER);
        splashFrame.setSize(1024,1024);
        splashFrame.setLocationRelativeTo(null);
        splashFrame.setVisible(true);

        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            // pass
        }

        splashFrame.dispose();
    }
    
}

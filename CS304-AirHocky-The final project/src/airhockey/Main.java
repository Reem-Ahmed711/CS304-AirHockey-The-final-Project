package airhockey;

import airhockey.introduction.Introduction;
import javax.swing.*;

public class  Main {
    public static void main(String[] args) {
        System.out.println(" Starting Air Hockey 2D - Final Project...");
        System.out.println("Developed for CS304 - Computer Graphics");
        System.out.println("Cairo University - Faculty of Science Department CS");

        try {
            // Set native look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Set better font rendering
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

            // Show introduction first
            Introduction.show();

        } catch (Exception e) {
            System.err.println("Error loading introduction: " + e.getMessage());
            e.printStackTrace();

            // Start game directly if introduction fails
            startGame();
        }
    }

    private static void startGame() {
        SwingUtilities.invokeLater(() -> {
            try {
                airhockey.game.GameEngine engine = new airhockey.game.GameEngine();
                engine.start();
            } catch (Exception e) {
                System.err.println("Failed to start game: " + e.getMessage());
                JOptionPane.showMessageDialog(null,
                        "Failed to start game: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
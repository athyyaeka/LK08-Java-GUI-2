package LK08;

import javax.swing.SwingUtilities;

public class MainApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Tampilkan form login terlebih dahulu
            new LoginForm();
        });
    }
}


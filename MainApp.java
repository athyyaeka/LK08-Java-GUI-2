package LK08;

// LibrarySystem.java
import javax.swing.SwingUtilities;

public class MainApp {
    public static void main(String[] args) {
        // Memastikan GUI dijalankan pada Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Tampilkan form login terlebih dahulu
            new LoginForm();
        });
    }
}


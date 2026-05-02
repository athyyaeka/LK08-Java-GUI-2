package LK08;

// LoginForm.java
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class LoginForm extends JFrame {
    private JTextField nipField;
    private JPasswordField namaField; // Menggunakan JPasswordField meskipun ini 'nama'

    public LoginForm() {
        setTitle("Login Pegawai Perpustakaan SMP");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null); // Tampil di tengah layar
        initComponents();
        setVisible(true);
    }

    private void initComponents() {
        // Menggunakan GridBagLayout untuk tata letak yang rapi dan terpusat
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("LOGIN PEGAWAI", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Input NIP
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("NIP:"), gbc);
        gbc.gridx = 1;
        nipField = new JTextField(15);
        add(nipField, gbc);

        // Input Nama (sebagai 'password' untuk login sederhana)
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Nama:"), gbc);
        gbc.gridx = 1;
        namaField = new JPasswordField(15);
        add(namaField, gbc);

        // Tombol Login
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton loginButton = new JButton("Login");
        // Event Listener untuk tombol Login
        loginButton.addActionListener(e -> attemptLogin()); //
        add(loginButton, gbc);
    }

    /**
     * Event Listener: Mencoba melakukan login pegawai.
     * Memvalidasi NIP dan Nama terhadap data di pegawai.txt.
     */
    private void attemptLogin() { //
        String nip = nipField.getText().trim();
        String nama = new String(namaField.getPassword()).trim();

        if (nip.isEmpty() || nama.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIP dan Nama harus diisi.", "Error Login", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Operasi File I/O: Baca daftar Pegawai
            List<Pegawai> daftarPegawai = FileManager.loadList(FileManager.PEGAWAI_FILE, Pegawai::fromFileString); //

            boolean loginSuccess = false;
            for (Pegawai p : daftarPegawai) {
                // Validasi: NIP harus sama dan Nama harus sama (case-insensitive)
                if (p.getId().equals(nip) && p.getNama().equalsIgnoreCase(nama)) {
                    loginSuccess = true;
                    break;
                }
            }

            if (loginSuccess) {
                JOptionPane.showMessageDialog(this, "Login berhasil sebagai " + nama + "!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Tutup form login 
                new MainFrame(nip, nama); // Buka menu utama
            } else {
                JOptionPane.showMessageDialog(this, "NIP atau Nama salah.", "Error Login", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException ex) {
            // Penanganan Exception I/O File
            JOptionPane.showMessageDialog(this, "Error saat membaca file pegawai.txt: " + ex.getMessage(), "Kesalahan I/O File", JOptionPane.ERROR_MESSAGE); //
        }
    }
}

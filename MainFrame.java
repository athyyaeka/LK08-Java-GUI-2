package LK08;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private final String loggedInPegawai;

    // CONSTRUCTOR SESUAI DENGAN LAPORAN ASLI KAMU (Menerima NIP dan Nama)
    public MainFrame(String nip, String nama) {
        this.loggedInPegawai = nama + " (NIP: " + nip + ")";
        setTitle("Sistem Perpustakaan SMP - Logged in as: " + loggedInPegawai);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null); // Tampil di tengah layar
        
        try {
            initComponents();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat panel: " + e.getMessage());
            e.printStackTrace();
        }
        
        // INI DIA PENYELAMAT KITA! Baris yang bikin layarnya benar-benar muncul!
        setVisible(true); 
    }

    private void initComponents() {
        // 1. Menu Bar (Untuk Logout)
        JMenuBar menuBar = new JMenuBar();
        JMenu menuSistem = new JMenu("Sistem");
        JMenuItem menuLogout = new JMenuItem("Logout");
        
        menuLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); // Tutup MainFrame
                new LoginForm(); // Buka kembali halaman login
            }
        });
        
        menuSistem.add(menuLogout);
        menuBar.add(menuSistem);
        setJMenuBar(menuBar);

        // 2. Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        tabbedPane.addTab("1. Data Siswa", new SiswaPanel());
        tabbedPane.addTab("2. Data Buku", new BukuPanel());
        tabbedPane.addTab("3. Data Pegawai", new PegawaiPanel());
        tabbedPane.addTab("4. Transaksi", new TransaksiPanel());
        tabbedPane.addTab("5. Laporan", new LaporanPanel());

        add(tabbedPane);
    }
}
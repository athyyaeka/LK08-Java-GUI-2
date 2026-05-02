package LK08;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class TransaksiPanel extends JPanel {
    private JTextField nisPinjamField, kodeBukuPinjamField;
    private JTextField kodeTransKembaliField;
    private JTable pinjamTable, kembaliTable;
    private DefaultTableModel pinjamTableModel, kembaliTableModel;
    private List<Transaksi> transaksiList;
    private List<Siswa> siswaList;
    private List<Buku> bukuList;
    private final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TransaksiPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        loadAllData();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        
        splitPane.setTopComponent(createPeminjamanPanel());
        splitPane.setBottomComponent(createPengembalianPanel());
        
        add(splitPane, BorderLayout.CENTER);

        refreshTables();
    }

    private void loadAllData() { //
        try {
            transaksiList = FileManager.loadList(FileManager.TRANSAKSI_FILE, Transaksi::fromFileString);
            siswaList = FileManager.loadList(FileManager.SISWA_FILE, Siswa::fromFileString);
            bukuList = FileManager.loadList(FileManager.BUKU_FILE, Buku::fromFileString);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saat memuat data Transaksi: " + ex.getMessage(), "Kesalahan I/O File", JOptionPane.ERROR_MESSAGE); //
            transaksiList = List.of(); 
            siswaList = List.of();
            bukuList = List.of();
        }
    }

    private void refreshTables() {
        loadAllData();

        pinjamTableModel.setRowCount(0);
        transaksiList.stream()
                .filter(t -> t.getStatus() == 0)
                .forEach(t -> pinjamTableModel.addRow(new Object[] {
                    t.getKodeTrans(), t.getNis(), t.getKodeBuku(), t.getTglPinjam()
                }));
                
        // Refresh Tabel Pengembalian (Semua Transaksi)
        kembaliTableModel.setRowCount(0);
        transaksiList.stream()
                .forEach(t -> kembaliTableModel.addRow(new Object[] {
                    t.getKodeTrans(), t.getNis(), t.getKodeBuku(), t.getTglPinjam(), t.getTglKembali(), t.getStatusText()
                }));
    }

    // --- Panel Peminjaman ---
    private JPanel createPeminjamanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Peminjaman Buku (Maks. 2 Buku)"));
        
        // Form Peminjaman (NORTH)
        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.add(new JLabel("NIS Siswa:"));
        nisPinjamField = new JTextField();
        form.add(nisPinjamField);

        form.add(new JLabel("Kode Buku:"));
        kodeBukuPinjamField = new JTextField();
        form.add(kodeBukuPinjamField);
        
        JButton pinjamBtn = new JButton("Proses Pinjam");
        pinjamBtn.addActionListener(e -> pinjamBuku()); //
        form.add(pinjamBtn);

        panel.add(form, BorderLayout.NORTH);
        
        // Tabel Transaksi Aktif (CENTER)
        pinjamTableModel = new DefaultTableModel(new String[] {"Kode Trans", "NIS", "Kode Buku", "Tgl Pinjam"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        pinjamTable = new JTable(pinjamTableModel);
        panel.add(new JScrollPane(pinjamTable), BorderLayout.CENTER);
        
        return panel;
    }

    // --- Panel Pengembalian ---
    private JPanel createPengembalianPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Pengembalian Buku"));

        // Form Pengembalian (NORTH)
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Kode Transaksi:"));
        kodeTransKembaliField = new JTextField(10);
        form.add(kodeTransKembaliField);
        
        JButton kembaliBtn = new JButton("Proses Kembali");
        kembaliBtn.addActionListener(e -> kembaliBuku()); //
        form.add(kembaliBtn);
        
        panel.add(form, BorderLayout.NORTH);
        
        // Tabel Semua Transaksi (CENTER)
        kembaliTableModel = new DefaultTableModel(new String[] {"Kode Trans", "NIS", "Kode Buku", "Pinjam", "Kembali", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        kembaliTable = new JTable(kembaliTableModel);
        panel.add(new JScrollPane(kembaliTable), BorderLayout.CENTER);
        
        return panel;
    }

    // --- Event Listener Implementations ---
    private void pinjamBuku() { //
        String nis = nisPinjamField.getText().trim();
        String kodeBuku = kodeBukuPinjamField.getText().trim();
        
        if (nis.isEmpty() || kodeBuku.isEmpty()) {
            JOptionPane.showMessageDialog(this, "NIS dan Kode Buku harus diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // 1. Validasi Siswa dan Buku
            boolean isSiswaValid = siswaList.stream().anyMatch(s -> s.getId().equals(nis));
            boolean isBukuValid = bukuList.stream().anyMatch(b -> b.getKode().equals(kodeBuku));

            if (!isSiswaValid) {
                JOptionPane.showMessageDialog(this, "NIS tidak terdaftar!", "Error Pinjam", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!isBukuValid) {
                JOptionPane.showMessageDialog(this, "Kode Buku tidak terdaftar!", "Error Pinjam", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Aturan Batas Maksimal Pinjam (Status 0: Belum Kembali)
            long count = transaksiList.stream()
                               .filter(t -> t.getNis().equals(nis) && t.getStatus() == 0)
                               .count();

            if (count >= 2) {
                JOptionPane.showMessageDialog(this, "Siswa " + nis + " sudah meminjam " + count + " buku. Maksimal 2 buku!", "Batas Pinjam Terlampaui", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. Buat Transaksi Baru
            String kodeT = "T" + (transaksiList.size() + 1); // Kode Transaksi Otomatis
            String tglPinjam = LocalDate.now().format(DATE_FORMAT);

            Transaksi newTrans = new Transaksi(kodeT, nis, kodeBuku, tglPinjam, "", 0); 
            
            transaksiList.add(newTrans);
            
            // Operasi I/O File: Simpan List Transaksi
            FileManager.saveList(FileManager.TRANSAKSI_FILE, transaksiList); //

            JOptionPane.showMessageDialog(this, "Peminjaman berhasil! Kode Trans: " + kodeT, "Sukses Pinjam", JOptionPane.INFORMATION_MESSAGE);
            nisPinjamField.setText("");
            kodeBukuPinjamField.setText("");
            refreshTables();
            
        } catch (IOException ex) {
            // Penanganan Exception I/O File
            JOptionPane.showMessageDialog(this, "Error saat proses peminjaman: " + ex.getMessage(), "Kesalahan I/O File", JOptionPane.ERROR_MESSAGE); //
        }
    }

    private void kembaliBuku() { //
        String kodeTrans = kodeTransKembaliField.getText().trim();
        
        if (kodeTrans.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Kode Transaksi harus diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Transaksi targetTrans = null;
            for (Transaksi t : transaksiList) {
                if (t.getKodeTrans().equals(kodeTrans)) {
                    targetTrans = t;
                    break;
                }
            }

            if (targetTrans == null) {
                JOptionPane.showMessageDialog(this, "Kode Transaksi tidak ditemukan!", "Error Kembali", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (targetTrans.getStatus() == 1) {
                JOptionPane.showMessageDialog(this, "Buku sudah dikembalikan sebelumnya.", "Peringatan", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Update status dan tanggal kembali
            targetTrans.setStatus(1);
            targetTrans.setTglKembali(LocalDate.now().format(DATE_FORMAT));
            
            FileManager.saveList(FileManager.TRANSAKSI_FILE, transaksiList); //

            JOptionPane.showMessageDialog(this, "Pengembalian buku sukses! Kode Trans: " + kodeTrans, "Sukses Kembali", JOptionPane.INFORMATION_MESSAGE);
            kodeTransKembaliField.setText("");
            refreshTables();
            
        } catch (IOException ex) {
            // Penanganan Exception I/O File
            JOptionPane.showMessageDialog(this, "Error saat proses pengembalian: " + ex.getMessage(), "Kesalahan I/O File", JOptionPane.ERROR_MESSAGE); //
        }
    }
}


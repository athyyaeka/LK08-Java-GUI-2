package LK08;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LaporanPanel extends JPanel {
    private JTable reportTable;
    private DefaultTableModel reportTableModel;
    private final int DUE_DAYS = 7; // Asumsi batas waktu peminjaman adalah 7 hari

    public LaporanPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Label Header
        JLabel header = new JLabel("Laporan Buku Jatuh Tempo & Belum Kembali", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        add(header, BorderLayout.NORTH);

        // 2. Setup Tabel Laporan
        reportTableModel = new DefaultTableModel(
            new String[] {"NIS", "Kode Transaksi", "Kode Buku", "Tgl Pinjam", "Tgl Jatuh Tempo", "Status Keterlambatan"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        reportTable = new JTable(reportTableModel);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        // 3. Tombol Segarkan (Refresh)
        JButton refreshBtn = new JButton("Segarkan Data Laporan");
        refreshBtn.addActionListener(e -> showOverdueReport());
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(refreshBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // 4. Tampilkan Laporan Pertama Kali
        showOverdueReport();
    }

    private void showOverdueReport() {
        reportTableModel.setRowCount(0); // Bersihkan tabel sebelum dimuat ulang
        try {
            // Load data dari file transaksi 
            List<Transaksi> transList = FileManager.loadList(FileManager.TRANSAKSI_FILE, Transaksi::fromFileString);
            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Transaksi t : transList) {
                // Filter: Hanya proses transaksi yang berstatus 0 (Belum Kembali) 
                if (t.getStatus() == 0) { 
                    try {
                        LocalDate tglPinjam = LocalDate.parse(t.getTglPinjam(), formatter);
                        LocalDate tglJatuhTempo = tglPinjam.plusDays(DUE_DAYS);
                        
                        // Menghitung selisih hari antara jatuh tempo dan hari ini
                        long terlambatHari = ChronoUnit.DAYS.between(tglJatuhTempo, today);
                        
                        String statusTerlambat;
                        if (terlambatHari > 0) {
                            statusTerlambat = "Terlambat " + terlambatHari + " Hari!";
                        } else {
                            statusTerlambat = "Masih Masa Pinjam";
                        }

                        // Tambahkan baris ke dalam tabel laporan
                        reportTableModel.addRow(new Object[] {
                            t.getNis(), 
                            t.getKodeTrans(), 
                            t.getKodeBuku(), 
                            t.getTglPinjam(), 
                            tglJatuhTempo.toString(), 
                            statusTerlambat
                        });
                        
                    } catch (Exception e) {
                        // Abaikan jika ada data tanggal yang formatnya korup/salah
                    }
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error memuat data laporan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
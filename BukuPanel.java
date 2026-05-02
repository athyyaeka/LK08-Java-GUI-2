package LK08;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BukuPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField kodeField, judulField, jenisField;
    private List<Buku> dataList = new ArrayList<>();

    public BukuPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(createFormPanel(), BorderLayout.NORTH); 
        topPanel.add(createButtonPanel(), BorderLayout.CENTER); 

        add(topPanel, BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER); 

        loadData();

        table.getSelectionModel().addListSelectionListener(e -> { 
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                fillFormFromTable();
            }
        });
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Header
        JLabel header = new JLabel("Manajemen Data Buku", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(header, gbc);

        // Kode
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Kode Buku:"), gbc);
        gbc.gridx = 1; kodeField = new JTextField(20);
        formPanel.add(kodeField, gbc);

        // Judul
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Judul:"), gbc);
        gbc.gridx = 1; judulField = new JTextField(20);
        formPanel.add(judulField, gbc);

        // Jenis
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Jenis Buku:"), gbc);
        gbc.gridx = 1; jenisField = new JTextField(20);
        formPanel.add(jenisField, gbc);

        return formPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton tambahBtn = new JButton("Tambah");
        JButton updateBtn = new JButton("Edit");
        JButton hapusBtn = new JButton("Hapus");
        JButton clearBtn = new JButton("Bersihkan Form");

        // Event Listeners
        tambahBtn.addActionListener(e -> addData()); 
        updateBtn.addActionListener(e -> updateData()); 
        hapusBtn.addActionListener(e -> deleteData()); 
        clearBtn.addActionListener(e -> clearForm()); 
        
        buttonPanel.add(tambahBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(hapusBtn);
        buttonPanel.add(clearBtn);

        return buttonPanel;
    }

    private JScrollPane createTablePanel() {
        tableModel = new DefaultTableModel(new String[] {"Kode", "Judul", "Jenis Buku"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        return new JScrollPane(table);
    }
  
    private void loadData() { 
        try {
            dataList = FileManager.loadList(FileManager.BUKU_FILE, Buku::fromFileString); 
            refreshTable();
        } catch (IOException ex) {
           
            JOptionPane.showMessageDialog(this, "Error saat memuat data Buku: " + ex.getMessage(), "Kesalahan I/O File", JOptionPane.ERROR_MESSAGE); 
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0); 
        for (Buku b : dataList) {
            tableModel.addRow(new Object[] {b.getKode(), b.getJudul(), b.getJenis()});
        }
    }

    private void addData() { // Event Listener Implementation
        String kode = kodeField.getText().trim();
        String judul = judulField.getText().trim();
        String jenis = jenisField.getText().trim();

        if (kode.isEmpty() || judul.isEmpty() || jenis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua kolom harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Cek duplikasi Kode Buku
        if (dataList.stream().anyMatch(b -> b.getKode().equals(kode))) {
            JOptionPane.showMessageDialog(this, "Error: Kode Buku [" + kode + "] sudah terdaftar!", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Buku baru = new Buku(kode, judul, jenis);
            dataList.add(baru);
            // Operasi I/O File: Simpan List yang sudah diperbarui ke file
            FileManager.saveList(FileManager.BUKU_FILE, dataList); 
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Data Buku berhasil ditambah!");
        } catch (IOException ex) {
            // Penanganan Exception I/O File
            JOptionPane.showMessageDialog(this, "Error saat menyimpan data: " + ex.getMessage(), "Kesalahan I/O File", JOptionPane.ERROR_MESSAGE); 
        }
    }

    private void updateData() { // Event Listener Implementation
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris di tabel yang ingin diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String kodeTarget = (String) tableModel.getValueAt(selectedRow, 0); 
        String judul = judulField.getText().trim();
        String jenis = jenisField.getText().trim();
        
        if (judul.isEmpty() || jenis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi Judul dan Jenis Buku baru!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Cari dan perbarui objek di dataList
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).getKode().equals(kodeTarget)) {
                    // Update hanya Judul dan Jenis
                    dataList.set(i, new Buku(kodeTarget, judul, jenis)); 
                    break;
                }
            }

            // Operasi I/O File: Simpan perubahan ke file
            FileManager.saveList(FileManager.BUKU_FILE, dataList); 
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Data Buku berhasil diupdate!");
        } catch (IOException ex) {
            // Penanganan Exception I/O File
            JOptionPane.showMessageDialog(this, "Error saat menyimpan data: " + ex.getMessage(), "Kesalahan I/O File", JOptionPane.ERROR_MESSAGE); 
        }
    }

    private void deleteData() { // Event Listener Implementation
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dataList.remove(selectedRow);

                // Operasi I/O File: Simpan List tanpa data yang dihapus
                FileManager.saveList(FileManager.BUKU_FILE, dataList); 
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
            } catch (IOException ex) {
                // Penanganan Exception I/O File
                JOptionPane.showMessageDialog(this, "Error saat menghapus data: " + ex.getMessage(), "Kesalahan I/O File", JOptionPane.ERROR_MESSAGE); 
            }
        }
    }
    
    private void fillFormFromTable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            kodeField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            judulField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            jenisField.setText(tableModel.getValueAt(selectedRow, 2).toString());

            kodeField.setEnabled(false); 
        }
    }

    private void clearForm() {
        kodeField.setText("");
        judulField.setText("");
        jenisField.setText("");
        kodeField.setEnabled(true);
        table.clearSelection();
    }
}

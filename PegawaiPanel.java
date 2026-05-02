package LK08;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PegawaiPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nipField, namaField, tglLahirField;
    private List<Pegawai> dataList = new ArrayList<>();

    public PegawaiPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(createFormPanel(), BorderLayout.NORTH);   // Form di paling atas
        topPanel.add(createButtonPanel(), BorderLayout.CENTER); // Tombol tepat di bawah form

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
        JLabel header = new JLabel("Manajemen Data Pegawai", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(header, gbc);

        // NIP
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("NIP:"), gbc);
        gbc.gridx = 1; nipField = new JTextField(20);
        formPanel.add(nipField, gbc);

        // Nama
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Nama:"), gbc);
        gbc.gridx = 1; namaField = new JTextField(20);
        formPanel.add(namaField, gbc);

        // Tanggal Lahir
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Tanggal Lahir (contoh: YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; tglLahirField = new JTextField(20);
        formPanel.add(tglLahirField, gbc);

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
        tableModel = new DefaultTableModel(new String[] {"NIP", "Nama", "Tanggal Lahir"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabel tidak bisa diedit langsung
            }
        };
        table = new JTable(tableModel);
        return new JScrollPane(table);
    }
    
    // --- Data Management Methods ---

    private void loadData() { 
        try {
            dataList = FileManager.loadList(FileManager.PEGAWAI_FILE, Pegawai::fromFileString); 
            refreshTable();
        } catch (IOException ex) {
            // Penanganan Exception I/O File
            JOptionPane.showMessageDialog(this, "Error saat memuat data Pegawai: " + ex.getMessage(), "Kesalahan I/O File", JOptionPane.ERROR_MESSAGE); 
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0); 
        for (Pegawai p : dataList) {
            tableModel.addRow(new Object[] {p.getId(), p.getNama(), p.getTglLahir()});
        }
    }

    private void addData() { // Event Listener Implementation
        String nip = nipField.getText().trim();
        String nama = namaField.getText().trim();
        String tglLahir = tglLahirField.getText().trim();

        if (nip.isEmpty() || nama.isEmpty() || tglLahir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua kolom harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Cek duplikasi NIP
        if (dataList.stream().anyMatch(p -> p.getId().equals(nip))) {
            JOptionPane.showMessageDialog(this, "Error: NIP [" + nip + "] sudah terdaftar!", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Pegawai baru = new Pegawai(nip, nama, tglLahir);
            dataList.add(baru);
            // Operasi I/O File: Simpan List yang sudah diperbarui ke file
            FileManager.saveList(FileManager.PEGAWAI_FILE, dataList); 
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Data Pegawai berhasil ditambah!");
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

        String nipTarget = (String) tableModel.getValueAt(selectedRow, 0); // NIP tidak boleh diubah
        String nama = namaField.getText().trim();
        String tglLahir = tglLahirField.getText().trim();
        
        if (nama.isEmpty() || tglLahir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi Nama dan Tanggal Lahir baru!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Cari dan perbarui objek di dataList
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).getId().equals(nipTarget)) {
                    dataList.set(i, new Pegawai(nipTarget, nama, tglLahir));
                    break;
                }
            }

            // Operasi I/O File: Simpan perubahan ke file
            FileManager.saveList(FileManager.PEGAWAI_FILE, dataList); 
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Data Pegawai berhasil diupdate!");
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
                FileManager.saveList(FileManager.PEGAWAI_FILE, dataList); 
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
            nipField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            namaField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            tglLahirField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            // NIP tidak bisa diedit saat update, jadi di-disable
            nipField.setEnabled(false); 
        }
    }

    private void clearForm() {
        nipField.setText("");
        namaField.setText("");
        tglLahirField.setText("");
        nipField.setEnabled(true);
        table.clearSelection();
    }
}

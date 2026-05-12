package LK08;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class SiswaPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField nisField, namaField, alamatField;
    private List<Siswa> dataList = new ArrayList<>();

    public SiswaPanel() {
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
        JLabel header = new JLabel("Manajemen Data Siswa", JLabel.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        formPanel.add(header, gbc);

        // NIS
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("NIS:"), gbc);
        gbc.gridx = 1; nisField = new JTextField(20);
        formPanel.add(nisField, gbc);

        // Nama
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Nama:"), gbc);
        gbc.gridx = 1; namaField = new JTextField(20);
        formPanel.add(namaField, gbc);

        // Alamat
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Alamat:"), gbc);
        gbc.gridx = 1; alamatField = new JTextField(20);
        formPanel.add(alamatField, gbc);

        return formPanel;
    }

    private JPanel createButtonPanel() { 
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5)); 
    
    JButton tambahBtn = new JButton("Tambah");
    JButton updateBtn = new JButton("Update");
    JButton hapusBtn = new JButton("Hapus");
    JButton clearBtn = new JButton("Clear");

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
        tableModel = new DefaultTableModel(new String[] {"NIS", "Nama", "Alamat"}, 0) {
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
            dataList = FileManager.loadList(FileManager.SISWA_FILE, Siswa::fromFileString); 
            refreshTable();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saat memuat data Siswa: " + ex.getMessage(), "Kesalahan I/O File", JOptionPane.ERROR_MESSAGE); 
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0); 
        for (Siswa s : dataList) {
            tableModel.addRow(new Object[] {s.getId(), s.getNama(), s.getAlamat()});
        }
    }

    private void addData() { 
        String nis = nisField.getText().trim();
        String nama = namaField.getText().trim();
        String alamat = alamatField.getText().trim();

        if (nis.isEmpty() || nama.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua kolom harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Cek duplikasi NIS
        if (dataList.stream().anyMatch(s -> s.getId().equals(nis))) {
            JOptionPane.showMessageDialog(this, "Error: NIS [" + nis + "] sudah terdaftar!", "Kesalahan Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Siswa baru = new Siswa(nis, nama, alamat);
            dataList.add(baru);
            // Simpan List yang sudah diperbarui ke file
            FileManager.saveList(FileManager.SISWA_FILE, dataList); 
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Data Siswa berhasil ditambah!");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saat menyimpan data: " + ex.getMessage(), "Kesalahan I/O File", JOptionPane.ERROR_MESSAGE); 
        }
    }

    private void updateData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih baris di tabel yang ingin diubah!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nisTarget = (String) tableModel.getValueAt(selectedRow, 0); // NIS tidak boleh diubah
        String nama = namaField.getText().trim();
        String alamat = alamatField.getText().trim();
        
        if (nama.isEmpty() || alamat.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi Nama dan Alamat baru!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Cari dan perbarui objek di dataList
            for (int i = 0; i < dataList.size(); i++) {
                if (dataList.get(i).getId().equals(nisTarget)) {
                    dataList.get(i).setNama(nama);
                    dataList.get(i).setAlamat(alamat);
                    break;
                }
            }

            // Simpan perubahan ke file
            FileManager.saveList(FileManager.SISWA_FILE, dataList); //
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Data Siswa berhasil diupdate!");
        } catch (IOException ex) {
            // Penanganan Exception I/O File
            JOptionPane.showMessageDialog(this, "Error saat menyimpan data: " + ex.getMessage(), "Kesalahan I/O File", JOptionPane.ERROR_MESSAGE); //
        }
    }

    private void deleteData() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                dataList.remove(selectedRow);

                FileManager.saveList(FileManager.SISWA_FILE, dataList); 
                refreshTable();
                clearForm();
                JOptionPane.showMessageDialog(this, "Data berhasil dihapus!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saat menghapus data: " + ex.getMessage(), "Kesalahan I/O File", JOptionPane.ERROR_MESSAGE); 
            }
        }
    }
    
    private void fillFormFromTable() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            nisField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            namaField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            alamatField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            // NIS tidak bisa diedit, jadi di-disable
            nisField.setEnabled(false); 
        }
    }

    private void clearForm() {
        nisField.setText("");
        namaField.setText("");
        alamatField.setText("");
        nisField.setEnabled(true);
        table.clearSelection();
    }
}

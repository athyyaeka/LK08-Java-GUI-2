package LK08;
// FileManager.java
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Kelas yang menangani semua operasi I/O File.
 * Semua interaksi file harus melalui kelas ini.
 */
public class FileManager {
    public static final String SISWA_FILE = "siswa.txt";
    public static final String BUKU_FILE = "buku.txt";
    public static final String PEGAWAI_FILE = "pegawai.txt";
    public static final String TRANSAKSI_FILE = "transaksi.txt";

    // --- Core I/O Operations ---

    /**
     * Membaca semua baris dari file.
     * @param fileName Nama file (.txt)
     * @return List<String> baris-baris dalam file
     * @throws IOException Jika terjadi kesalahan saat membaca file.
     */
    private static List<String> readAllLines(String fileName) throws IOException { 
    List<String> lines = new ArrayList<>(); 
    File file = new File(fileName);
    
    if (!file.exists()) { 
        // [PERBAIKAN] Jika file tidak ada, buat file baru dan kembalikan list kosong
        file.createNewFile();
        return lines; 
    }
    
    // Hanya mencoba membaca file jika filenya memang ada
    try (BufferedReader br = new BufferedReader(new FileReader(file))) { 
        String line;
        while ((line = br.readLine()) != null) { 
            if (!line.trim().isEmpty()) lines.add(line.trim());
        } 
    }
    return lines;
}

    /**
     * Menulis ulang (overwrite) seluruh isi file dengan data baru.
     * @param fileName Nama file
     * @param lines Data dalam bentuk List<String>
     * @throws IOException Jika terjadi kesalahan saat menulis file.
     */
    private static void rewriteFile(String fileName, List<String> lines) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }
    
    // --- Generic Load and Save Methods ---
    
    /**
     * Memuat data dari file dan mengkonversinya menjadi List objek generik.
     * @param fileName Nama file
     * @param parser Fungsi statis fromFileString dari model (e.g., Siswa::fromFileString)
     * @return List<T> daftar objek yang dimuat
     */
    public static <T> List<T> loadList(String fileName, Function<String, T> parser) throws IOException {
        List<T> list = new ArrayList<>();
        List<String> lines = readAllLines(fileName); //
        for (String line : lines) {
            try {
                // Konversi baris teks menjadi objek T menggunakan parser yang diberikan.
                list.add(parser.apply(line)); 
            } catch (Exception e) {
                // Dalam GUI, error ini harus ditampilkan di JOptionPane
                System.err.println("Error parsing line in " + fileName + ": " + e.getMessage());
            }
        }
        return list;
    }

    /**
     * Menyimpan List objek generik ke file dengan mengkonversinya ke toFileString().
     * @param fileName Nama file
     * @param list Daftar objek yang akan disimpan
     */
    public static <T> void saveList(String fileName, List<T> list) throws IOException {
        List<String> lines = new ArrayList<>();
        for (T item : list) {
            if (item instanceof Siswa s) lines.add(s.toFileString());
            else if (item instanceof Pegawai p) lines.add(p.toFileString());
            else if (item instanceof Buku b) lines.add(b.toFileString());
            else if (item instanceof Transaksi t) lines.add(t.toFileString());
            // Gunakan metode toFileString yang sudah ada di model class
        }
        rewriteFile(fileName, lines); //
    }
    
    // Metode untuk mencari, menghapus, dan update data spesifik pada file
    // Untuk tujuan konversi ke GUI, kita akan fokus pada memuat dan menyimpan seluruh List
    // agar sinkronisasi data lebih mudah diimplementasikan (seperti di LK07 SiswaApp).
}





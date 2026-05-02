package LK08;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FileManager {
    public static final String SISWA_FILE = "siswa.txt";
    public static final String BUKU_FILE = "buku.txt";
    public static final String PEGAWAI_FILE = "pegawai.txt";
    public static final String TRANSAKSI_FILE = "transaksi.txt";

    
    private static List<String> readAllLines(String fileName) throws IOException { 
    List<String> lines = new ArrayList<>(); 
    File file = new File(fileName);
    
    if (!file.exists()) { 
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

    private static void rewriteFile(String fileName, List<String> lines) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        }
    }
    
    // --- Generic Load and Save Methods ---
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

    public static <T> void saveList(String fileName, List<T> list) throws IOException {
        List<String> lines = new ArrayList<>();
        for (T item : list) {
            if (item instanceof Siswa s) lines.add(s.toFileString());
            else if (item instanceof Pegawai p) lines.add(p.toFileString());
            else if (item instanceof Buku b) lines.add(b.toFileString());
            else if (item instanceof Transaksi t) lines.add(t.toFileString());
        }
        rewriteFile(fileName, lines);
    }
}





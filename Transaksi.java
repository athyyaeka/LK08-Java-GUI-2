package LK08;

// Transaksi.java
import java.util.Objects;

public class Transaksi {
    private String kodeTrans;
    private String nis;
    private String kodeBuku;
    private String tglPinjam;
    private String tglKembali; // Kosong jika status = 0
    private int status; // 0: Belum Kembali, 1: Sudah Kembali

    public Transaksi(String kodeTrans, String nis, String kodeBuku, String tglPinjam, String tglKembali, int status) {
        this.kodeTrans = kodeTrans.trim();
        this.nis = nis.trim();
        this.kodeBuku = kodeBuku.trim();
        this.tglPinjam = tglPinjam.trim();
        this.tglKembali = Objects.requireNonNullElse(tglKembali, "").trim();
        this.status = status;
    }

    // Getters
    public String getKodeTrans() { return kodeTrans; }
    public String getNis() { return nis; }
    public String getKodeBuku() { return kodeBuku; }
    public String getTglPinjam() { return tglPinjam; }
    public String getTglKembali() { return tglKembali; }
    public int getStatus() { return status; }

    // Setters (penting untuk proses pengembalian)
    public void setTglKembali(String tglKembali) {
        this.tglKembali = tglKembali.trim();
    }

    public void setStatus(int status) {
        this.status = status;
    }

    // Metode untuk mengubah objek menjadi format string untuk disimpan di file
    public String toFileString() {
        return kodeTrans + ";" + nis + ";" + kodeBuku + ";" + tglPinjam + ";" + tglKembali + ";" + status;
    }

    // Digunakan oleh FileManager untuk memuat data dari file
    public static Transaksi fromFileString(String line) {
        String[] parts = line.split(";", 6);
        if (parts.length < 6) throw new IllegalArgumentException("Invalid Transaksi format: " + line);
        return new Transaksi(
                parts[0].trim(), parts[1].trim(), parts[2].trim(),
                parts[3].trim(), parts[4].trim(), Integer.parseInt(parts[5].trim())
        );
    }

    public String getStatusText() {
        return status == 0 ? "Belum Kembali" : "Sudah Kembali";
    }

    @Override
    public String toString() {
        return getKodeTrans() + ": " + getNis() + " - " + getKodeBuku() + " (Pinjam: " + getTglPinjam() + ", Status: " + getStatusText() + ")";
    }
}


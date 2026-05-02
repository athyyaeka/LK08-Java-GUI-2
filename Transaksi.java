package LK08;

import java.util.Objects;

public class Transaksi {
    private String kodeTrans;
    private String nis;
    private String kodeBuku;
    private String tglPinjam;
    private String tglKembali;
    private int status;

    public Transaksi(String kodeTrans, String nis, String kodeBuku, String tglPinjam, String tglKembali, int status) {
        this.kodeTrans = kodeTrans.trim();
        this.nis = nis.trim();
        this.kodeBuku = kodeBuku.trim();
        this.tglPinjam = tglPinjam.trim();
        this.tglKembali = Objects.requireNonNullElse(tglKembali, "").trim();
        this.status = status;
    }

    public String getKodeTrans() { return kodeTrans; }
    public String getNis() { return nis; }
    public String getKodeBuku() { return kodeBuku; }
    public String getTglPinjam() { return tglPinjam; }
    public String getTglKembali() { return tglKembali; }
    public int getStatus() { return status; }

    public void setTglKembali(String tglKembali) {
        this.tglKembali = tglKembali.trim();
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String toFileString() {
        return kodeTrans + ";" + nis + ";" + kodeBuku + ";" + tglPinjam + ";" + tglKembali + ";" + status;
    }

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


package LK08;

// Buku.java
public class Buku {
    private String kode;
    private String judul;
    private String jenis;

    public Buku(String kode, String judul, String jenis) {
        this.kode = kode.trim();
        this.judul = judul.trim();
        this.jenis = jenis.trim();
    }

    public String getKode() { return kode; }
    public String getJudul() { return judul; }
    public String getJenis() { return jenis; }

    // Metode untuk mengubah objek menjadi format string untuk disimpan di file
    public String toFileString() {
        return kode + ";" + judul + ";" + jenis;
    }

    // Digunakan oleh FileManager untuk memuat data dari file
    public static Buku fromFileString(String line) {
        String[] parts = line.split(";", 3);
        if (parts.length < 3)
            throw new IllegalArgumentException("Invalid Buku format");
        return new Buku(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }

    @Override
    public String toString() {
        return kode + " - " + judul + " (" + jenis + ")";
    }
}


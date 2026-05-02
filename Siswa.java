package LK08;
import java.util.Objects;
public class Siswa extends Person {
    private String alamat;

    public Siswa(String nis, String nama, String alamat) {
        super(nis, nama);
        this.alamat = Objects.requireNonNullElse(alamat, "").trim();
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = Objects.requireNonNullElse(alamat, "").trim();
    }

    @Override
    public String toFileString() {
        return super.toFileString() + ";" + alamat;
    }

    // Digunakan oleh FileManager untuk memuat data dari file
    public static Siswa fromFileString(String line) {
        String[] parts = line.split(";", 3);
        if (parts.length < 3)
            throw new IllegalArgumentException("Invalid Siswa format");
        return new Siswa(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }
}

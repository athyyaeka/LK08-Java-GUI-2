package LK08;

import java.util.Objects;

public class Pegawai extends Person {
    private String tglLahir;

    public Pegawai(String nip, String nama, String tglLahir) {
        super(nip, nama);
        this.tglLahir = Objects.requireNonNullElse(tglLahir, "").trim();
    }

    public String getTglLahir() {
        return tglLahir;
    }

    @Override
    public String toFileString() {
        return super.toFileString() + ";" + tglLahir;
    }

    public static Pegawai fromFileString(String line) {
        String[] parts = line.split(";", 3);
        if (parts.length < 3)
            throw new IllegalArgumentException("Invalid Pegawai format");
        return new Pegawai(parts[0].trim(), parts[1].trim(), parts[2].trim());
    }
}


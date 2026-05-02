package LK08;

abstract class Person {
    private String id;
    private String nama;

    public Person(String id, String nama) {
        this.id = id.trim();
        this.nama = nama.trim();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id.trim();
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama.trim();
    }

    public String toFileString() {
        return id + ";" + nama;
    }

    @Override
    public String toString() {
        return id + " - " + nama;
    }
}

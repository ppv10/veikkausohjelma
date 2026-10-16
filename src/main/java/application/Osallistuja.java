package application;

public class Osallistuja {
    private int osallistujaId;
    private String nimi;

    // Konstruktori
    public Osallistuja(int osallistujaId, String nimi) {
        this.osallistujaId = osallistujaId;
        this.nimi = nimi;
    }

    // Getterit ja setterit
    public int getOsallistujaId() { return osallistujaId; }
    public void setOsallistujaId(int osallistujaId) { this.osallistujaId = osallistujaId; }

    public String getNimi() { return nimi; }
    public void setNimi(String nimi) { this.nimi = nimi; }

    @Override
    public String toString() {
        return nimi; // Näyttää vain kisan nimen ComboBoxissa
    }
}
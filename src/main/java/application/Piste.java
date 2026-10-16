package application;

public class Piste {
    private int osallistujaId;
    private String osallistujanNimi;
    private int pisteet;
    private int pisteet1x2; // Uusi kenttä
    private int pisteetLisapisteet; // Uusi kenttä

    // Konstruktori
    public Piste(int osallistujaId, String osallistujanNimi, int pisteet, int pisteet1x2, int pisteetLisapisteet) {
        this.osallistujaId = osallistujaId;
        this.osallistujanNimi = osallistujanNimi;
        this.pisteet = pisteet;
        this.pisteet1x2 = pisteet1x2;
        this.pisteetLisapisteet = pisteetLisapisteet;
    }

    // Getterit ja setterit
    public int getOsallistujaId() { return osallistujaId; }
    public void setOsallistujaId(int osallistujaId) { this.osallistujaId = osallistujaId; }

    public String getOsallistujanNimi() { return osallistujanNimi; }
    public void setOsallistujanNimi(String osallistujanNimi) { this.osallistujanNimi = osallistujanNimi; }

    public int getPisteet() { return pisteet; }
    public void setPisteet(int pisteet) { this.pisteet = pisteet; }

    public int getPisteet1x2() { return pisteet1x2; }
    public void setPisteet1x2(int pisteet1x2) { this.pisteet1x2 = pisteet1x2; }

    public int getPisteetLisapisteet() { return pisteetLisapisteet; }
    public void setPisteetLisapisteet(int pisteetLisapisteet) { this.pisteetLisapisteet = pisteetLisapisteet; }
}
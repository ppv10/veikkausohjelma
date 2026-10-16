package application;

public class Veikkaus {
    private int veikkausId;
    private int osallistujaId; // Osallistujan, joka on tehnyt veikkauksen, tunniste
    private int otteluId; // Ottelun, johon veikkaus liittyy, tunniste
    private int kotimaalitVeikkaus; // Veikattu kotijoukkueen maalimäärä
    private int vierasmaalitVeikkaus; // Veikattu vierasjoukkueen maalimäärä

    // Konstruktori
    public Veikkaus(int veikkausId, int osallistujaId, int otteluId, int kotimaalitVeikkaus, int vierasmaalitVeikkaus) {
        this.veikkausId = veikkausId;
        this.osallistujaId = osallistujaId;
        this.otteluId = otteluId;
        this.kotimaalitVeikkaus = kotimaalitVeikkaus;
        this.vierasmaalitVeikkaus = vierasmaalitVeikkaus;
    }

    // Getterit ja setterit
    public int getVeikkausId() { return veikkausId; }
    public void setVeikkausId(int veikkausId) { this.veikkausId = veikkausId; }

    public int getOsallistujaId() { return osallistujaId; }
    public void setOsallistujaId(int osallistujaId) { this.osallistujaId = osallistujaId; }

    public int getOtteluId() { return otteluId; }
    public void setOtteluId(int otteluId) { this.otteluId = otteluId; }

    public int getKotimaalitVeikkaus() { return kotimaalitVeikkaus; }
    public void setKotimaalitVeikkaus(int kotimaalitVeikkaus) { this.kotimaalitVeikkaus = kotimaalitVeikkaus; }

    public int getVierasmaalitVeikkaus() { return vierasmaalitVeikkaus; }
    public void setVierasmaalitVeikkaus(int vierasmaalitVeikkaus) { this.vierasmaalitVeikkaus = vierasmaalitVeikkaus; }

}
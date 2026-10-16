package application;

public class Ottelu {
    private int otteluId;
    private int kisaId;
    private String kotijoukkue;
    private String vierasjoukkue;
    private int kotimaalit;
    private int vierasmaalit;

    // Konstruktori
    public Ottelu(int otteluId, int kisaId, String kotijoukkue, String vierasjoukkue, int kotimaalit, int vierasmaalit) {
        this.otteluId = otteluId;
        this.kisaId = kisaId;
        this.kotijoukkue = kotijoukkue;
        this.vierasjoukkue = vierasjoukkue;
        this.kotimaalit = kotimaalit;
        this.vierasmaalit = vierasmaalit;
    }

    // Getterit ja setterit
    public int getOtteluId() { return otteluId; }
    public void setOtteluId(int otteluId) { this.otteluId = otteluId; }

    public int getKisaId() { return kisaId; }
    public void setKisaId(int kisaId) { this.kisaId = kisaId; }

    public String getKotijoukkue() { return kotijoukkue; }
    public void setKotijoukkue(String kotijoukkue) { this.kotijoukkue = kotijoukkue; }

    public String getVierasjoukkue() { return vierasjoukkue; }
    public void setVierasjoukkue(String vierasjoukkue) { this.vierasjoukkue = vierasjoukkue; }

    public int getKotimaalit() { return kotimaalit; }
    public void setKotimaalit(int kotimaalit) { this.kotimaalit = kotimaalit; }

    public int getVierasmaalit() { return vierasmaalit; }
    public void setVierasmaalit(int vierasmaalit) { this.vierasmaalit = vierasmaalit; }

    @Override
    public String toString() {
    return "Kisa ID: " + kisaId + ", " + kotijoukkue + " vs. " + vierasjoukkue;
    }
}
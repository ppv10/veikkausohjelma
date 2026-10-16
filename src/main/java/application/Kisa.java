package application;

public class Kisa {
    private int kisaId;
    private String nimi;
    private String alkupvm;
    private String loppupvm;

    // Konstruktori
    public Kisa(int kisaId, String nimi, String alkupvm, String loppupvm) {
        this.kisaId = kisaId;
        this.nimi = nimi;
        this.alkupvm = alkupvm;
        this.loppupvm = loppupvm;
    }

    // Getterit ja setterit
    public int getKisaId() { return kisaId; }
    public void setKisaId(int kisaId) { this.kisaId = kisaId; }

    public String getNimi() { return nimi; }
    public void setNimi(String nimi) { this.nimi = nimi; }

    public String getAlkupvm() { return alkupvm; }
    public void setAlkupvm(String alkupvm) { this.alkupvm = alkupvm; }

    public String getLoppupvm() { return loppupvm; }
    public void setLoppupvm(String loppupvm) { this.loppupvm = loppupvm; }

    @Override
    public String toString() {
        return nimi; // Näyttää vain kisan nimen ComboBoxissa
    }
}
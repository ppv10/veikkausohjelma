package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FetchData {

    // Hakee veikkaukset ja niihin liittyvät ottelut tietokannasta osallistujaId:n perusteella
    public static ResultSet getVeikkauksetAndOttelut(int kisaId, int osallistujaId, int otteluId) throws SQLException {
        String query =  "SELECT v.osallistuja_id, os.nimi AS osallistujan_nimi, " +
                        "v.kotimaalit_veikkaus, v.vierasmaalit_veikkaus, " +
                        "o.kotimaalit, o.vierasmaalit " +
                        "FROM veikkaukset v " +
                        "JOIN ottelut o ON v.ottelu_id = o.ottelu_id " +
                        "JOIN osallistujat os ON v.osallistuja_id = os.osallistuja_id " +
                        "WHERE o.kisa_id = ? AND v.osallistuja_id = ? AND v.ottelu_id = ?";

        Connection connection = Database.connect();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, kisaId); // Aseta kisaId parametri
        statement.setInt(2, osallistujaId); // Aseta osallistujaId parametri
        statement.setInt(3, otteluId); // Aseta otteluId parametri
        return statement.executeQuery();
}

    // Hakee kaikki kisat, osallistujat ja ottelut tietokannasta
    public static List<Map<String, Integer>> haeKaikkiKisatOsallistujatJaOttelut() throws SQLException {
        List<Map<String, Integer>> data = new ArrayList<>();

        String query =  "SELECT k.kisa_id, o.ottelu_id, v.osallistuja_id " +
                        "FROM kisat k " +
                        "JOIN ottelut o ON k.kisa_id = o.kisa_id " +
                        "JOIN veikkaukset v ON o.ottelu_id = v.ottelu_id";

        try (Connection connection = Database.connect();
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Map<String, Integer> row = new HashMap<>();
                row.put("kisa_id", resultSet.getInt("kisa_id"));
                row.put("ottelu_id", resultSet.getInt("ottelu_id"));
                row.put("osallistuja_id", resultSet.getInt("osallistuja_id"));
                data.add(row);
            }
        }

        return data;
    }

    // Hakee käynnissä olevien kisojen pisteet tietokannasta ja palauttaa ne listana
    public static List<Piste> getKaynnissaOlevatKisatPisteet() throws SQLException {
        String query = "SELECT pisteet.osallistuja_id, osallistujat.nimi AS osallistujan_nimi, " +
                       "SUM(pisteet.pisteet) AS pisteet, " +
                       "SUM(pisteet.pisteet1x2) AS pisteet1x2, " + // Uusi kenttä
                       "SUM(pisteet.pisteetLisapisteet) AS pisteetLisapisteet " + // Uusi kenttä
                       "FROM pisteet " +
                       "JOIN osallistujat ON pisteet.osallistuja_id = osallistujat.osallistuja_id " +
                       "JOIN kisat ON pisteet.kisa_id = kisat.kisa_id " +
                       "WHERE kisat.loppupvm > DATE('now') " +
                       "GROUP BY pisteet.osallistuja_id, osallistujat.nimi " +
                       "ORDER BY pisteet DESC";
        return haePisteet(query);
    }
    
    // Hakee menneiden kisojen pisteet tietokannasta ja palauttaa ne listana
    public static List<Piste> getMenneetKisatPisteet() throws SQLException {
        String query = "SELECT pisteet.osallistuja_id, osallistujat.nimi AS osallistujan_nimi, " +
                       "SUM(pisteet.pisteet) AS pisteet, " +
                       "SUM(pisteet.pisteet1x2) AS pisteet1x2, " + // Uusi kenttä
                       "SUM(pisteet.pisteetLisapisteet) AS pisteetLisapisteet " + // Uusi kenttä
                       "FROM pisteet " +
                       "JOIN osallistujat ON pisteet.osallistuja_id = osallistujat.osallistuja_id " +
                       "JOIN kisat ON pisteet.kisa_id = kisat.kisa_id " +
                       "WHERE kisat.loppupvm < DATE('now') " +
                       "GROUP BY pisteet.osallistuja_id, osallistujat.nimi " +
                       "ORDER BY pisteet DESC";
        return haePisteet(query);
    }    

    // Yhteinen metodi pisteiden hakemiseen
    private static List<Piste> haePisteet(String query) throws SQLException {
        List<Piste> pisteet = new ArrayList<>();
    
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
    
            while (resultSet.next()) {
                int osallistujaId = resultSet.getInt("osallistuja_id");
                String osallistujanNimi = resultSet.getString("osallistujan_nimi");
                int pisteetSumma = resultSet.getInt("pisteet");
                int pisteet1x2 = resultSet.getInt("pisteet1x2");
                int pisteetLisapisteet = resultSet.getInt("pisteetLisapisteet");
    
                pisteet.add(new Piste(osallistujaId, osallistujanNimi, pisteetSumma, pisteet1x2, pisteetLisapisteet));
            }
        }
    
        return pisteet;
    }

}
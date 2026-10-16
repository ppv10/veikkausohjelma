package application;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ScoreCalculator {

    // Laskee pisteet veikkauksista ja otteluista.
    public static Map<Integer, Piste> calculateScores(ResultSet resultSet) throws SQLException {
        Map<Integer, Piste> scores = new HashMap<>();
    
        while (resultSet.next()) {
            int osallistujaId = resultSet.getInt("osallistuja_id");
            String osallistujanNimi = resultSet.getString("osallistujan_nimi");
            int kotimaalitVeikkaus = resultSet.getInt("kotimaalit_veikkaus");
            int vierasmaalitVeikkaus = resultSet.getInt("vierasmaalit_veikkaus");
            int kotimaalit = resultSet.getInt("kotimaalit");
            int vierasmaalit = resultSet.getInt("vierasmaalit");
    
            // Tulosta debug-tiedot
            System.out.println("Osallistuja ID: " + osallistujaId);
            System.out.println("Veikkaus: " + kotimaalitVeikkaus + "-" + vierasmaalitVeikkaus);
            System.out.println("Oikea tulos: " + kotimaalit + "-" + vierasmaalit);
    
            // Laske pisteet
            int points1x2 = calculate1x2Points(kotimaalit, vierasmaalit, kotimaalitVeikkaus, vierasmaalitVeikkaus);
            int additionalPoints = calculateAdditionalPoints(kotimaalit, vierasmaalit, kotimaalitVeikkaus, vierasmaalitVeikkaus);
            int totalPoints = points1x2 + additionalPoints;
    
            // Päivitä osallistujan pisteet
            if (scores.containsKey(osallistujaId)) {
                Piste piste = scores.get(osallistujaId);
                piste.setPisteet(piste.getPisteet() + totalPoints);
            } else {
                scores.put(osallistujaId, new Piste(osallistujaId, osallistujanNimi, totalPoints, points1x2, additionalPoints));
            }
        }
    
        return scores;
    }

    // Laskee 1X2-pisteet
    private static int calculate1x2Points(int kotimaalit, int vierasmaalit, int kotimaalitVeikkaus, int vierasmaalitVeikkaus) {
        if ((kotimaalit > vierasmaalit && kotimaalitVeikkaus > vierasmaalitVeikkaus) ||
            (kotimaalit == vierasmaalit && kotimaalitVeikkaus == vierasmaalitVeikkaus) ||
            (kotimaalit < vierasmaalit && kotimaalitVeikkaus < vierasmaalitVeikkaus)) {
            return 6; // Oikea 1X2-tulos
        }
        return 0; // Väärä 1X2-tulos
    }

    // Laskee lisäpisteet
    private static int calculateAdditionalPoints(int kotimaalit, int vierasmaalit, int kotimaalitVeikkaus, int vierasmaalitVeikkaus) {
        if (kotimaalit == kotimaalitVeikkaus && vierasmaalit == vierasmaalitVeikkaus) {
            return 6; // Molemmat maalimäärät oikein
        } else if (kotimaalit == kotimaalitVeikkaus || vierasmaalit == vierasmaalitVeikkaus) {
            return 3; // Toinen maalimäärä oikein
        }
        return 0; // Ei oikeita maalimääriä
    }
}
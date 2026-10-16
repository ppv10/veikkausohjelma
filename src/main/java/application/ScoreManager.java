package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ScoreManager {

    // Laskee pisteet, laskee valmiit tietokantakirjoitukset erissä ja sulkee resurssit.
    public static void calculateAndSaveScores() throws SQLException {
        try {
            List<Map<String, Integer>> data = FetchData.haeKaikkiKisatOsallistujatJaOttelut();
            String insertSql = "INSERT OR IGNORE INTO pisteet " +
                    "(osallistuja_id, kisa_id, ottelu_id, pisteet, pisteet1x2, pisteetLisapisteet) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (Connection writeConnection = Database.connect();
                 PreparedStatement insert = writeConnection.prepareStatement(insertSql)) {
                for (Map<String, Integer> row : data) {
                    int kisaId = row.get("kisa_id");
                    int otteluId = row.get("ottelu_id");
                    int osallistujaId = row.get("osallistuja_id");

                    // The unique key makes the insert idempotent, removing one SELECT per row.
                    try (ResultSet resultSet = FetchData.getVeikkauksetAndOttelut(
                                 kisaId, osallistujaId, otteluId);
                         java.sql.Statement queryStatement = resultSet.getStatement();
                         Connection queryConnection = queryStatement.getConnection()) {
                        Map<Integer, Piste> scores = ScoreCalculator.calculateScores(resultSet);
                        addScoresToBatch(insert, scores, kisaId, otteluId);
                    }
                }
                insert.executeBatch();
            }

            System.out.println("Pisteet laskettu ja tallennettu onnistuneesti kaikille kisoille ja otteluille.");
        } catch (SQLException e) {
            System.err.println("Virhe pisteiden laskennassa tai tallentamisessa: " + e.getMessage());
            throw e;
        }
    }

    private static void addScoresToBatch(
            PreparedStatement statement, Map<Integer, Piste> scores, int kisaId, int otteluId)
            throws SQLException {
        for (Piste piste : scores.values()) {
            statement.setInt(1, piste.getOsallistujaId());
            statement.setInt(2, kisaId);
            statement.setInt(3, otteluId);
            statement.setInt(4, piste.getPisteet());
            statement.setInt(5, piste.getPisteet1x2());
            statement.setInt(6, piste.getPisteetLisapisteet());
            statement.addBatch();
        }
    }
}

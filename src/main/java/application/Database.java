package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:kisat.db";

    // Yhdistä tietokantaan
    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection connection = DriverManager.getConnection(URL);
            try (Statement statement = connection.createStatement()) {
                statement.execute("PRAGMA foreign_keys = ON");
            } catch (SQLException e) {
                connection.close();
                throw e;
            }
            return connection;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("SQLite JDBC driver was not found.", e);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not connect to the SQLite database.", e);
        }
    }

    // Alusta tietokanta ja luo tarvittavat taulut
    public static void initialize() {
        String[] sqlCommands = {
            "CREATE TABLE IF NOT EXISTS kisat (" +
            "kisa_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nimi TEXT NOT NULL, " +
            "alkupvm TEXT NOT NULL, " +
            "loppupvm TEXT NOT NULL);",

            "CREATE TABLE IF NOT EXISTS ottelut (" +
            "ottelu_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "kisa_id INTEGER, " +
            "kotijoukkue TEXT NOT NULL, " +
            "vierasjoukkue TEXT NOT NULL, " +
            "kotimaalit INTEGER, " +
            "vierasmaalit INTEGER, " +
            "FOREIGN KEY (kisa_id) REFERENCES kisat(kisa_id));",

            "CREATE TABLE IF NOT EXISTS osallistujat (" +
            "osallistuja_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "nimi TEXT NOT NULL);",

            "CREATE TABLE IF NOT EXISTS veikkaukset (" +
            "veikkaus_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "osallistuja_id INTEGER, " +
            "ottelu_id INTEGER, " +
            "kotimaalit_veikkaus INTEGER, " +
            "vierasmaalit_veikkaus INTEGER, " +
            "FOREIGN KEY (osallistuja_id) REFERENCES osallistujat(osallistuja_id), " +
            "FOREIGN KEY (ottelu_id) REFERENCES ottelut(ottelu_id), " +
            "UNIQUE (osallistuja_id, ottelu_id));",

            "CREATE TABLE IF NOT EXISTS pisteet (" +
            "pisteet_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "osallistuja_id INTEGER NOT NULL, " +
            "kisa_id INTEGER NOT NULL, " +
            "ottelu_id INTEGER NOT NULL, " +
            "pisteet INTEGER, " +
            "pisteet1x2 INTEGER, " +
            "pisteetLisapisteet INTEGER, " +
            "FOREIGN KEY (osallistuja_id) REFERENCES osallistujat(osallistuja_id), " +
            "FOREIGN KEY (kisa_id) REFERENCES kisat(kisa_id), " +
            "FOREIGN KEY (ottelu_id) REFERENCES ottelut(ottelu_id), " +
            "UNIQUE (osallistuja_id, kisa_id, ottelu_id));"
        };

        // Suorita yllä olevat komennot tietokantaan
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            for (String sql : sqlCommands) {
                stmt.execute(sql);
            }
            System.out.println("Taulut luotu onnistuneesti.");
        } catch (SQLException e) {
            System.out.println("Virhe taulujen luonnissa: " + e.getMessage());
        }
    }
}

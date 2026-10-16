package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class Controller {
    // Comboboxien sisälle laitettavat listat
    private ObservableList<Kisa> kisat = FXCollections.observableArrayList();
    private ObservableList<Osallistuja> osallistujat = FXCollections.observableArrayList();
    private ObservableList<Ottelu> ottelut = FXCollections.observableArrayList();
    // lisaaKisan komponentit
    @FXML private TextField kisaNimiField;
    @FXML private TextField alkupvmField;
    @FXML private TextField loppupvmField;
    // lisaaOttelun komponentit
    @FXML private ComboBox<Kisa> kisaComboBox; // ComboBox kisat-olioille
    @FXML private TextField kotijoukkueField;
    @FXML private TextField vierasjoukkueField;
    @FXML private TextField kotimaalitField;
    @FXML private TextField vierasmaalitField;
    // lisaaOsallistujan komponentit
    @FXML private TextField osallistujaField;
    // lisaaVeikkauksen komponentit
    @FXML private ComboBox<Osallistuja> osallistujaComboBox;
    @FXML private ComboBox<Ottelu> otteluComboBox;
    @FXML private TextField kotimaalitveikkausField;
    @FXML private TextField vierasmaalitveikkausField;
    // Kisataulukon komponentit
    @FXML private TableView<Kisa> kisatTable;
    @FXML private TableColumn<Kisa, String> kisaNimiColumn;
    @FXML private TableColumn<Kisa, String> alkupvmColumn;
    @FXML private TableColumn<Kisa, String> loppupvmColumn;
    @FXML private Button poistaKisaNappi;
    // Ottelutaulukon komponentit
    @FXML private TableView<Ottelu> ottelutTable;
    @FXML private TableColumn<Ottelu, String> otteluKisaColumn;
    @FXML private TableColumn<Ottelu, String> kotijoukkueColumn;
    @FXML private TableColumn<Ottelu, String> vierasjoukkueColumn;
    @FXML private TableColumn<Ottelu, Integer> kotimaalitColumn;
    @FXML private TableColumn<Ottelu, Integer> vierasmaalitColumn;
    @FXML private Button poistaOtteluNappi;
    // Osallistujataulukon komponentit
    @FXML private TableView<Osallistuja> osallistujatTable;
    @FXML private TableColumn<Osallistuja, String> osallistujaNimiColumn;
    @FXML private Button poistaOsallistujaNappi;   
    // Veikkaustaulukon komponentit
    @FXML private TableView<Veikkaus> veikkauksetTable;
    @FXML private TableColumn<Veikkaus, String> veikkausOsallistujaColumn;
    @FXML private TableColumn<Veikkaus, String> veikkausOtteluColumn;
    @FXML private TableColumn<Veikkaus, Integer> kotimaalitVeikkausColumn;
    @FXML private TableColumn<Veikkaus, Integer> vierasmaalitVeikkausColumn;
    @FXML private Button poistaVeikkausNappi;
    // Käynnissä olevien pistetaulukon komponentit
    @FXML private TableView<Piste> kaynnissaOlevatKisatTable;
    @FXML private TableColumn<Piste, Integer> kaynnissaOsallistujaIdColumn;
    @FXML private TableColumn<Piste, String> kaynnissaOsallistujanNimiColumn;
    @FXML private TableColumn<Piste, Integer> kaynnissaPisteetColumn;
    @FXML private TableColumn<Piste, Integer> kaynnissaPisteet1x2Column;
    @FXML private TableColumn<Piste, Integer> kaynnissaPisteetLisapisteetColumn;
    @FXML private Button poistaKaynnissaOlevaKisaNappi;
    // Menneiden pistetaulukon komponentit
    @FXML private TableView<Piste> menneetKisatTable;
    @FXML private TableColumn<Piste, Integer> menneetOsallistujaIdColumn;
    @FXML private TableColumn<Piste, String> menneetOsallistujanNimiColumn;
    @FXML private TableColumn<Piste, Integer> menneetPisteetColumn;
    @FXML private TableColumn<Piste, Integer> menneetPisteet1x2Column;
    @FXML private TableColumn<Piste, Integer> menneetPisteetLisapisteetColumn;
    @FXML private Button poistaMennytKisaNappi;

    // Alusta tietokanta ja taulut, kun controller luodaan
    public Controller() {
        Database.initialize(); // Varmista, että taulut ovat olemassa
    }

    // Luodaan kisa käyttäjän syöttämien tietojen perusteella ja generoidaan valmiiksi KisaId, jotta käyttäjän ei tarvitse.
    @FXML
    private void lisaaKisa() {
        int kisaid = generoiKisaId();
        String nimi = kisaNimiField.getText();
        String alkupvm = alkupvmField.getText();
        String loppupvm = loppupvmField.getText();

        // Tarkista, että kaikki kentät on täytetty
        if (nimi.isEmpty() || alkupvm.isEmpty() || loppupvm.isEmpty()) {
            naytaVirhe("Kaikki kentät ovat pakollisia!");
            return;
        }

        if (!isValidDate(alkupvm) || !isValidDate(loppupvm)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virhe");
            alert.setHeaderText("Virheellinen päivämäärä");
            alert.setContentText("Anna päivämäärä muodossa YYYY-MM-DD.");
            alert.showAndWait();
            return;
        }

        // Luodaan Kisa-olio
        Kisa kisa = new Kisa(kisaid, nimi, alkupvm, loppupvm);
        
        // Lisää kisa tietokantaan
        String sql = "INSERT INTO kisat (kisa_id, nimi, alkupvm, loppupvm) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, kisa.getKisaId());
            pstmt.setString(2, kisa.getNimi());
            pstmt.setString(3, kisa.getAlkupvm());
            pstmt.setString(4, kisa.getLoppupvm());
            pstmt.executeUpdate();
            naytaViesti("Kisa lisätty onnistuneesti!");
            tyhjennaKentat(); // Tyhjennä kentät lisäyksen jälkeen
        } catch (SQLException e) {
            naytaVirhe("Virhe kisan lisäämisessä: " + e.getMessage());
        }
    paivitaTiedot();
    }

    // Luodaan ottelu käyttäjän syöttämien tietojen perusteella ja generoidaan valmiiksi KisaId, jotta käyttäjän ei tarvitse.
    @FXML
    private void lisaaOttelu() {
        // Hae syötteet
        Kisa valittuKisa = kisaComboBox.getValue();
        if (valittuKisa == null) {
            naytaVirhe("Valitse kisa!");
            return;
        }
        int otteluid = generoiOtteluId();
        String kotijoukkue = kotijoukkueField.getText();
        String vierasjoukkue = vierasjoukkueField.getText();
        String kotimaalitStr = kotimaalitField.getText();
        String vierasmaalitStr = vierasmaalitField.getText();
    
        // Tarkista, että kaikki kentät on täytetty
        if (kotijoukkue.isEmpty() || vierasjoukkue.isEmpty() ||
            kotimaalitStr.isEmpty() || vierasmaalitStr.isEmpty()) {
            naytaVirhe("Kaikki kentät ovat pakollisia!");
            return;
        }
    
        // Tarkista, että tarvitut syötteet ovat numeroita
        try {
            int kisaid = valittuKisa.getKisaId(); // Hae valitun kisan ID
            int kotimaalit = Integer.parseInt(kotimaalitStr);
            int vierasmaalit = Integer.parseInt(vierasmaalitStr);
    
            // Luodaan Ottelu-olio
            Ottelu ottelu = new Ottelu(otteluid, kisaid, kotijoukkue, vierasjoukkue, kotimaalit, vierasmaalit);
    
            // Lisää ottelu tietokantaan
            String sql = "INSERT INTO ottelut (ottelu_id, kisa_id, kotijoukkue, vierasjoukkue, kotimaalit, vierasmaalit) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, ottelu.getOtteluId());
                pstmt.setInt(2, ottelu.getKisaId());
                pstmt.setString(3, ottelu.getKotijoukkue());
                pstmt.setString(4, ottelu.getVierasjoukkue());
                pstmt.setInt(5, ottelu.getKotimaalit());
                pstmt.setInt(6, ottelu.getVierasmaalit());
                pstmt.executeUpdate();
                naytaViesti("Ottelu lisätty onnistuneesti!");
                tyhjennaKentat(); // Tyhjennä kentät lisäyksen jälkeen
            } catch (SQLException e) {
                naytaVirhe("Virhe ottelun lisäämisessä: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            naytaVirhe("Ottelun ID, kotimaalit ja vierasmaalit täytyy olla numeroita");
        }
    // Päivitetään tiedot kaikkiin FXML-komponentteihin.
    paivitaTiedot();
    }

    // Luodaan osallistuja käyttäjän syöttämien tietojen perusteella ja generoidaan valmiiksi KisaId, jotta käyttäjän ei tarvitse.
    @FXML
    private void lisaaOsallistuja() {
        int osallistujaid = generoiOsallistujaId();
        String osallistujaNimi = osallistujaField.getText();

        if (osallistujaNimi.isEmpty()) {
            naytaVirhe("Kaikki kentät ovat pakollisia!");
            return;
        }

        try {
            // Luodaan Osallistuja-olio
            Osallistuja osallistuja = new Osallistuja(osallistujaid, osallistujaNimi);

            String sql = "INSERT INTO osallistujat (osallistuja_id, nimi) VALUES (?, ?)";
            try (Connection conn = Database.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, osallistuja.getOsallistujaId());
                pstmt.setString(2, osallistuja.getNimi());
                pstmt.executeUpdate();
                naytaViesti("Osallistuja lisätty onnistuneesti!");
                tyhjennaKentat(); // Tyhjennä kentät lisäyksen jälkeen
            } catch (SQLException e) {
                naytaVirhe("Virhe osallistujan lisäämisessä: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            naytaVirhe("Syötä kelvollinen Kisan ID, sen täytyy olla numero");
        }
    paivitaTiedot();
    }

    // Luodaan veikkaus käyttäjän syöttämien tietojen perusteella ja generoidaan valmiiksi KisaId, jotta käyttäjän ei tarvitse.
    @FXML
    private void lisaaVeikkaus() {
        int veikkausid = generoiVeikkausId();
        Osallistuja valittuOsallistuja = osallistujaComboBox.getValue();
        if (valittuOsallistuja == null) {
            naytaVirhe("Valitse osallistuja!");
            return;
        }
        Ottelu valittuOttelu = otteluComboBox.getValue();
        if (valittuOttelu == null) {
            naytaVirhe("Valitse ottelu!");
            return;
        }
        String kotimaalitveikkaus1 = kotimaalitveikkausField.getText();
        String vierasmaalitveikkaus1 = vierasmaalitveikkausField.getText();

        if (kotimaalitveikkaus1.isEmpty() || vierasmaalitveikkaus1.isEmpty()) {
            naytaVirhe("Kaikki kentät ovat pakollisia!");
            return;
        }

        try {
            // Tarkistetaan, onko veikkaus jo tietokannassa
            if (onkoVeikkausOlemassa(valittuOsallistuja.getOsallistujaId(), valittuOttelu.getOtteluId())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Kaksoisveikkaus");
                alert.setHeaderText("Veikkaus on jo olemassa");
                alert.setContentText("Sama osallistuja ja ottelu -pari on jo olemassa.");
                alert.showAndWait();
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            // Tarkista, että tarvitut syötteet ovat numeroita
            int osallistujaid = valittuOsallistuja.getOsallistujaId(); // Hae valitun kisan ID
            int otteluid = valittuOttelu.getOtteluId(); // Hae valitun kisan ID
            int kotimaalitveikkaus = Integer.parseInt(kotimaalitveikkaus1);
            int vierasmaalitveikkaus = Integer.parseInt(vierasmaalitveikkaus1);

            // Luodaan Veikkaus-olio
            Veikkaus veikkaus = new Veikkaus(veikkausid, osallistujaid, otteluid, kotimaalitveikkaus, vierasmaalitveikkaus);

            // Syötetään tietokantaan
            String sql = "INSERT INTO veikkaukset (veikkaus_id, osallistuja_id, ottelu_id, kotimaalit_veikkaus, vierasmaalit_veikkaus) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = Database.connect();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, veikkaus.getVeikkausId());
                pstmt.setInt(2, veikkaus.getOsallistujaId());
                pstmt.setInt(3, veikkaus.getOtteluId());
                pstmt.setInt(4, veikkaus.getKotimaalitVeikkaus());
                pstmt.setInt(5, veikkaus.getVierasmaalitVeikkaus());
                pstmt.executeUpdate();
                naytaViesti("Veikkaus lisätty onnistuneesti!");
                tyhjennaKentat(); // Tyhjennä kentät lisäyksen jälkeen
            } catch (SQLException e) {
                naytaVirhe("Virhe veikkauksen lisäämisessä: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
        naytaVirhe("Syötä kelvollinen Kisan ID, sen täytyy olla numero");
        }
    paivitaTiedot();
    }

    // Tyhjennä syöttökentät
    private void tyhjennaKentat() {
        kisaNimiField.clear();
        alkupvmField.clear();
        loppupvmField.clear();
        kotijoukkueField.clear();
        vierasjoukkueField.clear();
        kotimaalitField.clear();
        vierasmaalitField.clear();
        osallistujaField.clear();
        kotimaalitveikkausField.clear();
        vierasmaalitveikkausField.clear();
    }

    // Näytä virheilmoitus
    private void naytaVirhe(String viesti) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Virhe");
        alert.setHeaderText(null);
        alert.setContentText(viesti);
        alert.showAndWait();
    }

    // Näytä onnistumisviesti
    private void naytaViesti(String viesti) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Onnistui");
        alert.setHeaderText(null);
        alert.setContentText(viesti);
        alert.showAndWait();
    }

    // Tarkistetaan, että päivämäärä on oikeassa muodossa lisaaKisassa
    private static boolean isValidDate(String date) {
    try {
        LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return true;
    } catch (DateTimeParseException e) {
        return false;
    }
    }

    // Tarkistetaan, onko veikkaus osallistuja- ja ottelu-ID -parille olemassa
    public static boolean onkoVeikkausOlemassa(int osallistujaId, int otteluId) throws SQLException {
        String query = "SELECT COUNT(*) AS maara FROM veikkaukset WHERE osallistuja_id = ? AND ottelu_id = ?";
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, osallistujaId);
            statement.setInt(2, otteluId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("maara") > 0; // Palauttaa true, jos veikkaus on jo olemassa
            }
        }
        return false;
    }

    // Alustetaan FXML-komponentit
    @FXML
    public void initialize() {
        // Kisat
        kisaNimiColumn.setCellValueFactory(new PropertyValueFactory<>("nimi"));
        alkupvmColumn.setCellValueFactory(new PropertyValueFactory<>("alkupvm"));
        loppupvmColumn.setCellValueFactory(new PropertyValueFactory<>("loppupvm"));
        // Kuuntelija poistoa varten
        kisatTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {});

        // Ottelut
        otteluKisaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(haeKisaNimi().get(cellData.getValue().getKisaId())));
        kotijoukkueColumn.setCellValueFactory(new PropertyValueFactory<>("kotijoukkue"));
        vierasjoukkueColumn.setCellValueFactory(new PropertyValueFactory<>("vierasjoukkue"));
        kotimaalitColumn.setCellValueFactory(new PropertyValueFactory<>("kotimaalit"));
        vierasmaalitColumn.setCellValueFactory(new PropertyValueFactory<>("vierasmaalit"));
        // Kuuntelija poistoa varten
        ottelutTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {});

        // Osallistujat
        osallistujaNimiColumn.setCellValueFactory(new PropertyValueFactory<>("nimi"));
        // Kuuntelija poistoa varten
        osallistujatTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {});

        // Veikkaukset
        veikkausOsallistujaColumn.setCellValueFactory(cellData -> new SimpleStringProperty(haeOsallistujaNimi().get(cellData.getValue().getOsallistujaId())));
        veikkausOtteluColumn.setCellValueFactory(cellData -> new SimpleStringProperty(haeOtteluNimi().get(cellData.getValue().getOtteluId())));
        kotimaalitVeikkausColumn.setCellValueFactory(new PropertyValueFactory<>("kotimaalitVeikkaus"));
        vierasmaalitVeikkausColumn.setCellValueFactory(new PropertyValueFactory<>("vierasmaalitVeikkaus"));
        // Kuuntelija poistoa varten
        veikkauksetTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {});

        // Alusta käynnissä olevien kisojen taulukko
        kaynnissaOsallistujaIdColumn.setCellValueFactory(new PropertyValueFactory<>("osallistujaId"));
        kaynnissaOsallistujanNimiColumn.setCellValueFactory(new PropertyValueFactory<>("osallistujanNimi"));
        kaynnissaPisteetColumn.setCellValueFactory(new PropertyValueFactory<>("pisteet"));
        kaynnissaPisteet1x2Column.setCellValueFactory(new PropertyValueFactory<>("pisteet1x2")); // Uusi sarake
        kaynnissaPisteetLisapisteetColumn.setCellValueFactory(new PropertyValueFactory<>("pisteetLisapisteet")); // Uusi sarake
        // Kuuntelija poistoa varten
        kaynnissaOlevatKisatTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {});

        // Alusta menneiden kisojen taulukko
        menneetOsallistujaIdColumn.setCellValueFactory(new PropertyValueFactory<>("osallistujaId"));
        menneetOsallistujanNimiColumn.setCellValueFactory(new PropertyValueFactory<>("osallistujanNimi"));
        menneetPisteetColumn.setCellValueFactory(new PropertyValueFactory<>("pisteet"));
        menneetPisteet1x2Column.setCellValueFactory(new PropertyValueFactory<>("pisteet1x2")); // Uusi sarake
        menneetPisteetLisapisteetColumn.setCellValueFactory(new PropertyValueFactory<>("pisteetLisapisteet")); // Uusi sarake
        // Kuuntelija poistoa varten
        menneetKisatTable.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {});
    
        paivitaTiedot();
    }
    // Päivitä kisat, ottelut, osallistujat ja pisteet niin SQL:ssä kuin FXML-komponenteissa
    @FXML
    public void paivitaTiedot() {
        paivitaKisat();
        paivitaOsallistujat();
        paivitaOttelut();
        paivitaPisteet();
        kisatTable.setItems(haeKisat());
        ottelutTable.setItems(haeOttelut());
        osallistujatTable.setItems(haeOsallistujat());
        veikkauksetTable.setItems(haeVeikkaukset());
    }

    // Generoidaan ID, jotta käyttäjän ei tarvitse
    private int generoiKisaId() {
        int uusiId = 1; // Oletusarvo, jos taulu on tyhjä
    
        try (Connection connection = Database.connect()) {
            String sql = "SELECT MAX(kisa_id) FROM kisat"; // Hae suurin ID
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
    
                if (resultSet.next()) {
                    uusiId = resultSet.getInt(1) + 1; // Kasvata viimeisintä ID:tä yhdellä
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virhe");
            alert.setHeaderText("ID:n generointi epäonnistui");
            alert.setContentText("Tietokantavirhe: " + e.getMessage());
            alert.showAndWait();
        }
    
        return uusiId;
    }
        // Generoidaan ID, jotta käyttäjän ei tarvitse
        private int generoiOtteluId() {
        int uusiId = 1; // Oletusarvo, jos taulu on tyhjä
    
        try (Connection connection = Database.connect()) {
            String sql = "SELECT MAX(ottelu_id) FROM ottelut"; // Hae suurin ID
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
    
                if (resultSet.next()) {
                    uusiId = resultSet.getInt(1) + 1; // Kasvata viimeisintä ID:tä yhdellä
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virhe");
            alert.setHeaderText("ID:n generointi epäonnistui");
            alert.setContentText("Tietokantavirhe: " + e.getMessage());
            alert.showAndWait();
        }
    
        return uusiId;
    }
    
    // Generoidaan ID, jotta käyttäjän ei tarvitse
    private int generoiOsallistujaId() {
        int uusiId = 1; // Oletusarvo, jos taulu on tyhjä
    
        try (Connection connection = Database.connect()) {
            String sql = "SELECT MAX(osallistuja_id) FROM osallistujat"; // Hae suurin ID
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
    
                if (resultSet.next()) {
                    uusiId = resultSet.getInt(1) + 1; // Kasvata viimeisintä ID:tä yhdellä
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virhe");
            alert.setHeaderText("ID:n generointi epäonnistui");
            alert.setContentText("Tietokantavirhe: " + e.getMessage());
            alert.showAndWait();
        }
    
        return uusiId;
    }
    
    // Generoidaan ID, jotta käyttäjän ei tarvitse
    private int generoiVeikkausId() {
        int uusiId = 1; // Oletusarvo, jos taulu on tyhjä
    
        try (Connection connection = Database.connect()) {
            String sql = "SELECT MAX(veikkaus_id) FROM veikkaukset"; // Hae suurin ID
            try (PreparedStatement statement = connection.prepareStatement(sql);
                 ResultSet resultSet = statement.executeQuery()) {
    
                if (resultSet.next()) {
                    uusiId = resultSet.getInt(1) + 1; // Kasvata viimeisintä ID:tä yhdellä
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virhe");
            alert.setHeaderText("ID:n generointi epäonnistui");
            alert.setContentText("Tietokantavirhe: " + e.getMessage());
            alert.showAndWait();
        }
    
        return uusiId;
    }

    // Hae kisat tietokannasta ja lisää ne observableArrayListille
    private ObservableList<Kisa> haeKisat() {
        ObservableList<Kisa> kisat = FXCollections.observableArrayList();
        String sql = "SELECT * FROM kisat";
        try (Connection conn = Database.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {  
            while (rs.next()) {
                kisat.add(new Kisa(
                    rs.getInt("kisa_id"),
                    rs.getString("nimi"), // nimi
                    rs.getString("alkupvm"), // alkupvm
                    rs.getString("loppupvm") // loppupvm
                )); 
            } 
        } catch (SQLException e) {
            System.out.println("Virhe kisojen haussa: " + e.getMessage());
        }
        return kisat;
    }
    
    // Hae ottelut tietokannasta ja lisää ne observableArrayListille
    private ObservableList<Ottelu> haeOttelut() {
        ObservableList<Ottelu> ottelut = FXCollections.observableArrayList();
        Map<Integer, String> kisat = haeKisaNimi(); // Hae kisat karttaan
        String sql = "SELECT * FROM ottelut ORDER BY ottelu_id ASC";
    
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                int kisaId = rs.getInt("kisa_id");
                String kisaNimi = kisat.get(kisaId); // Hae kisan nimi kartasta
                Ottelu ottelu = new Ottelu(
                    rs.getInt("ottelu_id"), // Hae otteluId
                    kisaId, // kisaId
                    rs.getString("kotijoukkue"), // kotijoukkue
                    rs.getString("vierasjoukkue"), // vierasjoukkue
                    rs.getInt("kotimaalit"), // kotimaalit
                    rs.getInt("vierasmaalit") // vierasmaalit
                );
                ottelut.add(ottelu);
            }
        } catch (SQLException e) {
            System.out.println("Virhe otteluiden haussa: " + e.getMessage());
        }
        return ottelut;
    }
    
    // Hae osallistujat tietokannasta ja lisää ne observableArrayListille
    private ObservableList<Osallistuja> haeOsallistujat() {
        ObservableList<Osallistuja> osallistujat = FXCollections.observableArrayList();
        String sql = "SELECT * FROM osallistujat";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                osallistujat.add(new Osallistuja(
                    rs.getInt("osallistuja_id"),
                    rs.getString("nimi") // nimi
                ));
            }
        } catch (SQLException e) {
            System.out.println("Virhe osallistujien haussa: " + e.getMessage());
        }
        return osallistujat;
    }

    // Hae veikkaukset tietokannasta ja lisää ne observableArrayListille
    private ObservableList<Veikkaus> haeVeikkaukset() {
        ObservableList<Veikkaus> veikkaukset = FXCollections.observableArrayList();
        Map<Integer, String> osallistujat = haeOsallistujaNimi(); // Hae osallistujat karttaan
        Map<Integer, String> ottelut = haeOtteluNimi(); // Hae ottelut karttaan
        String sql = "SELECT * FROM veikkaukset ORDER BY osallistuja_id ASC";
    
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                veikkaukset.add(new Veikkaus(
                    rs.getInt("veikkaus_id"), // veikkausId
                    rs.getInt("osallistuja_id"), // osallistujaId
                    rs.getInt("ottelu_id"), // otteluId
                    rs.getInt("kotimaalit_veikkaus"), // kotimaalitVeikkaus
                    rs.getInt("vierasmaalit_veikkaus") // vierasmaalitVeikkaus
                ));
            }
        } catch (SQLException e) {
            System.out.println("Virhe veikkausten haussa: " + e.getMessage());
        }
        return veikkaukset;
    }
    
    // Hae kisan nimi ottelujen näyttämistä varten käyttöliittymässä
    private Map<Integer, String> haeKisaNimi() {
        Map<Integer, String> kisat = new HashMap<>();
        String sql = "SELECT kisa_id, nimi FROM kisat";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                kisat.put(rs.getInt("kisa_id"), rs.getString("nimi"));
            }
        } catch (SQLException e) {
            System.out.println("Virhe kisojen haussa: " + e.getMessage());
        }
        return kisat;
    }
    
    // Hae osallistujan nimi veikkausten näyttämistä varten käyttöliittymässä
    private Map<Integer, String> haeOsallistujaNimi() {
        Map<Integer, String> osallistujat = new HashMap<>();
        String sql = "SELECT osallistuja_id, nimi FROM osallistujat";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                osallistujat.put(rs.getInt("osallistuja_id"), rs.getString("nimi"));
            }
        } catch (SQLException e) {
            System.out.println("Virhe osallistujien haussa: " + e.getMessage());
        }
        return osallistujat;
    }
    
    // Hae ottelun joukkueet veikkausten näyttämistä varten käyttöliittymässä
    private Map<Integer, String> haeOtteluNimi() {
        Map<Integer, String> ottelut = new HashMap<>();
        String sql = "SELECT ottelu_id, kotijoukkue, vierasjoukkue FROM ottelut";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String otteluInfo = rs.getString("kotijoukkue") + " vs. " + rs.getString("vierasjoukkue");
                ottelut.put(rs.getInt("ottelu_id"), otteluInfo);
            }
        } catch (SQLException e) {
            System.out.println("Virhe otteluiden haussa: " + e.getMessage());
        }
        return ottelut;
    }
    
    
    private void paivitaKisat() {
        kisat = haeKisat(); // Hae kisat tietokannasta
        kisaComboBox.setItems(kisat); // Aseta kisat-oliot ComboBoxiin
    }

    private void paivitaOttelut() {
        ottelut = haeOttelut(); // Hae ottelut tietokannasta
        otteluComboBox.setItems(ottelut); // Aseta ottelut-oliot ComboBoxiin
    }

    private void paivitaOsallistujat() {
        osallistujat = haeOsallistujat(); // Hae osallistujat tietokannasta
        osallistujaComboBox.setItems(osallistujat); // Aseta osallistujat-oliot ComboBoxiin
    }

    // Laske, hae ja syötä pisteet FXML-komponentteihin
    private void paivitaPisteet() {
        try {
            ScoreManager.calculateAndSaveScores();
    
            // Hae käynnissä olevien kisojen pisteet
            List<Piste> kaynnissaOlevatPisteet = FetchData.getKaynnissaOlevatKisatPisteet();
            kaynnissaOlevatKisatTable.setItems(FXCollections.observableArrayList(kaynnissaOlevatPisteet));
    
            // Hae menneiden kisojen pisteet
            List<Piste> menneetPisteet = FetchData.getMenneetKisatPisteet();
            menneetKisatTable.setItems(FXCollections.observableArrayList(menneetPisteet));
    
            // Tulosta debug-tiedot
            System.out.println("Käynnissä olevien kisojen pisteet: " + kaynnissaOlevatPisteet.size() + " riviä");
            System.out.println("Menneiden kisojen pisteet: " + menneetPisteet.size() + " riviä");
        } catch (SQLException e) {
            e.printStackTrace(); // Tulosta virheen yksityiskohdat konsoliin
            // Näytä virheilmoitus käyttäjälle
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virhe");
            alert.setHeaderText("Pisteiden päivitys epäonnistui");
            alert.setContentText("Tarkista tietokantayhteys ja yritä uudelleen.");
            alert.showAndWait();
        }
    }

    // Poista kuuntelijan avulla
    @FXML
    private void handlePoistaKisa() {
        // Hae valittu osallistuja TableView:stä
        Kisa valittuKisa = kisatTable.getSelectionModel().getSelectedItem();

        if (valittuKisa == null) {
            // Jos mitään ei ole valittu, näytä virheilmoitus
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ei valittu");
            alert.setHeaderText("Ei kisaa valittuna");
            alert.setContentText("Valitse poistettava kisa ensin.");
            alert.showAndWait();
            return;
        }

        // Poista osallistuja SQL-tietokannasta
        try (Connection conn = Database.connect()) {
            String sql = "DELETE FROM kisat WHERE kisa_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, valittuKisa.getKisaId());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Näytä virheilmoitus, jos poisto epäonnistuu
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virhe");
            alert.setHeaderText("Kisan poisto epäonnistui");
            alert.setContentText("Tietokantavirhe: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        // Poista osallistuja TableView:stä
        kisatTable.getItems().remove(valittuKisa);

        // Näytä vahvistusilmoitus
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Poisto onnistui");
        alert.setHeaderText("Kisa poistettu");
        alert.setContentText("Kisa " + valittuKisa.getNimi() + " on poistettu.");
        alert.showAndWait();
    }

    // Poista kuuntelijan avulla
    @FXML
    private void handlePoistaOttelu() {
        // Hae valittu osallistuja TableView:stä
        Ottelu valittuOttelu = ottelutTable.getSelectionModel().getSelectedItem();

        if (valittuOttelu == null) {
            // Jos mitään ei ole valittu, näytä virheilmoitus
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ei valittu");
            alert.setHeaderText("Ei ottelua valittuna");
            alert.setContentText("Valitse poistettava ottelu ensin.");
            alert.showAndWait();
            return;
        }

        // Poista osallistuja SQL-tietokannasta
        try (Connection conn = Database.connect()) {
            String sql = "DELETE FROM ottelut WHERE ottelu_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, valittuOttelu.getOtteluId());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Näytä virheilmoitus, jos poisto epäonnistuu
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virhe");
            alert.setHeaderText("Ottelun poisto epäonnistui");
            alert.setContentText("Tietokantavirhe: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        // Poista osallistuja TableView:stä
        ottelutTable.getItems().remove(valittuOttelu);

        // Näytä vahvistusilmoitus
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Poisto onnistui");
        alert.setHeaderText("Ottelu poistettu");
        alert.setContentText("Ottelu " + valittuOttelu.getOtteluId() + " on poistettu.");
        alert.showAndWait();
    }

    // Poista kuuntelijan avulla
    @FXML
    private void handlePoistaOsallistuja() {
        // Hae valittu osallistuja TableView:stä
        Osallistuja valittuOsallistuja = osallistujatTable.getSelectionModel().getSelectedItem();
    
        if (valittuOsallistuja == null) {
            // Jos mitään ei ole valittu, näytä virheilmoitus
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ei valittu");
            alert.setHeaderText("Ei osallistujaa valittuna");
            alert.setContentText("Valitse poistettava osallistuja ensin.");
            alert.showAndWait();
            return;
        }
    
        // Poista osallistuja SQL-tietokannasta
        try (Connection conn = Database.connect()) {
            String sql = "DELETE FROM osallistujat WHERE osallistuja_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, valittuOsallistuja.getOsallistujaId());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Näytä virheilmoitus, jos poisto epäonnistuu
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virhe");
            alert.setHeaderText("Osallistujan poisto epäonnistui");
            alert.setContentText("Tietokantavirhe: " + e.getMessage());
            alert.showAndWait();
            return;
        }
    
        // Poista osallistuja TableView:stä
            osallistujatTable.getItems().remove(valittuOsallistuja);
    
        // Näytä vahvistusilmoitus
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Poisto onnistui");
        alert.setHeaderText("Osallistuja poistettu");
        alert.setContentText("Osallistuja " + valittuOsallistuja.getNimi() + " on poistettu.");
        alert.showAndWait();
    }

    // Poista kuuntelijan avulla
    @FXML
    private void handlePoistaVeikkaus() {
        // Hae valittu osallistuja TableView:stä
        Veikkaus valittuVeikkaus = veikkauksetTable.getSelectionModel().getSelectedItem();

        if (valittuVeikkaus == null) {
            // Jos mitään ei ole valittu, näytä virheilmoitus
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ei valittu");
            alert.setHeaderText("Ei veikkausta valittuna");
            alert.setContentText("Valitse poistettava veikkaus ensin.");
            alert.showAndWait();
            return;
        }

        // Poista osallistuja SQL-tietokannasta
        try (Connection conn = Database.connect()) {
            String sql = "DELETE FROM veikkaukset WHERE veikkaus_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, valittuVeikkaus.getVeikkausId());
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Näytä virheilmoitus, jos poisto epäonnistuu
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Virhe");
            alert.setHeaderText("Veikkauksen poisto epäonnistui");
            alert.setContentText("Tietokantavirhe: " + e.getMessage());
            alert.showAndWait();
            return;
        }

        // Poista osallistuja TableView:stä
        veikkauksetTable.getItems().remove(valittuVeikkaus);

        // Näytä vahvistusilmoitus
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Poisto onnistui");
        alert.setHeaderText("Veikkaus poistettu");
        alert.setContentText("Veikkaus " + valittuVeikkaus.getVeikkausId() + " on poistettu.");
        alert.showAndWait();
    }

    // Poista kuuntelijan avulla
    @FXML
    private void handlePoistaKaynnissaOleva() {
    Piste valittuPiste = kaynnissaOlevatKisatTable.getSelectionModel().getSelectedItem();

    if (valittuPiste == null) {
        // Jos mitään ei ole valittu, näytä virheilmoitus
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ei valittu");
        alert.setHeaderText("Ei pisteitä valittuna");
        alert.setContentText("Valitse poistettavat pisteet ensin.");
        alert.showAndWait();
        return;
    }

    try {
        // Poista pisteet tietokannasta
        poistaPisteet(valittuPiste.getOsallistujaId(), true); // true = käynnissä olevat kisat

        // Poista pisteet TableView:stä
        kaynnissaOlevatKisatTable.getItems().remove(valittuPiste);

        // Näytä vahvistusilmoitus
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Poisto onnistui");
        alert.setHeaderText("Pisteet poistettu");
        alert.setContentText("Pisteet osallistujalle " + valittuPiste.getOsallistujanNimi() + " on poistettu.");
        alert.showAndWait();
        } catch (SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Virhe");
        alert.setHeaderText("Pisteiden poisto epäonnistui");
        alert.setContentText("Tietokantavirhe: " + e.getMessage());
        alert.showAndWait();
        }
    }

    // Poista kuuntelijan avulla
    @FXML
    private void handlePoistaMennyt() {
    Piste valittuPiste = menneetKisatTable.getSelectionModel().getSelectedItem();

    if (valittuPiste == null) {
        // Jos mitään ei ole valittu, näytä virheilmoitus
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ei valittu");
        alert.setHeaderText("Ei pisteitä valittuna");
        alert.setContentText("Valitse poistettavat pisteet ensin.");
        alert.showAndWait();
        return;
    }

    try {
        // Poista pisteet tietokannasta
        poistaPisteet(valittuPiste.getOsallistujaId(), false); // false = menneet kisat

        // Poista pisteet TableView:stä
        menneetKisatTable.getItems().remove(valittuPiste);

        // Näytä vahvistusilmoitus
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Poisto onnistui");
        alert.setHeaderText("Pisteet poistettu");
        alert.setContentText("Pisteet osallistujalle " + valittuPiste.getOsallistujanNimi() + " on poistettu.");
        alert.showAndWait();
    } catch (SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Virhe");
        alert.setHeaderText("Pisteiden poisto epäonnistui");
        alert.setContentText("Tietokantavirhe: " + e.getMessage());
        alert.showAndWait();
        }
    }

    // Poista pisteet yleisellä metodilla
    private void poistaPisteet(int osallistujaId, boolean kaynnissaOlevat) throws SQLException {
        String query;
        if (kaynnissaOlevat) {
            // Poista käynnissä olevien kisojen pisteet
            query = "DELETE FROM pisteet " +
                    "WHERE osallistuja_id = ? AND kisa_id IN " +
                    "(SELECT kisa_id FROM kisat WHERE loppupvm > DATE('now'))";
        } else {
            // Poista menneiden kisojen pisteet
            query = "DELETE FROM pisteet " +
                    "WHERE osallistuja_id = ? AND kisa_id IN " +
                    "(SELECT kisa_id FROM kisat WHERE loppupvm < DATE('now'))";
        }
    
        try (Connection connection = Database.connect();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, osallistujaId);
            statement.executeUpdate();
        }
    }

}
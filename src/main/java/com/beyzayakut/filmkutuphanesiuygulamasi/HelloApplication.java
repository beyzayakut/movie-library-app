package com.beyzayakut.filmkutuphanesiuygulamasi;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.*;

public class HelloApplication extends Application {

    private Connection connection; //SQLite veritabanıyla bağlantıyı temsil eder.
    private ObservableList<Film> filmListesi = FXCollections.observableArrayList(); //Film nesnelerini içeren bir dinamik liste oluşturur. Film nesneleri, her film için temel bilgileri içerir.

        @Override
        public void start(Stage primaryStage) { //JavaFX uygulamasını başlatır ve arayüzü oluşturur. GridPane kullanarak bileşenleri düzenler. Bu metotta ayrıca veritabanı bağlantısı oluşturulur ve mevcut filmler filmListesi listesine yüklenir.
            primaryStage.setTitle("Film Kütüphanesi"); //Stage bir JavaFX uygulamasının ana penceresini temsil eder ve uygulamanın kullanıcı arayüzünü barındırır. primaryStage ise uygulamanın başlangıcında oluşturulan ve gösterilen ilk ana penceredir.

            // Veritabanı bağlantısını oluştur
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:film_kutuphanesi.db"); //DriverManager Java uygulamalarının çeşitli veritabanlarına erişmesini sağlayan bir API'dir.
                System.out.println("Veritabanı bağlantısı başarılı.");

                // Filmleri veritabanından yükle
                filmListesi.addAll(getFilmler());
            } catch (SQLException e) { //hata ayıklama ve sorun giderme işlemlerinde yardımcı olabilir.
                e.printStackTrace();
            }

            GridPane grid = new GridPane();
            grid.setPadding(new Insets(10, 10, 10, 10)); //GridPane'in kenar boşluklarını ayarlar
            grid.setVgap(8); // sütunlar arasındaki boşluğu belirtirken
            grid.setHgap(10); // satırlar arasındaki boşluğu belirtir.

            // Film Adı(baslık)
            Label baslikLabel = new Label("Film Adı:");
            GridPane.setConstraints(baslikLabel, 0, 0);
            TextField baslikInput = new TextField();
            GridPane.setConstraints(baslikInput, 1, 0);

            // Yönetmen
            Label yonetmenLabel = new Label("Yönetmen:");
            GridPane.setConstraints(yonetmenLabel, 0, 1);
            TextField yonetmenInput = new TextField();
            GridPane.setConstraints(yonetmenInput, 1, 1);

            // Tür
            Label turLabel = new Label("Tür:");
            GridPane.setConstraints(turLabel, 0, 2);
            TextField turInput = new TextField();
            GridPane.setConstraints(turInput, 1, 2);

            // Yayın Yılı
            Label yayinYiliLabel = new Label("Yayın Yılı:");
            GridPane.setConstraints(yayinYiliLabel, 0, 3);
            TextField yayinYiliInput = new TextField();
            GridPane.setConstraints(yayinYiliInput, 1, 3);

            // Oyuncular
            Label oyuncularLabel = new Label("Oyuncular:");
            GridPane.setConstraints(oyuncularLabel, 0, 4);
            TextField oyuncularInput = new TextField();
            GridPane.setConstraints(oyuncularInput, 1, 4);

            // Kaydet Butonu
            Button kaydetButton = new Button("Kaydet");
            GridPane.setConstraints(kaydetButton, 1, 5);
            kaydetButton.setOnAction(e -> {
                String baslik = baslikInput.getText();
                String yonetmen = yonetmenInput.getText();
                String tur = turInput.getText();
                int yayinYili = Integer.parseInt(yayinYiliInput.getText());
                String oyuncular = oyuncularInput.getText();

                Film film = new Film(baslik, yonetmen, tur, yayinYili, oyuncular);
                filmListesi.add(film);

                // Veritabanına ekle
                try {
                    addFilmToDatabase(film);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }

                // Alanları temizle
                baslikInput.clear();
                yonetmenInput.clear();
                turInput.clear();
                yayinYiliInput.clear();
                oyuncularInput.clear();
            });

            // Film Listesi
            ListView<Film> filmListView = new ListView<>(filmListesi);
            filmListView.setPrefHeight(200);
            GridPane.setConstraints(filmListView, 2, 0, 1, 6);

            grid.getChildren().addAll(baslikLabel, baslikInput, yonetmenLabel, yonetmenInput, turLabel, turInput,
                    yayinYiliLabel, yayinYiliInput, oyuncularLabel, oyuncularInput, kaydetButton, filmListView);

            Scene scene = new Scene(grid, 500, 300);
            primaryStage.setScene(scene);
            primaryStage.show();
        }

        @Override
        public void stop() throws Exception {
            super.stop();
            connection.close();
        }

        // Veritabanından filmleri getir
        private ObservableList<Film> getFilmler() throws SQLException {
            ObservableList<Film> filmler = FXCollections.observableArrayList();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM filmler");
            while (resultSet.next()) {
                String baslik = resultSet.getString("baslik");
                String yonetmen = resultSet.getString("yonetmen");
                String tur = resultSet.getString("tur");
                int yayinYili = resultSet.getInt("yayin_yili");
                String oyuncular = resultSet.getString("oyuncular");

                Film film = new Film(baslik, yonetmen, tur, yayinYili, oyuncular);
                filmler.add(film);
            }
            return filmler;
        }

        // Film veritabanına ekle
        private void addFilmToDatabase(Film film) throws SQLException {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO filmler (baslik, yonetmen, tur, yayin_yili, oyuncular) VALUES (?, ?, ?, ?, ?)");
            statement.setString(1, film.getBaslik());
            statement.setString(2, film.getYonetmen());
            statement.setString(3, film.getTur());
            statement.setInt(4, film.getYayinYili());
            statement.setString(5, film.getOyuncular());
            statement.executeUpdate();
        }

        public static void main(String[] args) {
            launch(args);
        }
    }

    class Film {
        private String baslik;
        private String yonetmen;
        private String tur;
        private int yayinYili;
        private String oyuncular;

        public Film(String baslik, String yonetmen, String tur, int yayinYili, String oyuncular) {
            this.baslik = baslik;
            this.yonetmen = yonetmen;
            this.tur = tur;
            this.yayinYili = yayinYili;
            this.oyuncular = oyuncular;
        }

        public String getBaslik() {
            return baslik;
        }

        public String getYonetmen() {
            return yonetmen;
        }

        public String getTur() {
            return tur;
        }

        public int getYayinYili() {
            return yayinYili;
        }

        public String getOyuncular() {
            return oyuncular;
        }

        @Override
        public String toString() {
            return baslik + " (" + yayinYili + ")";
        }
    }

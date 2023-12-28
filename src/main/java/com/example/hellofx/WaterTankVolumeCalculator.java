package com.example.hellofx;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.*;

/**
 * Aplikasi JavaFX untuk menghitung volume dan biaya total tandon air.
 */

public class WaterTankVolumeCalculator extends Application {

    // Field untuk input
    private TextField shopNameField, numberOfTanksField, tankHeightField, tankDiameterField;

    // Area untuk menampilkan hasil perhitungan
    private TextArea resultArea;

    // Tabel untuk menampilkan data tandon
    private TableView<Tandon> tableView;

    // Daftar observabel untuk menyimpan data tandon
    private ObservableList<Tandon> tandonList = FXCollections.observableArrayList();

    /**
     * Representasi dari sebuah tandon, dengan properti sederhana untuk nama toko,
     * jumlah tandon, tinggi tandon, diameter tandon, volume total, dan biaya total.
     */
    public static class Tandon {
        private SimpleStringProperty shopName;
        private SimpleIntegerProperty numberOfTanks;
        private SimpleDoubleProperty tankHeight;
        private SimpleDoubleProperty tankDiameter;
        private SimpleDoubleProperty totalVolume;
        private SimpleDoubleProperty totalCost;

        // Konstruktor untuk membuat objek Tandon
        public Tandon(String shopName, int numberOfTanks, double tankHeight, double tankDiameter, double totalVolume, double totalCost) {
            // Inisialisasi properti sederhana
            this.shopName = new SimpleStringProperty(shopName);
            this.numberOfTanks = new SimpleIntegerProperty(numberOfTanks);
            this.tankHeight = new SimpleDoubleProperty(tankHeight);
            this.tankDiameter = new SimpleDoubleProperty(tankDiameter);
            this.totalVolume = new SimpleDoubleProperty(totalVolume);
            this.totalCost = new SimpleDoubleProperty(totalCost);
        }

        // Metode setter untuk mengatur nilai properti
        public void setNumberOfTanks(int numberOfTanks) {
            this.numberOfTanks.set(numberOfTanks);
        }

        public void setTankHeight(double tankHeight) {
            this.tankHeight.set(tankHeight);
        }

        public void setTankDiameter(double tankDiameter) {
            this.tankDiameter.set(tankDiameter);
        }

        public void setShopName(String shopName) {
            this.shopName.set(shopName);
        }

        // Metode getter untuk mendapatkan nilai properti
        public String getShopName() {
            return shopName.get();
        }

        public int getNumberOfTanks() {
            return numberOfTanks.get();
        }

        public double getTankHeight() {
            return tankHeight.get();
        }

        public double getTankDiameter() {
            return tankDiameter.get();
        }

        public double getTotalVolume() {
            return totalVolume.get();
        }

        public double getTotalCost() {
            return totalCost.get();
        }
    }

    /**
     * Metode untuk memulai aplikasi JavaFX.
     *
     * @param primaryStage Panggung utama untuk menampilkan antarmuka pengguna.
     */
    @Override
    public void start(Stage primaryStage) {
        // Pengaturan judul aplikasi
        primaryStage.setTitle("Water Tank Volume Calculator");

        // Membuat grid untuk tata letak antarmuka pengguna
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        // Membuat kolom input dengan label dan field
        shopNameField = createTextField("Nama Toko");
        numberOfTanksField = createTextField("Jumlah Tandon");
        tankHeightField = createTextField("Tinggi Tandon (m)");
        tankDiameterField = createTextField("Diameter Tandon (m)");

        // Membuat tombol untuk menghitung volume
        Button calculateButton = new Button("Hitung Volume");
        calculateButton.setOnAction(event -> calculateVolume());

        // Membuat tombol untuk menambah data tandon
        Button addButton = new Button("Tambah");
        addButton.setOnAction(event -> addTandon());

        // Membuat tombol untuk menghapus data tandon yang dipilih
        Button deleteButton = new Button("Hapus");
        deleteButton.setOnAction(event -> deleteTandon());

        // Membuat tombol untuk memperbarui data tandon yang dipilih
        Button updateButton = new Button("Update");
        updateButton.setOnAction(event -> updateTandon());

        // Area untuk menampilkan hasil perhitungan
        resultArea = new TextArea();
        resultArea.setEditable(false);
        resultArea.setPrefRowCount(10);

        // Tabel untuk menampilkan data tandon
        tableView = createTableView();
        tableView.setItems(tandonList);

        // Membaca data dari file pada saat aplikasi dimulai
        readDataFromFile();

        // Menambahkan elemen-elemen ke dalam grid
        grid.add(new Label("Masukkan Informasi Tandon"), 0, 0, 2, 1);
        grid.add(shopNameField, 0, 1, 2, 1);
        grid.add(numberOfTanksField, 0, 2, 2, 1);
        grid.add(tankHeightField, 0, 3, 2, 1);
        grid.add(tankDiameterField, 0, 4, 2, 1);
        grid.add(calculateButton, 0, 5);
        grid.add(new Label("Hasil Penghitungan"), 0, 6, 2, 1);
        grid.add(resultArea, 0, 7, 2, 1);
        grid.add(new Label("Data Tandon"), 0, 8, 2, 1);
        grid.add(tableView, 0, 9, 2, 1);
        grid.add(addButton, 0, 10);
        grid.add(updateButton, 1, 10);
        grid.add(deleteButton, 2, 10);

        // Membuat scene dan menampilkan panggung utama
        Scene scene = new Scene(grid, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Metode untuk membersihkan field input
     */

    private void clearFields() {
        shopNameField.clear();
        numberOfTanksField.clear();
        tankHeightField.clear();
        tankDiameterField.clear();
        resultArea.clear();
    }


    /**
     * Membuat dan mengembalikan objek TextField dengan teks petunjuk tertentu.
     *
     * @param promptText Teks petunjuk untuk TextField.
     * @return Objek TextField yang telah dibuat dengan teks petunjuk.
     */
    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }


    /**
     * Membuat dan mengembalikan objek TableView<Tandon> beserta kolom-kolomnya
     * untuk menampilkan informasi mengenai tandon.
     *
     * @return Objek TableView<Tandon> yang telah dibuat dengan kolom-kolom yang sesuai.
     */
    private TableView<Tandon> createTableView() {
        TableView<Tandon> table = new TableView<>();

        // Membuat kolom-kolom untuk menampilkan properti tandon
        TableColumn<Tandon, String> shopNameColumn = new TableColumn<>("Nama Toko");
        shopNameColumn.setCellValueFactory(new PropertyValueFactory<>("shopName"));

        TableColumn<Tandon, Integer> numberOfTanksColumn = new TableColumn<>("Jumlah Tandon");
        numberOfTanksColumn.setCellValueFactory(new PropertyValueFactory<>("numberOfTanks"));

        TableColumn<Tandon, Double> tankHeightColumn = new TableColumn<>("Tinggi Tandon (m)");
        tankHeightColumn.setCellValueFactory(new PropertyValueFactory<>("tankHeight"));

        TableColumn<Tandon, Double> tankDiameterColumn = new TableColumn<>("Diameter Tandon (m)");
        tankDiameterColumn.setCellValueFactory(new PropertyValueFactory<>("tankDiameter"));

        TableColumn<Tandon, Double> totalVolumeColumn = new TableColumn<>("Volume Total (m^3)");
        totalVolumeColumn.setCellValueFactory(new PropertyValueFactory<>("totalVolume"));

        TableColumn<Tandon, Double> totalCostColumn = new TableColumn<>("Biaya Total (Rp)");
        totalCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        // Menambahkan kolom-kolom ke dalam tabel
        table.getColumns().addAll(shopNameColumn, numberOfTanksColumn, tankHeightColumn, tankDiameterColumn,
                totalVolumeColumn, totalCostColumn);

        // Mengatur model seleksi untuk memungkinkan pemilihan beberapa baris
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        return table;
    }

    /**
     * Menghitung volume total dan biaya total berdasarkan input yang dimasukkan.
     */
    private void calculateVolume() {
        try {
            String shopName = shopNameField.getText();
            int numberOfTanks = Integer.parseInt(numberOfTanksField.getText());
            double tankHeight = Double.parseDouble(tankHeightField.getText());
            double tankDiameter = Double.parseDouble(tankDiameterField.getText());

            // Defensive programming
            if (numberOfTanks <= 0 || tankHeight <= 0 || tankDiameter <= 0) {
                resultArea.setText("Masukkan angka yang valid untuk menambah data.");
                return; // Menghentikan eksekusi lebih lanjut jika ada parameter yang tidak valid
            }

            double radius = tankDiameter / 2.0;
            double totalVolume = Math.PI * Math.pow(radius, 2) * tankHeight * numberOfTanks;
            double totalCost = totalVolume * 500;

            resultArea.setText(String.format("Nama Toko: %s\nJumlah Tandon: %d\nVolume Total: %.2f m^3\nBiaya Total: Rp.%.2f",
                    shopName, numberOfTanks, totalVolume, totalCost));

        } catch (NumberFormatException e) {
            resultArea.setText("Masukkan angka!.");
        }
    }

    /**
     * Menambahkan informasi tandon baru ke dalam daftar dan menyimpannya ke dalam file teks.
     */
    private void addTandon() {
        try {
            String shopName = shopNameField.getText();
            int numberOfTanks = Integer.parseInt(numberOfTanksField.getText());
            double tankHeight = Double.parseDouble(tankHeightField.getText());
            double tankDiameter = Double.parseDouble(tankDiameterField.getText());

            // Defensive programming
            if (numberOfTanks <= 0 || tankHeight <= 0 || tankDiameter <= 0) {
                resultArea.setText("Masukkan angka yang valid untuk menambah data.");
                return; // Menghentikan eksekusi lebih lanjut jika ada parameter yang tidak valid
            }

            double radius = tankDiameter / 2.0;
            double totalVolume = Math.PI * Math.pow(radius, 2) * tankHeight * numberOfTanks;
            double totalCost = totalVolume * 500;

            // Membuat objek Tandon baru dan menambahkannya ke dalam daftar observabel
            Tandon newTandon = new Tandon(shopName, numberOfTanks, tankHeight, tankDiameter, totalVolume, totalCost);
            tandonList.add(newTandon);

            // Menyimpan hasil ke dalam file teks
            saveResultToFile(String.format("%s,%d,%.2f,%.2f,%.2f,%.2f\n",
                    shopName, numberOfTanks, tankHeight, tankDiameter, totalVolume, totalCost));

            // Mengosongkan field input setelah penambahan
            clearFields();

        } catch (NumberFormatException e) {
            resultArea.setText("Masukkan angka!");
        }
    }



    /**
     * Metode untuk memperbarui data tandon yang dipilih
     */
    private void updateTandon() {
        ObservableList<Tandon> selectedItems = tableView.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
            // Hanya memproses satu item yang pertama kali terpilih
            Tandon selectedTandon = selectedItems.get(0);

            // Memperbarui nilai pada objek selectedTandon dengan nilai dari field input
            selectedTandon.setShopName(shopNameField.getText());
            selectedTandon.setNumberOfTanks(Integer.parseInt(numberOfTanksField.getText()));
            selectedTandon.setTankHeight(Double.parseDouble(tankHeightField.getText()));
            selectedTandon.setTankDiameter(Double.parseDouble(tankDiameterField.getText()));

            // Memperbarui file teks setelah pembaruan
            updateFileAfterUpdate();

            resultArea.setText("Data Tandon berhasil diperbarui.");

            // Mengosongkan field setelah pembaruan
            clearFields();
        } else {
            resultArea.setText("Pilih Tandon yang ingin diperbarui.");
        }
    }

    /**
     * Metode untuk memperbarui file teks setelah pembaruan data
     */
    private void updateFileAfterUpdate() {
        try {
            // Menulis ulang seluruh data ke dalam file
            File file = new File("hasil_penghitungan.txt");
            FileWriter writer = new FileWriter(file, false);

            for (Tandon tandon : tandonList) {
                String dataLine = String.format("%s,%d,%.2f,%.2f,%.2f,%.2f\n",
                        tandon.getShopName(), tandon.getNumberOfTanks(), tandon.getTankHeight(),
                        tandon.getTankDiameter(), tandon.getTotalVolume(), tandon.getTotalCost());
                writer.write(dataLine);
            }

            writer.close();

            // Memperbarui TableView di Thread Aplikasi JavaFX
            Platform.runLater(() -> {
                // Memperbarui TableView setelah menulis ke dalam file
                tableView.refresh();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metode untuk menghapus data tandon yang dipilih.
     */
    private void deleteTandon() {
        ObservableList<Tandon> selectedItems = tableView.getSelectionModel().getSelectedItems();
        if (!selectedItems.isEmpty()) {
            // Menghapus semua item tandon yang dipilih dari daftar observabel
            tandonList.removeAll(selectedItems);

            // Memperbarui file teks setelah menghapus data
            updateFileAfterDeletion();

            resultArea.setText("Data Tandon berhasil dihapus.");

        } else {
            resultArea.setText("Pilih Tandon yang ingin dihapus.");
        }
    }

    /**
     * Metode untuk memperbarui file teks setelah menghapus data tandon.
     */
    private void updateFileAfterDeletion() {
        try {
            // Menulis ulang seluruh data ke dalam file
            File file = new File("hasil_penghitungan.txt");
            FileWriter writer = new FileWriter(file, false);

            for (Tandon tandon : tandonList) {
                String dataLine = String.format("%s,%d,%.2f,%.2f,%.2f,%.2f\n",
                        tandon.getShopName(), tandon.getNumberOfTanks(), tandon.getTankHeight(),
                        tandon.getTankDiameter(), tandon.getTotalVolume(), tandon.getTotalCost());
                writer.write(dataLine);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metode untuk menyimpan hasil perhitungan ke dalam file teks.
     *
     * @param resultText Teks hasil perhitungan yang akan disimpan.
     */
    private void saveResultToFile(String resultText) {
        try {
            // Mengganti ekstensi file menjadi ".txt"
            File file = new File("hasil_penghitungan.txt");
            FileWriter writer = new FileWriter(file, true);
            writer.write(resultText);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metode untuk membaca data tandon dari file teks dan memuatnya ke dalam daftar observabel.
     *
     * @throws IOException jika terjadi kesalahan saat membaca dari file.
     */
    private void readDataFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("hasil_penghitungan.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 6) {
                    String shopName = data[0];
                    int numberOfTanks = Integer.parseInt(data[1]);
                    double tankHeight = Double.parseDouble(data[2]);
                    double tankDiameter = Double.parseDouble(data[3]);
                    double totalVolume = Double.parseDouble(data[4]);
                    double totalCost = Double.parseDouble(data[5]);

                    Tandon tandon = new Tandon(shopName, numberOfTanks, tankHeight, tankDiameter, totalVolume, totalCost);
                    tandonList.add(tandon);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metode utama untuk menjalankan aplikasi.
     *
     * @param args Argumen baris perintah (command-line).
     */
    public static void main(String[] args) {
        launch(args);
    }
}


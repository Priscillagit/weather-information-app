import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * WeatherApp.java
 * Main JavaFX application class for the Weather Information App.
 * Handles GUI layout, user interaction, and displays weather data.
 */

public class WeatherApp extends Application {

    private String API_KEY;

    private TextField cityField;
    private ComboBox<String> tempUnitBox;
    private ComboBox<String> windUnitBox;
    private Label locationLabel;
    private Label tempLabel;
    private Label humidityLabel;
    private Label windLabel;
    private Label conditionLabel;
    private Label iconLabel;
    private Label statusLabel;
    private VBox forecastBox;
    private ListView<String> historyList;

    private BorderPane root;
    private final List<String> searchHistory = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("config.properties"));
            API_KEY = props.getProperty("API_KEY");
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage.setTitle("Weather Information App");

        root = new BorderPane();
        root.setPadding(new Insets(15));

        Label title = new Label("Weather Information App");
        title.setFont(Font.font(24));
        title.setTextFill(Color.WHITE);

        HBox topBar = new HBox(title);
        topBar.setAlignment(Pos.CENTER);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: linear-gradient(to right, #4facfe, #00f2fe);");

        root.setTop(topBar);

        VBox inputPane = buildInputPane();
        VBox weatherPane = buildWeatherPane();
        VBox historyPane = buildHistoryPane();

        HBox centerLayout = new HBox(20, inputPane, weatherPane, historyPane);
        centerLayout.setPadding(new Insets(15));
        centerLayout.setAlignment(Pos.TOP_CENTER);

        root.setCenter(centerLayout);

        applyDynamicBackground();

        Scene scene = new Scene(root, 1100, 650);
        stage.setScene(scene);
        stage.show();
    }

    private VBox buildInputPane() {
        Label inputTitle = new Label("Search Weather");
        inputTitle.setFont(Font.font(18));

        cityField = new TextField();
        cityField.setPromptText("Enter city name");

        tempUnitBox = new ComboBox<>();
        tempUnitBox.getItems().addAll("Celsius", "Fahrenheit");
        tempUnitBox.setValue("Celsius");

        windUnitBox = new ComboBox<>();
        windUnitBox.getItems().addAll("m/s", "km/h", "mph");
        windUnitBox.setValue("m/s");

        Button searchButton = new Button("Get Weather");
        searchButton.setOnAction(e -> fetchWeather());

        statusLabel = new Label();
        statusLabel.setTextFill(Color.RED);

        VBox inputPane = new VBox(12,
                inputTitle,
                new Label("City:"),
                cityField,
                new Label("Temperature Unit:"),
                tempUnitBox,
                new Label("Wind Speed Unit:"),
                windUnitBox,
                searchButton,
                statusLabel
        );

        inputPane.setPadding(new Insets(15));
        inputPane.setPrefWidth(250);
        inputPane.setStyle("""
                -fx-background-color: white;
                -fx-border-color: lightgray;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                """);

        return inputPane;
    }

    private VBox buildWeatherPane() {
        Label weatherTitle = new Label("Current Weather");
        weatherTitle.setFont(Font.font(18));

        iconLabel = new Label("☀");
        iconLabel.setFont(Font.font(45));

        locationLabel = new Label("Location: -");
        tempLabel = new Label("Temperature: -");
        humidityLabel = new Label("Humidity: -");
        windLabel = new Label("Wind Speed: -");
        conditionLabel = new Label("Condition: -");

        Label forecastTitle = new Label("Short-Term Forecast");
        forecastTitle.setFont(Font.font(18));

        forecastBox = new VBox(8);

        VBox weatherPane = new VBox(12,
                weatherTitle,
                iconLabel,
                locationLabel,
                tempLabel,
                humidityLabel,
                windLabel,
                conditionLabel,
                new Separator(),
                forecastTitle,
                forecastBox
        );

        weatherPane.setPadding(new Insets(15));
        weatherPane.setPrefWidth(450);
        weatherPane.setStyle("""
                -fx-background-color: white;
                -fx-border-color: lightgray;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                """);

        return weatherPane;
    }

    private VBox buildHistoryPane() {
        Label historyTitle = new Label("Search History");
        historyTitle.setFont(Font.font(18));

        historyList = new ListView<>();
        historyList.setPrefHeight(500);

        VBox historyPane = new VBox(12, historyTitle, historyList);
        historyPane.setPadding(new Insets(15));
        historyPane.setPrefWidth(250);
        historyPane.setStyle("""
                -fx-background-color: white;
                -fx-border-color: lightgray;
                -fx-border-radius: 10;
                -fx-background-radius: 10;
                """);

        return historyPane;
    }

    private void fetchWeather() {
        String city = cityField.getText().trim();

        if (city.isEmpty()) {
            statusLabel.setText("Please enter a city name.");
            return;
        }

        statusLabel.setText("Loading weather data...");

        try {
            String tempUnit = tempUnitBox.getValue();
            String apiUnits = tempUnit.equals("Celsius") ? "metric" : "imperial";

            WeatherService service = new WeatherService(API_KEY);
            WeatherData current = service.getCurrentWeather(city, apiUnits);
            List<ForecastData> forecast = service.getForecast(city, apiUnits);

            updateCurrentWeather(current);
            updateForecast(forecast);
            addToHistory(city);
            statusLabel.setText("Weather data loaded successfully.");
            applyDynamicBackground();

        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void updateCurrentWeather(WeatherData data) {
        locationLabel.setText("Location: " + data.getCityName());
        tempLabel.setText("Temperature: " + String.format("%.1f %s",
                data.getTemperature(),
                tempUnitBox.getValue().equals("Celsius") ? "°C" : "°F"));
        humidityLabel.setText("Humidity: " + data.getHumidity() + "%");
        windLabel.setText("Wind Speed: " + String.format("%.2f %s",
                convertWindSpeed(data.getWindSpeed()),
                windUnitBox.getValue()));
        conditionLabel.setText("Condition: " + data.getCondition());
        iconLabel.setText(getWeatherIcon(data.getCondition()));
    }

    private void updateForecast(List<ForecastData> forecastList) {
        forecastBox.getChildren().clear();

        for (ForecastData item : forecastList) {
            Label forecastLabel = new Label(
                    item.getDateTime() + " | " +
                    String.format("%.1f", item.getTemperature()) +
                    (tempUnitBox.getValue().equals("Celsius") ? " °C" : " °F") +
                    " | " + item.getCondition()
            );
            forecastBox.getChildren().add(forecastLabel);
        }
    }

    private void addToHistory(String city) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String entry = city + " searched at " + timestamp;
        searchHistory.add(0, entry);
        historyList.getItems().setAll(searchHistory);
    }

    private double convertWindSpeed(double speed) {
        return switch (windUnitBox.getValue()) {
            case "km/h" -> speed * 3.6;
            case "mph" -> speed * 2.237;
            default -> speed;
        };
    }

    private String getWeatherIcon(String condition) {
        condition = condition.toLowerCase();

        if (condition.contains("clear")) return "☀";
        if (condition.contains("cloud")) return "☁";
        if (condition.contains("rain")) return "🌧";
        if (condition.contains("storm") || condition.contains("thunder")) return "⛈";
        if (condition.contains("snow")) return "❄";
        if (condition.contains("mist") || condition.contains("fog") || condition.contains("haze")) return "🌫";

        return "🌤";
    }

    private void applyDynamicBackground() {
        int hour = LocalDateTime.now().getHour();

        String style;
        if (hour >= 6 && hour < 12) {
            style = "-fx-background-color: linear-gradient(to bottom, #89f7fe, #66a6ff);";
        } else if (hour >= 12 && hour < 18) {
            style = "-fx-background-color: linear-gradient(to bottom, #f6d365, #fda085);";
        } else {
            style = "-fx-background-color: linear-gradient(to bottom, #141e30, #243b55);";
        }

        root.setStyle(style);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
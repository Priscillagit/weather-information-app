import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * WeatherService.java
 * Handles API requests to OpenWeatherMap and processes JSON responses.
 */

public class WeatherService {

    private final String apiKey;

    public WeatherService(String apiKey) {
        this.apiKey = apiKey;
    }

    public WeatherData getCurrentWeather(String city, String units) throws Exception {
        String endpoint = "https://api.openweathermap.org/data/2.5/weather?q=" +
                city.replace(" ", "%20") +
                "&appid=" + apiKey +
                "&units=" + units;

        String response = getApiResponse(endpoint);
        JSONObject json = new JSONObject(response);

        if (json.has("cod") && json.get("cod").toString().equals("404")) {
            throw new Exception("City not found.");
        }

        String cityName = json.getString("name");
        JSONObject main = json.getJSONObject("main");
        JSONArray weatherArray = json.getJSONArray("weather");
        JSONObject wind = json.getJSONObject("wind");

        double temperature = main.getDouble("temp");
        int humidity = main.getInt("humidity");
        double windSpeed = wind.getDouble("speed");
        String condition = weatherArray.getJSONObject(0).getString("main");

        return new WeatherData(cityName, temperature, humidity, windSpeed, condition);
    }

    public List<ForecastData> getForecast(String city, String units) throws Exception {
        String endpoint = "https://api.openweathermap.org/data/2.5/forecast?q=" +
                city.replace(" ", "%20") +
                "&appid=" + apiKey +
                "&units=" + units;

        String response = getApiResponse(endpoint);
        JSONObject json = new JSONObject(response);

        if (json.has("cod") && json.getString("cod").equals("404")) {
            throw new Exception("Forecast not available for this city.");
        }

        JSONArray list = json.getJSONArray("list");
        List<ForecastData> forecastData = new ArrayList<>();

        for (int i = 0; i < Math.min(5, list.length()); i++) {
            JSONObject item = list.getJSONObject(i);
            String dateTime = item.getString("dt_txt");
            double temperature = item.getJSONObject("main").getDouble("temp");
            String condition = item.getJSONArray("weather").getJSONObject(0).getString("main");

            forecastData.add(new ForecastData(dateTime, temperature, condition));
        }

        return forecastData;
    }

    private String getApiResponse(String endpoint) throws Exception {
        URL url = new URL(endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream())
            );
            StringBuilder error = new StringBuilder();
            String line;

            while ((line = errorReader.readLine()) != null) {
                error.append(line);
            }
            errorReader.close();
            throw new Exception("API request failed: " + error);
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
        );

        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }
}
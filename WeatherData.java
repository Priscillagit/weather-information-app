public class WeatherData {
    private final String cityName;
    private final double temperature;
    private final int humidity;
    private final double windSpeed;
    private final String condition;

    /**
 * WeatherData.java
 * Model class that stores current weather information.
 */

    public WeatherData(String cityName, double temperature, int humidity, double windSpeed, String condition) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.condition = condition;
    }

    public String getCityName() {
        return cityName;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public String getCondition() {
        return condition;
    }
}
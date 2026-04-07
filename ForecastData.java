public class ForecastData {
    private final String dateTime;
    private final double temperature;
    private final String condition;


    /**
 * ForecastData.java
 * Model class that stores short-term forecast data.
 */

    public ForecastData(String dateTime, double temperature, String condition) {
        this.dateTime = dateTime;
        this.temperature = temperature;
        this.condition = condition;
    }

    public String getDateTime() {
        return dateTime;
    }

    public double getTemperature() {
        return temperature;
    }

    public String getCondition() {
        return condition;
    }
}
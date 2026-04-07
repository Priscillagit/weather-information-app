# Weather Information App

## Description
This is a JavaFX-based Weather Information App that provides real-time weather updates using the OpenWeatherMap API.

The application allows users to enter a city name and retrieve weather details such as temperature, humidity, wind speed, and conditions.

---

## Features

- Search weather by city name
- Display current temperature, humidity, wind speed, and condition
- Show short-term weather forecast
- Weather icons using symbols
- Unit conversion (Celsius/Fahrenheit and wind speed units)
- Error handling for invalid city input
- Search history with timestamps
- Dynamic background based on time of day
- Uses JavaFX Task for background processing

---

## Technologies Used

- Java
- JavaFX
- OpenWeatherMap API
- org.json library

---

## How to Run

1. Open the project in VS Code
2. Ensure JavaFX SDK is installed
3. Ensure `json-20231013.jar` is in the `lib` folder
4. Run using:
   - F5 (Run and Debug)

---

## Project Structure


waetherApplication/
├── javafx-sdk-26/
├── lib/
│ └── json-20231013.jar
├── WeatherApp.java
├── WeatherService.java
├── WeatherData.java
├── ForecastData.java
├── .vscode/
│ ├── launch.json
│ └── settings.json
└── README.md


---

## Notes

- Internet connection is required for API requests.
- If an invalid city is entered, an error message will be displayed.
- The application uses background threading to prevent UI freezing.

---

## Author

Priscilla Duodu Dankwa
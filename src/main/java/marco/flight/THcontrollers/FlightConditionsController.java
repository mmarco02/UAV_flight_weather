package marco.flight.THcontrollers;

import marco.flight.services.WeatherService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.json.JSONObject;
import org.json.JSONArray;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Controller
public class FlightConditionsController {

    @Autowired
    private WeatherService weatherService;

    @GetMapping("/flight-conditions")
    public String getFlightConditions(@RequestParam double latitude, @RequestParam double longitude,
                                      @RequestParam(required = false) String date, Model model) throws JSONException {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime displayDate = date != null ? LocalDateTime.parse(date) : currentDate;

        try {
            String weatherData = weatherService.getWeather(latitude, longitude, displayDate);
            JSONObject weatherJson = new JSONObject(weatherData);
            JSONArray days = weatherJson.getJSONArray("days");
            String timezone = weatherJson.getString("timezone");

            int daysRange = (int) LocalDate.now().until(displayDate.toLocalDate(), java.time.temporal.ChronoUnit.DAYS);

            if (daysRange < 0 || daysRange >= days.length()) {
                model.addAttribute("errorMessage", "The selected date is out of the available range.");
                System.err.println("The selected date is out of the available range.");
                return "error";
            }

            JSONObject daysJSONObject = days.getJSONObject(daysRange);


            double tempMax = daysJSONObject.getDouble("tempmax");
            double tempMin = daysJSONObject.getDouble("tempmin");
            double windSpeed = daysJSONObject.getDouble("windspeed");
            double windGust = daysJSONObject.getDouble("windgust");
            double windDir = daysJSONObject.getDouble("winddir");
            double visibility = daysJSONObject.getDouble("visibility");
            double rain = daysJSONObject.getDouble("precip");
            double snow = daysJSONObject.getDouble("snow");
            double humidity = daysJSONObject.getDouble("humidity");
            var sunrise = daysJSONObject.getString("sunrise");
            var sunset = daysJSONObject.getString("sunset");
            String[] weatherStations = daysJSONObject.getString("stations").split(",");
            List<String> weatherStationsList = Arrays.asList(weatherStations);


            String flightSuggestion = evaluateFlightConditions(tempMax, tempMin, windSpeed, windGust, visibility, rain, snow, humidity);

            model.addAttribute("latitude", latitude);
            model.addAttribute("longitude", longitude);
            model.addAttribute("weatherDescription", daysJSONObject.getString("description"));
            model.addAttribute("tempMax", tempMax);
            model.addAttribute("tempMin", tempMin);
            model.addAttribute("windSpeed", windSpeed);
            model.addAttribute("windGust", windGust);
            model.addAttribute("windDir", windDir);
            model.addAttribute("visibility", visibility);
            model.addAttribute("rain", rain);
            model.addAttribute("snow", snow);
            model.addAttribute("humidity", humidity);
            model.addAttribute("flightSuggestion", flightSuggestion);
            model.addAttribute("date", displayDate);
            model.addAttribute("time", displayDate.toLocalTime());
            model.addAttribute("previousDate", displayDate.minusDays(1));
            model.addAttribute("nextDate", displayDate.plusDays(1));
            model.addAttribute("timezone", timezone);
            model.addAttribute("sunrise", sunrise);
            model.addAttribute("sunset", sunset);
            model.addAttribute("weatherStations", weatherStationsList);

            return "flightConditions";

        } catch (JSONException e) {
            model.addAttribute("errorMessage", "Error processing weather data.");
            System.err.println(e);
            return "error";
        }
    }

    private String evaluateFlightConditions(double tempMax, double tempMin, double windSpeed, double windGust, double visibility, double rain, double snow, double humidity) {
        if (windSpeed <= 10 && windGust <= 15 && tempMax >= 15 && tempMax <= 25 && visibility > 10 && rain == 0 && snow == 0 && humidity < 70) {
            return "Excellent day to fly! Conditions are ideal with mild temperatures, low wind, and clear visibility.";
        } else if (windSpeed <= 20 && windGust <= 25 && (tempMax >= 10 && tempMax <= 30) && visibility > 5 && rain <= 5 && snow <= 5 && humidity < 80) {
            return "Good day to fly! Conditions are favorable with moderate temperatures and manageable wind.";
        } else if (windSpeed <= 30 && windGust <= 40 && (tempMax >= 5 && tempMax <= 35) && visibility > 2 && rain <= 10 && snow <= 10 && humidity < 90) {
            return "Fair day to fly, exercise caution. Conditions are less ideal with higher wind speeds and possible precipitation.";
        }else if (windSpeed <= 40 && windGust <= 50 && (tempMax >= 0 && tempMax <= 40) && visibility > 1 && rain <= 20 && snow <= 20 && humidity < 100) {
            return "Marginal conditions for flying. It may be challenging due to strong winds, poor visibility, or precipitation.";
        }else if (windSpeed <= 50 && windGust <= 60 && (tempMax >= -5 && tempMax <= 45) && visibility > 0 && rain <= 30 && snow <= 30 && humidity < 110) {
            return "Poor conditions for flying. It might be unsafe due to high wind speeds, low visibility, or severe weather.";
        }else if (windSpeed <= 60 && windGust <= 70 && (tempMax >= -10 && tempMax <= 50) && visibility > -1 && rain <= 40 && snow <= 40 && humidity < 120) {
            return "Very poor conditions for flying. It is unsafe due to very high wind speeds, very low visibility, or severe weather.";
        }else if (windSpeed <= 70 && windGust <= 80 && (tempMax >= -15 && tempMax <= 55) && visibility > -2 && rain <= 50 && snow <= 50 && humidity < 130) {
            return "Extremely poor conditions for flying. It is extremely unsafe due to extremely high wind speeds, extremely low visibility, or extremely severe weather.";
        }else if (windSpeed <= 80 && windGust <= 90 && (tempMax >= -20 && tempMax <= 60) && visibility > -3 && rain <= 60 && snow <= 60 && humidity < 140) {
            return "Dangerous conditions for flying. It is dangerous due to dangerous wind speeds, dangerous visibility, or dangerous weather.";
        } else {
            return "Poor conditions for flying";
        }
    }
}
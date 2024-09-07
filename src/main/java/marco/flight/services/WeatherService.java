package marco.flight.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class WeatherService {

    private static final String API_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";
    private static final String API_KEY = "GZFF79XTV5ZBPGSXXF7DPH29S";  // Replace with your actual key

    public String getWeather(double latitude, double longitude, LocalDateTime displayDate) {
        RestTemplate restTemplate = new RestTemplate();

        LocalDate today = LocalDate.now();
        LocalDate threeDaysLater = today.plusDays(3);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String todayFormatted = today.format(formatter);
        String threeDaysLaterFormatted = threeDaysLater.format(formatter);

        String url = API_URL + latitude + "," + longitude + "/" + todayFormatted + "/" + threeDaysLaterFormatted + "?unitGroup=metric&key=" + API_KEY + "&contentType=json";

        return restTemplate.getForObject(url, String.class);
    }
}

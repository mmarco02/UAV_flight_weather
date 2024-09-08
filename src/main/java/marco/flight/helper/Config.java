package marco.flight.helper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
    public String getApiKey() {
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("src/main/resources/application.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop.getProperty("api.key");
    }
}
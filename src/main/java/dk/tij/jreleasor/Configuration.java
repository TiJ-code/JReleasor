package dk.tij.jreleasor;

import org.hjson.JsonObject;
import org.hjson.JsonValue;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Configuration {

    public static String TOKEN;

    public static void loadConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader("config.json"))) {
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line);
            }
            String jsonString = JsonValue.readHjson(fileContent.toString()).toString();
            JsonObject jsonObject = JsonValue.readJSON(jsonString).asObject();

            TOKEN = jsonObject.getString("token", "")
                    .replaceAll("\"", "");
        } catch (IOException ioException) {
            System.err.println("Error while loading config file:");
            System.err.println(ioException.getMessage());
        }
    }
}

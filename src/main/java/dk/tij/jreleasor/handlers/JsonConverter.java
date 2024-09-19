package dk.tij.jreleasor.handlers;

import dk.tij.jreleasor.utils.ReleaseGame;
import org.hjson.JsonArray;
import org.hjson.JsonObject;
import org.hjson.JsonValue;
import org.hjson.Stringify;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class JsonConverter {

    public static void CreateGuildFile(String guildId) {
        try {
            File guildFile = new File(guildId + ".json");
            guildFile.createNewFile();
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    public static void WriteToGuildFile(String guildId, JsonObject content) {
        CreateGuildFile(guildId);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(guildId + ".json"))) {
            content.writeTo(writer, Stringify.FORMATTED);
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
    }

    public static JsonObject ReadFromGuildFile(String guildId) {
        try (BufferedReader reader = new BufferedReader(new FileReader(guildId + ".json"))) {
            return JsonValue.readHjson(reader).asObject();
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }
        return null;
    }

    public static List<ReleaseGame> ReadGamesFromFile() {
        List<ReleaseGame> games = new ArrayList<>();

        JsonArray gamesArray = new JsonArray();
        try (BufferedReader reader = new BufferedReader(new FileReader("games.json"))) {
            JsonObject obj = JsonValue.readHjson(reader).asObject();
            gamesArray = obj.get("games").asArray();
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }

        if (!gamesArray.isEmpty()) {
            for (JsonValue gameValue : gamesArray) {
                JsonObject gameObject = gameValue.asObject();
                games.add(new ReleaseGame(gameObject.get("name").asString(),
                        gameObject.get("release_url").asString()));
            }
        }

        return games;
    }

    public static void SaveSettingsMessageToGuild(String guildId, String settingsMessageId) {
        JsonObject content = new JsonObject();
        try (BufferedReader reader = new BufferedReader(new FileReader(guildId + ".json"))) {
            content = JsonValue.readHjson(reader).asObject();
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
        }

        content.add("settings_message", settingsMessageId);
        WriteToGuildFile(guildId, content);
    }
}

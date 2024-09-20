package dk.tij.jreleasor.utils;

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

    public static String GetNotificationChannelFromGuild(String guildId) {
        JsonObject guildContent = ReadFromGuildFile(guildId);
        return (guildContent != null) ? guildContent.get("notification_channel").asString() : null;
    }

    public static String ReadNotificationRoleFromGuildFile(String guildId, ReleaseGame game) {
        JsonObject guildContent = ReadFromGuildFile(guildId);
        assert guildContent != null;
        JsonArray notificatorsArray = guildContent.get("notificators").asArray();
        for (JsonValue notificationValue : notificatorsArray) {
            JsonObject notificationObject = notificationValue.asObject();
            if (notificationObject.get("game").asString().equals(game.getName())) {
                return notificationObject.get("role").asString();
            }
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
                                          gameObject.get("release_url").asString(),
                                          gameObject.get("version").asString(),
                                          gameObject.get("thumbnail_url").asString()));
            }
        }

        return games;
    }

    public static void SetNewGameVersion(ReleaseGame editGame) {
        JsonArray gamesArray = new JsonArray();
        try (BufferedReader reader = new BufferedReader(new FileReader("games.json"))) {
            JsonObject obj = JsonValue.readHjson(reader).asObject();
            gamesArray = obj.get("games").asArray();
        } catch (IOException ioReadingException) {
            System.err.println(ioReadingException.getMessage());
        }

        if (!gamesArray.isEmpty()) {
            for (int i = 0; i < gamesArray.size(); i++) {
                JsonObject gameObject = gamesArray.get(i).asObject();
                if (gameObject.get("name").asString().equals(editGame.getName())) {
                    gameObject.set("version", editGame.getVersion());
                    gamesArray.set(i, gameObject);
                    break;
                }
            }
        }

        JsonObject gamesFileContent = new JsonObject().set("games", gamesArray);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("games.json"))) {
            gamesFileContent.writeTo(writer, Stringify.FORMATTED);
        } catch (IOException ioWritingException) {
            System.err.println(ioWritingException.getMessage());
        }
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

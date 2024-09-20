package dk.tij.jreleasor.handlers;

import dk.tij.jreleasor.JReleasor;
import dk.tij.jreleasor.utils.JsonConverter;
import dk.tij.jreleasor.utils.ReleaseGame;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.hjson.JsonObject;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReleaseNotificationHandler {

    private final JDA jda;

    private Map<TextChannel, Role> notificationParameters;
    private List<TextChannel> notificationChannels;

    public ReleaseNotificationHandler() {
        this.jda = JReleasor.instance.getJda();
    }

    public void CreateNotification(ReleaseGame releaseGame, String newVersion) {
        notificationParameters = GetAllNotificationParameters(releaseGame);
        for (Map.Entry<TextChannel, Role> entry : notificationParameters.entrySet()) {
            TextChannel notificationChannel = entry.getKey();
            Role notificationRole = entry.getValue();
            notificationChannel.sendMessage(notificationRole.getAsMention())
                               .addEmbeds(NewGameReleaseEmbed(releaseGame, newVersion))
                               .addActionRow(Button.link(releaseGame.getRelease_url(), "Github - " + releaseGame.getName()))
                               .queue();
        }
        releaseGame.setVersion(newVersion);
        JsonConverter.SetNewGameVersion(releaseGame);
    }

    private List<TextChannel> GetAllNotificationChannels() {
        List<TextChannel> channels = new ArrayList<>();
        for (Guild guild : jda.getGuilds()) {
            assert guild != null;
            String notificationChannelId = JsonConverter.GetNotificationChannelFromGuild(guild.getId());
            assert notificationChannelId != null;
            channels.add(guild.getTextChannelById(notificationChannelId));
        }
        return channels;
    }

    private Map<TextChannel, Role> GetAllNotificationParameters(ReleaseGame game) {
        Map<TextChannel, Role> parameters = new HashMap<>();
        for (Guild guild : jda.getGuilds()) {
            assert guild != null;
            String notificationChannelId = JsonConverter.GetNotificationChannelFromGuild(guild.getId());
            String notificationRoleId = JsonConverter.ReadNotificationRoleFromGuildFile(guild.getId(), game);
            if (notificationRoleId == null) {
                notificationRoleId = guild.getPublicRole().getId();
            }
            assert notificationChannelId != null;
            TextChannel channel = guild.getTextChannelById(notificationChannelId);
            Role role = guild.getRoleById(notificationRoleId);
            parameters.put(channel, role);
        }
        return parameters;
    }

    private MessageEmbed NewGameReleaseEmbed(ReleaseGame releaseGame, String newVersion) {
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("v" + newVersion + " - " + releaseGame.getName())
                .setDescription("New Version of " + releaseGame.getName() + " is now available.\n" +
                                "Access now on Github.")
                .setColor(new Color(66, 0, 255))
                .setUrl(releaseGame.getRelease_url())
                .setThumbnail(releaseGame.getThumbnailUrl());

        return builder.build();
    }
}

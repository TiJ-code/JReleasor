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
        for (Guild guild : jda.getGuilds()) {
            CreateGuildNotification(guild, releaseGame, newVersion);
        }
        releaseGame.setVersion(newVersion);
        JsonConverter.SetNewGameVersion(releaseGame);
    }

    public void CreateGuildNotification(Guild guild, ReleaseGame releaseGame, String newVersion) {
        String notificationChannelId = JsonConverter.GetNotificationChannelFromGuild(guild.getId());
        TextChannel notificationChannel = guild.getTextChannelById(notificationChannelId);
        Role notificationRole = GetNotificationRole(notificationChannel, releaseGame);
        notificationChannel.sendMessage(notificationRole.getAsMention())
                .addEmbeds(NewGameReleaseEmbed(releaseGame, newVersion))
                .addActionRow(Button.link(releaseGame.getRelease_url(), "Github - " + releaseGame.getName()))
                .queue();
    }

    private Role GetNotificationRole(TextChannel channel, ReleaseGame game) {
        Guild guild = channel.getGuild();
        String notificationRoleId = JsonConverter.ReadNotificationRoleFromGuildFile(guild.getId(), game);
        if (notificationRoleId == null) {
            notificationRoleId = guild.getPublicRole().getId();
        }
        return guild.getRoleById(notificationRoleId);
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

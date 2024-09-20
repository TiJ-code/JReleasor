package dk.tij.jreleasor.handlers;

import dk.tij.jreleasor.JReleasor;
import dk.tij.jreleasor.utils.JsonConverter;
import dk.tij.jreleasor.utils.ReleaseGame;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.hjson.JsonArray;
import org.hjson.JsonObject;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class ReleaseMessageHandler {

    private Message settingsMessage;
    private Message selectGameMessage;
    private Message selectRoleMessage;

    private final JDA jda;

    private ActionRow settingsButtons;

    private TextChannel setupChannel;

    private List<Role> mentionRoles;
    private Role selectedRole;
    private List<ReleaseGame> releasedGames;
    private ReleaseGame selectedGame;

    private int selectedGameIndex = 0;
    private int selectedRoleIndex = 0;

    public ReleaseMessageHandler() {
        this.jda = JReleasor.instance.getJda();
    }

    public void PostMessage(String setupChannelId) {
        setupChannel = jda.getTextChannelById(setupChannelId);
        assert setupChannel != null;

        EmbedBuilder settingsBuilder = new EmbedBuilder()
                .setTitle("Release Message Settings")
                .setDescription("Create custom settings for each release.")
                .addField("Whats editable?", "- Mention-Role", false)
                .setColor(new Color(222, 0, 0));

        settingsMessage = setupChannel.sendMessageEmbeds(settingsBuilder.build())
                .addActionRow(Button.secondary("settings-mention-role", "Mention-Role"))
                .submit().join();

        JsonConverter.SaveSettingsMessageToGuild(setupChannel.getGuild().getId(), settingsMessage.getId());
    }

    private void PostSelectGameMessage() {
        settingsButtons = settingsMessage.getActionRows().getFirst();
        settingsMessage.editMessageComponents().queue();
        settingsMessage.editMessageComponents(settingsButtons.asDisabled()).queue();

        EmbedBuilder gameBuilder = new EmbedBuilder()
                .setTitle("Select Game")
                .setDescription("Select the game whose mention role you want to change.")
                .setColor(new Color(222, 0, 0));

        releasedGames = JReleasor.instance.getReleaseGames();

        StringSelectMenu.Builder menu = StringSelectMenu.create("settings-games");
        for (int i = 0; i < releasedGames.size(); i++) {
            SelectOption option = SelectOption.of(releasedGames.get(i).getName(), "game-" + i);
            menu.addOptions(option.withDefault(i == 0));
        }
        menu.setPlaceholder("Select game...");

        setupChannel.sendMessageEmbeds(gameBuilder.build())
                .addActionRow(Button.secondary("settings-next", "Next"))
                .addActionRow(menu.build())
                .queue(this::setSelectGameMessage);
    }

    private void PostSelectRoleMessage() {
        selectGameMessage.delete().queue();

        selectedGame = releasedGames.get(selectedGameIndex);

        EmbedBuilder roleBuilder = new EmbedBuilder()
                .setTitle("Select Role")
                .setDescription("Select the role which you want to mention on a new release of **" + selectedGame.getName() + "**")
                .setColor(new Color(222, 0, 0));

        StringSelectMenu.Builder menu = StringSelectMenu.create("settings-roles");
        mentionRoles = new java.util.ArrayList<>(setupChannel.getGuild().getRoles().stream()
                .filter(role -> role.getName().contains("NEWS-"))
                .findAny().stream().toList());
        mentionRoles.addFirst(setupChannel.getGuild().getPublicRole());
        for (Role role : mentionRoles) {
            int i = mentionRoles.indexOf(role);
            SelectOption option = SelectOption.of(role.getName(), "role-" + i);
            menu.addOptions(option.withDefault(i == 0));
        }
        menu.setPlaceholder("Select role...");

        setupChannel.sendMessageEmbeds(roleBuilder.build())
                .addActionRow(Button.primary("settings-done", "Done"))
                .addActionRow(menu.build())
                .queue(this::setSelectRoleMessage);
    }

    public void LoadUp(ButtonInteractionEvent event) {
        Guild guild = event.getGuild();
        JsonObject content = JsonConverter.ReadFromGuildFile(Objects.requireNonNull(guild).getId());
        if (content != null) {
            setupChannel = guild.getTextChannelById(content.get("setup_channel").asString());
            System.out.println(setupChannel == null);
            setupChannel.retrieveMessageById(content.get("settings_message").asString()).queue(message -> {
                settingsMessage = message;
                System.out.println("true");
                MentionRole();
            });
        }
    }

    public void MentionRole() {
        PostSelectGameMessage();
    }

    public void Next() {
        PostSelectRoleMessage();
    }

    public void Done() {
        selectRoleMessage.delete().queue();

        selectedRole = mentionRoles.get(selectedRoleIndex);
        String guildId = setupChannel.getGuild().getId();

        JsonObject selectGameObject = new JsonObject()
                .add("game", selectedGame.getName())
                .add("role", selectedRole.getId());

        JsonObject content = Objects.requireNonNull(JsonConverter.ReadFromGuildFile(guildId));

        JsonArray notificatorsArray = new JsonArray();
        if (content.get("notificators") != null) {
            notificatorsArray = content.get("notificators").asArray()
                    .add(selectGameObject);
        } else {
            notificatorsArray.add(selectGameObject);
        }

        content = content.set("notificators", notificatorsArray);
        JsonConverter.WriteToGuildFile(guildId, content);

        settingsMessage.editMessageComponents(settingsButtons.asEnabled()).queue();
    }

    public void setSelectGameMessage(Message selectGameMessage) {
        this.selectGameMessage = selectGameMessage;
    }

    public Message getSelectGameMessage() {
        return selectGameMessage;
    }

    public void setSelectRoleMessage(Message selectRoleMessage) {
        this.selectRoleMessage = selectRoleMessage;
    }

    public Message getSelectRoleMessage() {
        return selectRoleMessage;
    }

    public void setSettingsMessage(Message settingsMessage) {
        this.settingsMessage = settingsMessage;
    }

    public void setSelectedGame(int index) {
        this.selectedGameIndex = index;
    }

    public void setSelectedRole(int index) {
        this.selectedRoleIndex = index;
    }
}

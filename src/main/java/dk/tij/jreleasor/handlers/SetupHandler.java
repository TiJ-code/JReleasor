package dk.tij.jreleasor.handlers;

import dk.tij.jreleasor.JReleasor;
import dk.tij.jreleasor.listeners.setup.SetupMessageListener;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.hjson.JsonObject;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SetupHandler {

    private String setup_channel_id;
    private String notification_channel_id;
    private String guild_id;

    private Message setupMessage;

    private SetupMessageListener setupMessageListener;

    public void Next() {
        EmbedBuilder embedBuilder = new EmbedBuilder()
                .setTitle("Setup Tour")
                .setDescription("Where do you want notifications to go?\n" +
                                "> `notification_channel` = 'send message id'")
                .addField("Instruction", "Send **\\s<id>**", false)
                .setColor(new Color(196, 0, 0))
                .setFooter("2/2");

        setupMessage.editMessageEmbeds(embedBuilder.build()).queue(this::setSetupMessage);

        setupMessage.editMessageComponents().queue();
        setupMessage.editMessageComponents(ActionRow.of(Button.danger("setup_cancel", "Cancel"),
                                                        Button.primary("setup_finish", "Finish").asDisabled()))
                    .queue();

        setupMessageListener = new SetupMessageListener(JReleasor.instance.getSetupHandler());
        JReleasor.instance.getEventHandler().registerEventListener(setupMessageListener);
    }

    public void Cancel() {
        if (setupMessageListener != null) {
            JReleasor.instance.getEventHandler().removeEventListener(setupMessageListener);
        }
        setupMessage.delete().queue();
    }

    public void Finish() {
        setupMessage.editMessageComponents().queue();

        String notificationChannelName = setupMessage.getGuild().getTextChannelById(notification_channel_id).getName();

        MessageEmbed currentEmbed = setupMessage.getEmbeds().getFirst();
        EmbedBuilder finishBuilder = new EmbedBuilder()
                .setTitle("Setup Tour Complete")
                .setDescription("This bot is now setup.\n\n" +
                                "This channel is now set up for behind-the-scenes stuff.\n" +
                                "The channel " + notification_channel_id + ", named " + notificationChannelName + ", is" +
                                " now registered for publishing releases.\n\uD83C\uDF10")
                .setColor(currentEmbed.getColor())
                .setFooter("This message will be deleted soon...");

        setupMessage.editMessageEmbeds(finishBuilder.build()).queue(this::setSetupMessage);
        setupMessage.delete().queueAfter(15, TimeUnit.SECONDS);

        JsonObject content = new JsonObject()
                .add("setup_channel", setup_channel_id)
                .add("notification_channel", notification_channel_id);
        JsonConverter.WriteToGuildFile(guild_id, content);

        JReleasor.instance.getReleaseMessageHandler().PostMessage(setup_channel_id);
    }

    private void EnableFinishButton() {
        MessageEmbed currentEmbed = setupMessage.getEmbeds().getLast();
        EmbedBuilder modify = new EmbedBuilder(currentEmbed)
                .setDescription(currentEmbed.getDescription().replace("'send message id'", "`" + notification_channel_id + "`"));

        setupMessage.editMessageEmbeds(modify.build()).queue(this::setSetupMessage);

        setupMessage.editMessageComponents().queue();
        setupMessage.editMessageComponents(ActionRow.of(Button.danger("setup_cancel", "Cancel"),
                                                        Button.primary("setup_finish", "Finish")))
                    .queue();
        JReleasor.instance.getEventHandler().removeEventListener(setupMessageListener);
    }

    public void setGuildId(String guild_id) {
        this.guild_id = guild_id;
    }

    public void setSetupChannelId(String setup_channel_id) {
        this.setup_channel_id = setup_channel_id;
    }

    public void setNotificationChannelId(String notification_channel_id) {
        this.notification_channel_id = notification_channel_id;
        EnableFinishButton();
    }

    public void setSetupMessage(Message setupMessage) {
        this.setupMessage = setupMessage;
    }

    public Message getSetupMessage() {
        return setupMessage;
    }
}

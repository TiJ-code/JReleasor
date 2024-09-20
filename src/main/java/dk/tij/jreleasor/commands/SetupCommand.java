package dk.tij.jreleasor.commands;

import dk.tij.jreleasor.JReleasor;
import dk.tij.jreleasor.commands.handler.SlashCommand;
import dk.tij.jreleasor.commands.handler.SlashCommandParameter;
import dk.tij.jreleasor.handlers.PermissionHandler;
import dk.tij.jreleasor.handlers.SetupHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class SetupCommand extends SlashCommand {

    private final SetupHandler setupHandler;

    public SetupCommand(String name, String description, List<SlashCommandParameter> parameters) {
        super(name, description, parameters);
        this.setupHandler = JReleasor.instance.getSetupHandler();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!PermissionHandler.HasPermission(Objects.requireNonNull(event.getMember()), Permission.ADMINISTRATOR)) {
            event.reply("You do not have the necessary permission, to run this command.").queue();
        }

        event.reply("Setup started").setEphemeral(true).queue();

        TextChannel setup_channel = event.getChannel().asTextChannel();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Setup Tour")
                    .setDescription("Set this bot up by giving it the necessary parameters.\n" +
                                    "> `setup_channel` = this ( " + setup_channel.getId() + " )")
                    .setColor(new Color(196, 0, 0))
                    .setFooter("1/2");

        Message message = setup_channel.sendMessageEmbeds(embedBuilder.build())
                                       .addActionRow(Button.danger("setup_cancel", "Cancel"),
                                                     Button.success("setup_next", "Next"))
                                       .submit().join();
        message.editMessageEmbeds(embedBuilder.build()).queue();

        setupHandler.setSetupMessage(message);

        setupHandler.setGuildId(Objects.requireNonNull( event.getGuild() ).getId());
        setupHandler.setSetupChannelId(setup_channel.getId());
    }
}

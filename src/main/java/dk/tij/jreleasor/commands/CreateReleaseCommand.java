package dk.tij.jreleasor.commands;

import dk.tij.jreleasor.JReleasor;
import dk.tij.jreleasor.commands.handler.SlashCommand;
import dk.tij.jreleasor.commands.handler.SlashCommandParameter;
import dk.tij.jreleasor.handlers.PermissionHandler;
import dk.tij.jreleasor.utils.ReleaseGame;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;
import java.util.Objects;

public class CreateReleaseCommand extends SlashCommand {

    private List<ReleaseGame> releaseGames;

    public CreateReleaseCommand(String name, String description, List<SlashCommandParameter> parameters) {
        super(name, description, parameters);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!PermissionHandler.HasPermission(event.getMember(), Permission.ADMINISTRATOR)) {
            event.reply("You don't have the necessary permission to execute this command").setEphemeral(true).queue();
        }

        releaseGames = JReleasor.instance.getReleaseGames();
        ReleaseGame selectedGame = null;

        String selectedGameOption = Objects.requireNonNull(event.getOption("game")).getAsString();
        for (ReleaseGame releaseGame : releaseGames) {
            if (releaseGame.getName().equals(selectedGameOption)) {
                selectedGame = releaseGame;
                break;
            }
        }
        assert selectedGame != null;

        JReleasor.instance.getReleaseNotificationHandler().CreateGuildNotification(event.getGuild(), selectedGame, selectedGame.getVersion());

        event.reply("Release post created").setEphemeral(true).queue();
    }

}

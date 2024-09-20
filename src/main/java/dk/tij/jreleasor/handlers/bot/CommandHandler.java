package dk.tij.jreleasor.handlers.bot;

import dk.tij.jreleasor.commands.CreateReleaseCommand;
import dk.tij.jreleasor.commands.SetupCommand;
import dk.tij.jreleasor.commands.handler.SlashCommand;
import dk.tij.jreleasor.commands.handler.SlashCommandManager;
import dk.tij.jreleasor.commands.handler.SlashCommandParameter;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    private final SlashCommandManager slashCommandManager;

    public CommandHandler(SlashCommandManager slashCommandManager) {
        this.slashCommandManager = slashCommandManager;
    }

    public void registerCommands() {
        String name = "setup";
        String description = "Sets up the bot on this server";
        List<SlashCommandParameter> parameters = new ArrayList<>();
        SlashCommand setupCommand = new SetupCommand(name, description, parameters);

        name = "manualrelease";
        description = "Create a release post without an actual new version";
        parameters = new ArrayList<>();
        parameters.add(new SlashCommandParameter("game",
                "The game you want to create a release post about.",
                true, true, OptionType.STRING));
        SlashCommand createReleaseCommand = new CreateReleaseCommand(name, description, parameters);

        slashCommandManager.addCommand(setupCommand);
        slashCommandManager.addCommand(createReleaseCommand);

        slashCommandManager.listen();
    }
}

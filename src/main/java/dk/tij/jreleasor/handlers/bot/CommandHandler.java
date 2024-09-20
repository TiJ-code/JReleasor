package dk.tij.jreleasor.handlers.bot;

import dk.tij.jreleasor.commands.SetupCommand;
import dk.tij.jreleasor.commands.handler.SlashCommand;
import dk.tij.jreleasor.commands.handler.SlashCommandManager;
import dk.tij.jreleasor.commands.handler.SlashCommandParameter;

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

        slashCommandManager.addCommand(setupCommand);

        slashCommandManager.listen();
    }
}

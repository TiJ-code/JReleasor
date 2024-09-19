package dk.tij.jreleasor.commands.handler;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

import java.util.List;

public abstract class SlashCommand {

    private final String name;
    private final String description;
    private final List<SlashCommandParameter> parameters;

    public SlashCommand(String name, String description, List<SlashCommandParameter> parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }

    public abstract void execute(SlashCommandInteractionEvent event);

    public CommandData getInfo() {
        CommandDataImpl commandData = new CommandDataImpl(name, description);
        for (SlashCommandParameter parameter : parameters) {
            commandData.addOptions(new OptionData(parameter.getType(), parameter.getName(), parameter.getDescription(), parameter.isRequired(), parameter.isAutoComplete()));
        }
        return commandData;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

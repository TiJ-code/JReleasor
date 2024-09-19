package dk.tij.jreleasor.commands.handler;

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class SlashCommandParameter {

    private final String name;
    private final String description;
    private final boolean required;
    private final boolean autoComplete;
    private final OptionType type;

    public SlashCommandParameter(String name, String description, boolean required, OptionType type) {
        this(name, description, required, false, type);
    }

    public SlashCommandParameter(String name, String description, boolean required, boolean autoComplete, OptionType type) {
        this.name = name;
        this.description = description;
        this.required = required;
        this.autoComplete = autoComplete;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isAutoComplete() {
        return autoComplete;
    }

    public OptionType getType() {
        return type;
    }
}

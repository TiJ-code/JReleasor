package dk.tij.jreleasor.commands.handler;

import dk.tij.jreleasor.JReleasor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SlashCommandManager {

    private final JDA jda;
    private final SlashEventHandler eventHandler;
    private final Map<String, SlashCommand> slashCommandCollection = new HashMap<>();

    public SlashCommandManager() {
        this.jda = JReleasor.instance.getJda();
        this.eventHandler = new SlashEventHandler(this);
    }

    public void addCommand(@NotNull SlashCommand command) {
        for (Guild guild : jda.getGuilds()) {
            guild.upsertCommand(command.getInfo()).queue();
        }
        if (!slashCommandCollection.containsKey(command.getName())) {
            slashCommandCollection.put(command.getName(), command);
        }
    }

    public synchronized void listen() {
        jda.addEventListener(this.eventHandler);
    }

    public SlashEventHandler getEventHandler() {
        return eventHandler;
    }

    public Map<String, SlashCommand> getSlashCommandCollection() {
        return slashCommandCollection;
    }
}

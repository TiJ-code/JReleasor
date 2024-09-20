package dk.tij.jreleasor.listeners;

import dk.tij.jreleasor.JReleasor;
import dk.tij.jreleasor.utils.ReleaseGame;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CreateReleaseAutoCompleteListener extends ListenerAdapter {

    private List<ReleaseGame> releaseGames;
    private final List<String> releaseGameNames;

    public CreateReleaseAutoCompleteListener() {
        releaseGames = JReleasor.instance.getReleaseGames();
        releaseGameNames = new ArrayList<>();
        for (ReleaseGame game : releaseGames) {
            releaseGameNames.add(game.getName());
        }
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("manualrelease") && event.getFocusedOption().getName().equals("game")) {
            List<Command.Choice> options = Stream.of(releaseGameNames.toArray(String[]::new))
                    .filter(word -> word.startsWith(event.getFocusedOption().getValue()))
                    .map(word -> new Command.Choice(word, word))
                    .collect(Collectors.toList());
            event.replyChoices(options).queue();
        }
    }
}

package dk.tij.jreleasor.listeners.setup;

import dk.tij.jreleasor.JReleasor;
import dk.tij.jreleasor.handlers.SetupHandler;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class SetupButtonListener extends ListenerAdapter {

    private final SetupHandler setupHandler;

    public SetupButtonListener() {
        setupHandler = JReleasor.instance.getSetupHandler();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponent().getId();

        assert buttonId != null;
        if (!buttonId.startsWith("setup")) return;

        if (buttonId.contains("cancel")) {
            setupHandler.Cancel();
        } else if (buttonId.contains("next")) {
            setupHandler.Next();
        } else if (buttonId.contains("finish")) {
            setupHandler.Finish();
        }

        event.deferEdit().queue();
    }
}

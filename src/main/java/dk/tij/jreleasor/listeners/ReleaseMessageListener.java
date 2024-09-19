package dk.tij.jreleasor.listeners;

import dk.tij.jreleasor.JReleasor;
import dk.tij.jreleasor.handlers.ReleaseMessageHandler;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReleaseMessageListener extends ListenerAdapter {

    private final ReleaseMessageHandler releaseMessageHandler;

    private int selectedGameIndex;
    private int selectedRoleIndex;

    public ReleaseMessageListener() {
        this.releaseMessageHandler = JReleasor.instance.getReleaseMessageHandler();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponent().getId();
        assert buttonId != null;
        if (!buttonId.startsWith("settings-")) return;

        if (buttonId.contains("mention-role")) {
            releaseMessageHandler.LoadUp(event);
        } else if (buttonId.contains("next")) {
            releaseMessageHandler.Next();
            releaseMessageHandler.setSelectedGame(selectedGameIndex);
        } else if (buttonId.contains("done")) {
            releaseMessageHandler.Done();
            releaseMessageHandler.setSelectedRole(selectedRoleIndex);
        }

        event.deferEdit().queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        String componentId = event.getComponent().getId();
        assert componentId != null;
        if (componentId.contains("settings-games")) {
            selectedGameIndex = Integer.parseInt(event.getValues().get(0).split("-")[1]);
        } else if (componentId.contains("settings-roles")) {
            selectedRoleIndex = Integer.parseInt(event.getValues().get(0).split("-")[1]);
        }
        event.deferEdit().queue();
    }
}

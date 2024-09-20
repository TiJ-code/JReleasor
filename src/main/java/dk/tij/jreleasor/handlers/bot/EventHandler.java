package dk.tij.jreleasor.handlers.bot;

import dk.tij.jreleasor.JReleasor;
import dk.tij.jreleasor.listeners.ReleaseSettingsListener;
import dk.tij.jreleasor.listeners.ShutdownListener;
import dk.tij.jreleasor.listeners.setup.SetupButtonListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class EventHandler {

    private final JDA jda;

    public EventHandler() {
        this.jda = JReleasor.instance.getJda();
    }

    public void registerEventListeners() {
        jda.addEventListener(new ShutdownListener());

        jda.addEventListener(new ReleaseSettingsListener());
        jda.addEventListener(new SetupButtonListener());
    }

    public void registerEventListener(ListenerAdapter listener) {
        jda.addEventListener(listener);
    }

    public void removeEventListener(ListenerAdapter listener) {
        jda.removeEventListener(listener);
    }
}

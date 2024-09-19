package dk.tij.jreleasor.listeners;

import dk.tij.jreleasor.JReleasor;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ShutdownListener extends ListenerAdapter {

    private final JReleasor jReleasor;

    public ShutdownListener() {
        this.jReleasor = JReleasor.instance;
    }

    @Override
    public void onShutdown(ShutdownEvent event) {
        jReleasor.setRunning(false);
        try {
            jReleasor.getJda().awaitShutdown();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        super.onShutdown(event);
    }
}

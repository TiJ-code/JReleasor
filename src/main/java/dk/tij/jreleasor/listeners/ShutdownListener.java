package dk.tij.jreleasor.listeners;

import dk.tij.jreleasor.JReleasor;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ShutdownListener extends ListenerAdapter {

    private final JReleasor jReleasor;

    public ShutdownListener() {
        this.jReleasor = JReleasor.instance;
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        jReleasor.setRunning(false);
        for (Thread thread : jReleasor.getThreads()) {
            thread.interrupt();
        }
        try {
            jReleasor.getJda().awaitShutdown();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
        super.onShutdown(event);
    }
}

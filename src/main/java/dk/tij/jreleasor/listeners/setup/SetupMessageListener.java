package dk.tij.jreleasor.listeners.setup;

import dk.tij.jreleasor.handlers.SetupHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class SetupMessageListener extends ListenerAdapter {

    private final SetupHandler setupHandler;

    public SetupMessageListener(SetupHandler setupHandler) {
        this.setupHandler = setupHandler;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        String messageContent = event.getMessage().getContentDisplay();
        if (!messageContent.startsWith("\\s")) return;

        String channelId = messageContent.substring(2);

        for (TextChannel channel : event.getGuild().getTextChannels()) {
            if (channel.getId().equals(channelId)) {
                if (channel.getType().isMessage()) {
                    setupHandler.setNotificationChannelId(channelId);
                    event.getMessage().delete().queueAfter(10, TimeUnit.SECONDS);
                } else {
                    Message msg = channel.sendMessage("This is not a valid (text) channel").submit().join();
                    msg.delete().queueAfter(10, TimeUnit.SECONDS);
                }
                break;
            }
        }

    }
}

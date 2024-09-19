package dk.tij.jreleasor;

import dk.tij.jreleasor.commands.handler.SlashCommandManager;
import dk.tij.jreleasor.handlers.CommandHandler;
import dk.tij.jreleasor.handlers.EventHandler;
import dk.tij.jreleasor.handlers.ReleaseMessageHandler;
import dk.tij.jreleasor.handlers.SetupHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

public class JReleasor {

    public static void main(String[] args) {
        new JReleasor();
    }

    private final JDA jda;
    private boolean running;

    public static JReleasor instance;

    private final EventHandler eventHandler;
    private SetupHandler setupHandler;
    private ReleaseMessageHandler releaseMessageHandler;

    public JReleasor() {
        instance = this;
        running = true;

        Configuration.loadConfig();

        jda = JDABuilder.createDefault(Configuration.TOKEN)
                .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES)
                .enableIntents(GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.MESSAGE_CONTENT)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .build();

        InstantiateHandlers();

        eventHandler = new EventHandler();
        eventHandler.registerEventListeners();

        try {
            jda.awaitReady();
            System.out.println("Bot is ready!");
        } catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
            System.err.println(interruptedException.getMessage());
        }

        SlashCommandManager slashCommandManager = new SlashCommandManager();
        CommandHandler commandHandler = new CommandHandler(slashCommandManager);
        commandHandler.registerCommands();

        System.out.println(jda.getRestPing().complete());
        jda.getPresence().setActivity(Activity.customStatus("Running on v0.0.1"));
    }

    private void InstantiateHandlers() {
        setupHandler = new SetupHandler();
        releaseMessageHandler = new ReleaseMessageHandler();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public SetupHandler getSetupHandler() {
        return setupHandler;
    }

    public ReleaseMessageHandler getReleaseMessageHandler() {
        return releaseMessageHandler;
    }

    public boolean isRunning() {
        return running;
    }

    public EventHandler getEventHandler() {
        return eventHandler;
    }

    public JDA getJda() {
        return jda;
    }
}

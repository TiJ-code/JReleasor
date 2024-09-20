package dk.tij.jreleasor;

import dk.tij.jreleasor.commands.handler.SlashCommandManager;
import dk.tij.jreleasor.handlers.GithubReleaseHandler;
import dk.tij.jreleasor.handlers.ReleaseNotificationHandler;
import dk.tij.jreleasor.handlers.bot.CommandHandler;
import dk.tij.jreleasor.handlers.bot.EventHandler;
import dk.tij.jreleasor.handlers.ReleaseSettingsHandler;
import dk.tij.jreleasor.handlers.SetupHandler;
import dk.tij.jreleasor.utils.JsonConverter;
import dk.tij.jreleasor.utils.ReleaseGame;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.ArrayList;
import java.util.List;

public class JReleasor {

    public static void main(String[] args) {
        new JReleasor();
    }

    private final JDA jda;
    private boolean running;

    public static JReleasor instance;

    private final EventHandler eventHandler;
    private SetupHandler setupHandler;
    private ReleaseSettingsHandler releaseMessageHandler;
    private ReleaseNotificationHandler releaseNotificationHandler;

    private List<Thread> threads;
    private List<ReleaseGame> releaseGames;

    public JReleasor() {
        instance = this;
        running = true;
        threads = new ArrayList<>();

        Configuration.loadConfig();
        releaseGames = JsonConverter.ReadGamesFromFile();

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
        jda.getPresence().setActivity(Activity.customStatus("Running on v0.0.3"));

        GithubReleaseHandler grH = new GithubReleaseHandler();
        grH.start();
        threads.add(grH);
    }

    private void InstantiateHandlers() {
        setupHandler = new SetupHandler();
        releaseMessageHandler = new ReleaseSettingsHandler();
        releaseNotificationHandler = new ReleaseNotificationHandler();
    }

    public List<Thread> getThreads() {
        return threads;
    }

    public List<ReleaseGame> getReleaseGames() {
        return releaseGames;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public SetupHandler getSetupHandler() {
        return setupHandler;
    }

    public ReleaseSettingsHandler getReleaseMessageHandler() {
        return releaseMessageHandler;
    }

    public ReleaseNotificationHandler getReleaseNotificationHandler() {
        return releaseNotificationHandler;
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

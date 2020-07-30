package de.sean.splugin;

/* SPlugin */
import de.sean.splugin.discord.SHandler;
import de.sean.splugin.spigot.commands.*;
import de.sean.splugin.spigot.events.*;
import de.sean.splugin.util.SUtil;

/* Java */
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.login.LoginException;

/* Spigot */
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/* Discord */
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;

public class App extends JavaPlugin {
    private static JDA jda;

    private static App instance;

    private static FileConfiguration config;

    @Override
    public void onEnable() {
        /* Config */
        File configFile = App.getInstance().getConfigFile();
        config = YamlConfiguration.loadConfiguration(configFile);

        /* Spigot */
        instance = this;

        // When we're reloading we want to set default values to all players, as old data gets lost on a reload.
        for (Player player : this.getServer().getOnlinePlayers()) {
            SUtil.setLastActivityForPlayer(player.getUniqueId(), System.currentTimeMillis());
            SUtil.setPlayerAFK(player.getUniqueId(), false);
        }

        // Sleep checker task
        // Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new SleepChecker(), 0L, 20L);
        // Afk checker task
        // Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new AfkChecker(), 0L, 20L);

        PluginManager pm = Bukkit.getServer().getPluginManager();
        registerEvents(pm);
        registerCommands();

        /* Discord */
        SUtil.GUILD_ID = config.getString("DiscordGuild");
        SUtil.CHANNEL_ID = config.getString("DiscordChannel");
        // Only initialize discord stuff if a guild and channel are present.
        if (SUtil.GUILD_ID != null || SUtil.CHANNEL_ID != null) {
            JDABuilder builder = new JDABuilder(config.getString("DiscordToken"));
            try {
                builder.setActivity(Activity.playing("Minecraft"));
                jda = builder.build();
                jda.awaitReady();
                getLogger().info("Discord has started successfully!");
            } catch (LoginException | InterruptedException e) {
                e.printStackTrace();
            }

            // Add event listeners for discord
            jda.addEventListener(new SHandler());
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void registerEvents(@NotNull PluginManager pm) {
        // pm.registerEvents(new AdvancementEvent(), this);    // Handles every player advancement
        pm.registerEvents(new BedEnterEvent(), this);       // Handles every user join event
        pm.registerEvents(new BedLeaveEvent(), this);       // Handles every user leave event
        pm.registerEvents(new BlockEvent(), this);          // Handles the placement/destruction of blocks by players
        // pm.registerEvents(new BroadcastEvent(), this);      // Handles every broadcast to all players, including ones from plugins
        // pm.registerEvents(new ChangeWorldEvent(), this);    // Runs everytime a user changes worlds (goes through a portal)
        pm.registerEvents(new DeathEvent(), this);          // Handles every player death event
        pm.registerEvents(new InteractEvent(), this);       // Handles every block interaction by a player
        pm.registerEvents(new JoinEvent(), this);           // Handles every user join event
        pm.registerEvents(new LeaveEvent(), this);          // Handles every user leave event
        pm.registerEvents(new MessageEvent(), this);        // Handles every chat message event
        pm.registerEvents(new MoveEvent(), this);           // Handles every move of a player
        //pm.registerEvents(new RespawnEvent(), this);        // Handles every respawn of a player
    }

    private void registerCommands() {
        getCommand("lock").setExecutor(new LockExecutor());
    }

    public File getConfigFile() {
        return new File(getDataFolder() + File.separator + "config.yml");
    }

    public JDA getDiscordInstance() { return jda; }

    public static App getInstance() { return instance; }
}
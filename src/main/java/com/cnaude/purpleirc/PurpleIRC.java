package com.cnaude.purpleirc;

import com.cnaude.purpleirc.GameListeners.CleverNotchListener;
import com.cnaude.purpleirc.GameListeners.GameListeners;
import com.cnaude.purpleirc.GameListeners.HeroChatListener;
import com.cnaude.purpleirc.Hooks.VaultHook;
import com.cnaude.purpleirc.Utilities.ColorConverter;
import com.cnaude.purpleirc.Utilities.RegexGlobber;
import com.google.common.base.Joiner;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Chris Naudé
 */
public class PurpleIRC extends JavaPlugin {

    public static String LOG_HEADER;
    static final Logger log = Logger.getLogger("Minecraft");
    private final String sampleFileName = "SampleBot.yml";
    private File pluginFolder;
    private File botsFolder;
    private File configFile;
    public static long startTime;
    public String gameChat, gameAction, gameDeath, gameQuit, gameJoin, gameKick, gameSend;
    public String mcMMOAdminChat, mcMMOPartyChat, consoleChat, heroChat;
    public String factionPublicChat, factionAllyChat, factionEnemyChat;
    public String ircChat, ircAction, ircPart, ircKick, ircJoin, ircTopic;
    public String cleverSend;
    private boolean debugEnabled;
    private boolean stripGameColors;
    private boolean stripIRCColors;
    Long ircConnCheckInterval;
    BotWatcher botWatcher;
    public ColorConverter colorConverter;
    public RegexGlobber regexGlobber;
    public HashMap<String, PurpleBot> ircBots = new HashMap<String, PurpleBot>();
    public HashMap<String, Boolean> botConnected = new HashMap<String, Boolean>();
    VaultHook vaultHelpers;

    @Override
    public void onEnable() {
        LOG_HEADER = "[" + this.getName() + "]";
        pluginFolder = getDataFolder();
        botsFolder = new File(pluginFolder + "/bots");
        configFile = new File(pluginFolder, "config.yml");
        createConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        loadConfig();
        getServer().getPluginManager().registerEvents(new GameListeners(this), this);
        if (isHeroChatEnabled()) {
            logInfo("Enabling HeroChat support.");
            getServer().getPluginManager().registerEvents(new HeroChatListener(this), this);
        } else {
            logInfo("HeroChat not detected.");
        }
        if (isCleverNotchEnabled()) {
            logInfo("Enabling CleverNotch support.");
            getServer().getPluginManager().registerEvents(new CleverNotchListener(this), this);
        } else {
            logInfo("CleverNotch not detected.");
        }
        getCommand("irc").setExecutor(new CommandHandlers(this));
        regexGlobber = new RegexGlobber();
        loadBots();
        createSampleBot();
        botWatcher = new BotWatcher(this);
        setupVault();
    }

    @Override
    public void onDisable() {
        if (botWatcher != null) {
            botWatcher.cancel();
        }
        if (ircBots.isEmpty()) {
            logInfo("No IRC bots to disconnect.");
        } else {
            logInfo("Disconnecting IRC bots.");
            Iterator it = ircBots.entrySet().iterator();
            while (it.hasNext()) {
                Entry entry = (Entry) it.next();
                PurpleBot ircBot = (PurpleBot) entry.getValue();
                ircBot.saveConfig(getServer().getConsoleSender());
                ircBot.quit();
                it.remove();
            }
        }
    }

    public void debugMode(boolean debug) {
        debugEnabled = debug;
        getConfig().set("Debug", debug);
        try {
            getConfig().save(configFile);
        } catch (Exception ex) {
            logError("Problem saving to " + configFile.getName() + ": " + ex.getMessage());
        }
    }

    public boolean debugMode() {
        return debugEnabled;
    }

    private void loadConfig() {
        debugEnabled = getConfig().getBoolean("Debug");
        stripGameColors = getConfig().getBoolean("strip-game-colors", false);
        stripIRCColors = getConfig().getBoolean("strip-irc-colors", false);
        colorConverter = new ColorConverter(stripGameColors, stripIRCColors);
        logDebug("strip-game-colors: " + stripGameColors);
        logDebug("strip-irc-colors: " + stripIRCColors);
        gameAction = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-action", ""));
        gameChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-chat", ""));
        gameSend = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-send", ""));
        cleverSend = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.clever-send", ""));
        mcMMOAdminChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.mcmmo-admin-chat", ""));
        mcMMOPartyChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.mcmmo-party-chat", ""));
        heroChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.hero-chat", ""));
        factionPublicChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.faction-public-chat", ""));
        factionAllyChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.faction-ally-chat", ""));
        factionEnemyChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.faction-enemy-chat", ""));
        consoleChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.console-chat", ""));
        gameDeath = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-death", ""));
        gameJoin = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-join", ""));
        gameQuit = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-quit", ""));

        ircAction = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-action", ""));
        ircChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-chat", ""));
        ircKick = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-kick", ""));
        ircJoin = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-join", ""));
        ircPart = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-part", ""));
        ircTopic = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-topic", ""));

        ircConnCheckInterval = getConfig().getLong("conn-check-interval");
        logDebug("Debug enabled");
    }

    private void loadBots() {
        if (botsFolder.exists()) {
            logInfo("Checking for bot files in " + botsFolder);
            for (final File file : botsFolder.listFiles()) {
                if (file.getName().endsWith(".yml")) {
                    logInfo("Loading bot file: " + file.getName());
                    PurpleBot pircBot = new PurpleBot(file, this);
                }
            }
        }
    }

    public boolean isMcMMOEnabled() {
        return (getServer().getPluginManager().getPlugin("mcMMO") != null);
    }

    public boolean isFactionChatEnabled() {
        return (getServer().getPluginManager().getPlugin("FactionChat") != null);
    }

    public boolean isHeroChatEnabled() {
        return (getServer().getPluginManager().getPlugin("Herochat") != null);
    }

    public boolean isCleverNotchEnabled() {
        return (getServer().getPluginManager().getPlugin("CleverNotch") != null);
    }

    private void createSampleBot() {
        File file = new File(pluginFolder + "/" + sampleFileName);
        try {
            InputStream in = PurpleIRC.class.getResourceAsStream("/" + sampleFileName);
            byte[] buf = new byte[1024];
            int len;
            OutputStream out = new FileOutputStream(file);
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
        } catch (Exception ex) {
            logError("Problem creating sample bot: " + ex.getMessage());
        }
    }

    public void reloadMainConfig(CommandSender sender) {
        sender.sendMessage("Reloading config.yml...");
        reloadConfig();
        getConfig().options().copyDefaults(false);
        loadConfig();
        sender.sendMessage("Done.");
    }

    private void createConfig() {
        if (!pluginFolder.exists()) {
            try {
                pluginFolder.mkdir();
            } catch (Exception e) {
                logError(e.getMessage());
            }
        }

        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (Exception e) {
                logError(e.getMessage());
            }
        }

        if (!botsFolder.exists()) {
            try {
                botsFolder.mkdir();
            } catch (Exception e) {
                logError(e.getMessage());
            }
        }
    }

    public void logInfo(String _message) {
        log.log(Level.INFO, String.format("%s %s", LOG_HEADER, _message));
    }

    public void logError(String _message) {
        log.log(Level.SEVERE, String.format("%s %s", LOG_HEADER, _message));
    }

    public void logDebug(String _message) {
        if (debugEnabled) {
            log.log(Level.INFO, String.format("%s [DEBUG] %s", LOG_HEADER, _message));
        }
    }

    public String getMCUptime() {
        long jvmUptime = ManagementFactory.getRuntimeMXBean().getUptime();
        String msg = "Server uptime: " + (int) (jvmUptime / 86400000L) + " days"
                + " " + (int) (jvmUptime / 3600000L % 24L) + " hours"
                + " " + (int) (jvmUptime / 60000L % 60L) + " minutes"
                + " " + (int) (jvmUptime / 1000L % 60L) + " seconds.";
        return msg;
    }

    public String getMCPlayers() {             
        ArrayList<String> playerList = new ArrayList<String>();
        for (Player player : getServer().getOnlinePlayers()) {
            playerList.add(player.getName());
        }
        Collections.sort(playerList, Collator.getInstance());
        String msg = "Players currently online("
                + getServer().getOnlinePlayers().length
                + "/" + getServer().getMaxPlayers() + "): "
                + Joiner.on(", ").join(playerList);        
        return msg;
    }

    public void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            vaultHelpers = new VaultHook(this);
            logInfo("Hooked into Vault!");
        } else {
            logInfo("Not hooked into Vault!");
        }
    }

    public String getPlayerGroup(Player player) {
        String groupName = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.permission != null) {
                groupName = vaultHelpers.permission.getPrimaryGroup(player);
            }
        }
        return groupName;
    }

    public String getPlayerPrefix(Player player) {
        String prefix = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.chat != null) {
                prefix = vaultHelpers.chat.getPlayerPrefix(player);
            }
        }
        return prefix;
    }

    public String getGroupPrefix(Player player) {
        String prefix = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.chat != null) {
                prefix = vaultHelpers.chat.getGroupPrefix(player.getLocation().getWorld(), player.getName());
            }
        }
        return prefix;
    }
}

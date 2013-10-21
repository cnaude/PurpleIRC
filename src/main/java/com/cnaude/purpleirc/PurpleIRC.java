package com.cnaude.purpleirc;

import com.cnaude.purpleirc.GameListeners.CleverNotchListener;
import com.cnaude.purpleirc.GameListeners.GameListeners;
import com.cnaude.purpleirc.GameListeners.HeroChatListener;
import com.cnaude.purpleirc.GameListeners.ReportRTSListener;
import com.cnaude.purpleirc.GameListeners.TitanChatListener;
import com.cnaude.purpleirc.Hooks.VaultHook;
import com.cnaude.purpleirc.Utilities.ColorConverter;
import com.cnaude.purpleirc.Utilities.RegexGlobber;
import com.google.common.base.Joiner;
import com.onarandombox.MultiverseCore.api.MVPlugin;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.cnaude.purpleirc.Hooks.FactionChatHook;
import com.cnaude.purpleirc.Hooks.VanishHook;
import com.cnaude.purpleirc.Utilities.NetPackets;
import java.io.IOException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
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
    public String gameChat, gameAction, gameDeath, gameQuit, gameJoin, gameKick;
    public String gameSend, gameCommand;
    public String mcMMOAdminChat, mcMMOPartyChat, consoleChat, heroChat;
    public String factionPublicChat, factionAllyChat, factionEnemyChat;
    public String titanChat;
    public String ircTitanChat;
    public String ircHeroChat, ircHeroAction, ircHeroPart, ircHeroKick, ircHeroJoin, ircHeroTopic;
    public String ircChat, ircPChat, ircAction, ircPart, ircKick, ircJoin, ircTopic, ircQuit, ircNickChange, ircMode, ircNotice;
    public String defaultPlayerSuffix, defaultPlayerPrefix, defaultPlayerGroup, defaultGroupPrefix, defaultPlayerWorld;
    public String invalidIRCCommand, noPermForIRCCommand;
    public final String invalidBotName = ChatColor.RED + "Invalid bot name: " + ChatColor.WHITE + "%BOT%"
            + ChatColor.RED + "'. Type '" + ChatColor.WHITE + "/irc listbots"
            + ChatColor.RED + "' to see valid bots.";
    public final String invalidChannel = ChatColor.RED + "Invalid channel: " + ChatColor.WHITE + "%CHANNEL%";
    public final String noPermission = ChatColor.RED + "You do not have permission to use this command.";
    public String customTabPrefix;
    public String reportRTSSend;
    public String cleverSend;
    public String broadcastMessage, broadcastConsoleMessage;
    private boolean debugEnabled;
    private boolean stripGameColors;
    private boolean stripIRCColors;
    private boolean customTabList;
    public boolean exactNickMatch;
    Long ircConnCheckInterval;
    Long ircChannelCheckInterval;
    BotWatcher botWatcher;
    ChannelWatcher channelWatcher;
    public ColorConverter colorConverter;
    public RegexGlobber regexGlobber;
    public HashMap<String, PurpleBot> ircBots = new HashMap<String, PurpleBot>();
    public HashMap<String, Boolean> botConnected = new HashMap<String, Boolean>();
    VaultHook vaultHelpers;
    VanishHook vanishHook;
    public FactionChatHook fcHook;
    public NetPackets netPackets = null;

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
        if (isTitanChatEnabled()) {
            logInfo("Enabling TitanChat support.");
            getServer().getPluginManager().registerEvents(new TitanChatListener(this), this);
        } else {
            logInfo("TitanChat not detected.");
        }
        if (isCleverNotchEnabled()) {
            logInfo("Enabling CleverNotch support.");
            getServer().getPluginManager().registerEvents(new CleverNotchListener(this), this);
        } else {
            logInfo("CleverNotch not detected.");
        }
        if (isFactionsEnabled()) {
            if (isFactionChatEnabled()) {
                logInfo("Enabling FactionChat support.");
                fcHook = new FactionChatHook(this);
            } else {
                logInfo("FactionChat not detected.");
            }
        }
        vanishHook = new VanishHook(this);
        if (isReportRTSEnabled()) {
            logInfo("Enabling ReportRTS support.");
            getServer().getPluginManager().registerEvents(new ReportRTSListener(this), this);
        } else {
            logInfo("ReportRTS not detected.");
        }
        getCommand("irc").setExecutor(new CommandHandlers(this));
        regexGlobber = new RegexGlobber();
        loadBots();
        createSampleBot();
        botWatcher = new BotWatcher(this);
        channelWatcher = new ChannelWatcher(this);
        setupVault();
        if (customTabList) {
            if (checkForProtocolLib()) {
                logInfo("Hooked into ProtocolLib!");
                netPackets = new NetPackets(this);
            } else {
                logError("ProtocolLib not found! The custom tab list is disabled.");
                netPackets = null;
            }
        } else {
            netPackets = null;
        }
    }

    @Override
    public void onDisable() {
        if (botWatcher != null) {
            botWatcher.cancel();
        }
        if (channelWatcher != null) {
            channelWatcher.cancel();
        }
        if (ircBots.isEmpty()) {
            logInfo("No IRC bots to disconnect.");
        } else {
            logInfo("Disconnecting IRC bots.");
            for (Entry entry : ircBots.entrySet()) {
                PurpleBot ircBot = (PurpleBot) entry.getValue();
                ircBot.commandQueue.cancel();
                ircBot.saveConfig(getServer().getConsoleSender());
                ircBot.quit();
            }
        }
    }

    public void debugMode(boolean debug) {
        debugEnabled = debug;
        getConfig().set("Debug", debug);
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            logError("Problem saving to " + configFile.getName() + ": " + ex.getMessage());
        }
    }

    public boolean debugMode() {
        return debugEnabled;
    }

    private void loadConfig() {
        debugEnabled = getConfig().getBoolean("Debug");
        logDebug("Debug enabled");
        stripGameColors = getConfig().getBoolean("strip-game-colors", false);
        stripIRCColors = getConfig().getBoolean("strip-irc-colors", false);
        exactNickMatch = getConfig().getBoolean("nick-exact-match", true);
        colorConverter = new ColorConverter(stripGameColors, stripIRCColors);
        logDebug("strip-game-colors: " + stripGameColors);
        logDebug("strip-irc-colors: " + stripIRCColors);
        gameAction = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-action", ""));
        gameChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-chat", ""));
        gameSend = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-send", ""));
        gameDeath = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-death", ""));
        gameJoin = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-join", ""));
        gameQuit = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-quit", ""));
        gameCommand = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-command", ""));

        cleverSend = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.clever-send", ""));

        mcMMOAdminChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.mcmmo-admin-chat", ""));
        mcMMOPartyChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.mcmmo-party-chat", ""));

        heroChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.hero-chat", ""));
        ircHeroAction = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-hero-action", ""));
        ircHeroChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-hero-chat", ""));
        ircHeroKick = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-hero-kick", ""));
        ircHeroJoin = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-hero-join", ""));
        ircHeroPart = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-hero-part", ""));
        ircHeroTopic = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-hero-topic", ""));        

        titanChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.titan-chat", ""));
        ircTitanChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-titan-chat", ""));

        factionPublicChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.faction-public-chat", ""));
        factionAllyChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.faction-ally-chat", ""));
        factionEnemyChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.faction-enemy-chat", ""));

        consoleChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.console-chat", ""));

        ircAction = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-action", ""));
        ircChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-chat", ""));
        ircPChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-pchat", ""));
        ircKick = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-kick", ""));
        ircJoin = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-join", ""));
        ircPart = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-part", ""));
        ircQuit = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-quit", ""));
        ircTopic = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-topic", ""));
        ircNickChange = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-nickchange", ""));
        ircMode = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-mode", ""));
        ircNotice = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-notice", ""));

        invalidIRCCommand = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.invalid-irc-command", ""));
        noPermForIRCCommand = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.no-perm-for-irc-command", ""));

        broadcastMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.broadcast-message", ""));
        broadcastConsoleMessage = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.broadcast-console-message", ""));

        reportRTSSend = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.rts-notify", ""));

        defaultPlayerSuffix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-player-suffix", ""));
        defaultPlayerPrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-player-prefix", ""));
        defaultPlayerGroup = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-player-group", ""));
        defaultGroupPrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-group-prefix", ""));
        defaultPlayerWorld = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-player-world", ""));

        ircConnCheckInterval = getConfig().getLong("conn-check-interval");
        ircChannelCheckInterval = getConfig().getLong("channel-check-interval");

        customTabList = getConfig().getBoolean("custom-tab-list", false);
        customTabPrefix = getConfig().getString("custom-tab-prefix", "[IRC} ");
        logDebug("custom-tab-list: " + customTabList);
        logDebug("custom-tab-prefix: " + customTabPrefix);
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

    public boolean isFactionsEnabled() {
        if (getServer().getPluginManager().getPlugin("Factions") != null) {
            String v = getServer().getPluginManager().getPlugin("Factions").getDescription().getVersion();
            logDebug("Factions version => " + v);
            if (v.startsWith("2.")) {
                return true;
            } else {
                logInfo("Invalid Factions version! Only version 2.0.0 and higher is supported.");
            }
        }
        return false;
    }

    public boolean isReportRTSEnabled() {
        return (getServer().getPluginManager().getPlugin("ReportRTS") != null);
    }

    public boolean isHeroChatEnabled() {
        return (getServer().getPluginManager().getPlugin("Herochat") != null);
    }

    public boolean isTitanChatEnabled() {
        return (getServer().getPluginManager().getPlugin("TitanChat") != null);
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
        } catch (IOException ex) {
            logError("Problem creating sample bot: " + ex.getMessage());
        }
    }

    public String getWorldAlias(String worldName) {
        String alias = worldName;
        Plugin plugin = getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (plugin != null) {
            MVPlugin mvPlugin = (MVPlugin) plugin;
            try {
                alias = mvPlugin.getCore().getMVWorldManager().getMVWorld(worldName).getAlias();
            } catch (Exception ex) {
                logDebug("Problem getting alias name for '" + worldName + "': " + ex.getMessage());
            }
        }
        if (alias == null) {
            alias = worldName;
        }
        return alias;
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
            } catch (IOException e) {
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
                try {
                    groupName = vaultHelpers.permission.getPrimaryGroup(player);
                } catch (Exception ex) {
                    logDebug("Problem with primary group (" + player.getName() + "): " + ex.getMessage());
                }
            }
        }
        if (groupName == null) {
            groupName = "";
        }
        return ChatColor.translateAlternateColorCodes('&', groupName);
    }

    public String getPlayerGroup(String worldName, String player) {
        String groupName = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.permission != null) {
                try {
                    groupName = vaultHelpers.permission.getPrimaryGroup(worldName, player);
                } catch (Exception ex) {
                    logDebug("Problem with primary group (" + player + "): " + ex.getMessage());
                }
            }
        }
        if (groupName == null) {
            groupName = "";
        }
        return ChatColor.translateAlternateColorCodes('&', groupName);
    }

    public String getPlayerPrefix(Player player) {
        String prefix = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.chat != null) {
                prefix = vaultHelpers.chat.getPlayerPrefix(player);
            }
        }
        if (prefix == null) {
            prefix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public String getPlayerPrefix(String worldName, String player) {
        String prefix = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.chat != null) {
                prefix = vaultHelpers.chat.getPlayerPrefix(worldName, player);
            }
        }
        if (prefix == null) {
            prefix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public String getPlayerSuffix(Player player) {
        String suffix = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.chat != null) {
                suffix = vaultHelpers.chat.getPlayerSuffix(player);
            }
        }
        if (suffix == null) {
            suffix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', suffix);
    }

    public String getPlayerSuffix(String worldName, String player) {
        String suffix = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.chat != null) {
                suffix = vaultHelpers.chat.getPlayerSuffix(worldName, player);
            }
        }
        if (suffix == null) {
            suffix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', suffix);
    }

    public String getGroupPrefix(Player player) {
        String prefix = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.chat != null) {
                String group = "";
                try {
                    group = vaultHelpers.permission.getPrimaryGroup(player);
                } catch (Exception ex) {
                    logDebug("Problem with primary group (" + player.getName() + "): " + ex.getMessage());
                }
                if (group == null) {
                    group = "";
                }
                prefix = vaultHelpers.chat.getGroupPrefix(player.getLocation().getWorld(), group);
            }
        }
        if (prefix == null) {
            prefix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public String getGroupPrefix(String worldName, String player) {
        String prefix = "";
        if (vaultHelpers != null) {
            if (vaultHelpers.chat != null) {
                String group = "";
                try {
                    group = vaultHelpers.permission.getPrimaryGroup(worldName, player);
                } catch (Exception ex) {
                    logDebug("Problem with primary group (" + player + "): " + ex.getMessage());
                }
                if (group == null) {
                    group = "";
                }
                prefix = vaultHelpers.chat.getGroupPrefix(worldName, group);
            }
        }
        if (prefix == null) {
            prefix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }

    public boolean checkForProtocolLib() {
        return (getServer().getPluginManager().getPlugin("ProtocolLib") != null);
    }
}

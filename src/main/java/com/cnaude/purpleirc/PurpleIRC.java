package com.cnaude.purpleirc;

import com.cnaude.purpleirc.GameListeners.CleverNotchListener;
import com.cnaude.purpleirc.GameListeners.DeathMessagesListener;
import com.cnaude.purpleirc.GameListeners.GamePlayerChatListener;
import com.cnaude.purpleirc.GameListeners.GamePlayerCommandPreprocessingListener;
import com.cnaude.purpleirc.GameListeners.GamePlayerDeathListener;
import com.cnaude.purpleirc.GameListeners.GamePlayerJoinListener;
import com.cnaude.purpleirc.GameListeners.GamePlayerKickListener;
import com.cnaude.purpleirc.GameListeners.GamePlayerQuitListener;
import com.cnaude.purpleirc.GameListeners.GameServerCommandListener;
import com.cnaude.purpleirc.GameListeners.HeroChatListener;
import com.cnaude.purpleirc.GameListeners.McMMOChatListener;
import com.cnaude.purpleirc.GameListeners.ReportRTSListener;
import com.cnaude.purpleirc.GameListeners.TitanChatListener;
import com.cnaude.purpleirc.GameListeners.TownyChatListener;
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
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.cnaude.purpleirc.Hooks.FactionChatHook;
import com.cnaude.purpleirc.Hooks.JobsHook;
import com.cnaude.purpleirc.Hooks.TownyChatHook;
import com.cnaude.purpleirc.Hooks.VanishHook;
import com.cnaude.purpleirc.Utilities.CaseInsensitiveMap;
import com.cnaude.purpleirc.Utilities.ChatTokenizer;
import com.cnaude.purpleirc.Utilities.IRCMessageHandler;
import com.cnaude.purpleirc.Utilities.NetPackets;
import com.cnaude.purpleirc.Utilities.Query;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import java.io.IOException;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.pircbotx.IdentServer;

/**
 *
 * @author Chris Naudé
 */
public class PurpleIRC extends JavaPlugin {

    public String LOG_HEADER;
    public String LOG_HEADER_F;
    static final Logger log = Logger.getLogger("Minecraft");
    private final String sampleFileName = "SampleBot.yml";
    private final String MAINCONFIG = "MAIN-CONFIG";
    private File pluginFolder;
    private File botsFolder;
    private File configFile;
    public static long startTime;
    public boolean identServerEnabled;
    private final CaseInsensitiveMap<HashMap<String, String>> messageTmpl;
    private CaseInsensitiveMap<HashMap<String, String>> ircHeroChannelMessages;
    private CaseInsensitiveMap<HashMap<String, String>> heroChannelMessages;
    private CaseInsensitiveMap<HashMap<String, String>> heroActionChannelMessages;
    public String defaultPlayerSuffix,
            defaultPlayerPrefix,
            defaultPlayerGroup,
            defaultGroupPrefix,
            defaultPlayerWorld,
            defaultGroupSuffix,
            customTabPrefix,
            heroChatEmoteFormat,
            listFormat,
            listSeparator,
            listPlayer;

    public ArrayList<String> kickedPlayers = new ArrayList<String>();

    public final String invalidBotName = ChatColor.RED + "Invalid bot name: "
            + ChatColor.WHITE + "%BOT%"
            + ChatColor.RED + ". Type '" + ChatColor.WHITE + "/irc listbots"
            + ChatColor.RED + "' to see valid bots.";

    public final String invalidChannelName = ChatColor.RED + "Invalid channel name: "
            + ChatColor.WHITE + "%CHANNEL%";

    public final String invalidChannel = ChatColor.RED + "Invalid channel: "
            + ChatColor.WHITE + "%CHANNEL%";
    public final String noPermission = ChatColor.RED + "You do not have permission to use this command.";

    private boolean debugEnabled;
    private boolean stripGameColors;
    private boolean stripIRCColors;
    private boolean customTabList;
    private boolean listSortByName;
    public boolean exactNickMatch;
    public Long ircConnCheckInterval;
    public Long ircChannelCheckInterval;
    public ChannelWatcher channelWatcher;
    public ColorConverter colorConverter;
    public RegexGlobber regexGlobber;
    public CaseInsensitiveMap<PurpleBot> ircBots = new CaseInsensitiveMap<PurpleBot>();
    public FactionChatHook fcHook;
    public TownyChatHook tcHook;
    public JobsHook jobsHook;
    public NetPackets netPackets = null;
    public CommandHandlers commandHandlers;
    private BotWatcher botWatcher;
    public IRCMessageHandler ircMessageHandler;

    public CommandQueueWatcher commandQueue;
    public ChatTokenizer tokenizer;
    private File heroConfigFile;
    public VaultHook vaultHelpers;
    public VanishHook vanishHook;
    private YamlConfiguration heroConfig;

    public PurpleIRC() {
        this.messageTmpl = new CaseInsensitiveMap<HashMap<String, String>>();
        this.ircHeroChannelMessages = new CaseInsensitiveMap<HashMap<String, String>>();
        this.heroChannelMessages = new CaseInsensitiveMap<HashMap<String, String>>();
        this.heroActionChannelMessages = new CaseInsensitiveMap<HashMap<String, String>>();
    }

    /**
     *
     */
    @Override
    public void onEnable() {
        LOG_HEADER = "[" + this.getName() + "]";
        LOG_HEADER_F = ChatColor.DARK_PURPLE + "[" + this.getName() + "]"
                + ChatColor.WHITE;
        pluginFolder = getDataFolder();
        botsFolder = new File(pluginFolder + "/bots");
        configFile = new File(pluginFolder, "config.yml");
        createConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        loadConfig();
        if (identServerEnabled) {
            logInfo("Starting Ident Server ...");
            try {
                IdentServer.startServer();
            } catch (Exception ex) {
                logError(ex.getMessage());
            }
        }
        getServer().getPluginManager().registerEvents(new GamePlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new GamePlayerCommandPreprocessingListener(this), this);
        getServer().getPluginManager().registerEvents(new GamePlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new GamePlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new GamePlayerKickListener(this), this);
        getServer().getPluginManager().registerEvents(new GamePlayerQuitListener(this), this);
        getServer().getPluginManager().registerEvents(new GameServerCommandListener(this), this);
        if (isHeroChatEnabled()) {
            logInfo("Enabling HeroChat support.");
            getServer().getPluginManager().registerEvents(new HeroChatListener(this), this);
            heroConfig = new YamlConfiguration();
            heroConfigFile = new File(getServer().getPluginManager()
                    .getPlugin("Herochat").getDataFolder(), "config.yml");
            try {
                heroConfig.load(heroConfigFile);
            } catch (IOException ex) {
                Logger.getLogger(PurpleIRC.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidConfigurationException ex) {
                Logger.getLogger(PurpleIRC.class.getName()).log(Level.SEVERE, null, ex);
            }
            heroChatEmoteFormat = heroConfig.getString("format.emote", "");
        } else {
            logInfo("HeroChat not detected.");
        }
        if (isTitanChatEnabled()) {
            logInfo("Enabling TitanChat support.");
            getServer().getPluginManager().registerEvents(new TitanChatListener(this), this);
        } else {
            logInfo("TitanChat not detected.");
        }
        if (isTownyChatEnabled()) {
            logInfo("Enabling TownyChat support.");
            getServer().getPluginManager().registerEvents(new TownyChatListener(this), this);
            tcHook = new TownyChatHook(this);
        } else {
            logInfo("TownyChat not detected.");
        }
        if (isCleverNotchEnabled()) {
            logInfo("Enabling CleverNotch support.");
            getServer().getPluginManager().registerEvents(new CleverNotchListener(this), this);
        } else {
            logInfo("CleverNotch not detected.");
        }
        if (isMcMMOEnabled()) {
            logInfo("Enabling mcMMO support.");
            getServer().getPluginManager().registerEvents(new McMMOChatListener(this), this);
        } else {
            logInfo("mcMMO not detected.");
        }
        if (isFactionsEnabled()) {
            if (isFactionChatEnabled()) {
                logInfo("Enabling FactionChat support.");
                fcHook = new FactionChatHook(this);
            } else {
                logInfo("FactionChat not detected.");
            }
        }
        if (isJobsEnabled()) {
            logInfo("Enabling Jobs support.");
            jobsHook = new JobsHook(this);
        } else {
            logInfo("Jobs not detected.");
        }
        if (isDeathMessagesEnabled()) {
            logInfo("Enabling DeathMessages support.");
            getServer().getPluginManager().registerEvents(new DeathMessagesListener(this), this);
        } else {
            logInfo("DeathMessages not detected.");
        }
        /*
         if (isPrismEnabled()) {
         logInfo("Enabling Prism support.");            
         } else {
         logInfo("Prism not detected.");
         }*/
        /*
         if (isEssentialsEnabled()) {
         logInfo("Enabling Essentials support.");
         getServer().getPluginManager().registerEvents(new EssentialsListener(this), this);
         } else {
         logInfo("Essentials not detected.");
         }
         */
        vanishHook = new VanishHook(this);
        if (isReportRTSEnabled()) {
            logInfo("Enabling ReportRTS support.");
            getServer().getPluginManager().registerEvents(new ReportRTSListener(this), this);
        } else {
            logInfo("ReportRTS not detected.");
        }
        commandHandlers = new CommandHandlers(this);
        getCommand("irc").setExecutor(commandHandlers);
        regexGlobber = new RegexGlobber();
        tokenizer = new ChatTokenizer(this);
        loadBots();
        createSampleBot();
        channelWatcher = new ChannelWatcher(this);
        setupVault();
        if (customTabList) {
            if (checkForProtocolLib()) {
                logInfo("Hooked into ProtocolLib! Custom tab list is enabled.");
                netPackets = new NetPackets(this);
            } else {
                logError("ProtocolLib not found! The custom tab list is disabled.");
                netPackets = null;
            }
        } else {
            netPackets = null;
        }
        botWatcher = new BotWatcher(this);
        ircMessageHandler = new IRCMessageHandler(this);
        commandQueue = new CommandQueueWatcher(this);
    }

    /**
     *
     */
    @Override
    public void onDisable() {
        if (channelWatcher != null) {
            logDebug("Disabling channelWatcher ...");
            channelWatcher.cancel();
        }
        if (botWatcher != null) {
            logDebug("Disabling botWatcher ...");
            botWatcher.cancel();
        }
        if (ircBots.isEmpty()) {
            logInfo("No IRC bots to disconnect.");
        } else {
            logInfo("Disconnecting IRC bots.");
            for (PurpleBot ircBot : ircBots.values()) {
                commandQueue.cancel();
                ircBot.saveConfig(getServer().getConsoleSender());
                ircBot.quit();
            }
            ircBots.clear();
        }
        if (identServerEnabled) {
            logInfo("Stopping Ident Server");
            try {
                IdentServer.stopServer();
            } catch (IOException ex) {
                logError(ex.getMessage());
            }
        }
    }

    /**
     *
     * @param debug
     */
    public void debugMode(boolean debug) {
        debugEnabled = debug;
        getConfig().set("Debug", debug);
        try {
            getConfig().save(configFile);
        } catch (IOException ex) {
            logError("Problem saving to " + configFile.getName() + ": " + ex.getMessage());
        }
    }

    /**
     *
     * @return
     */
    public boolean debugMode() {
        return debugEnabled;
    }

    public String getHeroMsgTemplate(String botName, String tmpl) {
        if (messageTmpl.containsKey(botName)) {
            if (messageTmpl.get(botName).containsKey(tmpl)) {
                return messageTmpl.get(botName).get(tmpl);
            }
        }
        if (messageTmpl.get(MAINCONFIG).containsKey(tmpl)) {
            return messageTmpl.get(MAINCONFIG).get(tmpl);
        }
        return "INVALID TEMPLATE";
    }

    public String getMsgTemplate(String botName, String tmpl) {
        if (messageTmpl.containsKey(botName)) {
            if (messageTmpl.get(botName).containsKey(tmpl)) {
                return messageTmpl.get(botName).get(tmpl);
            }
        }
        if (messageTmpl.get(MAINCONFIG).containsKey(tmpl)) {
            return messageTmpl.get(MAINCONFIG).get(tmpl);
        }
        return "INVALID TEMPLATE";
    }

    public String getMsgTemplate(String tmpl) {
        return getMsgTemplate(MAINCONFIG, tmpl);
    }

    public String getHeroChatChannelTemplate(String botName, String hChannel) {
        if (heroChannelMessages.containsKey(botName)) {
            if (heroChannelMessages.get(botName).containsKey(hChannel)) {
                return heroChannelMessages.get(botName).get(hChannel.toLowerCase());
            } else {
                return getMsgTemplate(botName, TemplateName.HERO_CHAT);
            }
        } else {
            if (heroChannelMessages.get(MAINCONFIG).containsKey(hChannel)) {
                return heroChannelMessages.get(MAINCONFIG).get(hChannel.toLowerCase());
            } else {
                return getMsgTemplate(botName, TemplateName.HERO_CHAT);
            }
        }
    }

    public String getHeroActionChannelTemplate(String botName, String hChannel) {
        if (heroChannelMessages.containsKey(botName)) {
            if (heroChannelMessages.get(botName).containsKey(hChannel)) {
                return heroChannelMessages.get(botName).get(hChannel.toLowerCase());
            } else {
                return getMsgTemplate(botName, TemplateName.HERO_ACTION);
            }
        } else {
            if (heroChannelMessages.get(MAINCONFIG).containsKey(hChannel)) {
                return heroChannelMessages.get(MAINCONFIG).get(hChannel.toLowerCase());
            } else {
                return getMsgTemplate(botName, TemplateName.HERO_ACTION);
            }
        }
    }

    public String getIRCHeroChatChannelTemplate(String botName, String hChannel) {
        if (ircHeroChannelMessages.containsKey(botName)) {
            if (ircHeroChannelMessages.get(botName).containsKey(hChannel)) {
                return ircHeroChannelMessages.get(botName).get(hChannel.toLowerCase());
            } else {
                return getMsgTemplate(botName, TemplateName.IRC_HERO_CHAT);
            }
        } else {
            if (ircHeroChannelMessages.get(MAINCONFIG).containsKey(hChannel)) {
                return ircHeroChannelMessages.get(MAINCONFIG).get(hChannel.toLowerCase());
            } else {
                return getMsgTemplate(botName, TemplateName.IRC_HERO_CHAT);
            }
        }
    }

    public void loadTemplates(YamlConfiguration config, String configName) {
        messageTmpl.put(configName, new HashMap<String, String>());
        ircHeroChannelMessages.put(configName, new HashMap<String, String>());
        heroChannelMessages.put(configName, new HashMap<String, String>());
        heroActionChannelMessages.put(configName, new HashMap<String, String>());

        if (config.contains("message-format")) {
            for (String t : config.getConfigurationSection("message-format").getKeys(false)) {
                if (!t.startsWith("MemorySection")) {
                    messageTmpl.get(configName).put(t, ChatColor.translateAlternateColorCodes('&',
                            config.getString("message-format." + t, "")));
                    logDebug("message-format: " + t + " => " + messageTmpl.get(configName).get(t));
                }
            }

            if (config.contains("message-format.irc-hero-channels")) {
                for (String hChannelName : config.getConfigurationSection("message-format.irc-hero-channels").getKeys(false)) {
                    ircHeroChannelMessages.get(configName).put(hChannelName.toLowerCase(),
                            ChatColor.translateAlternateColorCodes('&',
                                    config.getString("message-format.irc-hero-channels."
                                            + hChannelName)));
                }
            }

            if (config.contains("message-format.hero-channels")) {
                for (String hChannelName : config.getConfigurationSection("message-format.hero-channels").getKeys(false)) {
                    heroChannelMessages.get(configName).put(hChannelName.toLowerCase(),
                            ChatColor.translateAlternateColorCodes('&',
                                    config.getString("message-format.hero-channels."
                                            + hChannelName)));
                }
            }
            if (config.contains("message-format.hero-action-channels")) {
                for (String hChannelName : config.getConfigurationSection("message-format.hero-action-channels").getKeys(false)) {
                    heroActionChannelMessages.get(configName).put(hChannelName.toLowerCase(),
                            ChatColor.translateAlternateColorCodes('&',
                                    config.getString("message-format.hero-action-channels."
                                            + hChannelName)));
                }
            }
        } else {
            logDebug("No message-format section found for " + configName);
        }
    }

    private void loadConfig() {
        debugEnabled = getConfig().getBoolean("Debug");
        identServerEnabled = getConfig().getBoolean("enable-ident-server");
        logDebug("Debug enabled");
        stripGameColors = getConfig().getBoolean("strip-game-colors", false);
        stripIRCColors = getConfig().getBoolean("strip-irc-colors", false);
        exactNickMatch = getConfig().getBoolean("nick-exact-match", true);
        colorConverter = new ColorConverter(stripGameColors, stripIRCColors);
        logDebug("strip-game-colors: " + stripGameColors);
        logDebug("strip-irc-colors: " + stripIRCColors);

        loadTemplates((YamlConfiguration) this.getConfig(), MAINCONFIG);

        defaultPlayerSuffix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-player-suffix", ""));
        defaultPlayerPrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-player-prefix", ""));
        defaultPlayerGroup = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-player-group", ""));
        defaultGroupSuffix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-group-suffix", ""));
        defaultGroupPrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-group-prefix", ""));
        defaultPlayerWorld = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.default-player-world", ""));

        listFormat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("list-format", ""));
        listSeparator = ChatColor.translateAlternateColorCodes('&', getConfig().getString("list-separator", ""));
        listPlayer = ChatColor.translateAlternateColorCodes('&', getConfig().getString("list-player", ""));
        listSortByName = getConfig().getBoolean("list-sort-by-name", true);

        ircConnCheckInterval = getConfig().getLong("conn-check-interval");
        ircChannelCheckInterval = getConfig().getLong("channel-check-interval");

        customTabList = getConfig().getBoolean("custom-tab-list", false);
        customTabPrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("custom-tab-prefix", "[IRC] "));
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
                    ircBots.put(pircBot.botNick, pircBot);
                    logInfo("Loaded bot: " + pircBot.botNick);
                }
            }
        }
    }

    /**
     *
     * @return
     */
    public boolean isMcMMOEnabled() {
        return (getServer().getPluginManager().getPlugin("mcMMO") != null);
    }

    public boolean isJobsEnabled() {
        return (getServer().getPluginManager().getPlugin("Jobs") != null);
    }
    
    public boolean isDeathMessagesEnabled() {
        return (getServer().getPluginManager().getPlugin("DeathMessages") != null);
    }

    /**
     *
     * @return
     */
    public boolean isFactionChatEnabled() {
        return (getServer().getPluginManager().getPlugin("FactionChat") != null);
    }

    /**
     *
     * @return
     */
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

    /**
     *
     * @return
     */
    public boolean isEssentialsEnabled() {
        return getServer().getPluginManager().getPlugin("Essentials") != null;
    }

    /**
     *
     * @return
     */
    public boolean isReportRTSEnabled() {
        return (getServer().getPluginManager().getPlugin("ReportRTS") != null);
    }

    /**
     *
     * @return
     */
    public boolean isHeroChatEnabled() {
        return (getServer().getPluginManager().getPlugin("Herochat") != null);
    }

    /**
     *
     * @return
     */
    public boolean isTitanChatEnabled() {
        return (getServer().getPluginManager().getPlugin("TitanChat") != null);
    }

    public boolean isTownyChatEnabled() {
        return (getServer().getPluginManager().getPlugin("TownyChat") != null);
    }

    /**
     *
     * @return
     */
    public boolean isCleverNotchEnabled() {
        return (getServer().getPluginManager().getPlugin("CleverNotch") != null);
    }

    private void createSampleBot() {
        File file = new File(pluginFolder + "/" + sampleFileName);
        try {
            InputStream in = PurpleIRC.class.getResourceAsStream("/" + sampleFileName);
            logDebug("in: " + in.available());
            byte[] buf = new byte[1024];
            int len;
            OutputStream out = new FileOutputStream(file);
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            in.close();
        } catch (IOException ex) {
            logError("Problem creating sample bot: " + ex.getMessage());
        }
    }

    /**
     *
     * @param worldName
     * @return
     */
    public String getWorldAlias(String worldName) {
        String alias = worldName;
        Plugin plugin = getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (plugin != null) {
            MVPlugin mvPlugin = (MVPlugin) plugin;
            MultiverseWorld world = mvPlugin.getCore().getMVWorldManager().getMVWorld(worldName);
            if (world != null) {
                alias = world.getAlias();
            }
        }
        if (alias == null) {
            alias = worldName;
        }
        logDebug("getWorldAlias: worldName => " + worldName);
        logDebug("getWorldAlias: alias => " + alias);
        return alias;
    }

    /**
     *
     * @param sender
     */
    public void reloadMainConfig(CommandSender sender) {
        sender.sendMessage(LOG_HEADER_F + "Reloading config.yml ...");
        reloadConfig();
        getConfig().options().copyDefaults(false);
        loadConfig();
        sender.sendMessage(LOG_HEADER_F + ChatColor.WHITE + "Done.");
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

    /**
     *
     * @param message
     */
    public void logInfo(String message) {
        log.log(Level.INFO, String.format("%s %s", LOG_HEADER, message));
    }

    /**
     *
     * @param message
     */
    public void logError(String message) {
        log.log(Level.SEVERE, String.format("%s %s", LOG_HEADER, message));
    }

    /**
     *
     * @param message
     */
    public void logDebug(String message) {
        if (debugEnabled) {
            log.log(Level.INFO, String.format("%s [DEBUG] %s", LOG_HEADER, message));
        }
    }

    /**
     *
     * @return
     */
    public String getMCUptime() {
        long jvmUptime = ManagementFactory.getRuntimeMXBean().getUptime();
        String msg = "Server uptime: " + (int) (jvmUptime / 86400000L) + " days"
                + " " + (int) (jvmUptime / 3600000L % 24L) + " hours"
                + " " + (int) (jvmUptime / 60000L % 60L) + " minutes"
                + " " + (int) (jvmUptime / 1000L % 60L) + " seconds.";
        return msg;
    }

    public String getServerMotd() {
        return "MOTD: " + getServer().getMotd();
    }

    /**
     *
     * @param ircBot
     * @param channelName
     * @return
     */
    public String getMCPlayers(PurpleBot ircBot, String channelName) {
        Map<String, String> playerList = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER)
        for (Player player : getServer().getOnlinePlayers()) {
            if (ircBot.hideListWhenVanished.get(channelName)) {
                logDebug("List: Checking if player " + player.getName() + " is vanished.");
                if (vanishHook.isVanished(player)) {
                    logDebug("Not adding player to list command" + player.getName() + " due to being vanished.");
                    continue;
                }
            }
            String pName = tokenizer.playerTokenizer(player, listPlayer);
            playerList.put(player.getName(), pName);
        }

        String pList;
        if (!listSortByName) {
            // sort as before
            ArrayList<String> tmp = new ArrayList<String>(playerList.values());
            Collections.sort(tmp, Collator.getInstance());
            pList = Joiner.on(listSeparator).join(tmp);
        } else // sort without nick prefixes
        {
            pList = Joiner.on(listSeparator).join(playerList.values());
        }

        String msg = listFormat
                .replace("%COUNT%", Integer.toString(playerList.size()))
                .replace("%MAX%", Integer.toString(getServer().getMaxPlayers()))
                .replace("%PLAYERS%", pList);
        logDebug("L: " + msg);
        return colorConverter.gameColorsToIrc(msg);
    }

    public String getRemotePlayers(String commandArgs) {
        if (commandArgs != null) {
            String host;
            int port = 25565;
            if (commandArgs.contains(":")) {
                host = commandArgs.split(":")[0];
                port = Integer.parseInt(commandArgs.split(":")[1]);
            } else {
                host = commandArgs;
            }
            Query query = new Query(host, port);
            try {
                query.sendQuery();
            } catch (IOException ex) {
                return ex.getMessage();
            }
            String players[] = query.getOnlineUsernames();
            String m;
            if (players.length == 0) {
                m = "There are no players on " + host
                        + ":" + port;
            } else {
                m = "Players on " + host + "("
                        + players.length
                        + "): " + Joiner.on(", ")
                        .join(players);
            }
            return m;
        } else {
            return "Invalid host.";
        }
    }

    /**
     *
     */
    public void setupVault() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            vaultHelpers = new VaultHook(this);
            logInfo("Hooked into Vault!");
        } else {
            logInfo("Not hooked into Vault!");
        }
    }

    /**
     *
     * @param player
     * @return
     */
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

    /**
     *
     * @param worldName
     * @param player
     * @return
     */
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

    /**
     *
     * @param player
     * @return
     */
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

    /**
     *
     * @param worldName
     * @param player
     * @return
     */
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

    /**
     *
     * @param player
     * @return
     */
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

    /**
     *
     * @param worldName
     * @param player
     * @return
     */
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

    /**
     *
     * @param player
     * @return
     */
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

    /**
     *
     * @param worldName
     * @param player
     * @return
     */
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

    /**
     *
     * @param worldName
     * @return
     */
    public String getWorldColor(String worldName) {
        String color = worldName;
        Plugin plugin = getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (plugin != null) {
            MVPlugin mvPlugin = (MVPlugin) plugin;
            MultiverseWorld world = mvPlugin.getCore().getMVWorldManager().getMVWorld(worldName);
            if (world != null) {
                color = world.getColor().toString();
            }
        }
        if (color == null) {
            color = worldName;
        }
        logDebug("getWorldColor: worldName => " + worldName);
        logDebug("getWorldColor: color => " + color);
        return color;
    }

    /**
     *
     * @param player
     * @return
     */
    public String getGroupSuffix(Player player) {
        String suffix = "";
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
                suffix = vaultHelpers.chat.getGroupSuffix(player.getLocation().getWorld(), group);
            }
        }
        if (suffix == null) {
            suffix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', suffix);
    }

    /**
     *
     * @param worldName
     * @param player
     * @return
     */
    public String getGroupSuffix(String worldName, String player) {
        String suffix = "";
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
                suffix = vaultHelpers.chat.getGroupSuffix(worldName, group);
            }
        }
        if (suffix == null) {
            suffix = "";
        }
        return ChatColor.translateAlternateColorCodes('&', suffix);
    }

    /**
     *
     * @return
     */
    public boolean checkForProtocolLib() {
        return (getServer().getPluginManager().getPlugin("ProtocolLib") != null);
    }

    public boolean isPrismEnabled() {
        return (getServer().getPluginManager().getPlugin("Prism") != null);
    }
}

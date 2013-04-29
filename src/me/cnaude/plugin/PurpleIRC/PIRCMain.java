package me.cnaude.plugin.PurpleIRC;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jibble.pircbot.Colors;

/**
 *
 * @author Chris Naudé
 */
public class PIRCMain extends JavaPlugin {

    public static String LOG_HEADER;
    static final Logger log = Logger.getLogger("Minecraft");
    private File pluginFolder;
    private File botsFolder;
    private File configFile;
    public static long startTime;
    public String gameChat, gameAction, gameDeath, gameQuit, gameJoin, gameKick;
    public String ircChat, ircAction, ircPart, ircKick, ircJoin, ircTopic;
    private boolean debugEnabled;
    private boolean stripGameColors;
    private boolean stripIRCColors;
    Long ircConnCheckInterval;
    PIRCBotWatcher botWatcher;
    EnumMap<ChatColor, String> ircColorMap = new EnumMap<ChatColor, String>(ChatColor.class);
    HashMap<String, ChatColor> gameColorMap = new HashMap<String, ChatColor>();
    public HashMap<String, PIRCBot> ircBots = new HashMap<String, PIRCBot>();
    public HashMap<String, Boolean> botConnected = new HashMap<String, Boolean>();

    @Override
    public void onEnable() {
        LOG_HEADER = "[" + this.getName() + "]";
        pluginFolder = getDataFolder();
        botsFolder = new File(pluginFolder + "/bots");
        configFile = new File(pluginFolder, "config.yml");
        createConfig();
        getConfig();
        loadConfig();
        getServer().getPluginManager().registerEvents(new PIRCListener(this), this);
        getCommand("irc").setExecutor(new PIRCCommands(this));
        buildIRCColorMap();
        buildGameColorMap();
        loadBots();
        botWatcher = new PIRCBotWatcher(this);
    }

    @Override
    public void onDisable() {
        if (ircBots.isEmpty()) {
            logInfo("No IRC bots to disconnect.");
        } else {
            logInfo("Disconnecting IRC bots.");
            for (PIRCBot ircBot : ircBots.values()) {
                ircBot.quit();
            }
        }
        botWatcher.cancel();
    }

    private void loadConfig() {
        debugEnabled = getConfig().getBoolean("Debug");
        stripGameColors = getConfig().getBoolean("strip-game-colors", false);
        stripIRCColors = getConfig().getBoolean("strip-irc-colors", false);
        gameAction = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-action", ""));
        gameChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-chat", ""));
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
                if (file.getName().endsWith("bot")) {
                    logInfo("Loading bot: " + file.getName());
                    PIRCBot pircBot = new PIRCBot(file, this);

                }
            }
        }
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
        String msg = "Players currently online("
                + getServer().getOnlinePlayers().length
                + "/" + getServer().getMaxPlayers() + "): ";
        for (Player p : getServer().getOnlinePlayers()) {
            msg = msg + p.getName() + ", ";
        }
        msg = msg.substring(0, msg.length() - 1);
        return msg;
    }

    private void buildIRCColorMap() {
        ircColorMap.put(ChatColor.AQUA, Colors.CYAN);
        ircColorMap.put(ChatColor.BLACK, Colors.BLACK);
        ircColorMap.put(ChatColor.BLUE, Colors.BLUE);
        ircColorMap.put(ChatColor.BOLD, Colors.BOLD);
        ircColorMap.put(ChatColor.DARK_AQUA, Colors.TEAL);
        ircColorMap.put(ChatColor.DARK_BLUE, Colors.DARK_BLUE);
        ircColorMap.put(ChatColor.DARK_GRAY, Colors.DARK_GRAY);
        ircColorMap.put(ChatColor.DARK_GREEN, Colors.DARK_GREEN);
        ircColorMap.put(ChatColor.DARK_PURPLE, Colors.PURPLE);
        ircColorMap.put(ChatColor.DARK_RED, Colors.RED);
        ircColorMap.put(ChatColor.GOLD, Colors.YELLOW);
        ircColorMap.put(ChatColor.GRAY, Colors.DARK_GRAY);
        ircColorMap.put(ChatColor.GREEN, Colors.GREEN);
        ircColorMap.put(ChatColor.LIGHT_PURPLE, Colors.MAGENTA);
        ircColorMap.put(ChatColor.RED, Colors.RED);
        ircColorMap.put(ChatColor.YELLOW, Colors.YELLOW);
        ircColorMap.put(ChatColor.WHITE, Colors.WHITE);
    }

    private void buildGameColorMap() {
        for (ChatColor chatColor : ircColorMap.keySet()) {
            gameColorMap.put(ircColorMap.get(chatColor), chatColor);
        }
    }

    public String gameColorsToIrc(String message) {
        if (stripGameColors) {
            return ChatColor.stripColor(message);
        } else {
            String newMessage = message;
            for (ChatColor gameColor : ircColorMap.keySet()) {
                newMessage = Matcher.quoteReplacement(newMessage).replaceAll(gameColor.toString(), ircColorMap.get(gameColor));
            }
            // We return the message with the remaining MC color codes stripped out
            return ChatColor.stripColor(newMessage);
        }
    }
    
    public String ircColorsToGame(String message) {
        if (stripIRCColors) {
            return Colors.removeFormattingAndColors(message);
        } else {
            String newMessage = message;
            for (String ircColor : gameColorMap.keySet()) {
                newMessage = Matcher.quoteReplacement(newMessage).replaceAll(ircColor.toString(), gameColorMap.get(ircColor).toString());
            }            
            // We return the message with the remaining IRC color codes stripped out
            return Colors.removeFormattingAndColors(message);
        }
    }
}

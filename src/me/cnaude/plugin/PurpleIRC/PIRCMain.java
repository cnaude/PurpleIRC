package me.cnaude.plugin.PurpleIRC;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public String ircChat, ircAction, ircPart, ircQuit, ircJoin;
    private boolean debugEnabled;
    private boolean stripGameColors;
    Long ircConnCheckInterval;
    PIRCBotWatcher botWatcher;
    EnumMap<ChatColor,String> colorMap = new EnumMap<ChatColor,String>(ChatColor.class);
    public HashMap<String, PIRCBot> ircBots = new HashMap<String, PIRCBot>();

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
        getServer().getPluginManager().registerEvents(new PIRCListener(this), this);
        getCommand("irc").setExecutor(new PIRCCommands(this));
        buildColorMap();
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
        gameAction = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-action"));
        gameChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-chat"));        
        gameDeath = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-death"));
        gameJoin = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-join"));
        gameQuit = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.game-quit"));
        
        ircAction = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-action"));
        ircChat = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-chat"));        
        ircJoin = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-join"));
        ircPart = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-part"));
        ircQuit = ChatColor.translateAlternateColorCodes('&', getConfig().getString("message-format.irc-quit"));
        
        
        
        
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

    private void buildColorMap() {
        colorMap.put(ChatColor.AQUA,Colors.CYAN);       
        colorMap.put(ChatColor.BLACK,Colors.BLACK);        
        colorMap.put(ChatColor.BLUE,Colors.BLUE);        
        colorMap.put(ChatColor.BOLD,Colors.BOLD);        
        colorMap.put(ChatColor.DARK_AQUA,Colors.TEAL);        
        colorMap.put(ChatColor.DARK_BLUE,Colors.DARK_BLUE);        
        colorMap.put(ChatColor.DARK_GRAY,Colors.DARK_GRAY);        
        colorMap.put(ChatColor.DARK_GREEN,Colors.DARK_GREEN);        
        colorMap.put(ChatColor.DARK_PURPLE,Colors.PURPLE);        
        colorMap.put(ChatColor.DARK_RED,Colors.RED);        
        colorMap.put(ChatColor.GOLD,Colors.YELLOW);        
        colorMap.put(ChatColor.GRAY,Colors.DARK_GRAY);        
        colorMap.put(ChatColor.GREEN,Colors.GREEN);        
        colorMap.put(ChatColor.LIGHT_PURPLE,Colors.MAGENTA);        
        colorMap.put(ChatColor.RED,Colors.RED);        
        colorMap.put(ChatColor.YELLOW,Colors.YELLOW);        
        colorMap.put(ChatColor.WHITE,Colors.WHITE);
    }
    
    public String gameColorsToIrc(String message) {  
        if (stripGameColors) {
            return ChatColor.stripColor(message);
        } else {
            String newMessage = message;        
            for (ChatColor chatColor : colorMap.keySet()) {
                newMessage = newMessage.replaceAll(chatColor.toString(), colorMap.get(chatColor));
            }
            // We return the message with the remaining MC color codes stripped out
            return ChatColor.stripColor(newMessage);
        }
    }
}

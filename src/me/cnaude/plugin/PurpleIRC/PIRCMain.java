/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author cnaude
 */
public class PIRCMain extends JavaPlugin {
    
    public static String LOG_HEADER;
    static final Logger log = Logger.getLogger("Minecraft");
    private File pluginFolder;
    private File botsFolder;
    private File configFile; 
    
    private boolean debugEnabled;
    
    public HashMap<String,PIRCBot> ircBots = new HashMap<String,PIRCBot>();

    @Override
    public void onEnable() {
        LOG_HEADER = "[" + this.getName() + "]";
        pluginFolder = getDataFolder();
        botsFolder = new File (pluginFolder + "/bots");
        configFile = new File(pluginFolder, "config.yml");
        createConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
        loadConfig();
        getServer().getPluginManager().registerEvents(new PIRCListener(this), this);
        getCommand("irc").setExecutor(new PIRCCommands(this));     
        
        loadBots();        
    }
    
    @Override
    public void onDisable() {
        for (PIRCBot ircBot : ircBots.values()) {
            ircBot.disconnect();
        }
    }    
    
    private void loadConfig() {
        debugEnabled = getConfig().getBoolean("Debug");
        logDebug("Debug enabled");
    }
    
    private void loadBots() {   
        if (botsFolder.exists()) {
            logInfo("Checking for bot files in " +  botsFolder);
            for (File file : botsFolder.listFiles()) {
                logInfo("Loading bot: " + file.getName());
                ircBots.put(file.getName().replace(".bot",""), new PIRCBot(file,this));
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
}

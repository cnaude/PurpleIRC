package me.cnaude.plugin.PurpleIRC;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jibble.pircbot.PircBot;

/**
 *
 * @author Chris Naude
 */
public final class PIRCBot extends PircBot {
    private boolean autoConnect;
    private String botServer;
    private String botNick;
    private String botServerPass;
    private int botServerPort;
    private String channelPrefix;
    private String commandPrefix;
    private String quitMessage;
    private final PIRCMain plugin;
    private YamlConfiguration config;
    // channel, #channel
    private HashMap<String, String> botChannels = new HashMap<String, String>();
    // #channel, channel
    private HashMap<String, String> channelKeys = new HashMap<String, String>();
    private HashMap<String, String> channelPassword = new HashMap<String, String>();
    // channel, topic
    private HashMap<String, String> channelTopic = new HashMap<String, String>();
    // channel, topic
    private HashMap<String, String> activeTopic = new HashMap<String, String>();
    private HashMap<String, Boolean> channelTopicProtected = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> channelAutoJoin = new HashMap<String, Boolean>();
    private Map<String, Collection<String>> opsList = new HashMap<String, Collection<String>>();
    private Map<String, Collection<String>> muteList = new HashMap<String, Collection<String>>();
    //          channel     command option     value
    private Map<String, Map<String, Map<String,String>>> commandMap = new HashMap<String, Map<String, Map<String,String>>>();

    private void loadConfig(File file) {
        try {
            config.load(file);
            autoConnect = config.getBoolean("autoconnect", true);
            botNick = config.getString("nick", "");
            botServer = config.getString("server", "");
            botServerPort = config.getInt("port");
            botServerPass = config.getString("password", "");
            channelPrefix = config.getString("channel-prefix", "");
            commandPrefix = config.getString("command-prefix", ".");
            quitMessage = config.getString("quit-message","");
            plugin.logInfo("Nick => " + botNick);
            plugin.logInfo("Server => " + botServer);
            plugin.logInfo("Port => " + botServerPort);
            plugin.logInfo("Channel Prefix => " + channelPrefix);
            plugin.logInfo("Command Prefix => " + commandPrefix);
            plugin.logDebug("Server Password => " + botServerPass);
            plugin.logInfo("Quit Message => " + quitMessage);
            for (String channel : config.getConfigurationSection("channels").getKeys(false)) {
                plugin.logInfo("Channel  => " + channelPrefix + channel);
                botChannels.put(channel,channelPrefix + channel);
                channelKeys.put(channelPrefix + channel,channel);

                channelAutoJoin.put(channel, config.getBoolean("channels." + channel + ".autojoin", true));
                plugin.logInfo("  Autojoin => " + channelAutoJoin.get(channel));

                channelPassword.put(channel, config.getString("channels." + channel + ".password", ""));
                plugin.logDebug("  Password => " + channelTopic.get(channel));

                channelTopic.put(channel, config.getString("channels." + channel + ".topic", ""));
                plugin.logInfo("  Topic => " + channelTopic.get(channel));

                channelTopicProtected.put(channel, config.getBoolean("channels." + channel + ".topic-protect", false));
                plugin.logInfo("  Topic Protected => " + channelTopicProtected.get(channel).toString());

                // build channel op list
                Collection<String> cOps = new ArrayList<String>();
                for (String channelOper : config.getStringList("channels." + channel + ".ops")) {
                    cOps.add(channelOper);
                    plugin.logInfo("  Channel Op => " + channelOper);
                }
                opsList.put(channel, cOps);
                
                // build mute list
                Collection<String> m = new ArrayList<String>();
                for (String channelOper : config.getStringList("channels." + channel + ".muted")) {
                    m.add(channelOper);
                    plugin.logInfo("  Channel Mute => " + channelOper);
                }
                opsList.put(channel, m);
                
                // build command map
                Map<String, Map<String,String>> map = new HashMap<String, Map<String,String>>();
                for (String command : config.getConfigurationSection("channels." + channel + ".commands").getKeys(false)) {
                    plugin.logInfo("  Command => " + command);                    
                    Map<String,String> optionPair = new HashMap<String,String>();
                    for (String commandOption : config.getConfigurationSection("channels." + channel + ".commands." + command).getKeys(false)) {                        
                        String commandOptionValue = config.getString("channels." + channel + ".commands." + command + "." + commandOption);                        
                        optionPair.put(commandOption, commandOptionValue);
                        plugin.logInfo("    " + commandOption + " => " + commandOptionValue);                          
                    }
                    map.put(command, optionPair);
                    
                }
                commandMap.put(channel, map);                
            }
        } catch (Exception ex) {
            plugin.logError(ex.getMessage());
        }
    }

    public PIRCBot(File file, PIRCMain plugin) {
        this.plugin = plugin;
        config = new YamlConfiguration();
        loadConfig(file);
        setName(botNick);
        if (autoConnect) {
            connect();
        } else {
            this.plugin.logInfo("Autoconnect is disabled. Not connecting to " + botServer + " as " + getName());
        }
    }
    
    public void connect() {
        try {
            this.plugin.logInfo("Connecting to " + botServer + " as " + getName());
            connect(botServer, botServerPort, botServerPass);
        } catch (Exception ex) {
            this.plugin.logError("Problem connecting to " + botServer + " => "
                    + " as " + getName() + " [Error: " + ex.getMessage() + "]");
        }
    }
    
    public void connect(CommandSender sender) {
        try {
            String msg = "Connecting to " + botServer + " as " + getName();
            sender.sendMessage(msg);
            this.plugin.logInfo(msg);
            connect(botServer, botServerPort, botServerPass);
        } catch (Exception ex) {
            String msg = "Problem connecting to " + botServer + " => "
                    + " as " + getName() + " [Error: " + ex.getMessage() + "]";
            this.plugin.logError(msg);
            sender.sendMessage(msg);
        }
    }
    
    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        if (!botChannels.containsValue(channel)) {
            return;
        }
        String myChannel = channelKeys.get(channel);
        if (message.startsWith(commandPrefix)) {        
            String command = message.split(" ")[0].substring(1);
            plugin.logDebug("IRC command detected: " + command);
            if (commandMap.get(myChannel).containsKey(command)) {
                String gameCommand = (String) commandMap.get(myChannel).get(command).get("game_command");
                String modes = (String) commandMap.get(myChannel).get(command).get("modes");
                boolean privateCommand = Boolean.parseBoolean(commandMap.get(myChannel).get(command).get("private"));
                
                plugin.logDebug(gameCommand + ":" + modes + ":" + privateCommand);

                if (gameCommand.equals("@list")) {
                    sendMessage(channel, plugin.getMCPlayers());
                } else if (gameCommand.equals("@uptime")) {
                    sendMessage(channel, plugin.getMCUptime());
                } else if (gameCommand.equals("@help")) {
                    sendCommands(channel);
                } else {                    
                    plugin.getServer().dispatchCommand(new PIRCCommandSender(this, channel), gameCommand);
                }
            } else {
                sendMessage(channel, "I'm sorry, I don't understand that command. Try the .help command.");
            }
        } else {
            plugin.getServer().broadcast("[IRC]<" + sender + "> " + message, "irc.message.chat");
        }
    }

    public void message(Player player, String message) {
        String world = player.getLocation().getWorld().getName();
        String pName = player.getName();
        for (String channel : botChannels.values()) {
            this.sendMessage(channel, "[" + world + "]<" + pName + "> " + message);
        }
    }

    public void joinMessage(Player player, String message) {
        String world = player.getLocation().getWorld().getName();
        for (String channel : botChannels.values()) {
            this.sendMessage(channel, "[" + world + "] " + ChatColor.stripColor(message));
        }
    }

    public void quitMessage(Player player, String message) {
        String world = player.getLocation().getWorld().getName();
        for (String channel : botChannels.values()) {
            this.sendMessage(channel, "[" + world + "] " + ChatColor.stripColor(message));
        }
    }

    public void action(Player player, String message) {
        String world = player.getLocation().getWorld().getName();
        for (String channel : botChannels.values()) {
            this.sendMessage(channel, "[" + world + "] " + message);
        }
    }

    public void death(Player player, String message) {
        String world = player.getLocation().getWorld().getName();
        for (String channel : botChannels.values()) {
            this.sendMessage(channel, "[" + world + "] " + message);
        }
    }
      
    public void fixTopic(String channel, String topic, String setBy) {
        if (setBy.equals(botNick)) {
            return;
        }
        String myChannel = channelKeys.get(channel);
        if (channelTopic.containsKey(myChannel)) {
            if (channelTopicProtected.containsKey(myChannel)) {
                if (channelTopicProtected.containsKey(myChannel)) {
                    String myTopic = channelTopic.get(myChannel);
                    if (!topic.equals(myTopic)) {
                        setTopic(channel,myTopic);
                    }
                }
            }
        }        
    }
    
    public void saveTopic(String channel, String topic) {
        String myChannel = channelKeys.get(channel);
        if (channelTopic.containsKey(myChannel)) {            
            channelTopic.put(myChannel,topic);
        }        
    }

    @Override
    public void onJoin(String channel, String sender, String login, String hostname) {
        if (!botChannels.containsValue(channel)) {
            return;
        }
        plugin.getServer().broadcast("[IRC] " + sender + " has entered the room.", "irc.message.join");        
    }

    @Override
    public void onAction(String sender, String login, String hostname, String target, String action) {
        if (!botChannels.containsValue(target)) {
            return;
        }
        plugin.getServer().broadcast("[IRC] *** " + sender + " " + action, "irc.message.action");
    }

    @Override
    public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        if (!botChannels.containsValue(channel)) {
            return;
        }
        plugin.getServer().broadcast("[IRC] " + recipientNick + " was kicked by " + kickerNick + ". (Reason: " + reason + ")", "irc.message.kick");
    }

    @Override
    public void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
        if (!botChannels.containsValue(channel) || !changed) {
            return;
        }
        fixTopic(channel,topic,setBy);
        if (changed) {
            plugin.getServer().broadcast("[IRC] " + setBy + " changed the topic: " + topic, "irc.message.topic");
        }
        activeTopic.put(channelKeys.get(channel), topic);
    }

    @Override
    public void onVersion(String sourceNick, String sourceLogin, String sourceHostname, String target) {
        this.sendMessage(sourceNick,
                "[Name: " + plugin.getDescription().getFullName() + "]"
                + "[Desc: " + plugin.getDescription().getDescription() + "]"
                + "[Version: " + plugin.getDescription().getVersion() + "]"
                + "[URL: " + plugin.getDescription().getWebsite() + "]");
    }

    @Override
    public void onDisconnect() {
        plugin.getServer().broadcast("[" + botNick + "] Disconnected from IRC server.", "irc.message.disconnect");
    }
    
    public void quit(CommandSender sender) {
        sender.sendMessage("Disconnecting " + getName() + " from IRC server " + botServer);
        quit();
    }
    
    public void quit() {
        if (quitMessage.isEmpty()) {               
            quitServer();
        } else {            
            quitServer(quitMessage);
        }
    }

    @Override
    public void onPart(String channel, String sender, String login, String hostname) {
        if (!botChannels.containsValue(channel)) {
            return;
        }
        plugin.getServer().broadcast("[IRC] " + sender + " has left the room.", "irc.message.part");
    }

    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        plugin.getServer().broadcast("[IRC] " + sourceNick + " has quit (Reason: " + reason + ")", "irc.message.quit");
    }

    @Override
    public void onConnect() {
        for (String channel : this.botChannels.keySet()) {
            if (channelAutoJoin.containsKey(channel)) {
                if (channelAutoJoin.get(channel)) {                    
                    this.plugin.logInfo("Auto joining channel " + botChannels.get(channel));
                    this.joinChannel(botChannels.get(channel));                    
                } else {
                    this.plugin.logInfo("Not auto joining channel " + botChannels.get(channel));
                }
            }
        }
    }
    
    public void sendCommands(String channel) {
        String myChannel = channelKeys.get(channel);
        if (commandMap.containsKey(myChannel)) {
            String commands = "";
            for (String command : commandMap.get(myChannel).keySet()) {
                commands = commands + ", " + command;
            }
            sendMessage(channel, "Valid commands:" + commands.substring(1));
        }
    }
    
    public void sendTopic(CommandSender sender) {
        for (String channel : this.botChannels.keySet()) {
            if (commandMap.containsKey(channel)) {            
                sender.sendMessage(channel + "topic: " + activeTopic.get(channel));
            }
        }
    }
}

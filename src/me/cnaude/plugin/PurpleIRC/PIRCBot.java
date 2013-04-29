package me.cnaude.plugin.PurpleIRC;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

/**
 *
 * @author Chris Naude
 */
public final class PIRCBot extends PircBot {

    public boolean autoConnect;
    private String botServer;
    private String botNick;
    private String botServerPass;
    private int botServerPort;
    private String channelPrefix;
    private String commandPrefix;
    private String quitMessage;
    private final PIRCMain plugin;
    private File file;
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
    // channel, modes
    private HashMap<String, String> channelModes = new HashMap<String, String>();
    private HashMap<String, Boolean> channelTopicProtected = new HashMap<String, Boolean>();
    private HashMap<String, Boolean> channelAutoJoin = new HashMap<String, Boolean>();
    private Map<String, Collection<String>> opsList = new HashMap<String, Collection<String>>();
    private Map<String, Collection<String>> muteList = new HashMap<String, Collection<String>>();
    private Map<String, Collection<String>> enabledMessages = new HashMap<String, Collection<String>>();
    //          channel     command option     value
    private Map<String, Map<String, Map<String, String>>> commandMap = new HashMap<String, Map<String, Map<String, String>>>();

    public PIRCBot(File file, PIRCMain plugin) {
        this.plugin = plugin;
        this.file = file;
        config = new YamlConfiguration();
        loadConfig();
        asyncConnect(false);
    }

    public void reload(CommandSender sender) {
        config = new YamlConfiguration();
        loadConfig();
        asyncReConnect();
    }

    public void reloadConfig(CommandSender sender) {
        config = new YamlConfiguration();
        loadConfig();
    }

    public void asyncReConnect() {
        // We want to void blocking the main Bukkit thread        
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    plugin.logInfo("Reconnecting to " + botServer + " as " + getName());
                    reconnect();
                } catch (Exception ex) {
                    plugin.logError("Problem reconnecting to " + botServer + " => "
                            + " as " + getName() + " [Error: " + ex.getMessage() + "]");
                }
            }
        });
    }

    public void mute(String channel, CommandSender sender, String user) {
        String myChannel = channelKeys.get(channel);
        if (muteList.get(myChannel).contains(user)) {
            sender.sendMessage("User '" + user + "' is already muted.");
        } else {
            sender.sendMessage("User '" + user + "' is now muted.");
            muteList.get(myChannel).add(user);
        }
    }

    public void unMute(String channel, CommandSender sender, String user) {
        String myChannel = channelKeys.get(channel);
        if (muteList.get(myChannel).contains(user)) {
            sender.sendMessage("User '" + user + "' is no longer muted.");
            muteList.get(myChannel).remove(user);
        } else {
            sender.sendMessage("User '" + user + "' is not muted.");
        }
    }

    public void asyncConnect(boolean fromCommand) {
        // We want to void blocking the main Bukkit thread
        if (autoConnect || fromCommand) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        plugin.logInfo("Connecting to " + botServer + " as " + getName());
                        connect(botServer, botServerPort, botServerPass);
                    } catch (Exception ex) {
                        plugin.logError("Problem connecting to " + botServer + " => "
                                + " as " + getName() + " [Error: " + ex.getMessage() + "]");
                    }
                }
            });
        } else {
            this.plugin.logInfo("Autoconnect is disabled. Not connecting to " + botServer + " as " + getName());
        }
    }

    public void asyncConnect(CommandSender sender, boolean fromCommand) {
        // We want to void blocking the main Bukkit thread
        if (autoConnect || fromCommand) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        plugin.logInfo("Connecting to " + botServer + " as " + getName());
                        connect(botServer, botServerPort, botServerPass);
                    } catch (Exception ex) {
                        plugin.logError("Problem connecting to " + botServer + " => "
                                + " as " + getName() + " [Error: " + ex.getMessage() + "]");
                    }
                }
            });
        } else {
            this.plugin.logInfo("Autoconnect is disabled. Not connecting to " + botServer + " as " + getName());
        }
    }
    
    public void saveConfig(CommandSender sender) {        
        try {
            config.save(file);     
            sender.sendMessage("Saving bot \"" + botNick + "\" to " + file.getName());
        } catch (Exception ex) {
            plugin.logError(ex.getMessage());
            sender.sendMessage(ex.getMessage());
        }
    }
    
    public void saveConfig() {
        try {
            config.save(file);            
        } catch (Exception ex) {
            plugin.logError(ex.getMessage());
        }
    }

    private void loadConfig() {
        try {
            config.load(file);
            autoConnect = config.getBoolean("autoconnect", true);
            botNick = config.getString("nick", "");
            setName(botNick);
            plugin.ircBots.put(botNick, this);
            plugin.botConnected.put(botNick, this.isConnected());
            botServer = config.getString("server", "");
            botServerPort = config.getInt("port");
            botServerPass = config.getString("password", "");
            channelPrefix = config.getString("channel-prefix", "");
            commandPrefix = config.getString("command-prefix", ".");
            quitMessage = ChatColor.translateAlternateColorCodes('&', config.getString("quit-message", ""));
            plugin.logDebug("Nick => " + botNick);
            plugin.logDebug("Server => " + botServer);
            plugin.logDebug("Port => " + botServerPort);
            plugin.logDebug("Channel Prefix => " + channelPrefix);
            plugin.logDebug("Command Prefix => " + commandPrefix);
            plugin.logDebug("Server Password => " + botServerPass);
            plugin.logInfo("Quit Message => " + quitMessage);
            for (String channel : config.getConfigurationSection("channels").getKeys(false)) {
                plugin.logDebug("Channel  => " + channelPrefix + channel);
                botChannels.put(channel, channelPrefix + channel);
                channelKeys.put(channelPrefix + channel, channel);

                channelAutoJoin.put(channel, config.getBoolean("channels." + channel + ".autojoin", true));
                plugin.logDebug("  Autojoin => " + channelAutoJoin.get(channel));

                channelPassword.put(channel, config.getString("channels." + channel + ".password", ""));
                plugin.logDebug("  Password => " + channelTopic.get(channel));

                channelTopic.put(channel, config.getString("channels." + channel + ".topic", ""));
                plugin.logDebug("  Topic => " + channelTopic.get(channel));

                channelModes.put(channel, config.getString("channels." + channel + ".modes", ""));
                plugin.logDebug("  Channel Modes => " + channelModes.get(channel));

                channelTopicProtected.put(channel, config.getBoolean("channels." + channel + ".topic-protect", false));
                plugin.logDebug("  Topic Protected => " + channelTopicProtected.get(channel).toString());

                // build channel op list
                Collection<String> cOps = new ArrayList<String>();
                for (String channelOper : config.getStringList("channels." + channel + ".ops")) {
                    cOps.add(channelOper);
                    plugin.logDebug("  Channel Op => " + channelOper);
                }
                opsList.put(channel, cOps);

                // build mute list
                Collection<String> m = new ArrayList<String>();
                for (String mutedUser : config.getStringList("channels." + channel + ".muted")) {
                    m.add(mutedUser);
                    plugin.logDebug("  Channel Mute => " + mutedUser);
                }
                muteList.put(channel, m);

                // build valid chat list
                Collection<String> c = new ArrayList<String>();
                for (String validChat : config.getStringList("channels." + channel + ".enabled-messages")) {
                    c.add(validChat);
                    plugin.logDebug("  Enabled Message => " + validChat);
                }
                enabledMessages.put(channel, c);

                // build command map
                Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
                for (String command : config.getConfigurationSection("channels." + channel + ".commands").getKeys(false)) {
                    plugin.logDebug("  Command => " + command);
                    Map<String, String> optionPair = new HashMap<String, String>();
                    for (String commandOption : config.getConfigurationSection("channels." + channel + ".commands." + command).getKeys(false)) {
                        String commandOptionValue = config.getString("channels." + channel + ".commands." + command + "." + commandOption);
                        optionPair.put(commandOption, commandOptionValue);
                        plugin.logDebug("    " + commandOption + " => " + commandOptionValue);
                    }
                    map.put(command, optionPair);

                }
                commandMap.put(channel, map);
            }
        } catch (Exception ex) {
            plugin.logError(ex.getMessage());
        }
    }

    private boolean isOp(String user, String channel) {
        User users[] = getUsers(channel);
        for (User u : users) {
            if (u.isOp()) {
                plugin.logDebug("User '" + user + "' prefix: " + u.getPrefix());
                return true;
            }
        }
        return false;
    }

    private boolean hasVoice(String user, String channel) {
        User users[] = getUsers(channel);
        for (User u : users) {
            if (u.hasVoice()) {
                plugin.logDebug("User '" + user + "' prefix: " + u.getPrefix());
                return true;
            }
        }
        return false;
    }

    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        if (!botChannels.containsValue(channel)) {
            return;
        }
        String myChannel = channelKeys.get(channel);
        if (muteList.get(myChannel).contains(sender)) {
            return;
        }
        if (message.startsWith(commandPrefix)) {
            String command = message.split(" ")[0].substring(1);
            plugin.logDebug("IRC command detected: " + command);
            if (commandMap.get(myChannel).containsKey(command)) {
                String gameCommand = (String) commandMap.get(myChannel).get(command).get("game_command");
                String modes = (String) commandMap.get(myChannel).get(command).get("modes");
                boolean privateCommand = Boolean.parseBoolean(commandMap.get(myChannel).get(command).get("private"));
                String target = channel;
                if (privateCommand) {
                    target = sender;
                }
                plugin.logDebug(target + ":" + gameCommand + ":" + modes + ":" + privateCommand);

                boolean modeOkay = false;
                if (modes.equals("*")) {
                    modeOkay = true;
                } else if (modes.contains("o")) {
                    modeOkay = isOp(sender, channel);
                } else if (modes.contains("v")) {
                    modeOkay = hasVoice(sender, channel);
                }

                if (modeOkay) {
                    if (gameCommand.equals("@list")) {
                        sendMessage(target, plugin.getMCPlayers());
                    } else if (gameCommand.equals("@uptime")) {
                        sendMessage(target, plugin.getMCUptime());
                    } else if (gameCommand.equals("@help")) {
                        sendCommands(target);
                    } else {
                        plugin.getServer().dispatchCommand(new PIRCCommandSender(this, target, plugin), gameCommand);
                    }
                } else {
                    plugin.logDebug("User '" + sender + "' mode not okay");
                }
            } else {
                sendMessage(channel, "I'm sorry, I don't understand that command. For a list of commands type \"" + commandPrefix + "help\".");
            }
        } else {
            if (enabledMessages.get(myChannel).contains("irc-chat")) {
                plugin.getServer().broadcast(plugin.ircColorsToGame(Matcher.quoteReplacement(plugin.ircChat)
                        .replaceAll("%NAME%", sender)
                        .replaceAll("%MESSAGE%", Matcher.quoteReplacement(message))
                        .replaceAll("%CHANNEL%", channel)), "irc.message.chat");
            }
        }
    }

    public void gameChat(Player player, String message) {
        if (!this.isConnected()) {
            return;
        }
        for (String channel : botChannels.values()) {
            if (enabledMessages.get(channelKeys.get(channel)).contains("game-chat")) {                
                this.sendMessage(channel, plugin.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameChat)
                        .replaceAll("%NAME%", player.getName())
                        .replaceAll("%MESSAGE%", message)
                        .replaceAll("%WORLD%", player.getLocation().getWorld().getName())));
            }
        }
    }

    public void gameJoin(Player player, String message) {
        if (!this.isConnected()) {
            return;
        }
        for (String channel : botChannels.values()) {
            if (enabledMessages.get(channelKeys.get(channel)).contains("game-join")) {
                this.sendMessage(channel, plugin.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameJoin)
                        .replaceAll("%NAME%", player.getName())
                        .replaceAll("%MESSAGE%", message)
                        .replaceAll("%WORLD%", player.getLocation().getWorld().getName())));
            }
        }
    }

    public void gameQuit(Player player, String message) {
        if (!this.isConnected()) {
            return;
        }
        for (String channel : botChannels.values()) {
            if (enabledMessages.get(channelKeys.get(channel)).contains("game-quit")) {
                this.sendMessage(channel, plugin.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameQuit)
                        .replaceAll("%NAME%", player.getName())
                        .replaceAll("%MESSAGE%", message)
                        .replaceAll("%WORLD%", player.getLocation().getWorld().getName())));
            }
        }
    }

    public void gameAction(Player player, String message) {
        if (!this.isConnected()) {
            return;
        }
        for (String channel : botChannels.values()) {
            if (enabledMessages.get(channelKeys.get(channel)).contains("game-action")) {
                this.sendMessage(channel, plugin.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameAction)
                        .replaceAll("%NAME%", player.getName())
                        .replaceAll("%MESSAGE%", message)
                        .replaceAll("%WORLD%", player.getLocation().getWorld().getName())));
            }
        }
    }

    public void gameDeath(Player player, String message) {
        if (!this.isConnected()) {
            return;
        }
        for (String channel : botChannels.values()) {
            if (enabledMessages.get(channelKeys.get(channel)).contains("game-death")) {
                this.sendMessage(channel, plugin.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameDeath)
                        .replaceAll("%NAME%", player.getName())
                        .replaceAll("%MESSAGE%", message)
                        .replaceAll("%WORLD%", player.getLocation().getWorld().getName())));
            }
        }
    }
    
    public void changeTopic(String channel, String topic, CommandSender sender) {
        setTopic(channel,topic);        
        config.set("channels." + channelKeys.get(channel) + ".topic", topic);
        sender.sendMessage("IRC topic for " + channel + " changed to \"" + topic + "\"");
    }
    
    public void setServer(CommandSender sender, String botServer) {              
        setServer(sender,botServer,autoConnect);
    }
    
    public void setServer(CommandSender sender, String botServer, Boolean autoConnect) {  
        this.botServer = botServer;
        config.set("server", botServer);
        this.autoConnect = autoConnect;
        config.set("autoconnect", autoConnect.toString());
        sender.sendMessage("IRC server changed to \"" + botServer + "\". (AutoConnect: " + autoConnect.toString() + ")");
    }
    
    public void addOp(String channel, String userMask, CommandSender sender) {
        String myChannel = channelKeys.get(channel);
        if (opsList.get(myChannel).contains(userMask)) {
            sender.sendMessage("User mask'" + userMask + "' is already in the ops list.");
        } else {
            sender.sendMessage("User mask'" + userMask + "' has been added to the ops list.");
            opsList.get(myChannel).add(userMask);
        }
        config.set("channels." + myChannel + ".ops", opsList.get(myChannel));
    }
    
    public void removeOp(String channel, String userMask, CommandSender sender) {
        String myChannel = channelKeys.get(channel);
        if (opsList.get(myChannel).contains(userMask)) {
            sender.sendMessage("User mask'" + userMask + "' has been removed to the ops list.");
            opsList.get(myChannel).remove(userMask);            
        } else {
            sender.sendMessage("User mask'" + userMask + "' is not in the ops list.");
        }
        config.set("channels." + myChannel + ".ops", opsList.get(myChannel));
    }    

    public void fixTopic(String channel, String topic, String setBy) {
        String myChannel = channelKeys.get(channel);
        if (setBy.equals(botNick)) {            
            config.set("channels." + myChannel + ".topic", topic);
            return;
        }
        
        if (channelTopic.containsKey(myChannel)) {
            if (channelTopicProtected.containsKey(myChannel)) {
                if (channelTopicProtected.containsKey(myChannel)) {
                    String myTopic = channelTopic.get(myChannel);
                    if (!topic.equals(myTopic)) {
                        setTopic(channel, myTopic);
                    }
                }
            }
        }
    }

    @Override
    public void onJoin(String channel, String sender, String login, String hostname) {
        if (!botChannels.containsValue(channel)) {
            return;
        }
        if (enabledMessages.get(channelKeys.get(channel)).contains("irc-join")) {
            plugin.getServer().broadcast(plugin.ircColorsToGame(Matcher.quoteReplacement(plugin.ircJoin)
                    .replaceAll("%NAME%", sender)
                    .replaceAll("%CHANNEL%", channel)), "irc.message.join");
        }
        opFriends(channel, sender, login, hostname);
        if (sender.equals(botNick)) {
            plugin.logDebug("Setting channel modes: " + channel + " => " + channelModes.get(channelKeys.get(channel)));
            setMode(channel, channelModes.get(channelKeys.get(channel)));
        }
    }

    private void opFriends(String channel, String sender, String login, String hostname) {
        if (sender.equals(botNick)) {
            return;
        }
        String myChannel = channelKeys.get(channel);
        for (String user : opsList.get(myChannel)) {
            plugin.logDebug("OP => " + user);
            //sender!*login@hostname
            String mask[] = user.split("[\\!\\@]", 3);
            if (mask.length == 3) {
                String gUser = createRegexFromGlob(mask[0]);
                String gLogin = createRegexFromGlob(mask[1]);
                String gHost = createRegexFromGlob(mask[2]);
                plugin.logDebug("Nick: " + sender + " =~ " + gUser + " = " + sender.matches(gUser));
                plugin.logDebug("Name: " + login + " =~ " + gLogin + " = " + login.matches(gLogin));
                plugin.logDebug("Hostname: " + hostname + " =~ " + gHost + " = " + hostname.matches(gHost));
                if (sender.matches(gUser) && login.matches(gLogin) && hostname.matches(gHost)) {
                    plugin.logInfo("Auto-opping " + sender + " on " + channel);
                    this.op(channel, sender);
                    // leave after our first match
                    return;
                } else {
                    plugin.logDebug("No match: " + sender + "!" + login + "@" + hostname + " != " + user);
                }
            } else {
                plugin.logInfo("Invalid op mask: " + user);
            }
        }
    }

    @Override
    public void onAction(String sender, String login, String hostname, String channel, String action) {
        if (!botChannels.containsValue(channel)) {
            return;
        }
        if (enabledMessages.get(channelKeys.get(channel)).contains("irc-action")) {
            plugin.getServer().broadcast(plugin.ircColorsToGame(Matcher.quoteReplacement(plugin.ircAction)
                    .replaceAll("%NAME%", sender)
                    .replaceAll("%MESSAGE%", action)
                    .replaceAll("%CHANNEL%", channel)), "irc.message.action");
        }
    }

    @Override
    public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        if (!botChannels.containsValue(channel)) {
            return;
        }
        if (enabledMessages.get(channelKeys.get(channel)).contains("irc-kick")) {
            plugin.getServer().broadcast(plugin.ircColorsToGame(Matcher.quoteReplacement(plugin.ircKick)
                    .replaceAll("%NAME%", recipientNick)
                    .replaceAll("%REASON%", reason)
                    .replaceAll("%KICKER%", kickerNick)
                    .replaceAll("%CHANNEL%", channel)), "irc.message.kick");
        }
    }

    @Override
    public void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
        if (!botChannels.containsValue(channel)) {
            return;
        }
        fixTopic(channel, topic, setBy);
        if (changed) {
            if (enabledMessages.get(channelKeys.get(channel)).contains("irc-topic")) {
                plugin.getServer().broadcast(plugin.ircColorsToGame(Matcher.quoteReplacement(plugin.ircTopic)
                        .replaceAll("%NAME%", setBy)
                        .replaceAll("%TOPIC%", topic)
                        .replaceAll("%CHANNEL%", channel)), "irc.message.topic");
            }
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
        plugin.botConnected.put(botNick, false);
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
            quitServer(plugin.gameColorsToIrc(quitMessage));
        }
    }

    @Override
    public void onPart(String channel, String sender, String login, String hostname) {
        if (!botChannels.containsValue(channel)) {
            return;
        }
        if (enabledMessages.get(channelKeys.get(channel)).contains("irc-part")) {
            plugin.getServer().broadcast(plugin.ircColorsToGame(Matcher.quoteReplacement(plugin.ircPart)
                    .replaceAll("%NAME%", sender)
                    .replaceAll("%CHANNEL%", channel)), "irc.message.part");
        }
    }

    //@Override
    //public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
    //    plugin.getServer().broadcast("[IRC] " + sourceNick + " has quit (Reason: " + reason + ")", "irc.message.quit");        
    //}
    @Override
    public void onConnect() {
        plugin.botConnected.put(botNick, true);
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
                sender.sendMessage(botChannels.get(channel) + " topic: " + activeTopic.get(channel));
            }
        }
    }

    public void sendUserList(CommandSender sender, String channel) {
        if (!botChannels.containsValue(channel)) {
            sender.sendMessage(ChatColor.RED + "Invalid channel name.");
            return;
        }
        sender.sendMessage(ChatColor.RED + "-----[  " + ChatColor.WHITE + channel
                + ChatColor.RED + " - " + ChatColor.WHITE + getName() + ChatColor.RED + " ]-----");
        if (!this.isConnected()) {
            sender.sendMessage(ChatColor.RED + " Not connected!");
            return;
        }
        List<String> channelUsers = new ArrayList<String>();
        for (User user : this.getUsers(channel)) {
            channelUsers.add(user.toString());
        }
        Collections.sort(channelUsers, Collator.getInstance());
        for (String userName : channelUsers) {
            sender.sendMessage(ChatColor.RED + "  " + ChatColor.WHITE + userName);
        }
    }

    public void sendUserList(CommandSender sender) {
        for (String channel : botChannels.values()) {
            sendUserList(sender, channel);
        }
    }
    
    //http://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
    private static String createRegexFromGlob(String glob) {
        String out = "^";
        for (int i = 0; i < glob.length(); ++i) {
            final char c = glob.charAt(i);
            switch (c) {
                case '*':
                    out += ".*";
                    break;
                case '?':
                    out += '.';
                    break;
                case '.':
                    out += "\\.";
                    break;
                case '\\':
                    out += "\\\\";
                    break;
                default:
                    out += c;
            }
        }
        out += '$';
        return out;
    }    
}

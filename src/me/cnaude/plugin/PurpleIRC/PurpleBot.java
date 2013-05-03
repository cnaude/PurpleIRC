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
import me.cnaude.plugin.PurpleIRC.IRC.ActionListener;
import me.cnaude.plugin.PurpleIRC.IRC.ConnectListener;
import me.cnaude.plugin.PurpleIRC.IRC.DisconnectListener;
import me.cnaude.plugin.PurpleIRC.IRC.JoinListener;
import me.cnaude.plugin.PurpleIRC.IRC.KickListener;
import me.cnaude.plugin.PurpleIRC.IRC.MessageListener;
import me.cnaude.plugin.PurpleIRC.IRC.PartListener;
import me.cnaude.plugin.PurpleIRC.IRC.TopicListener;
import me.cnaude.plugin.PurpleIRC.IRC.VersionListener;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;


/**
 *
 * @author Chris Naude
 */
public final class PurpleBot {

    public PircBotX bot;
    public boolean autoConnect;
    public String botServer;
    public String botNick;
    public String botLogin;
    public String botServerPass;
    public int botServerPort;    
    public String commandPrefix;
    public String quitMessage;
    public final PIRCMain plugin;
    private File file;
    private YamlConfiguration config;

    public ArrayList<String> botChannels = new ArrayList<String>();
    public HashMap<String, String> channelPassword = new HashMap<String, String>();    
    public HashMap<String, String> channelTopic = new HashMap<String, String>();
    public HashMap<String, String> activeTopic = new HashMap<String, String>();
    public HashMap<String, String> channelModes = new HashMap<String, String>();
    public HashMap<String, Boolean> channelTopicProtected = new HashMap<String, Boolean>();
    public HashMap<String, Boolean> channelAutoJoin = new HashMap<String, Boolean>();
    public Map<String, Collection<String>> opsList = new HashMap<String, Collection<String>>();
    public Map<String, Collection<String>> worldList = new HashMap<String, Collection<String>>();    
    public Map<String, Collection<String>> muteList = new HashMap<String, Collection<String>>();
    public Map<String, Collection<String>> enabledMessages = new HashMap<String, Collection<String>>();
    //          channel     command option     value
    public Map<String, Map<String, Map<String, String>>> commandMap = new HashMap<String, Map<String, Map<String, String>>>();

    public PurpleBot(File file, PIRCMain plugin) {
        bot = new PircBotX();
        bot.getListenerManager().addListener(new ActionListener(plugin, this));
        bot.getListenerManager().addListener(new ConnectListener(plugin, this));
        bot.getListenerManager().addListener(new DisconnectListener(plugin, this));
        bot.getListenerManager().addListener(new JoinListener(plugin, this));
        bot.getListenerManager().addListener(new KickListener(plugin, this));
        bot.getListenerManager().addListener(new MessageListener(plugin, this));
        bot.getListenerManager().addListener(new PartListener(plugin, this));
        bot.getListenerManager().addListener(new TopicListener(plugin, this));
        bot.getListenerManager().addListener(new VersionListener(plugin));        
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
                    plugin.logInfo("Reconnecting to " + botServer + " as " + bot.getName());
                    bot.reconnect();
                } catch (Exception ex) {
                    plugin.logError("Problem reconnecting to " + botServer + " => "
                            + " as " + bot.getName() + " [Error: " + ex.getMessage() + "]");
                }
            }
        });
    }

    public void mute(String channelName, CommandSender sender, String user) {        
        if (muteList.get(channelName).contains(user)) {
            sender.sendMessage("User '" + user + "' is already muted.");
        } else {
            sender.sendMessage("User '" + user + "' is now muted.");
            muteList.get(channelName).add(user);
            saveConfig();
        }
        
    }

    public void unMute(String channelName, CommandSender sender, String user) {        
        if (muteList.get(channelName).contains(user)) {
            sender.sendMessage("User '" + user + "' is no longer muted.");
            muteList.get(channelName).remove(user);
            saveConfig();
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
                        plugin.logInfo("Connecting to " + botServer + " as " + bot.getName());
                        bot.connect(botServer, botServerPort, botServerPass);
                    } catch (Exception ex) {
                        plugin.logError("Problem connecting to " + botServer + " => "
                                + " as " + bot.getName() + " [Error: " + ex.getMessage() + "]");
                    }
                }
            });
        } else {
            this.plugin.logInfo("Autoconnect is disabled. Not connecting to " + botServer + " as " + bot.getName());
        }
    }

    public void asyncConnect(CommandSender sender, boolean fromCommand) {
        // We want to void blocking the main Bukkit thread
        if (autoConnect || fromCommand) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        plugin.logInfo("Connecting to " + botServer + " as " + bot.getName());
                        bot.connect(botServer, botServerPort, botServerPass);
                    } catch (Exception ex) {
                        plugin.logError("Problem connecting to " + botServer + " => "
                                + " as " + bot.getName() + " [Error: " + ex.getMessage() + "]");
                    }
                }
            });
        } else {
            this.plugin.logInfo("Autoconnect is disabled. Not connecting to " + botServer + " as " + bot.getName());
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

    public void changeNick(CommandSender sender, String nick) {
        bot.setName(nick);
        bot.changeNick(nick);
        sender.sendMessage("Setting nickname to " + nick);
        config.set("nick", nick);
        saveConfig();
    }

    public void changeLogin(CommandSender sender, String name) {
        bot.setLogin(name);
        sender.sendMessage("Setting login to " + name);
        config.set("nick", name);
        saveConfig();
    }

    private void loadConfig() {
        try {
            config.load(file);
            autoConnect = config.getBoolean("autoconnect", true);
            botNick = config.getString("nick", "");
            botLogin = config.getString("login", "PircBot");
            bot.setName(botNick);
            bot.setLogin(botLogin);
            plugin.ircBots.put(botNick, this);
            plugin.botConnected.put(botNick, bot.isConnected());
            botServer = config.getString("server", "");
            botServerPort = config.getInt("port");
            botServerPass = config.getString("password", "");            
            commandPrefix = config.getString("command-prefix", ".");
            quitMessage = ChatColor.translateAlternateColorCodes('&', config.getString("quit-message", ""));
            plugin.logDebug("Nick => " + botNick);
            plugin.logDebug("Login => " + botLogin);
            plugin.logDebug("Server => " + botServer);
            plugin.logDebug("Port => " + botServerPort);
            plugin.logDebug("Command Prefix => " + commandPrefix);
            plugin.logDebug("Server Password => " + botServerPass);
            plugin.logDebug("Quit Message => " + quitMessage);
            for (String channelName : config.getConfigurationSection("channels").getKeys(false)) {
                plugin.logDebug("Channel  => " + channelName);
                botChannels.add(channelName);                

                channelAutoJoin.put(channelName, config.getBoolean("channels." + channelName + ".autojoin", true));
                plugin.logDebug("  Autojoin => " + channelAutoJoin.get(channelName));

                channelPassword.put(channelName, config.getString("channels." + channelName + ".password", ""));
                plugin.logDebug("  Password => " + channelTopic.get(channelName));

                channelTopic.put(channelName, config.getString("channels." + channelName + ".topic", ""));
                plugin.logDebug("  Topic => " + channelTopic.get(channelName));

                channelModes.put(channelName, config.getString("channels." + channelName + ".modes", ""));
                plugin.logDebug("  Channel Modes => " + channelModes.get(channelName));

                channelTopicProtected.put(channelName, config.getBoolean("channels." + channelName + ".topic-protect", false));
                plugin.logDebug("  Topic Protected => " + channelTopicProtected.get(channelName).toString());

                // build channel op list
                Collection<String> cOps = new ArrayList<String>();
                for (String channelOper : config.getStringList("channels." + channelName + ".ops")) {
                    cOps.add(channelOper);
                    plugin.logDebug("  Channel Op => " + channelOper);
                }
                opsList.put(channelName, cOps);

                // build mute list
                Collection<String> m = new ArrayList<String>();
                for (String mutedUser : config.getStringList("channels." + channelName + ".muted")) {
                    m.add(mutedUser);
                    plugin.logDebug("  Channel Mute => " + mutedUser);
                }
                muteList.put(channelName, m);

                // build valid chat list
                Collection<String> c = new ArrayList<String>();
                for (String validChat : config.getStringList("channels." + channelName + ".enabled-messages")) {
                    c.add(validChat);
                    plugin.logDebug("  Enabled Message => " + validChat);
                }
                enabledMessages.put(channelName, c);

                // build valid world list
                Collection<String> w = new ArrayList<String>();
                for (String validWorld : config.getStringList("channels." + channelName + ".worlds")) {
                    w.add(validWorld);
                    plugin.logDebug("  Enabled World => " + validWorld);
                }
                worldList.put(channelName, w);
                
                // build command map
                Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
                for (String command : config.getConfigurationSection("channels." + channelName + ".commands").getKeys(false)) {
                    plugin.logDebug("  Command => " + command);
                    Map<String, String> optionPair = new HashMap<String, String>();
                    for (String commandOption : config.getConfigurationSection("channels." + channelName + ".commands." + command).getKeys(false)) {
                        String commandOptionValue = config.getString("channels." + channelName + ".commands." + command + "." + commandOption);
                        optionPair.put(commandOption, commandOptionValue);
                        plugin.logDebug("    " + commandOption + " => " + commandOptionValue);
                    }
                    map.put(command, optionPair);

                }
                commandMap.put(channelName, map);
            }
        } catch (Exception ex) {
            plugin.logError(ex.getMessage());
        }
    }
    
    private boolean isPlayerInValidWorld(Player player, String channelName) {        
        if (worldList.containsKey(channelName)) {
            if (worldList.get(channelName).contains("*")) {
                return true;
            }
            if (worldList.get(channelName).contains(player.getWorld().getName())) {
                return true;
            }                
        } return false;
    }

    public void gameChat(Player player, String message) {
        if (!bot.isConnected()) {
            return;
        }        
        for (String channelName : botChannels) {
            if (!isPlayerInValidWorld(player, channelName)) {
                return;
            }
            if (enabledMessages.get(channelName).contains("game-chat")) {
                bot.sendMessage(channelName, plugin.colorConverter.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameChat)
                        .replaceAll("%NAME%", player.getName())
                        .replaceAll("%MESSAGE%", message)
                        .replaceAll("%WORLD%", player.getLocation().getWorld().getName())));
            }
        }
    }

    public void gameJoin(Player player, String message) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains("game-join")) {
                if (!isPlayerInValidWorld(player, channelName)) {
                    return;
                }
                bot.sendMessage(channelName, plugin.colorConverter.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameJoin)
                        .replaceAll("%NAME%", player.getName())
                        .replaceAll("%MESSAGE%", message)
                        .replaceAll("%WORLD%", player.getLocation().getWorld().getName())));
            }
        }
    }

    public void gameQuit(Player player, String message) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains("game-quit")) {
                if (!isPlayerInValidWorld(player, channelName)) {
                return;
            }
                bot.sendMessage(channelName, plugin.colorConverter.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameQuit)
                        .replaceAll("%NAME%", player.getName())
                        .replaceAll("%MESSAGE%", message)
                        .replaceAll("%WORLD%", player.getLocation().getWorld().getName())));
            }
        }
    }

    public void gameAction(Player player, String message) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains("game-action")) {
                if (!isPlayerInValidWorld(player, channelName)) {
                return;
            }
                bot.sendMessage(channelName, plugin.colorConverter.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameAction)
                        .replaceAll("%NAME%", player.getName())
                        .replaceAll("%MESSAGE%", message)
                        .replaceAll("%WORLD%", player.getLocation().getWorld().getName())));
            }
        }
    }

    public void gameDeath(Player player, String message) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains("game-death")) {
                if (!isPlayerInValidWorld(player, channelName)) {
                return;
            }
                bot.sendMessage(channelName, plugin.colorConverter.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameDeath)
                        .replaceAll("%NAME%", player.getName())
                        .replaceAll("%MESSAGE%", message)
                        .replaceAll("%WORLD%", player.getLocation().getWorld().getName())));
            }
        }
    }

    public void changeTopic(String channelName, String topic, CommandSender sender) {
        changeTopic(bot.getChannel(channelName),topic,sender);
    }
    
    public void changeTopic(Channel channel, String topic, CommandSender sender) {
        String channelName = channel.getName();
        bot.setTopic(channel, topic);
        config.set("channels." + channelName + ".topic", topic);
        saveConfig();
        sender.sendMessage("IRC topic for " + channelName + " changed to \"" + topic + "\"");
    }

    public void setServer(CommandSender sender, String botServer) {
        setServer(sender, botServer, autoConnect);
    }

    public void setServer(CommandSender sender, String botServer, Boolean autoConnect) {
        this.botServer = botServer;
        config.set("server", botServer);
        this.autoConnect = autoConnect;
        config.set("autoconnect", autoConnect);
        saveConfig();
        sender.sendMessage("IRC server changed to \"" + botServer + "\". (AutoConnect: " + autoConnect.toString() + ")");
    }

    public void addOp(String channelName, String userMask, CommandSender sender) {
        if (opsList.get(channelName).contains(userMask)) {
            sender.sendMessage("User mask'" + userMask + "' is already in the ops list.");
        } else {
            sender.sendMessage("User mask'" + userMask + "' has been added to the ops list.");
            opsList.get(channelName).add(userMask);
        }
        config.set("channels." + channelName + ".ops", opsList.get(channelName));
        saveConfig();
    }

    public void removeOp(String channelName, String userMask, CommandSender sender) {        
        if (opsList.get(channelName).contains(userMask)) {
            sender.sendMessage("User mask'" + userMask + "' has been removed to the ops list.");
            opsList.get(channelName).remove(userMask);
        } else {
            sender.sendMessage("User mask'" + userMask + "' is not in the ops list.");
        }
        config.set("channels." + channelName + ".ops", opsList.get(channelName));
        saveConfig();
    }

    public void op(String channelName, String nick) {
        bot.op(bot.getChannel(channelName), bot.getUser(nick));
    }
    
    public void fixTopic(Channel channel, String topic, String setBy) { 
        String channelName = channel.getName();
        if (setBy.equals(botNick)) {
            config.set("channels." + channelName + ".topic", topic);
            saveConfig();
            return;
        }

        if (channelTopic.containsKey(channelName)) {
            if (channelTopicProtected.containsKey(channelName)) {
                if (channelTopicProtected.containsKey(channelName)) {
                    String myTopic = channelTopic.get(channelName);
                    if (!topic.equals(myTopic)) {
                        bot.setTopic(channel, myTopic);
                    }
                }
            }
        }
    }

    public void quit(CommandSender sender) {
        sender.sendMessage("Disconnecting " + bot.getNick() + " from IRC server " + botServer);
        quit();
    }

    public void quit() {
        if (quitMessage.isEmpty()) {
            bot.quitServer();
        } else {
            bot.quitServer(plugin.colorConverter.gameColorsToIrc(quitMessage));
        }
    }

    public void sendTopic(CommandSender sender) {
        for (String channelName : botChannels) {
            if (commandMap.containsKey(channelName)) {
                sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE
                        + botNick + ChatColor.WHITE + "]" + ChatColor.RESET
                        + " IRC topic for " + ChatColor.WHITE + channelName
                        + ChatColor.RESET + ": \""
                        + ChatColor.WHITE + activeTopic.get(channelName) + ChatColor.RESET + "\"");
            }
        }
    }

    public void sendUserList(CommandSender sender, String channel) {
        sendUserList(sender, bot.getChannel(channel));
    }
    
    public void sendUserList(CommandSender sender, Channel channel) {
        String channelName = channel.getName();
        if (!botChannels.contains(channelName)) {
            sender.sendMessage(ChatColor.RED + "Invalid channel name.");
            return;
        }
        sender.sendMessage(ChatColor.DARK_PURPLE + "-----[  " + ChatColor.WHITE + channelName
                + ChatColor.DARK_PURPLE + " - " + ChatColor.WHITE + bot.getName() + ChatColor.DARK_PURPLE + " ]-----");
        if (!bot.isConnected()) {
            sender.sendMessage(ChatColor.RED + " Not connected!");
            return;
        }
        List<String> channelUsers = new ArrayList<String>();
        for (User user : bot.getUsers(channel)) {
            String nick = user.getNick();
            if (user.isIrcop()) {
                nick = "~" + nick;
            } else if (user.getChannelsSuperOpIn().contains(channel)) {
                nick = "&" + nick;
            } else if (user.getChannelsOpIn().contains(channel)) {
                nick = "@" + nick;
            } else if (user.getChannelsHalfOpIn().contains(channel)) {
                nick = "%" + nick;
            } else if (user.getChannelsVoiceIn().contains(channel)) {
                nick = "+" + nick;
            } 
            channelUsers.add(nick);
        }
        Collections.sort(channelUsers, Collator.getInstance());
        for (String userName : channelUsers) {
            sender.sendMessage("  " + ChatColor.WHITE + userName);
        }
    }

    public void sendUserList(CommandSender sender) {
        for (String channelName : botChannels) {
            sendUserList(sender, bot.getChannel(channelName));
        }
    }
    
    public void opFriends(Channel channel) {
        for (User user : bot.getUsers(channel)) {
            opFriends(channel, user);
        }
    }    
    public void opFriends(String channelName) {
        Channel channel = bot.getChannel(channelName);
        for (User user : bot.getUsers(channel)) {
            opFriends(channel, user);
        }
    }
        
    public void opFriends(Channel channel, User user) {
        if (user.getNick().equals(botNick)) {
            return;
        }
        String myChannel = channel.getName();
        for (String opsUser : opsList.get(myChannel)) {
            plugin.logDebug("OP => " + user);
            //sender!*login@hostname            
            String mask[] = opsUser.split("[\\!\\@]", 3);
            if (mask.length == 3) {
                String gUser = createRegexFromGlob(mask[0]);
                String gLogin = createRegexFromGlob(mask[1]);
                String gHost = createRegexFromGlob(mask[2]);
                String sender = user.getNick();
                String login = user.getLogin();
                String hostname = user.getHostmask();
                plugin.logDebug("Nick: " + sender + " =~ " + gUser + " = " + sender.matches(gUser));
                plugin.logDebug("Name: " + login + " =~ " + gLogin + " = " + login.matches(gLogin));
                plugin.logDebug("Hostname: " + hostname + " =~ " + gHost + " = " + hostname.matches(gHost));
                if (sender.matches(gUser) && login.matches(gLogin) && hostname.matches(gHost)) {
                    plugin.logInfo("Auto-opping " + sender + " on " + channel);
                    user.getBot().op(channel, user);
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

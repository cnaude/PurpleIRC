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
public final class PIRCBot {

    public PircBotX bot;
    public boolean autoConnect;
    public String botServer;
    public String botNick;
    public String botLogin;
    public String botServerPass;
    public int botServerPort;
    public String channelPrefix;
    public String commandPrefix;
    public String quitMessage;
    public final PIRCMain plugin;
    private File file;
    private YamlConfiguration config;
    // channel, #channel
    public HashMap<String, String> botChannels = new HashMap<String, String>();
    // #channel, channel
    public HashMap<String, String> channelKeys = new HashMap<String, String>();
    public HashMap<String, String> channelPassword = new HashMap<String, String>();
    // channel, topic
    public HashMap<String, String> channelTopic = new HashMap<String, String>();
    // channel, topic
    public HashMap<String, String> activeTopic = new HashMap<String, String>();
    // channel, modes
    public HashMap<String, String> channelModes = new HashMap<String, String>();
    public HashMap<String, Boolean> channelTopicProtected = new HashMap<String, Boolean>();
    public HashMap<String, Boolean> channelAutoJoin = new HashMap<String, Boolean>();
    public Map<String, Collection<String>> opsList = new HashMap<String, Collection<String>>();
    public Map<String, Collection<String>> muteList = new HashMap<String, Collection<String>>();
    public Map<String, Collection<String>> enabledMessages = new HashMap<String, Collection<String>>();
    //          channel     command option     value
    public Map<String, Map<String, Map<String, String>>> commandMap = new HashMap<String, Map<String, Map<String, String>>>();

    public PIRCBot(File file, PIRCMain plugin) {
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
    }

    public void changeLogin(CommandSender sender, String name) {
        bot.setLogin(name);
        sender.sendMessage("Setting login to " + name);
        config.set("nick", name);
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
            channelPrefix = config.getString("channel-prefix", "");
            commandPrefix = config.getString("command-prefix", ".");
            quitMessage = ChatColor.translateAlternateColorCodes('&', config.getString("quit-message", ""));
            plugin.logDebug("Nick => " + botNick);
            plugin.logDebug("Login => " + botLogin);
            plugin.logDebug("Server => " + botServer);
            plugin.logDebug("Port => " + botServerPort);
            plugin.logDebug("Channel Prefix => " + channelPrefix);
            plugin.logDebug("Command Prefix => " + commandPrefix);
            plugin.logDebug("Server Password => " + botServerPass);
            plugin.logDebug("Quit Message => " + quitMessage);
            for (String myChannel : config.getConfigurationSection("channels").getKeys(false)) {
                plugin.logDebug("Channel  => " + channelPrefix + myChannel);
                botChannels.put(myChannel, channelPrefix + myChannel);
                channelKeys.put(channelPrefix + myChannel, myChannel);

                channelAutoJoin.put(myChannel, config.getBoolean("channels." + myChannel + ".autojoin", true));
                plugin.logDebug("  Autojoin => " + channelAutoJoin.get(myChannel));

                channelPassword.put(myChannel, config.getString("channels." + myChannel + ".password", ""));
                plugin.logDebug("  Password => " + channelTopic.get(myChannel));

                channelTopic.put(myChannel, config.getString("channels." + myChannel + ".topic", ""));
                plugin.logDebug("  Topic => " + channelTopic.get(myChannel));

                channelModes.put(myChannel, config.getString("channels." + myChannel + ".modes", ""));
                plugin.logDebug("  Channel Modes => " + channelModes.get(myChannel));

                channelTopicProtected.put(myChannel, config.getBoolean("channels." + myChannel + ".topic-protect", false));
                plugin.logDebug("  Topic Protected => " + channelTopicProtected.get(myChannel).toString());

                // build channel op list
                Collection<String> cOps = new ArrayList<String>();
                for (String channelOper : config.getStringList("channels." + myChannel + ".ops")) {
                    cOps.add(channelOper);
                    plugin.logDebug("  Channel Op => " + channelOper);
                }
                opsList.put(myChannel, cOps);

                // build mute list
                Collection<String> m = new ArrayList<String>();
                for (String mutedUser : config.getStringList("channels." + myChannel + ".muted")) {
                    m.add(mutedUser);
                    plugin.logDebug("  Channel Mute => " + mutedUser);
                }
                muteList.put(myChannel, m);

                // build valid chat list
                Collection<String> c = new ArrayList<String>();
                for (String validChat : config.getStringList("channels." + myChannel + ".enabled-messages")) {
                    c.add(validChat);
                    plugin.logDebug("  Enabled Message => " + validChat);
                }
                enabledMessages.put(myChannel, c);

                // build command map
                Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
                for (String command : config.getConfigurationSection("channels." + myChannel + ".commands").getKeys(false)) {
                    plugin.logDebug("  Command => " + command);
                    Map<String, String> optionPair = new HashMap<String, String>();
                    for (String commandOption : config.getConfigurationSection("channels." + myChannel + ".commands." + command).getKeys(false)) {
                        String commandOptionValue = config.getString("channels." + myChannel + ".commands." + command + "." + commandOption);
                        optionPair.put(commandOption, commandOptionValue);
                        plugin.logDebug("    " + commandOption + " => " + commandOptionValue);
                    }
                    map.put(command, optionPair);

                }
                commandMap.put(myChannel, map);
            }
        } catch (Exception ex) {
            plugin.logError(ex.getMessage());
        }
    }

    public void gameChat(Player player, String message) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channel : botChannels.values()) {
            if (enabledMessages.get(channelKeys.get(channel)).contains("game-chat")) {
                bot.sendMessage(channel, plugin.colorConverter.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameChat)
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
        for (String channel : botChannels.values()) {
            if (enabledMessages.get(channelKeys.get(channel)).contains("game-join")) {
                bot.sendMessage(channel, plugin.colorConverter.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameJoin)
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
        for (String channel : botChannels.values()) {
            if (enabledMessages.get(channelKeys.get(channel)).contains("game-quit")) {
                bot.sendMessage(channel, plugin.colorConverter.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameQuit)
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
        for (String channel : botChannels.values()) {
            if (enabledMessages.get(channelKeys.get(channel)).contains("game-action")) {
                bot.sendMessage(channel, plugin.colorConverter.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameAction)
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
        for (String channel : botChannels.values()) {
            if (enabledMessages.get(channelKeys.get(channel)).contains("game-death")) {
                bot.sendMessage(channel, plugin.colorConverter.gameColorsToIrc(Matcher.quoteReplacement(plugin.gameDeath)
                        .replaceAll("%NAME%", player.getName())
                        .replaceAll("%MESSAGE%", message)
                        .replaceAll("%WORLD%", player.getLocation().getWorld().getName())));
            }
        }
    }

    public void changeTopic(String channel, String topic, CommandSender sender) {
        changeTopic(bot.getChannel(channel),topic,sender);
    }
    
    public void changeTopic(Channel channel, String topic, CommandSender sender) {
        bot.setTopic(channel, topic);
        config.set("channels." + channelKeys.get(channel.getName()) + ".topic", topic);
        sender.sendMessage("IRC topic for " + channel.getName() + " changed to \"" + topic + "\"");
    }

    public void setServer(CommandSender sender, String botServer) {
        setServer(sender, botServer, autoConnect);
    }

    public void setServer(CommandSender sender, String botServer, Boolean autoConnect) {
        this.botServer = botServer;
        config.set("server", botServer);
        this.autoConnect = autoConnect;
        config.set("autoconnect", autoConnect);
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

    public void op(String channel, String nick) {
        bot.op(bot.getChannel(channel), bot.getUser(nick));
    }
    
    public void fixTopic(Channel channel, String topic, String setBy) {
        String myChannel = channelKeys.get(channel.getName());
        if (setBy.equals(botNick)) {
            config.set("channels." + myChannel + ".topic", topic);
            return;
        }

        if (channelTopic.containsKey(myChannel)) {
            if (channelTopicProtected.containsKey(myChannel)) {
                if (channelTopicProtected.containsKey(myChannel)) {
                    String myTopic = channelTopic.get(myChannel);
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
        for (String channel : this.botChannels.keySet()) {
            if (commandMap.containsKey(channel)) {
                sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE
                        + this.botNick + ChatColor.WHITE + "]" + ChatColor.RESET
                        + " IRC topic for " + ChatColor.WHITE + botChannels.get(channel)
                        + ChatColor.RESET + ": \""
                        + ChatColor.WHITE + activeTopic.get(channel) + ChatColor.RESET + "\"");
            }
        }
    }

    public void sendUserList(CommandSender sender, String channel) {
        sendUserList(sender, bot.getChannel(channel));
    }
    
    public void sendUserList(CommandSender sender, Channel channel) {
        if (!botChannels.containsValue(channel.getName())) {
            sender.sendMessage(ChatColor.RED + "Invalid channel name.");
            return;
        }
        sender.sendMessage(ChatColor.DARK_PURPLE + "-----[  " + ChatColor.WHITE + channel.getName()
                + ChatColor.DARK_PURPLE + " - " + ChatColor.WHITE + bot.getName() + ChatColor.DARK_PURPLE + " ]-----");
        if (!bot.isConnected()) {
            sender.sendMessage(ChatColor.RED + " Not connected!");
            return;
        }
        List<String> channelUsers = new ArrayList<String>();
        for (User user : bot.getUsers(channel)) {
            String nick = user.getNick();
            if (user.getChannelsOpIn().contains(channel)) {
                nick = "@" + nick;
            }
            channelUsers.add(nick);
        }
        Collections.sort(channelUsers, Collator.getInstance());
        for (String userName : channelUsers) {
            sender.sendMessage("  " + ChatColor.WHITE + userName);
        }
    }

    public void sendUserList(CommandSender sender) {
        for (String channel : botChannels.values()) {
            sendUserList(sender, bot.getChannel(channel));
        }
    }
}

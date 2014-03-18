package com.cnaude.purpleirc;

import com.dthielke.herochat.Chatter;
import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import com.cnaude.purpleirc.IRCListeners.ActionListener;
import com.cnaude.purpleirc.IRCListeners.ConnectListener;
import com.cnaude.purpleirc.IRCListeners.DisconnectListener;
import com.cnaude.purpleirc.IRCListeners.JoinListener;
import com.cnaude.purpleirc.IRCListeners.KickListener;
import com.cnaude.purpleirc.IRCListeners.MessageListener;
import com.cnaude.purpleirc.IRCListeners.ModeListener;
import com.cnaude.purpleirc.IRCListeners.MotdListener;
import com.cnaude.purpleirc.IRCListeners.NickChangeListener;
import com.cnaude.purpleirc.IRCListeners.NoticeListener;
import com.cnaude.purpleirc.IRCListeners.PartListener;
import com.cnaude.purpleirc.IRCListeners.PrivateMessageListener;
import com.cnaude.purpleirc.IRCListeners.QuitListener;
import com.cnaude.purpleirc.IRCListeners.ServerResponseListener;
import com.cnaude.purpleirc.IRCListeners.TopicListener;
import com.cnaude.purpleirc.IRCListeners.VersionListener;
import com.cnaude.purpleirc.IRCListeners.WhoisListener;
import com.cnaude.purpleirc.Utilities.CaseInsensitiveMap;
import com.dthielke.herochat.Herochat;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSortedSet;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import com.nyancraft.reportrts.data.HelpRequest;
import com.titankingdoms.dev.titanchat.core.participant.Participant;
import java.io.IOException;
import java.nio.charset.Charset;
import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UtilSSLSocketFactory;
import org.pircbotx.exception.IrcException;
import org.pircbotx.hooks.ListenerAdapter;

/**
 *
 * @author Chris Naude
 */
public final class PurpleBot {

    private PircBotX bot;

    public final PurpleIRC plugin;
    private final File file;
    private YamlConfiguration config;
    private boolean connected;
    public boolean autoConnect;
    public boolean ssl;
    public boolean trustAllCerts;
    public boolean sendRawMessageOnConnect;
    public boolean showMOTD;
    public boolean channelCmdNotifyEnabled;
    public boolean relayPrivateChat;
    public boolean partInvalidChannels;
    public int botServerPort;
    public long chatDelay;
    public String botServer;
    public String botNick;
    public String botLogin;
    public String botRealName;
    public String botServerPass;
    public String charSet;
    public String commandPrefix;
    public String quitMessage;
    public String botIdentPassword;
    public String rawMessage;
    public String channelCmdNotifyMode;
    public String partInvalidChannelsMsg;
    private String connectMessage;
    public ArrayList<String> botChannels;
    public CaseInsensitiveMap<Collection<String>> channelNicks;
    public CaseInsensitiveMap<Collection<String>> tabIgnoreNicks;
    public CaseInsensitiveMap<Collection<String>> filters;
    public CaseInsensitiveMap<String> channelPassword;
    public CaseInsensitiveMap<String> channelTopic;
    public CaseInsensitiveMap<Boolean> channelTopicChanserv;
    public CaseInsensitiveMap<String> activeTopic;
    public CaseInsensitiveMap<String> channelModes;
    public CaseInsensitiveMap<String> joinMsg;
    public CaseInsensitiveMap<Boolean> msgOnJoin;
    public CaseInsensitiveMap<Boolean> channelTopicProtected;
    public CaseInsensitiveMap<Boolean> channelAutoJoin;
    public CaseInsensitiveMap<Boolean> ignoreIRCChat;
    public CaseInsensitiveMap<Boolean> hideJoinWhenVanished;
    public CaseInsensitiveMap<Boolean> hideListWhenVanished;
    public CaseInsensitiveMap<Boolean> hideQuitWhenVanished;
    public CaseInsensitiveMap<Boolean> invalidCommandPrivate;
    public CaseInsensitiveMap<Boolean> invalidCommandCTCP;
    public CaseInsensitiveMap<Boolean> logIrcToHeroChat;
    public CaseInsensitiveMap<Boolean> enableMessageFiltering;
    private final CaseInsensitiveMap<Boolean> shortify;
    public CaseInsensitiveMap<String> heroChannel;
    public CaseInsensitiveMap<String> townyChannel;
    public CaseInsensitiveMap<Collection<String>> opsList;
    public CaseInsensitiveMap<Collection<String>> worldList;
    public CaseInsensitiveMap<Collection<String>> muteList;
    public CaseInsensitiveMap<Collection<String>> enabledMessages;
    public CaseInsensitiveMap<CaseInsensitiveMap<CaseInsensitiveMap<String>>> commandMap;
    public ArrayList<CommandSender> whoisSenders;
    public List<String> channelCmdNotifyRecipients;
    private final ArrayList<ListenerAdapter> ircListeners;
    public IRCMessageQueueWatcher messageQueue;
    private final String fileName;

    /**
     *
     * @param file
     * @param plugin
     */
    public PurpleBot(File file, PurpleIRC plugin) {
        fileName = file.getName();
        this.connected = false;
        this.botChannels = new ArrayList<String>();
        this.ircListeners = new ArrayList<ListenerAdapter>();
        this.channelCmdNotifyRecipients = new ArrayList<String>();
        this.commandMap = new CaseInsensitiveMap<CaseInsensitiveMap<CaseInsensitiveMap<String>>>();
        this.enabledMessages = new CaseInsensitiveMap<Collection<String>>();
        this.muteList = new CaseInsensitiveMap<Collection<String>>();
        this.worldList = new CaseInsensitiveMap<Collection<String>>();
        this.opsList = new CaseInsensitiveMap<Collection<String>>();
        this.heroChannel = new CaseInsensitiveMap<String>();
        this.townyChannel = new CaseInsensitiveMap<String>();
        this.invalidCommandCTCP = new CaseInsensitiveMap<Boolean>();
        this.logIrcToHeroChat = new CaseInsensitiveMap<Boolean>();
        this.shortify = new CaseInsensitiveMap<Boolean>();
        this.invalidCommandPrivate = new CaseInsensitiveMap<Boolean>();
        this.hideQuitWhenVanished = new CaseInsensitiveMap<Boolean>();
        this.hideListWhenVanished = new CaseInsensitiveMap<Boolean>();
        this.hideJoinWhenVanished = new CaseInsensitiveMap<Boolean>();
        this.ignoreIRCChat = new CaseInsensitiveMap<Boolean>();
        this.channelAutoJoin = new CaseInsensitiveMap<Boolean>();
        this.channelTopicProtected = new CaseInsensitiveMap<Boolean>();
        this.channelModes = new CaseInsensitiveMap<String>();
        this.activeTopic = new CaseInsensitiveMap<String>();
        this.channelTopic = new CaseInsensitiveMap<String>();
        this.channelPassword = new CaseInsensitiveMap<String>();
        this.tabIgnoreNicks = new CaseInsensitiveMap<Collection<String>>();
        this.filters = new CaseInsensitiveMap<Collection<String>>();
        this.channelNicks = new CaseInsensitiveMap<Collection<String>>();
        this.channelTopicChanserv = new CaseInsensitiveMap<Boolean>();
        this.joinMsg = new CaseInsensitiveMap<String>();
        this.msgOnJoin = new CaseInsensitiveMap<Boolean>();
        this.enableMessageFiltering = new CaseInsensitiveMap<Boolean>();
        this.plugin = plugin;
        this.file = file;
        whoisSenders = new ArrayList<CommandSender>();
        config = new YamlConfiguration();
        loadConfig();
        addListeners();
        buildBot();
        messageQueue = new IRCMessageQueueWatcher(this, plugin);
    }

    public void buildBot() {
        Configuration.Builder configBuilder = new Configuration.Builder()
                .setName(botNick)
                .setLogin(botLogin)
                .setAutoNickChange(true)
                .setCapEnabled(true)
                .setMessageDelay(chatDelay)
                .setRealName(botRealName)
                //.setAutoReconnect(autoConnect) // Why doesn't this work?
                .setServer(botServer, botServerPort, botServerPass);
        addAutoJoinChannels(configBuilder);
        for (ListenerAdapter ll : ircListeners) {
            configBuilder.addListener(ll);
        }
        if (!botIdentPassword.isEmpty()) {
            plugin.logInfo("Setting IdentPassword ...");
            configBuilder.setNickservPassword(botIdentPassword);
        }
        if (ssl) {
            UtilSSLSocketFactory socketFactory = new UtilSSLSocketFactory();
            socketFactory.disableDiffieHellman();
            if (trustAllCerts) {
                plugin.logInfo("Enabling SSL and trusting all certificates ...");
                socketFactory.trustAllCertificates();
            } else {
                plugin.logInfo("Enabling SSL ...");
            }
            configBuilder.setSocketFactory(socketFactory);
        }
        if (charSet.isEmpty()) {
            plugin.logInfo("Using default character set: " + Charset.defaultCharset());
        } else {
            if (Charset.isSupported(charSet)) {
                plugin.logInfo("Using character set: " + charSet);
                configBuilder.setEncoding(Charset.forName(charSet));
            } else {
                plugin.logError("Invalid character set: " + charSet);
                plugin.logInfo("Available character sets: " + Joiner.on(", ").join(Charset.availableCharsets().keySet()));
                plugin.logInfo("Using default character set: " + Charset.defaultCharset());
            }
        }
        Configuration configuration = configBuilder.buildConfiguration();
        bot = new PircBotX(configuration);
        if (autoConnect) {
            asyncConnect();
        } else {
            plugin.logInfo("Auto-connect is disabled. To connect: /irc connect " + bot.getNick());
        }
    }

    private void addListeners() {
        ircListeners.add(new ActionListener(plugin, this));
        ircListeners.add(new ConnectListener(plugin, this));
        ircListeners.add(new DisconnectListener(plugin, this));
        ircListeners.add(new JoinListener(plugin, this));
        ircListeners.add(new KickListener(plugin, this));
        ircListeners.add(new MessageListener(plugin, this));
        ircListeners.add(new ModeListener(plugin, this));
        ircListeners.add(new NickChangeListener(plugin, this));
        ircListeners.add(new NoticeListener(plugin, this));
        ircListeners.add(new PartListener(plugin, this));
        ircListeners.add(new PrivateMessageListener(plugin, this));
        ircListeners.add(new QuitListener(plugin, this));
        ircListeners.add(new TopicListener(plugin, this));
        ircListeners.add(new VersionListener(plugin));
        ircListeners.add(new WhoisListener(plugin, this));
        ircListeners.add(new MotdListener(plugin, this));
        ircListeners.add(new ServerResponseListener(plugin, this));
    }

    private void addAutoJoinChannels(Configuration.Builder configBuilder) {
        for (String channelName : botChannels) {
            if (channelAutoJoin.containsKey(channelName)) {
                if (channelAutoJoin.get(channelName)) {
                    if (channelPassword.get(channelName).isEmpty()) {
                        configBuilder.addAutoJoinChannel(channelName
                                .toLowerCase());
                    } else {
                        configBuilder.addAutoJoinChannel(channelName
                                .toLowerCase(), channelPassword.get(channelName));
                    }
                }
            }
        }
    }

    public void reload(CommandSender sender) {
        sender.sendMessage("Reloading bot: " + botNick);
        reload();
    }

    public void reload() {
        asyncQuit(true);
    }

    /**
     *
     * @param sender
     */
    public void reloadConfig(CommandSender sender) {
        config = new YamlConfiguration();
        loadConfig();
        sender.sendMessage("[PurpleIRC] [" + botNick + "] IRC bot configuration reloaded.");
    }

    /**
     *
     * @param channelName
     * @param sender
     * @param user
     */
    public void mute(String channelName, CommandSender sender, String user) {
        if (muteList.get(channelName).contains(user)) {
            sender.sendMessage("User '" + user + "' is already muted.");
        } else {
            sender.sendMessage("User '" + user + "' is now muted.");
            muteList.get(channelName).add(user);
            saveConfig();
        }
    }

    /**
     *
     * @param channelName
     * @param sender
     */
    public void muteList(String channelName, CommandSender sender) {
        if (muteList.get(channelName).isEmpty()) {
            sender.sendMessage("There are no users muted for " + channelName);
        } else {
            sender.sendMessage("Muted users for " + channelName
                    + ": " + Joiner.on(", ").join(muteList.get(channelName)));
            saveConfig();
        }
    }

    /**
     *
     * @param channelName
     * @param sender
     * @param user
     */
    public void unMute(String channelName, CommandSender sender, String user) {
        if (muteList.get(channelName).contains(user)) {
            sender.sendMessage("User '" + user + "' is no longer muted.");
            muteList.get(channelName).remove(user);
            saveConfig();
        } else {
            sender.sendMessage("User '" + user + "' is not muted.");
        }
    }

    public void asyncConnect(CommandSender sender) {
        sender.sendMessage(connectMessage);
        asyncConnect();
    }

    public boolean isShortifyEnabled(String channelName) {
        if (shortify.containsKey(channelName)) {
            return shortify.get(channelName);
        }
        return false;
    }

    /**
     *
     */
    public void asyncConnect() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    plugin.logInfo(connectMessage);
                    bot.startBot();
                } catch (IOException ex) {
                    plugin.logError("Problem connecting to " + botServer + " => "
                            + " as " + botNick + " [Error: " + ex.getMessage() + "]");
                } catch (IrcException ex) {
                    plugin.logError("Problem connecting to " + botServer + " => "
                            + " as " + botNick + " [Error: " + ex.getMessage() + "]");
                }
            }
        });
    }

    public void asyncIRCMessage(final String target, final String message) {
        plugin.logDebug("Entering aysncIRCMessage");
        messageQueue.add(new IRCMessage(target, plugin.colorConverter.
                gameColorsToIrc(message), false));
    }

    public void asyncCTCPMessage(final String target, final String message) {
        plugin.logDebug("Entering asyncCTCPMessage");
        messageQueue.add(new IRCMessage(target, plugin.colorConverter
                .gameColorsToIrc(message), true));
    }

    public void blockingIRCMessage(final String target, final String message) {
        plugin.logDebug("[blockingIRCMessage] About to send IRC message to " + target);
        bot.sendIRC().message(target, plugin.colorConverter
                .gameColorsToIrc(message));
        plugin.logDebug("[blockingIRCMessage] Message sent to " + target);
    }

    public void blockingCTCPMessage(final String target, final String message) {
        plugin.logDebug("[blockingCTCPMessage] About to send IRC message to " + target);
        bot.sendIRC().ctcpResponse(target, plugin.colorConverter
                .gameColorsToIrc(message));
        plugin.logDebug("[blockingCTCPMessage] Message sent to " + target);
    }

    public void asyncCTCPCommand(final String target, final String command) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                bot.sendIRC().ctcpCommand(target, command);
            }
        });
    }

    /**
     *
     * @param sender
     */
    public void saveConfig(CommandSender sender) {
        try {
            config.save(file);
            sender.sendMessage(plugin.LOG_HEADER_F
                    + " Saving bot \"" + botNick + "\" to " + file.getName());
        } catch (IOException ex) {
            plugin.logError(ex.getMessage());
            sender.sendMessage(ex.getMessage());
        }
    }

    /**
     *
     */
    public void saveConfig() {
        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.logError(ex.getMessage());
        }
    }

    /**
     *
     * @param sender
     * @param newNick
     */
    public void asyncChangeNick(final CommandSender sender, final String newNick) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                bot.sendIRC().changeNick(newNick);

            }
        });
        sender.sendMessage("Setting nickname to " + newNick);
        config.set("nick", newNick);
        saveConfig();
    }

    public void asyncJoinChannel(final String channelName, final String password) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                bot.sendIRC().joinChannel(channelName, password);
            }
        });
    }

    public void asyncNotice(final String target, final String message) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                bot.sendIRC().notice(target, message);
            }
        });
    }

    public void asyncRawlineNow(final String message) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                bot.sendRaw().rawLineNow(message);
            }
        });
    }

    public void asyncIdentify(final String password) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                bot.sendIRC().identify(password);
            }
        });
    }

    /**
     *
     * @param sender
     * @param newLogin
     */
    public void changeLogin(CommandSender sender, String newLogin) {
        sender.sendMessage(ChatColor.DARK_PURPLE
                + "Login set to " + ChatColor.WHITE
                + newLogin + ChatColor.DARK_PURPLE
                + ". Reload the bot for the change to take effect.");
        config.set("login", newLogin);
        saveConfig();
    }

    private void sanitizeServerName() {
        botServer = botServer.replace("^.*\\/\\/", "");
        botServer = botServer.replace(":\\d+$", "");
        config.set("server", botServer);
        saveConfig();
    }

    private void loadConfig() {
        try {
            config.load(file);
            autoConnect = config.getBoolean("autoconnect", true);
            ssl = config.getBoolean("ssl", false);
            trustAllCerts = config.getBoolean("trust-all-certs", false);
            sendRawMessageOnConnect = config.getBoolean("raw-message-on-connect", false);
            rawMessage = config.getString("raw-message", "");
            relayPrivateChat = config.getBoolean("relay-private-chat", false);
            partInvalidChannels = config.getBoolean("part-invalid-channels", false);
            partInvalidChannelsMsg = config.getString("part-invalid-channels-message", "");
            botNick = config.getString("nick", "");
            plugin.loadTemplates(config, botNick);
            botLogin = config.getString("login", "PircBot");
            botRealName = config.getString("realname", "");
            if (botRealName.isEmpty()) {
                botRealName = plugin.getServer()
                        .getPluginManager().getPlugin("PurpleIRC")
                        .getDescription().getWebsite();
            }
            botServer = config.getString("server", "");
            charSet = config.getString("charset", "");
            sanitizeServerName();
            showMOTD = config.getBoolean("show-motd", false);
            botServerPort = config.getInt("port");
            botServerPass = config.getString("password", "");
            botIdentPassword = config.getString("ident-password", "");
            commandPrefix = config.getString("command-prefix", ".");
            chatDelay = config.getLong("message-delay", 1000);
            plugin.logDebug("Message Delay => " + chatDelay);
            quitMessage = ChatColor.translateAlternateColorCodes('&', config.getString("quit-message", ""));
            plugin.logDebug("Nick => " + botNick);
            plugin.logDebug("Login => " + botLogin);
            plugin.logDebug("Server => " + botServer);
            plugin.logDebug("SSL => " + ssl);
            plugin.logDebug("Trust All Certs => " + trustAllCerts);
            plugin.logDebug("Port => " + botServerPort);
            plugin.logDebug("Command Prefix => " + commandPrefix);
            //plugin.logDebug("Server Password => " + botServerPass);
            plugin.logDebug("Quit Message => " + quitMessage);
            botChannels.clear();
            opsList.clear();
            muteList.clear();
            enabledMessages.clear();
            worldList.clear();
            commandMap.clear();

            channelCmdNotifyEnabled = config.getBoolean("command-notify.enabled", false);
            plugin.logDebug(" CommandNotifyEnabled => " + channelCmdNotifyEnabled);

            channelCmdNotifyMode = config.getString("command-notify.mode", "msg");
            plugin.logDebug(" channelCmdNotifyMode => " + channelCmdNotifyMode);

            // build command notify recipient list            
            for (String recipient : config.getStringList("command-notify.recipients")) {
                if (!channelCmdNotifyRecipients.contains(recipient)) {
                    channelCmdNotifyRecipients.add(recipient);
                }
                plugin.logDebug(" Command Notify Recipient => " + recipient);
            }
            if (channelCmdNotifyRecipients.isEmpty()) {
                plugin.logInfo(" No command recipients defined.");
            }

            for (String enChannelName : config.getConfigurationSection("channels").getKeys(false)) {
                String channelName = decodeChannel(enChannelName);
                plugin.logDebug("Channel  => " + channelName);
                botChannels.add(channelName.toLowerCase());

                channelAutoJoin.put(channelName, config.getBoolean("channels." + enChannelName + ".autojoin", true));
                plugin.logDebug("  Autojoin => " + channelAutoJoin.get(channelName));

                channelPassword.put(channelName, config.getString("channels." + enChannelName + ".password", ""));

                channelTopic.put(channelName, config.getString("channels." + enChannelName + ".topic", ""));
                plugin.logDebug("  Topic => " + channelTopic.get(channelName));

                channelModes.put(channelName, config.getString("channels." + enChannelName + ".modes", ""));
                plugin.logDebug("  Channel Modes => " + channelModes.get(channelName));

                channelTopicProtected.put(channelName, config.getBoolean("channels." + enChannelName + ".topic-protect", false));
                plugin.logDebug("  Topic Protected => " + channelTopicProtected.get(channelName).toString());

                channelTopicChanserv.put(channelName, config.getBoolean("channels." + enChannelName + ".topic-chanserv", false));
                plugin.logDebug("  Topic Chanserv Mode => " + channelTopicChanserv.get(channelName).toString());

                heroChannel.put(channelName, config.getString("channels." + enChannelName + ".hero-channel", ""));
                plugin.logDebug("  HeroChannel => " + heroChannel.get(channelName));

                townyChannel.put(channelName, config.getString("channels." + enChannelName + ".towny-channel", ""));
                plugin.logDebug("  TownyChannel => " + townyChannel.get(channelName));

                logIrcToHeroChat.put(channelName, config.getBoolean("channels." + enChannelName + ".log-irc-to-hero-chat", false));
                plugin.logDebug("  LogIrcToHeroChat => " + logIrcToHeroChat.get(channelName));

                ignoreIRCChat.put(channelName, config.getBoolean("channels." + enChannelName + ".ignore-irc-chat", false));
                plugin.logDebug("  IgnoreIRCChat => " + ignoreIRCChat.get(channelName));

                hideJoinWhenVanished.put(channelName, config.getBoolean("channels." + enChannelName + ".hide-join-when-vanished", true));
                plugin.logDebug("  HideJoinWhenVanished => " + hideJoinWhenVanished.get(channelName));

                hideListWhenVanished.put(channelName, config.getBoolean("channels." + enChannelName + ".hide-list-when-vanished", true));
                plugin.logDebug("  HideListWhenVanished => " + hideListWhenVanished.get(channelName));

                hideQuitWhenVanished.put(channelName, config.getBoolean("channels." + enChannelName + ".hide-quit-when-vanished", true));
                plugin.logDebug("  HideQuitWhenVanished => " + hideQuitWhenVanished.get(channelName));

                invalidCommandPrivate.put(channelName, config.getBoolean("channels." + enChannelName + ".invalid-command.private", false));
                plugin.logDebug("  InvalidCommandPrivate => " + invalidCommandPrivate.get(channelName));

                invalidCommandCTCP.put(channelName, config.getBoolean("channels." + enChannelName + ".invalid-command.ctcp", false));
                plugin.logDebug("  InvalidCommandCTCP => " + invalidCommandCTCP.get(channelName));

                shortify.put(channelName, config.getBoolean("channels." + enChannelName + ".shortify", true));
                plugin.logDebug("  Shortify => " + shortify.get(channelName));

                joinMsg.put(channelName, config.getString("channels." + enChannelName + ".raw-message", ""));
                plugin.logDebug("  JoinMessage => " + joinMsg.get(channelName));

                msgOnJoin.put(channelName, config.getBoolean("channels." + enChannelName + ".raw-message-on-join", false));
                plugin.logDebug("  SendMessageOnJoin => " + msgOnJoin.get(channelName));

                enableMessageFiltering.put(channelName, config.getBoolean("channels." + enChannelName + ".enable-filtering", false));
                plugin.logDebug("  EnableMessageFiltering => " + enableMessageFiltering.get(channelName));

                // build channel op list
                Collection<String> cOps = new ArrayList<String>();
                for (String channelOper : config.getStringList("channels." + enChannelName + ".ops")) {
                    if (!cOps.contains(channelOper)) {
                        cOps.add(channelOper);
                    }
                    plugin.logDebug("  Channel Op => " + channelOper);
                }
                opsList.put(channelName, cOps);
                if (opsList.isEmpty()) {
                    plugin.logInfo("No channel ops defined.");
                }

                // build mute list
                Collection<String> m = new ArrayList<String>();
                for (String mutedUser : config.getStringList("channels." + enChannelName + ".muted")) {
                    if (!m.contains(mutedUser)) {
                        m.add(mutedUser);
                    }
                    plugin.logDebug("  Channel Mute => " + mutedUser);
                }
                muteList.put(channelName, m);
                if (muteList.isEmpty()) {
                    plugin.logInfo("IRC mute list is empty.");
                }

                // build valid chat list
                Collection<String> c = new ArrayList<String>();
                for (String validChat : config.getStringList("channels." + enChannelName + ".enabled-messages")) {
                    if (!c.contains(validChat)) {
                        c.add(validChat);
                    }
                    plugin.logDebug("  Enabled Message => " + validChat);
                }
                enabledMessages.put(channelName, c);
                if (enabledMessages.isEmpty()) {
                    plugin.logInfo("There are no enabled messages!");
                }

                // build valid world list
                Collection<String> w = new ArrayList<String>();
                for (String validWorld : config.getStringList("channels." + enChannelName + ".worlds")) {
                    if (!w.contains(validWorld)) {
                        w.add(validWorld);
                    }
                    plugin.logDebug("  Enabled World => " + validWorld);
                }
                worldList.put(channelName, w);
                if (worldList.isEmpty()) {
                    plugin.logInfo("World list is empty!");
                }

                // build valid world list
                Collection<String> t = new ArrayList<String>();
                for (String name : config.getStringList("channels." + enChannelName + ".custom-tab-ignore-list")) {
                    if (!t.contains(name)) {
                        t.add(name);
                    }
                    plugin.logDebug("  Tab Ignore => " + name);
                }
                tabIgnoreNicks.put(channelName, t);
                if (tabIgnoreNicks.isEmpty()) {
                    plugin.logInfo("World list is empty!");
                }

                // build valid world list
                Collection<String> f = new ArrayList<String>();
                for (String word : config.getStringList("channels." + enChannelName + ".filter-list")) {
                    if (!f.contains(word)) {
                        f.add(word);
                    }
                    plugin.logDebug("  Filtered From IRC => " + word);
                }
                filters.put(channelName, f);
                if (filters.isEmpty()) {
                    plugin.logInfo("World list is empty!");
                }

                // build command map
                CaseInsensitiveMap<CaseInsensitiveMap<String>> map
                        = new CaseInsensitiveMap<CaseInsensitiveMap<String>>();
                for (String command : config.getConfigurationSection("channels." + enChannelName + ".commands").getKeys(false)) {
                    plugin.logDebug("  Command => " + command);
                    CaseInsensitiveMap<String> optionPair = new CaseInsensitiveMap<String>();
                    String commandKey = "channels." + enChannelName + ".commands." + command + ".";
                    optionPair.put("modes", config.getString(commandKey + "modes", "*"));
                    optionPair.put("private", config.getString(commandKey + "private", "false"));
                    optionPair.put("ctcp", config.getString(commandKey + "ctcp", "false"));
                    optionPair.put("game_command", config.getString(commandKey + "game_command", "@help"));
                    optionPair.put("private_listen", config.getString(commandKey + "private_listen", "true"));
                    optionPair.put("channel_listen", config.getString(commandKey + "channel_listen", "true"));
                    for (String s : optionPair.keySet()) {
                        config.set(commandKey + s, optionPair.get(s));
                    }
                    map.put(command, optionPair);
                }
                commandMap.put(channelName, map);
                if (map.isEmpty()) {
                    plugin.logInfo("No commands specified!");
                }
                connectMessage = "Connecting to \"" + botServer + ":"
                        + botServerPort + "\" as \"" + botNick
                        + "\" [SSL: " + ssl + "]" + " [TrustAllCerts: "
                        + trustAllCerts + "]";
            }
        } catch (IOException ex) {
            plugin.logError(ex.getMessage());
        } catch (InvalidConfigurationException ex) {
            plugin.logError(ex.getMessage());
        }
    }

    /**
     *
     * @param sender
     * @param delay
     */
    public void setIRCDelay(CommandSender sender, long delay) {
        config.set("message-delay", delay);
        saveConfig();
        sender.sendMessage(ChatColor.DARK_PURPLE
                + "IRC message delay changed to "
                + ChatColor.WHITE + delay + ChatColor.DARK_PURPLE + " ms. "
                + "Reload for the change to take effect.");
    }

    private boolean isPlayerInValidWorld(Player player, String channelName) {
        if (worldList.containsKey(channelName)) {
            if (worldList.get(channelName).contains("*")) {
                return true;
            }
            if (worldList.get(channelName).contains(player.getWorld().getName())) {
                return true;
            }
        }
        return false;
    }

    // Called from normal game chat listener
    /**
     *
     * @param player
     * @param message
     */
    public void gameChat(Player player, String message) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (!isPlayerInValidWorld(player, channelName)) {
                continue;
            }
            if (plugin.fcHook != null) {
                String playerChatMode;
                String playerFactionName;
                try {
                    playerChatMode = plugin.fcHook.getChatMode(player);
                } catch (IllegalAccessError ex) {
                    plugin.logDebug("FC Error: " + ex.getMessage());
                    playerChatMode = "public";
                }
                try {
                    playerFactionName = plugin.fcHook.getFactionName(player);
                } catch (IllegalAccessError ex) {
                    plugin.logDebug("FC Error: " + ex.getMessage());
                    playerFactionName = "unknown";
                }

                String chatName = "faction-" + playerChatMode.toLowerCase() + "-chat";
                plugin.logDebug("Faction [Player: " + player.getName()
                        + "] [Tag: " + playerFactionName + "] [Mode: "
                        + playerChatMode + "]");
                if (enabledMessages.get(channelName)
                        .contains(chatName)) {
                    asyncIRCMessage(channelName, plugin.tokenizer
                            .chatFactionTokenizer(player, botNick, message,
                                    playerFactionName, playerChatMode));
                } else {
                    plugin.logDebug("Player " + player.getName() + " is in chat mode \""
                            + playerChatMode + "\" but \"" + chatName + "\" is disabled.");
                }
            } else {
                plugin.logDebug("No Factions");
            }
            if (enabledMessages.get(channelName).contains(TemplateName.GAME_CHAT)) {
                plugin.logDebug("[" + TemplateName.GAME_CHAT + "] => "
                        + channelName + " => " + message);
                asyncIRCMessage(channelName, plugin.tokenizer
                        .gameChatToIRCTokenizer(player, plugin.getMsgTemplate(botNick, TemplateName.GAME_CHAT), message));
            } else {
                plugin.logDebug("Ignoring message due to "
                        + TemplateName.GAME_CHAT + " not being listed.");
            }
        }
    }

    // Called from HeroChat listener
    /**
     *
     * @param chatter
     * @param chatColor
     * @param message
     */
    public void heroChat(Chatter chatter, ChatColor chatColor, String message) {
        plugin.logDebug("H1");
        Player player = chatter.getPlayer();
        if (!bot.isConnected()) {
            return;
        }
        plugin.logDebug("H2");
        for (String channelName : botChannels) {
            plugin.logDebug("H3");
            if (!isPlayerInValidWorld(player, channelName)) {
                continue;
            }
            plugin.logDebug("H3.1");
            String hChannel = chatter.getActiveChannel().getName();
            String hNick = chatter.getActiveChannel().getNick();
            String hColor = chatColor.toString();
            plugin.logDebug("HC Channel: " + hChannel);
            plugin.logDebug("H3.2");
            if (enabledMessages.get(channelName).contains("hero-" + hChannel + "-chat")
                    || enabledMessages.get(channelName).contains(TemplateName.HERO_CHAT)) {
                plugin.logDebug("H3.3");
                asyncIRCMessage(channelName, plugin.tokenizer
                        .chatHeroTokenizer(player, message, hColor, hChannel,
                                hNick, plugin.getHeroChatChannelTemplate(botNick, hChannel)));
                plugin.logDebug("H3.4");
            } else {
                plugin.logDebug("Player " + player.getName() + " is in \""
                        + hChannel + "\" but hero-" + hChannel + "-chat is disabled.");
            }
        }
        plugin.logDebug("H4");
    }

    public void mcMMOAdminChat(Player player, String message) {
        for (String channelName : botChannels) {
            if (!isPlayerInValidWorld(player, channelName)) {
                continue;
            }
            if (enabledMessages.get(channelName).contains(TemplateName.MCMMO_ADMIN_CHAT)) {
                plugin.logDebug("Sending message because " + TemplateName.MCMMO_ADMIN_CHAT + " is enabled.");
                asyncIRCMessage(channelName, plugin.tokenizer
                        .gameChatToIRCTokenizer(player, plugin.getMsgTemplate(
                                        botNick, TemplateName.MCMMO_ADMIN_CHAT), message));
            } else {
                plugin.logDebug("Player " + player.getName()
                        + " is in mcMMO PartyChat but " + TemplateName.MCMMO_ADMIN_CHAT + " is disabled.");
            }
        }
    }

    public void mcMMOPartyChat(Player player, String partyName, String message) {
        for (String channelName : botChannels) {
            if (!isPlayerInValidWorld(player, channelName)) {
                continue;
            }
            if (enabledMessages.get(channelName).contains(TemplateName.MCMMO_PARTY_CHAT)) {
                plugin.logDebug("Sending message because " + TemplateName.MCMMO_PARTY_CHAT + " is enabled.");
                asyncIRCMessage(channelName, plugin.tokenizer
                        .mcMMOChatToIRCTokenizer(player, plugin.getMsgTemplate(
                                        botNick, TemplateName.MCMMO_PARTY_CHAT), message, partyName));
            } else {
                plugin.logDebug("Player " + player.getName()
                        + " is in mcMMO PartyChat but " + TemplateName.MCMMO_PARTY_CHAT + " is disabled.");
            }
        }
    }

    public void mcMMOChat(Player player, String message) {
        for (String channelName : botChannels) {
            if (!isPlayerInValidWorld(player, channelName)) {
                continue;
            }
            if (enabledMessages.get(channelName).contains(TemplateName.MCMMO_CHAT)) {
                plugin.logDebug("Sending message because " + TemplateName.MCMMO_CHAT + " is enabled.");
                asyncIRCMessage(channelName, plugin.tokenizer
                        .gameChatToIRCTokenizer(player, plugin.getMsgTemplate(
                                        botNick, TemplateName.MCMMO_CHAT), message));
            } else {
                plugin.logDebug("Player " + player.getName()
                        + " is in mcMMO Chat but " + TemplateName.MCMMO_CHAT + " is disabled.");
            }
        }
    }

    public void townyChat(Player player,
            com.palmergames.bukkit.TownyChat.channels.Channel townyChannel, String message) {
        if (!bot.isConnected()) {
            return;
        }
        if (plugin.tcHook != null) {
            for (String channelName : botChannels) {
                if (!isPlayerInValidWorld(player, channelName)) {
                    continue;
                }
                plugin.logDebug("townyChat: Checking for towny-"
                        + townyChannel.getName() + "-chat"
                        + " or " + "towny-" + townyChannel.getChannelTag() + "-chat"
                        + " or towny-chat");
                if (enabledMessages.get(channelName).contains("towny-" + townyChannel.getName() + "-chat")
                        || enabledMessages.get(channelName).contains("towny-" + townyChannel.getChannelTag() + "-chat")
                        || enabledMessages.get(channelName).contains("towny-chat")
                        || enabledMessages.get(channelName).contains("towny-channel-chat")) {
                    asyncIRCMessage(channelName, plugin.tokenizer
                            .chatTownyChannelTokenizer(player, townyChannel, message,
                                    plugin.getMsgTemplate(botNick, "towny-channel-chat")));
                }
            }
        }
    }

    public void heroAction(Chatter chatter, ChatColor chatColor, String message) {
        Player player = chatter.getPlayer();
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (!isPlayerInValidWorld(player, channelName)) {
                continue;
            }
            String hChannel = chatter.getActiveChannel().getName();
            String hNick = chatter.getActiveChannel().getNick();
            String hColor = chatColor.toString();
            plugin.logDebug("HC Channel: " + hChannel);
            if (enabledMessages.get(channelName).contains("hero-" + hChannel + "-action")
                    || enabledMessages.get(channelName).contains("hero-action")) {
                asyncIRCMessage(channelName, plugin.tokenizer
                        .chatHeroTokenizer(player, message, hColor, hChannel,
                                hNick, plugin.getHeroActionChannelTemplate(botNick, hChannel)));
            } else {
                plugin.logDebug("Player " + player.getName() + " is in \""
                        + hChannel + "\" but hero-" + hChannel + "-action is disabled.");
            }
        }
    }

    // Called from TitanChat listener
    /**
     *
     * @param participant
     * @param tChannel
     * @param tColor
     * @param message
     */
    public void titanChat(Participant participant, String tChannel, String tColor, String message) {
        Player player = plugin.getServer().getPlayer(participant.getName());
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (!isPlayerInValidWorld(player, channelName)) {
                continue;
            }
            plugin.logDebug("TC Channel: " + tChannel);
            if (enabledMessages.get(channelName).contains("titan-" + tChannel + "-chat")
                    || enabledMessages.get(channelName).contains("titan-chat")) {
                asyncIRCMessage(channelName, plugin.tokenizer
                        .titanChatTokenizer(player, tChannel, tColor, message,
                                plugin.getMsgTemplate(botNick, "titan-chat")));
            } else {
                plugin.logDebug("Player " + player.getName() + " is in \""
                        + tChannel + "\" but titan-" + tChannel + "-chat is disabled.");
            }
        }
    }

    // Called from /irc send
    /**
     *
     * @param player
     * @param channelName
     * @param message
     */
    public void gameChat(Player player, String channelName, String message) {
        if (!bot.isConnected()) {
            return;
        }
        if (isValidChannel(channelName)) {
            asyncIRCMessage(channelName, plugin.tokenizer
                    .gameChatToIRCTokenizer(player, plugin.getMsgTemplate(
                                    botNick, TemplateName.GAME_SEND), message));
        }
    }

    // Called from CleverEvent
    /**
     *
     * @param cleverBotName
     * @param message
     */
    public void cleverChat(String cleverBotName, String message) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains("clever-chat")) {
                asyncIRCMessage(channelName, plugin.tokenizer
                        .gameChatToIRCTokenizer(cleverBotName, plugin.getMsgTemplate(botNick, "clever-send"), message));
            }
        }
    }

    // Called from ReportRTS event
    /**
     *
     * @param pName
     * @param request
     * @param botNick
     * @param messageType
     */
    public void reportRTSNotify(String pName, HelpRequest request,
            String botNick, String messageType) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains(messageType)) {
                asyncIRCMessage(channelName, plugin.tokenizer
                        .reportRTSTokenizer(pName, plugin.getMsgTemplate(botNick, messageType), request));
            }
        }
    }

    public void reportRTSNotify(CommandSender sender, String message, String botNick, String messageType) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains(messageType)) {
                asyncIRCMessage(channelName, plugin.tokenizer
                        .reportRTSTokenizer(sender, message, plugin.getMsgTemplate(botNick, messageType)));
            }
        }
    }

    /**
     *
     * @param channelName
     * @param message
     */
    public void consoleChat(String channelName, String message) {
        if (!bot.isConnected()) {
            return;
        }
        if (isValidChannel(channelName)) {
            asyncIRCMessage(channelName, plugin.tokenizer
                    .gameChatToIRCTokenizer("CONSOLE", message, plugin.getMsgTemplate(
                                    botNick, TemplateName.GAME_SEND)));
        }
    }

    /**
     *
     * @param message
     */
    public void consoleChat(String message) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains(TemplateName.CONSOLE_CHAT)) {
                asyncIRCMessage(channelName, plugin.tokenizer
                        .gameChatToIRCTokenizer(plugin.getMsgTemplate(botNick,
                                        TemplateName.CONSOLE_CHAT), ChatColor.translateAlternateColorCodes('&', message)));
            }
        }
    }

    /**
     *
     * @param player
     * @param message
     */
    public void gameBroadcast(Player player, String message) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains("broadcast-message")) {
                asyncIRCMessage(channelName, plugin.tokenizer
                        .gameChatToIRCTokenizer(player, plugin.getMsgTemplate(botNick, "broadcast-message"), ChatColor.translateAlternateColorCodes('&', message)));
            }
        }
    }

    /**
     *
     * @param name
     * @param message
     * @param source
     */
    public void dynmapWebChat(String source, String name, String message) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains(TemplateName.DYNMAP_WEB_CHAT)) {
                asyncIRCMessage(channelName, plugin.tokenizer
                        .dynmapWebChatToIRCTokenizer(source, name, plugin.getMsgTemplate(
                                        botNick, TemplateName.DYNMAP_WEB_CHAT),
                                ChatColor.translateAlternateColorCodes('&', message)));
            }
        }
    }

    /**
     *
     * @param message
     */
    public void consoleBroadcast(String message) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains(TemplateName.BROADCAST_CONSOLE_MESSAGE)) {
                asyncIRCMessage(channelName, plugin.tokenizer
                        .gameChatToIRCTokenizer(plugin.getMsgTemplate(botNick,
                                        TemplateName.BROADCAST_CONSOLE_MESSAGE), ChatColor.translateAlternateColorCodes('&', message)));
            }
        }
    }

    /**
     *
     * @param player
     * @param message
     */
    public void gameJoin(Player player, String message) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains(TemplateName.GAME_JOIN)) {
                if (!isPlayerInValidWorld(player, channelName)) {
                    return;
                }
                if (hideJoinWhenVanished.get(channelName)) {
                    plugin.logDebug("Checking if player " + player.getName()
                            + " is vanished.");
                    if (plugin.vanishHook.isVanished(player)) {
                        plugin.logDebug("Not sending join message to IRC for player "
                                + player.getName() + " due to being vanished.");
                        continue;
                    }
                }
                asyncIRCMessage(channelName, plugin.tokenizer
                        .gameChatToIRCTokenizer(player, plugin.getMsgTemplate(
                                        botNick, TemplateName.GAME_JOIN), message));
            } else {
                plugin.logDebug("Not sending join message due to "
                        + TemplateName.GAME_JOIN + " being disabled");
            }
        }
    }

    /**
     *
     * @param player
     * @param message
     */
    public void gameQuit(Player player, String message) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains(TemplateName.GAME_QUIT)) {
                if (!isPlayerInValidWorld(player, channelName)) {
                    return;
                }
                if (hideQuitWhenVanished.get(channelName)) {
                    plugin.logDebug("Checking if player " + player.getName()
                            + " is vanished.");
                    if (plugin.vanishHook.isVanished(player)) {
                        plugin.logDebug("Not sending quit message to IRC for player "
                                + player.getName() + " due to being vanished.");
                        continue;
                    }
                }
                asyncIRCMessage(channelName, plugin.tokenizer
                        .gameChatToIRCTokenizer(player, plugin.getMsgTemplate(
                                        botNick, TemplateName.GAME_QUIT), message));
            }
        }
    }

    /**
     *
     * @param player
     * @param achievement
     */
    public void gameAchievement(Player player, Achievement achievement) {
        String message = achievement.toString();
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains(TemplateName.GAME_ACHIEVEMENT)) {
                if (!isPlayerInValidWorld(player, channelName)) {
                    return;
                }
                asyncIRCMessage(channelName, plugin.tokenizer
                        .gameChatToIRCTokenizer(player, plugin.getMsgTemplate(
                                        botNick, TemplateName.GAME_ACHIEVEMENT), message));
            }
        }
    }

    /**
     *
     * @param player
     * @param message
     * @param reason
     */
    public void gameKick(Player player, String message, String reason) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains(TemplateName.GAME_KICK)) {
                if (!isPlayerInValidWorld(player, channelName)) {
                    return;
                }
                asyncIRCMessage(channelName, plugin.tokenizer
                        .gameKickTokenizer(player, plugin.getMsgTemplate(
                                        botNick, TemplateName.GAME_KICK), message, reason));
            }
        }
    }

    /**
     *
     * @param player
     * @param message
     */
    public void gameAction(Player player, String message) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains(TemplateName.GAME_ACTION)) {
                if (!isPlayerInValidWorld(player, channelName)) {
                    return;
                }
                asyncIRCMessage(channelName, plugin.tokenizer
                        .gameChatToIRCTokenizer(player, plugin.getMsgTemplate(
                                        botNick, TemplateName.GAME_ACTION), message));
            }
        }
    }

    /**
     *
     * @param player
     * @param message
     * @param templateName
     */
    public void gameDeath(Player player, String message, String templateName) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains(templateName)) {
                if (!isPlayerInValidWorld(player, channelName)) {
                    return;
                }
                asyncIRCMessage(channelName, plugin.tokenizer
                        .gameChatToIRCTokenizer(player, plugin.getMsgTemplate(
                                        botNick, templateName), message));
            }
        }
    }

    /**
     *
     * @param channelName
     * @param topic
     * @param sender
     */
    public void changeTopic(String channelName, String topic, CommandSender sender) {
        Channel channel = this.getChannel(channelName);
        String tTopic = tokenizedTopic(topic);
        if (channel != null) {
            setTheTopic(channel, tTopic);
            config.set("channels." + encodeChannel(channelName) + ".topic", topic);
            channelTopic.put(channelName, topic);
            saveConfig();
            sender.sendMessage("IRC topic for " + channelName + " changed to \"" + topic + "\"");
        } else {
            sender.sendMessage("Invalid channel: " + channelName);
        }
    }

    public Channel getChannel(String channelName) {
        Channel channel = null;
        for (Channel c : getChannels()) {
            if (c.getName().equalsIgnoreCase(channelName)) {
                return c;
            }
        }
        return channel;
    }

    /**
     *
     * @param sender
     * @param botServer
     */
    public void setServer(CommandSender sender, String botServer) {
        setServer(sender, botServer, autoConnect);
    }

    /**
     *
     * @param sender
     * @param server
     * @param auto
     */
    public void setServer(CommandSender sender, String server, Boolean auto) {

        if (server.contains(":")) {
            botServerPort = Integer.parseInt(server.split(":")[1]);
            botServer = server.split(":")[0];
        } else {
            botServer = server;
        }
        sanitizeServerName();
        autoConnect = auto;
        config.set("server", botServer);
        config.set("port", botServerPort);
        config.set("autoconnect", autoConnect);

        sender.sendMessage("IRC server changed to \"" + botServer + ":"
                + botServerPort + "\". (AutoConnect: "
                + autoConnect + ")");
    }

    /**
     *
     * @param channelName
     * @param userMask
     * @param sender
     */
    public void addOp(String channelName, String userMask, CommandSender sender) {
        if (opsList.get(channelName).contains(userMask)) {
            sender.sendMessage("User mask " + ChatColor.WHITE + userMask
                    + ChatColor.RESET + " is already in the ops list.");
        } else {
            sender.sendMessage("User mask " + ChatColor.WHITE + userMask
                    + ChatColor.RESET + " has been added to the ops list.");
            opsList.get(channelName).add(userMask);
        }
        config.set("channels." + encodeChannel(channelName) + ".ops", opsList.get(channelName));
        saveConfig();
    }

    /**
     *
     * @param channelName
     * @param userMask
     * @param sender
     */
    public void removeOp(String channelName, String userMask, CommandSender sender) {
        if (opsList.get(channelName).contains(userMask)) {
            sender.sendMessage("User mask " + ChatColor.WHITE + userMask
                    + ChatColor.RESET + " has been removed to the ops list.");
            opsList.get(channelName).remove(userMask);
        } else {
            sender.sendMessage("User mask " + ChatColor.WHITE + userMask
                    + ChatColor.RESET + " is not in the ops list.");
        }
        config.set("channels." + encodeChannel(channelName) + ".ops", opsList.get(channelName));
        saveConfig();
    }

    /**
     *
     * @param channelName
     * @param nick
     */
    public void op(String channelName, String nick) {
        Channel channel;
        channel = getChannel(channelName);
        if (channel != null) {
            for (User user : channel.getUsers()) {
                if (user.getNick().equals(nick)) {
                    channel.send().op(user);
                    return;
                }
            }
        }
    }

    /**
     *
     * @param channelName
     * @param nick
     */
    public void deOp(String channelName, String nick) {
        Channel channel;
        channel = getChannel(channelName);
        if (channel != null) {
            for (User user : channel.getUsers()) {
                if (user.getNick().equals(nick)) {
                    channel.send().deOp(user);
                    return;
                }
            }
        }
    }

    /**
     *
     * @param channelName
     * @param nick
     */
    public void kick(String channelName, String nick) {
        Channel channel;
        channel = getChannel(channelName);
        if (channel != null) {
            for (User user : channel.getUsers()) {
                if (user.getNick().equals(nick)) {
                    channel.send().kick(user);
                    return;
                }
            }
        }
    }

    private String encodeChannel(String s) {
        return s.replace(".", "%2E");
    }

    private String decodeChannel(String s) {
        return s.replace("%2E", ".");
    }

    /**
     *
     * @param channel
     * @param topic
     * @param setBy
     */
    public void fixTopic(Channel channel, String topic, String setBy) {
        String channelName = channel.getName();
        String tTopic = tokenizedTopic(topic);
        if (setBy.equals(botNick)) {
            //config.set("channels." + encodeChannel(channelName) + ".topic", topic);
            //saveConfig();
            return;
        }

        if (channelTopic.containsKey(channelName)) {
            if (channelTopicProtected.containsKey(channelName)) {
                if (channelTopicProtected.get(channelName)) {
                    plugin.logDebug("[" + channel.getName() + "] Topic protected.");
                    String myTopic = tokenizedTopic(channelTopic.get(channelName));
                    plugin.logDebug("rTopic: " + channelTopic.get(channelName));
                    plugin.logDebug("tTopic: " + tTopic);
                    plugin.logDebug("myTopic: " + myTopic);
                    if (!tTopic.equals(myTopic)) {
                        plugin.logDebug("Topic is not correct. Fixing it.");
                        setTheTopic(channel, myTopic);
                    } else {
                        plugin.logDebug("Topic is correct.");
                    }
                }
            }
        }
    }

    private void setTheTopic(Channel channel, String topic) {
        String myChannel = channel.getName();
        if (channelTopicChanserv.containsKey(myChannel)) {
            if (channelTopicChanserv.get(myChannel)) {
                String msg = String.format("TOPIC %s %s", myChannel, topic);
                plugin.logDebug("Sending chanserv rmessage: " + msg);
                asyncIRCMessage("chanserv", msg);
                return;
            }
        }
        channel.send().setTopic(topic);
    }

    private String tokenizedTopic(String topic) {
        return plugin.colorConverter
                .gameColorsToIrc(topic.replace("%MOTD%", plugin.getServer().getMotd()));
    }

    /**
     *
     * @param sender
     */
    public void asyncQuit(CommandSender sender) {
        sender.sendMessage("Disconnecting " + bot.getNick() + " from IRC server " + botServer);
        asyncQuit(false);
    }

    /**
     *
     * @param reload
     */
    public void asyncQuit(final Boolean reload) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                quit();
                if (reload) {
                    buildBot();
                }
            }
        });

    }

    public void quit() {
        if (bot.isConnected()) {
            plugin.logDebug("Q: " + quitMessage);
            if (quitMessage.isEmpty()) {
                bot.sendIRC().quitServer();
            } else {
                bot.sendIRC().quitServer(plugin.colorConverter.gameColorsToIrc(quitMessage));
            }
        }
    }

    /**
     *
     * @param sender
     */
    public void sendTopic(CommandSender sender) {
        for (String channelName : botChannels) {
            if (commandMap.containsKey(channelName)) {
                sender.sendMessage(ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE
                        + botNick + ChatColor.WHITE + "]" + ChatColor.RESET
                        + " IRC topic for " + ChatColor.WHITE + channelName
                        + ChatColor.RESET + ": \""
                        + ChatColor.WHITE + plugin.colorConverter
                        .ircColorsToGame(activeTopic.get(channelName))
                        + ChatColor.RESET + "\"");
            }
        }
    }

    /**
     *
     * @param sender
     * @param nick
     */
    public void sendUserWhois(CommandSender sender, String nick) {
        User user = null;
        for (Channel channel : getChannels()) {
            for (User u : channel.getUsers()) {
                if (u.getNick().equals(nick)) {
                    user = u;
                }
            }
        }

        if (user == null) {
            sender.sendMessage(ChatColor.RED + "Invalid user: " + ChatColor.WHITE + nick);
        } else {
            bot.sendRaw().rawLineNow(String.format("WHOIS %s %s", nick, nick));
            whoisSenders.add(sender);
        }
    }

    /**
     *
     * @param sender
     * @param channelName
     */
    public void sendUserList(CommandSender sender, String channelName) {
        String invalidChannel = ChatColor.RED + "Invalid channel: "
                + ChatColor.WHITE + channelName;
        if (!isValidChannel(channelName)) {
            sender.sendMessage(invalidChannel);
            return;
        }
        Channel channel = getChannel(channelName);
        if (channel != null) {
            sendUserList(sender, channel);
        } else {
            sender.sendMessage(invalidChannel);
        }
    }

    /**
     *
     * @param sender
     * @param channel
     */
    public void sendUserList(CommandSender sender, Channel channel) {
        String channelName = channel.getName();
        if (!isValidChannel(channelName)) {
            sender.sendMessage(ChatColor.RED + "Invalid channel: "
                    + ChatColor.WHITE + channelName);
            return;
        }
        sender.sendMessage(ChatColor.DARK_PURPLE + "-----[  " + ChatColor.WHITE + channelName
                + ChatColor.DARK_PURPLE + " - " + ChatColor.WHITE + bot.getNick() + ChatColor.DARK_PURPLE + " ]-----");
        if (!bot.isConnected()) {
            sender.sendMessage(ChatColor.RED + " Not connected!");
            return;
        }
        List<String> channelUsers = new ArrayList<String>();
        for (User user : channel.getUsers()) {
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
            if (user.isAway()) {
                nick = nick + ChatColor.GRAY + " | Away";
            }
            if (nick.equals(bot.getNick())) {
                nick = ChatColor.DARK_PURPLE + nick;
            }
            channelUsers.add(nick);
        }
        Collections.sort(channelUsers, Collator.getInstance());
        for (String userName : channelUsers) {
            sender.sendMessage("  " + ChatColor.WHITE + userName);
        }
    }

    /**
     *
     * @param sender
     */
    public void sendUserList(CommandSender sender) {
        for (Channel channel : getChannels()) {
            if (isValidChannel(channel.getName())) {
                sendUserList(sender, channel);
            }
        }
    }

    /**
     *
     * @param channel
     */
    public void updateNickList(Channel channel) {
        // Build current list of names in channel
        ArrayList<String> users = new ArrayList<String>();
        for (User user : channel.getUsers()) {
            //plugin.logDebug("N: " + user.getNick());
            users.add(user.getNick());
        }
        // Iterate over previous list and remove from tab list
        String channelName = channel.getName();
        if (channelNicks.containsKey(channelName)) {
            for (String name : channelNicks.get(channelName)) {
                //plugin.logDebug("O: " + name);
                if (!users.contains(name)) {
                    plugin.logDebug("Removing " + name + " from list.");
                    if (plugin.netPackets != null) {
                        plugin.netPackets.remFromTabList(name);
                    }
                }
            }
            channelNicks.remove(channelName);
        }
        channelNicks.put(channelName, users);
    }

    /**
     *
     * @param channel
     */
    public void opFriends(Channel channel) {
        for (User user : channel.getUsers()) {
            opFriends(channel, user);
        }
    }

    /**
     *
     * @param channelName
     */
    public void opFriends(String channelName) {
        Channel channel = getChannel(channelName);
        if (channel != null) {
            for (User user : channel.getUsers()) {
                opFriends(channel, user);
            }
        }
    }

    /**
     *
     * @param channel
     * @param user
     */
    public void opFriends(Channel channel, User user) {
        if (user.getNick().equals(botNick)) {
            return;
        }
        String channelName = channel.getName();
        for (String opsUser : opsList.get(channelName)) {
            plugin.logDebug("OP => " + user);
            //sender!*login@hostname            
            String mask[] = opsUser.split("[\\!\\@]", 3);
            if (mask.length == 3) {
                String gUser = plugin.regexGlobber.createRegexFromGlob(mask[0]);
                String gLogin = plugin.regexGlobber.createRegexFromGlob(mask[1]);
                String gHost = plugin.regexGlobber.createRegexFromGlob(mask[2]);
                String sender = user.getNick();
                String login = user.getLogin();
                String hostname = user.getHostmask();
                plugin.logDebug("Nick: " + sender + " =~ " + gUser + " = " + sender.matches(gUser));
                plugin.logDebug("Name: " + login + " =~ " + gLogin + " = " + login.matches(gLogin));
                plugin.logDebug("Hostname: " + hostname + " =~ " + gHost + " = " + hostname.matches(gHost));
                if (sender.matches(gUser) && login.matches(gLogin) && hostname.matches(gHost)) {
                    if (!channel.getOps().contains(user)) {
                        plugin.logInfo("Giving operator status to " + sender + " on " + channelName);
                        channel.send().op(user);
                    } else {
                        plugin.logInfo("User " + sender + " is already an operator on " + channelName);
                    }
                    return;
                } else {
                    plugin.logDebug("No match: " + sender + "!" + login + "@" + hostname + " != " + user);
                }
            } else {
                plugin.logInfo("Invalid op mask: " + opsUser);
            }
        }
    }

    public String filterMessage(String message, String myChannel) {
        if (filters.containsKey(myChannel)) {
            if (!filters.get(myChannel).isEmpty()) {
                for (String filter : filters.get(myChannel)) {
                    if (filter.startsWith("/") && filter.endsWith("/")) {
                        filter = filter.substring(1, filter.length() - 1);
                        plugin.logDebug("Regex filtering " + filter + " from " + message);
                        message = message.replaceAll(filter, "");
                    } else {
                        plugin.logDebug("Filtering " + filter + " from " + message);
                        message = message.replace(filter, "");
                    }
                }
            }
        }
        return message;
    }

    // Broadcast chat messages from IRC
    /**
     *
     * @param nick
     * @param myChannel
     * @param message
     * @param override
     */
    public void broadcastChat(String nick, String myChannel, String message, boolean override) {
        if (plugin.dynmapHook != null) {
            plugin.logDebug("Checking if " + TemplateName.IRC_DYNMAP_WEB_CHAT + " is enabled ...");
            if (enabledMessages.get(myChannel).contains(TemplateName.IRC_DYNMAP_WEB_CHAT)) {
                plugin.logDebug("Yes, " + TemplateName.IRC_DYNMAP_WEB_CHAT + " is enabled...");
                plugin.logDebug("broadcastChat [DW]: " + message);
                String template = plugin.getMsgTemplate(botNick, TemplateName.IRC_DYNMAP_WEB_CHAT);
                String rawDWMessage = filterMessage(
                        plugin.tokenizer.ircChatToGameTokenizer(nick, myChannel, template, message), myChannel);
                plugin.dynmapHook.sendMessage(nick, rawDWMessage);
            } else {
                plugin.logDebug("Nope, " + TemplateName.IRC_DYNMAP_WEB_CHAT + " is NOT enabled...");
            }
        }
        if (plugin.tcHook != null) {
            plugin.logDebug("Checking if " + TemplateName.IRC_TOWNY_CHAT + " is enabled ...");
            if (enabledMessages.get(myChannel).contains(TemplateName.IRC_TOWNY_CHAT)) {
                plugin.logDebug("Yes, " + TemplateName.IRC_TOWNY_CHAT + " is enabled...");
                if (townyChannel.containsKey(myChannel)) {
                    String tChannel = townyChannel.get(myChannel);
                    if (!tChannel.isEmpty()) {
                        String tmpl = plugin.getIRCTownyChatChannelTemplate(botNick, tChannel);
                        plugin.logDebug("broadcastChat [TC]: " + tChannel + ": " + tmpl);
                        String rawTCMessage = filterMessage(
                                plugin.tokenizer.ircChatToTownyChatTokenizer(
                                        nick, myChannel, tmpl, message, tChannel), myChannel);
                        plugin.tcHook.sendMessage(tChannel, rawTCMessage);
                    }
                }
            } else {
                plugin.logDebug("Nope, " + TemplateName.IRC_TOWNY_CHAT + " is NOT enabled...");
            }
        }

        plugin.logDebug("Checking if " + TemplateName.IRC_CHAT
                + " is enabled before broadcasting chat from IRC");
        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_CHAT) || override) {
            plugin.logDebug("Yup we can broadcast due to " + TemplateName.IRC_CHAT + " enabled");
            String newMessage = filterMessage(
                    plugin.tokenizer.ircChatToGameTokenizer(
                            nick, myChannel, plugin.getMsgTemplate(
                                    botNick, TemplateName.IRC_CHAT), message), myChannel);
            if (!newMessage.isEmpty()) {
                plugin.getServer().broadcast(newMessage, "irc.message.chat");
            }
        } else {
            plugin.logDebug("NOPE we can't broadcast due to " + TemplateName.IRC_CHAT
                    + " disabled");
        }

        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_CONSOLE_CHAT)) {
            String tmpl = plugin.getMsgTemplate(botNick, TemplateName.IRC_CONSOLE_CHAT);
            plugin.logDebug("broadcastChat [Console]: " + tmpl);
            plugin.getServer().getConsoleSender().sendMessage(plugin.tokenizer.ircChatToGameTokenizer(
                    nick, myChannel, plugin.getMsgTemplate(botNick,
                            TemplateName.IRC_CONSOLE_CHAT), message));
        }

        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_HERO_CHAT)) {
            String hChannel = heroChannel.get(myChannel);
            String tmpl = plugin.getIRCHeroChatChannelTemplate(botNick, hChannel);
            plugin.logDebug("broadcastChat [HC]: " + hChannel + ": " + tmpl);
            String rawHCMessage = filterMessage(
                    plugin.tokenizer.ircChatToHeroChatTokenizer(
                            nick, myChannel, tmpl, message, Herochat.getChannelManager(), hChannel), myChannel);
            if (!rawHCMessage.isEmpty()) {
                Herochat.getChannelManager().getChannel(hChannel).sendRawMessage(rawHCMessage);
                if (logIrcToHeroChat.containsKey(myChannel)) {
                    if (logIrcToHeroChat.get(myChannel)) {
                        plugin.getServer().getConsoleSender().sendMessage(rawHCMessage);
                    }
                }
            }
        }
    }

    // Broadcast chat messages from IRC to specific hero channel
    /**
     *
     * @param nick
     * @param ircChannel
     * @param target
     * @param message
     */
    public void broadcastHeroChat(String nick, String ircChannel, String target, String message) {
        if (message == null) {
            plugin.logDebug("H: NULL MESSAGE");
            asyncIRCMessage(target, "No channel specified!");
            return;
        }
        if (message.contains(" ")) {
            String hChannel;
            String msg;
            hChannel = message.split(" ", 2)[0];
            msg = message.split(" ", 2)[1];
            plugin.logDebug("Check if " + TemplateName.IRC_HERO_CHAT + " is enabled before broadcasting chat from IRC");
            if (enabledMessages.get(ircChannel).contains(TemplateName.IRC_HERO_CHAT)) {
                plugin.logDebug("Checking if " + hChannel + " is a valid hero channel...");
                if (Herochat.getChannelManager().hasChannel(hChannel)) {
                    hChannel = Herochat.getChannelManager().getChannel(hChannel).getName();
                    String template = plugin.getIRCHeroChatChannelTemplate(botNick, hChannel);
                    plugin.logDebug("T: " + template);
                    String t = plugin.tokenizer.ircChatToHeroChatTokenizer(nick,
                            ircChannel, template, msg,
                            Herochat.getChannelManager(), hChannel);
                    plugin.logDebug("Sending message to" + hChannel + ":" + t);
                    Herochat.getChannelManager().getChannel(hChannel)
                            .sendRawMessage(t);
                    plugin.logDebug("Channel format: " + Herochat.getChannelManager().getChannel(hChannel).getFormat());
                    // Let the sender know the message was sent
                    String responseTemplate = plugin.getMsgTemplate(botNick, TemplateName.IRC_HCHAT_RESPONSE);
                    if (!responseTemplate.isEmpty()) {
                        asyncIRCMessage(target, plugin.tokenizer
                                .targetChatResponseTokenizer(hChannel, msg, responseTemplate));
                    }
                } else {
                    asyncIRCMessage(target, "Hero channel \"" + hChannel + "\" does not exist!");
                }
            } else {
                plugin.logDebug("NOPE we can't broadcast due to "
                        + TemplateName.IRC_HERO_CHAT + " disabled");
            }
        } else {
            asyncIRCMessage(target, "No message specified.");
        }
    }

    // Send chat messages from IRC to player
    /**
     *
     * @param nick
     * @param myChannel
     * @param target
     * @param message
     */
    public void playerChat(String nick, String myChannel, String target, String message) {
        if (message == null) {
            plugin.logDebug("H: NULL MESSAGE");
            asyncIRCMessage(target, "No player specified!");
            return;
        }
        if (message.contains(" ")) {
            String pName;
            String msg;
            pName = message.split(" ", 2)[0];
            msg = message.split(" ", 2)[1];
            plugin.logDebug("Check if " + TemplateName.IRC_PCHAT + " is enabled before broadcasting chat from IRC");
            if (enabledMessages.get(myChannel).contains(TemplateName.IRC_PCHAT)) {
                plugin.logDebug("Yup we can broadcast due to " + TemplateName.IRC_PCHAT
                        + " enabled... Checking if " + pName + " is a valid player...");
                Player player = plugin.getServer().getPlayer(pName);
                if (player != null) {
                    if (player.isOnline()) {
                        plugin.logDebug("Yup, " + pName + " is a valid player...");
                        String template = plugin.getMsgTemplate(botNick, TemplateName.IRC_PCHAT);
                        String t = plugin.tokenizer.ircChatToGameTokenizer(nick,
                                myChannel, template, msg);
                        String responseTemplate = plugin.getMsgTemplate(botNick,
                                TemplateName.IRC_PCHAT_RESPONSE);
                        if (!responseTemplate.isEmpty()) {
                            asyncIRCMessage(target, plugin.tokenizer
                                    .targetChatResponseTokenizer(pName, msg, responseTemplate));
                        }
                        plugin.logDebug("Tokenized message: " + t);
                        player.sendMessage(t);
                    } else {
                        asyncIRCMessage(target, "Player is offline: " + pName);
                    }
                } else {
                    asyncIRCMessage(target, "Player not found (possibly offline): " + pName);
                }
            } else {
                plugin.logDebug("NOPE we can't broadcast due to irc-pchat disabled");
            }
        } else {
            asyncIRCMessage(target, "No message specified.");
        }
    }

// Broadcast action messages from IRC
    /**
     *
     * @param nick
     * @param myChannel
     * @param message
     */
    public void broadcastAction(String nick, String myChannel, String message) {
        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_ACTION)) {
            plugin.getServer().broadcast(plugin.tokenizer.ircChatToGameTokenizer(
                    nick, myChannel, plugin.getMsgTemplate(botNick,
                            TemplateName.IRC_ACTION), message), "irc.message.action");
        } else {
            plugin.logDebug("Ignoring action due to "
                    + TemplateName.IRC_ACTION + " is false");
        }

        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_HERO_ACTION)) {
            Herochat.getChannelManager().getChannel(heroChannel.get(myChannel))
                    .sendRawMessage(plugin.tokenizer.ircChatToHeroChatTokenizer(nick,
                                    myChannel, plugin.getMsgTemplate(botNick,
                                            TemplateName.IRC_HERO_ACTION), message,
                                    Herochat.getChannelManager(),
                                    heroChannel.get(myChannel)
                            )
                    );
        }
    }

    /**
     *
     * @param recipient
     * @param kicker
     * @param reason
     * @param myChannel
     */
    public void broadcastIRCKick(String recipient, String kicker, String reason, String myChannel) {
        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_KICK)) {
            plugin.getServer().broadcast(plugin.tokenizer.ircKickTokenizer(recipient,
                    kicker, reason, myChannel, plugin.getMsgTemplate(botNick, TemplateName.IRC_KICK)),
                    "irc.message.kick");
        }

        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_HERO_KICK)) {
            Herochat.getChannelManager().getChannel(heroChannel.get(myChannel))
                    .sendRawMessage(plugin.tokenizer
                            .ircKickToHeroChatTokenizer(
                                    recipient, kicker,
                                    reason, myChannel,
                                    plugin.getMsgTemplate(botNick, TemplateName.IRC_HERO_KICK),
                                    Herochat.getChannelManager(),
                                    heroChannel.get(myChannel)
                            )
                    );
        }
    }

    /**
     *
     * @return
     */
    public boolean isConnectedBlocking() {
        return bot.isConnected();
    }

    /**
     *
     * @param nick
     * @param mode
     * @param myChannel
     */
    public void broadcastIRCMode(String nick, String mode, String myChannel) {
        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_MODE)) {
            plugin.getServer().broadcast(plugin.tokenizer.ircModeTokenizer(nick, mode,
                    myChannel, plugin.getMsgTemplate(botNick,
                            TemplateName.IRC_MODE)), "irc.message.mode");
        }
    }

    /**
     *
     * @param nick
     * @param message
     * @param notice
     * @param myChannel
     */
    public void broadcastIRCNotice(String nick, String message, String notice, String myChannel) {
        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_NOTICE)) {
            plugin.getServer().broadcast(plugin.tokenizer.ircNoticeTokenizer(nick,
                    message, notice, myChannel, plugin.getMsgTemplate(botNick,
                            TemplateName.IRC_NOTICE)), "irc.message.notice");
        }
    }

    /**
     *
     * @param nick
     * @param myChannel
     */
    public void broadcastIRCJoin(String nick, String myChannel) {
        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_JOIN)) {
            plugin.logDebug("[broadcastIRCJoin] Broadcasting join message because "
                    + TemplateName.IRC_JOIN + " is true.");
            plugin.getServer().broadcast(plugin.tokenizer.chatIRCTokenizer(
                    nick, myChannel, plugin.getMsgTemplate(botNick, TemplateName.IRC_JOIN)), "irc.message.join");
        } else {
            plugin.logDebug("[broadcastIRCJoin] NOT broadcasting join message because irc-join is false.");
        }

        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_HERO_JOIN)) {
            Herochat.getChannelManager().getChannel(heroChannel.get(myChannel))
                    .sendRawMessage(plugin.tokenizer.ircChatToHeroChatTokenizer(nick,
                                    myChannel, plugin.getMsgTemplate(botNick,
                                            TemplateName.IRC_HERO_JOIN),
                                    Herochat.getChannelManager(),
                                    heroChannel.get(myChannel)));
        }
    }

    public void broadcastIRCPart(String nick, String myChannel) {
        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_PART)) {
            plugin.logDebug("[broadcastIRCPart]  Broadcasting join message because "
                    + TemplateName.IRC_PART + " is true.");
            plugin.getServer().broadcast(plugin.tokenizer.chatIRCTokenizer(nick,
                    myChannel, plugin.getMsgTemplate(botNick, TemplateName.IRC_PART)), "irc.message.part");
        } else {
            plugin.logDebug("[broadcastIRCPart] NOT broadcasting join message because "
                    + TemplateName.IRC_PART + " is false.");
        }

        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_HERO_PART)) {
            Herochat.getChannelManager().getChannel(heroChannel.get(myChannel))
                    .sendRawMessage(plugin.tokenizer.ircChatToHeroChatTokenizer(nick,
                                    myChannel, plugin.getMsgTemplate(botNick, TemplateName.IRC_HERO_PART),
                                    Herochat.getChannelManager(),
                                    heroChannel.get(myChannel)));
        }
    }

    public void broadcastIRCQuit(String nick, String myChannel, String reason) {

        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_QUIT)) {
            plugin.logDebug("[broadcastIRCQuit] Broadcasting quit message because "
                    + TemplateName.IRC_QUIT + " is true.");
            plugin.getServer().broadcast(plugin.tokenizer.chatIRCTokenizer(nick,
                    myChannel, plugin.getMsgTemplate(botNick, TemplateName.IRC_QUIT))
                    .replace("%NAME%", nick)
                    .replace("%REASON%", reason)
                    .replace("%CHANNEL%", myChannel), "irc.message.quit");
        } else {
            plugin.logDebug("[broadcastIRCQuit] NOT broadcasting quit message because "
                    + TemplateName.IRC_QUIT + " is false.");
        }

        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_HERO_QUIT)) {
            Herochat.getChannelManager().getChannel(heroChannel.get(myChannel))
                    .sendRawMessage(plugin.tokenizer.ircChatToHeroChatTokenizer(nick,
                                    myChannel, plugin.getMsgTemplate(botNick, TemplateName.IRC_HERO_QUIT),
                                    Herochat.getChannelManager(),
                                    heroChannel.get(myChannel)));
        }

    }

    // Broadcast topic changes from IRC
    /**
     *
     * @param nick
     * @param myChannel
     * @param message
     */
    public void broadcastIRCTopic(String nick, String myChannel, String message) {
        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_TOPIC)) {
            plugin.getServer().broadcast(plugin.tokenizer.chatIRCTokenizer(nick,
                    myChannel, plugin.getMsgTemplate(botNick, TemplateName.IRC_TOPIC)), "irc.message.topic");
        }

        if (enabledMessages.get(myChannel).contains(TemplateName.IRC_HERO_TOPIC)) {
            Herochat.getChannelManager().getChannel(heroChannel.get(myChannel))
                    .sendRawMessage(plugin.tokenizer.ircChatToHeroChatTokenizer(nick, myChannel,
                                    plugin.getMsgTemplate(botNick, TemplateName.IRC_HERO_TOPIC), message,
                                    Herochat.getChannelManager(),
                                    heroChannel.get(myChannel)));
        }
    }

    // Broadcast disconnect messages from IRC
    /**
     *
     * @param nick
     */
    public void broadcastIRCDisconnect(String nick) {
        plugin.getServer().broadcast("[" + nick + "] Disconnected from IRC server.", "irc.message.disconnect");
    }

    // Broadcast connect messages from IRC
    /**
     *
     * @param nick
     */
    public void broadcastIRCConnect(String nick) {
        plugin.getServer().broadcast("[" + nick + "] Connected to IRC server.", "irc.message.connect");
    }

    // Notify when players use commands
    /**
     *
     * @param player
     * @param cmd
     * @param params
     */
    public void commandNotify(Player player, String cmd, String params) {
        String msg = plugin.tokenizer.gameCommandToIRCTokenizer(player,
                plugin.getMsgTemplate(botNick, TemplateName.GAME_COMMAND), cmd, params);
        if (channelCmdNotifyMode.equalsIgnoreCase("msg")) {
            for (String recipient : channelCmdNotifyRecipients) {
                asyncIRCMessage(recipient, msg);
            }
        } else if (channelCmdNotifyMode.equalsIgnoreCase("ctcp")) {
            for (String recipient : channelCmdNotifyRecipients) {
                asyncCTCPMessage(recipient, msg);
            }
        }
    }

    // Notify when player goes AFK
    /**
     *
     * @param player
     * @param afk
     */
    public void essentialsAFK(Player player, boolean afk) {
        if (!bot.isConnected()) {
            return;
        }
        for (String channelName : botChannels) {
            if (enabledMessages.get(channelName).contains(TemplateName.GAME_AFK)) {
                if (!isPlayerInValidWorld(player, channelName)) {
                    return;
                }
                String template;
                if (afk) {
                    template = plugin.getMsgTemplate(botNick, TemplateName.PLAYER_AFK);
                } else {
                    template = plugin.getMsgTemplate(botNick, TemplateName.PLAYER_NOT_AFK);
                }
                plugin.logDebug("Sending AFK message to " + channelName);
                asyncIRCMessage(channelName, plugin.tokenizer.gamePlayerAFKTokenizer(player, template));
            }
        }
    }

    /**
     *
     * @param sender
     * @param nick
     * @param message
     */
    public void msgPlayer(Player sender, String nick, String message) {
        String msg = plugin.tokenizer.gameChatToIRCTokenizer(sender,
                plugin.getMsgTemplate(botNick, TemplateName.GAME_PCHAT), message);
        asyncIRCMessage(nick, msg);
    }

    /**
     *
     * @param nick
     * @param message
     */
    public void consoleMsgPlayer(String nick, String message) {
        String msg = plugin.tokenizer.gameChatToIRCTokenizer("console",
                plugin.getMsgTemplate(botNick, TemplateName.CONSOLE_CHAT), message);
        asyncIRCMessage(nick, msg);
    }

    /**
     *
     * @param player
     * @return
     */
    protected String getFactionName(Player player) {
        UPlayer uPlayer = UPlayer.get(player);
        Faction faction = uPlayer.getFaction();
        return faction.getName();
    }

    public boolean isConnected() {
        return connected;
    }

    public ImmutableSortedSet<Channel> getChannels() {
        if (bot.getNick().isEmpty()) {
            return ImmutableSortedSet.<Channel>naturalOrder().build();
        }
        return bot.getUserBot().getChannels();
    }

    public long getMessageDelay() {
        return bot.getConfiguration().getMessageDelay();
    }

    public String getMotd() {
        return bot.getServerInfo().getMotd();
    }

    public boolean isValidChannel(String channelName) {
        if (botChannels.contains(channelName.toLowerCase())) {
            return true;
        }
        plugin.logDebug("Channel " + channelName + " is not valid.");
        return false;
    }

    public PircBotX getBot() {
        return bot;
    }

    /**
     *
     * @param connected
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public String getFileName() {
        return fileName;
    }
}

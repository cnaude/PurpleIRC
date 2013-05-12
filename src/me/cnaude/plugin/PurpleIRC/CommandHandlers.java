package me.cnaude.plugin.PurpleIRC;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.pircbotx.Channel;

/**
 *
 * @author cnaude
 */
public class CommandHandlers implements CommandExecutor {

    private PIRCMain plugin;
    private final String invalidBotName = ChatColor.RED + "Invalid bot name: " + ChatColor.WHITE + "%BOT%"
            + ChatColor.RED + "'. Type '" + ChatColor.WHITE + "/irc listbots"
            + ChatColor.RED + "' to see valid bots.";
    private final String invalidChannel = ChatColor.RED + "Invalid channel: " + ChatColor.WHITE + "%CHANNEL%";
    private final String noPermission = ChatColor.RED + "You do not have permission to use this command.";

    public CommandHandlers(PIRCMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

        if (args.length >= 1) {
            String subCmd = args[0].toLowerCase();
            if (!sender.hasPermission("irc." + subCmd)) {
                sender.sendMessage(noPermission);
                return true;
            }
            if (subCmd.equalsIgnoreCase("listbots")) {
                sender.sendMessage(ChatColor.DARK_PURPLE + "-----[  " + ChatColor.WHITE + "IRC Bots"
                        + ChatColor.DARK_PURPLE + "   ]-----");
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    sender.sendMessage(ChatColor.DARK_PURPLE + "* " + ChatColor.WHITE + ircBot.bot.getName());
                    for (Channel channel : ircBot.bot.getChannels()) {
                        sender.sendMessage(ChatColor.DARK_PURPLE + "  - " + ChatColor.WHITE + channel.getName());
                    }
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("connect")) {
                if (args.length == 1) {
                    for (PurpleBot ircBot : plugin.ircBots.values()) {
                        ircBot.asyncConnect(sender, true);
                    }
                } else if (args.length == 2) {
                    String bot = args[1];
                    if (plugin.ircBots.containsKey(bot)) {
                        plugin.ircBots.get(bot).asyncConnect(sender, true);
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc connect ([bot])");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("reloadbot")) {
                if (args.length == 2) {
                    String bot = args[1];
                    if (plugin.ircBots.containsKey(bot)) {
                        plugin.ircBots.get(bot).reload(sender);
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc reloadbot [bot]");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("reloadbots")) {
                if (args.length == 1) {
                    for (PurpleBot ircBot : plugin.ircBots.values()) {
                        ircBot.reload(sender);
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc reloadbots");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("reloadbotconfig")) {
                if (args.length == 2) {
                    String bot = args[1];
                    if (plugin.ircBots.containsKey(bot)) {
                        plugin.ircBots.get(bot).reloadConfig(sender);
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc reloadbotconfig [bot]");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("reloadbotconfigs")) {
                if (args.length == 1) {
                    for (PurpleBot ircBot : plugin.ircBots.values()) {
                        ircBot.reloadConfig(sender);
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc reloadbotconfigs");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("reloadconfig")) {
                if (args.length == 1) {
                    plugin.reloadMainConfig(sender);
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc reloadconfig");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("disconnect")) {
                if (args.length == 1) {
                    for (PurpleBot ircBot : plugin.ircBots.values()) {
                        ircBot.quit(sender);
                    }
                } else if (args.length == 2) {
                    String bot = args[1];
                    if (plugin.ircBots.containsKey(bot)) {
                        plugin.ircBots.get(bot).quit(sender);
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc disconnect ([bot])");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("topic")) {
                if (args.length == 1) {
                    for (PurpleBot ircBot : plugin.ircBots.values()) {
                        ircBot.sendTopic(sender);
                        sender.sendMessage(ChatColor.WHITE + "To change the topic: " + ChatColor.GOLD + "/irc topic [bot] [channel] [topic]");
                    }
                } else if (args.length >= 4) {
                    String bot = args[1];
                    String channelName = args[2];
                    if (plugin.ircBots.containsKey(bot)) {
                        String topic = "";
                        for (int i = 3; i < args.length; i++) {
                            topic = topic + " " + args[i];
                        }
                        plugin.ircBots.get(bot).changeTopic(channelName, topic.substring(1), sender);
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc topic [bot] [channel] [topic]");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("say")) {
                if (args.length >= 4) {
                    String bot = args[1];
                    String channelName = args[2];
                    if (plugin.ircBots.containsKey(bot)) {
                        String msg = "";
                        for (int i = 3; i < args.length; i++) {
                            msg = msg + " " + args[i];
                        }
                        plugin.ircBots.get(bot).bot.sendMessage(channelName, msg.substring(1));
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc say [bot] [channel] [message]");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("send")) {
                if (args.length >= 2) {
                    int msgIdx = 1;
                    String channelName = null;
                    List<PurpleBot> myBots = new ArrayList<PurpleBot>();                    
                    if (plugin.ircBots.containsKey(args[1])) {
                        myBots.add(plugin.ircBots.get(args[1]));
                        msgIdx = 2;
                        if (args.length >= 3) {
                            if (plugin.ircBots.get(args[1]).botChannels.contains(args[2])) {
                                channelName = args[2];
                            }
                        }
                    } else {
                        myBots.addAll(plugin.ircBots.values());
                    }
                    for (PurpleBot ircBot : myBots) {
                        String msg = "";
                        for (int i = msgIdx; i < args.length; i++) {
                            msg = msg + " " + args[i];
                        }                        
                        if (channelName == null) {
                            for (String c : ircBot.botChannels) {
                                if (sender instanceof Player) {
                                    ircBot.gameChat((Player) sender, c, msg.substring(1));
                                } else {
                                    ircBot.consoleChat(c, msg.substring(1));
                                }
                            }
                        } else {
                            if (sender instanceof Player) {
                                ircBot.gameChat((Player) sender, channelName, msg.substring(1));
                            } else {
                                ircBot.consoleChat(channelName, msg.substring(1));
                            }
                        }
                        
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc send ([bot]) ([channel]) [message]");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("op")) {
                if (args.length >= 4) {
                    String bot = args[1];
                    String channelName = args[2];
                    if (plugin.ircBots.containsKey(bot)) {
                        for (int i = 2; i < args.length; i++) {
                            // #channel, user
                            plugin.ircBots.get(bot).op(channelName, args[i]);
                        }
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc op [bot] [channel] [user(s)]");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("addop")) {
                if (args.length == 4) {
                    String bot = args[1];
                    String channelName = args[2];
                    if (plugin.ircBots.containsKey(bot)) {
                        // #channel, user
                        plugin.ircBots.get(bot).addOp(channelName, args[3], sender);
                        plugin.ircBots.get(bot).opFriends(channelName);
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc addop [bot] [channel] [user mask]");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("listops")) {
                if (args.length == 3) {
                    String bot = args[1];
                    String channelName = args[2];
                    if (plugin.ircBots.containsKey(bot)) {
                        if (plugin.ircBots.get(bot).opsList.containsKey(channelName)) {
                            sender.sendMessage(ChatColor.DARK_PURPLE + "-----[  " + ChatColor.WHITE + channelName
                                    + ChatColor.DARK_PURPLE + " - " + ChatColor.WHITE + "Auto Op Masks" + ChatColor.DARK_PURPLE + " ]-----");
                            for (String userMask : plugin.ircBots.get(bot).opsList.get(channelName)) {
                                sender.sendMessage(" - " + userMask);
                            }
                        } else {
                            sender.sendMessage(invalidChannel.replaceAll("%CHANNEL%", channelName));
                        }
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc listops [bot] [channel]");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("remove")) {
                if (args.length == 4) {
                    String bot = args[1];
                    String channel = args[2];
                    if (plugin.ircBots.containsKey(bot)) {
                        // #channel, user
                        plugin.ircBots.get(bot).removeOp(channel, args[3], sender);
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc removeop [bot] [channel] [user mask]");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("mute")) {
                if (args.length >= 4) {
                    String bot = args[1];
                    String channel = args[2];
                    if (plugin.ircBots.containsKey(bot)) {
                        for (int i = 2; i < args.length; i++) {
                            // #channel, user
                            plugin.ircBots.get(bot).mute(channel, sender, args[i]);
                        }
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc mute [bot] [channel] [user(s)]");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("save")) {
                if (args.length == 1) {
                    for (PurpleBot ircBot : plugin.ircBots.values()) {
                        ircBot.saveConfig(sender);
                    }
                } else if (args.length >= 2) {
                    String bot = args[1];
                    if (plugin.ircBots.containsKey(bot)) {
                        plugin.ircBots.get(bot).saveConfig(sender);
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc save ([bot])");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("server")) {
                if (args.length >= 3) {
                    String bot = args[1];
                    String server = args[2];
                    if (plugin.ircBots.containsKey(bot)) {
                        if (args.length == 3) {
                            plugin.ircBots.get(bot).setServer(sender, server);
                        } else if (args.length == 4) {
                            plugin.ircBots.get(bot).setServer(sender, server, Boolean.parseBoolean(args[3]));
                        }
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc server [bot] [server] ([true|false])");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("nick")) {
                if (args.length == 3) {
                    String bot = args[1];
                    String nick = args[2];
                    if (plugin.ircBots.containsKey(bot)) {
                        if (plugin.ircBots.containsKey(nick)) {
                            sender.sendMessage(ChatColor.RED + "There is already a bot with that nick!");
                            return true;
                        } else {
                            plugin.ircBots.get(bot).changeNick(sender, nick);
                            PurpleBot ircBot = plugin.ircBots.remove(bot);
                            plugin.ircBots.put(nick, ircBot);
                            boolean isConnected = plugin.botConnected.remove(bot);
                            plugin.botConnected.put(nick, isConnected);
                        }
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc nick [bot] [nick]");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("login")) {
                if (args.length == 3) {
                    String bot = args[1];
                    String login = args[2];
                    if (plugin.ircBots.containsKey(bot)) {
                        plugin.ircBots.get(bot).changeLogin(sender, login);
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc login [bot] [login]");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("list")) {
                if (args.length == 1) {
                    for (PurpleBot ircBot : plugin.ircBots.values()) {
                        ircBot.sendUserList(sender);
                    }
                } else if (args.length > 1) {
                    String bot = args[1];
                    if (plugin.ircBots.containsKey(bot)) {
                        PurpleBot ircBot = plugin.ircBots.get(bot);
                        if (args.length > 2) {
                            ircBot.sendUserList(sender, args[2]);
                        } else {
                            ircBot.sendUserList(sender);
                        }
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("whois")) {
                if (args.length == 2) {
                    String nick = args[1];
                    for (PurpleBot ircBot : plugin.ircBots.values()) {
                        ircBot.sendUserWhois(sender, nick);
                    }
                } else if (args.length == 3) {
                    String bot = args[1];
                    String nick = args[2];
                    if (plugin.ircBots.containsKey(bot)) {
                        PurpleBot ircBot = plugin.ircBots.get(bot);
                        ircBot.sendUserWhois(sender, nick);
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc whois [bot] [nick]");
                }
                return true;
            }
        }
        return false;
    }
}

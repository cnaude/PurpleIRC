package me.cnaude.plugin.PurpleIRC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.pircbotx.Channel;

/**
 *
 * @author cnaude
 */
public class CommandHandlers implements CommandExecutor {

    private PIRCMain plugin;
    
    private String invalidBotName = ChatColor.RED + "Invalid bot name: " + ChatColor.WHITE + "%BOT%"
                                + ChatColor.RED + "'. Type '" + ChatColor.WHITE + "/irc listbots" + ChatColor.RED + "' to see valid bots.";
    private String invalidChannel = ChatColor.RED + "Invalid channel: " + ChatColor.WHITE + "%CHANNEL%";                                

    public CommandHandlers(PIRCMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String noPermission = ChatColor.RED + "You do not have permission to use this command.";
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
                    String channel = args[2];
                    if (plugin.ircBots.containsKey(bot)) {
                        String topic = "";
                        for (int i = 3; i < args.length; i++) {
                            topic = topic + " " + args[i];
                        }
                        plugin.ircBots.get(bot).changeTopic(channel, topic.substring(1), sender);
                    } else {
                        sender.sendMessage(invalidBotName.replaceAll("%BOT%", bot));
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc topic [bot] [channel] [topic]");
                }
                return true;
            }
            if (subCmd.equalsIgnoreCase("op")) {
                if (args.length >= 4) {
                    String bot = args[1];
                    String channel = args[2];
                    if (plugin.ircBots.containsKey(bot)) {
                        for (int i = 2; i < args.length; i++) {
                            // #channel, user
                            plugin.ircBots.get(bot).op(channel, args[i]);
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
                    String channel = args[2];                    
                    if (plugin.ircBots.containsKey(bot)) {                        
                        // #channel, user
                        plugin.ircBots.get(bot).addOp(channel, args[3], sender);                        
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
                            plugin.botConnected.put(nick,isConnected);
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
        }
        return false;
    } 
}


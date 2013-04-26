/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jibble.pircbot.User;

/**
 *
 * @author cnaude
 */
public class PIRCCommands implements CommandExecutor {

    private PIRCMain plugin;        

    public PIRCCommands(PIRCMain plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        String noPermission = ChatColor.RED + "You do not have permission to use this command.";
        if (args.length >= 1) {
            String subCmd = args[0].toLowerCase();
            if (!sender.hasPermission("irc." + subCmd)) {
                sender.sendMessage(noPermission);
            }
            if (subCmd.equals("listbots")) {
                sender.sendMessage(ChatColor.RED + "-----[  " + ChatColor.WHITE + "IRC Bots"
                        + ChatColor.RED + "   ]-----");
                for (PIRCBot ircBot : plugin.ircBots.values()) {
                    sender.sendMessage(ChatColor.RED + "* " + ChatColor.WHITE + ircBot.getName());
                    for (String channel : ircBot.getChannels()) {
                        sender.sendMessage(ChatColor.RED + "  - " + ChatColor.WHITE + channel);
                    }
                }
            }
            if (subCmd.equals("connect")) {
                if (args.length == 1) {                    
                    for (PIRCBot ircBot : plugin.ircBots.values()) {
                        ircBot.connect(sender);
                    }
                } else if (args.length == 2) {                    
                    String bot = args[1];                    
                    if (plugin.ircBots.containsKey(bot)) {    
                        plugin.ircBots.get(bot).connect(sender);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid bot name: '" + ChatColor.WHITE + bot 
                                + ChatColor.RED + "'. Type '/irc listbots' to see valid bots.");
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc connect ([bot])");
                }
            }
            if (subCmd.equals("disconnect")) {
                if (args.length == 1) {                    
                    for (PIRCBot ircBot : plugin.ircBots.values()) {
                        ircBot.quit(sender);
                    }
                } else if (args.length == 2) {                    
                    String bot = args[1];                    
                    if (plugin.ircBots.containsKey(bot)) {    
                        plugin.ircBots.get(bot).quit(sender);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid bot name: '" + ChatColor.WHITE + bot 
                                + ChatColor.RED + "'. Type '/irc listbots' to see valid bots.");
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc disconnect ([bot])");
                }
            }
            if (subCmd.equals("topic")) {
                if (args.length == 1) {                    
                    for (PIRCBot ircBot : plugin.ircBots.values()) {
                        ircBot.sendTopic(sender);
                    }
                } else if (args.length >= 2) {                    
                    String bot = args[1];
                    String channel = args[2];
                    if (plugin.ircBots.containsKey(bot)) {    
                        String topic = "";
                        for (int i = 2; i < args.length; i++) {                                              
                            topic = topic + " " + args[i];                            
                        }
                        plugin.ircBots.get(bot).setTopic(channel, topic.substring(1));
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid bot name: '" + ChatColor.WHITE + bot 
                                + ChatColor.RED + "'. Type '/irc listbots' to see valid bots.");
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc topic [bot] [channel] [topic]");
                }
            }
            if (subCmd.equals("op")) {
                if (args.length >= 2) {                    
                    String bot = args[1];
                    String channel = args[2];
                    if (plugin.ircBots.containsKey(bot)) {                           
                        for (int i = 2; i < args.length; i++) {                                              
                            // #channel, user
                            plugin.ircBots.get(bot).op(channel, args[i]);
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid bot name: '" + ChatColor.WHITE + bot 
                                + ChatColor.RED + "'. Type '/irc listbots' to see valid bots.");
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc op [bot] [channel] [user(s)]");
                }
            }
            if (subCmd.equals("deop")) {
                if (args.length >= 2) {                    
                    String bot = args[1];
                    String channel = args[2];
                    if (plugin.ircBots.containsKey(bot)) {                           
                        for (int i = 2; i < args.length; i++) {                                              
                            // #channel, user
                            plugin.ircBots.get(bot).deOp(channel, args[i]);
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid bot name: '" + ChatColor.WHITE + bot 
                                + ChatColor.RED + "'. Type '/irc listbots' to see valid bots.");
                    }
                } else {
                    sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc deop [bot] [channel] [user(s)]");
                }
            }
            if (subCmd.equals("list")) {
                if (args.length == 1) {  
                    for (PIRCBot ircBot : plugin.ircBots.values()) {
                        ircBot.sendUserList(sender);                        
                    }
                } else if (args.length > 1) {
                    String bot = args[1];
                    if (plugin.ircBots.containsKey(bot)) {
                        PIRCBot ircBot = plugin.ircBots.get(bot);                       
                        if (args.length > 2) {
                            ircBot.sendUserList(sender,args[2]);
                        } else {
                            ircBot.sendUserList(sender);
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Invalid bot name: '" + ChatColor.WHITE + bot 
                                + ChatColor.RED + "'. Type '/irc listbots' to see valid bots.");
                    }
                }
            }
        }
        return true;
    }
}

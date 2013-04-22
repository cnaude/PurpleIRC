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
                }
            }
            if (subCmd.equals("disconnect")) {
                if (args.length == 1) {                    
                    for (PIRCBot ircBot : plugin.ircBots.values()) {
                        ircBot.disconnect(sender);
                    }
                }
            }
            if (subCmd.equals("list")) {
                if (args.length == 1) {                    
                    for (PIRCBot ircBot : plugin.ircBots.values()) {
                        sender.sendMessage(ChatColor.RED + "-----[  " 
                                + ChatColor.WHITE + "IRC Users"
                                + " - " + ircBot.getName() + ChatColor.RED + " ]-----");                        
                        for (String channel : ircBot.getChannels()) {
                            sender.sendMessage(ChatColor.RED + " " + ChatColor.GRAY + channel);
                            for (User user : ircBot.getUsers(channel)) {
                                sender.sendMessage(ChatColor.RED + "  " + ChatColor.WHITE + user.toString());
                            }
                        }
                    }
                } else if (args.length > 1) {
                    if (plugin.ircBots.containsKey(args[1])) {
                        PIRCBot ircBot = plugin.ircBots.get(args[1]);
                        sender.sendMessage(ChatColor.RED + "* " + ChatColor.WHITE + ircBot.getName());
                        if (args.length > 2) {
                            for (String channel : ircBot.getChannels()) {
                                if (args[2].equalsIgnoreCase(channel)) {
                                    sender.sendMessage(ChatColor.RED + " " + ChatColor.GRAY + channel);
                                    for (User user : ircBot.getUsers(channel)) {
                                        sender.sendMessage(ChatColor.RED + "  " + ChatColor.WHITE + user.toString());
                                    }
                                }
                            }
                        } else {
                            for (String channel : ircBot.getChannels()) {
                                sender.sendMessage(ChatColor.RED + " " + ChatColor.GRAY + channel);
                                for (User user : ircBot.getUsers(channel)) {
                                    sender.sendMessage(ChatColor.RED + "  " + ChatColor.WHITE + user.toString());
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}

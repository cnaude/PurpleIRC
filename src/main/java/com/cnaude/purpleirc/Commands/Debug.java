/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class Debug {

    private final PurpleIRC plugin;

    public Debug(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void dispatch(CommandSender sender, String[] args) {
        String usage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc debug ([t|f])";
        if (args.length == 1) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "Debug mode is currently "
                    + ChatColor.WHITE + plugin.debugMode());
        } else if (args.length == 2) {
            if (args[1].startsWith("t")) {
                plugin.debugMode(true);
            } else if (args[1].startsWith("f")) {
                plugin.debugMode(false);
            } else {
                sender.sendMessage(usage);
            }
            sender.sendMessage(ChatColor.DARK_PURPLE + "Debug mode is now "
                    + ChatColor.WHITE + plugin.debugMode());
        } else {
            sender.sendMessage(usage);
        }
    }
}

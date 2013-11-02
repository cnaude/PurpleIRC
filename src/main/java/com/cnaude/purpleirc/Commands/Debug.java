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
public class Debug implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([t|f])";
    private final String desc = "Enable or disable debug mode.";
    private final String name = "debug";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 
    
    /**
     *
     * @param plugin
     */
    public Debug(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {        
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
            sender.sendMessage(fullUsage);
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public String usage() {
        return usage;
    }
}

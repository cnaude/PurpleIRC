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
public class Help implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([command])";
    private final String desc = "Display all available commands.";
    private final String name = "help";    

    /**
     *
     * @param plugin
     */
    public Help(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&5-----[  &fPurpleIRC&5 - &f" + plugin.getServer().getPluginManager()
                .getPlugin("PurpleIRC").getDescription().getVersion() + "&5 ]-----"));
        for (String s : plugin.commandHandlers.sortedCommands) {
            if (plugin.commandHandlers.commands.containsKey(s)) {
                String n = plugin.commandHandlers.commands.get(s).name();
                String d = plugin.commandHandlers.commands.get(s).desc();
                String u = plugin.commandHandlers.commands.get(s).usage();
                String h = ChatColor.translateAlternateColorCodes('&', "&5/irc "
                        + n + " &6" + u + " &f- " + d);
                sender.sendMessage(h);
            }
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

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
public class Mute implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[bot] [channel] [user(s)]";
    private final String desc = "Mute IRC user(s) in a channel.";
    private final String name = "mute";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 

    /**
     *
     * @param plugin
     */
    public Mute(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length >= 4) {
            String bot = args[1];
            String channel = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                for (int i = 2; i < args.length; i++) {
                    // #channel, user
                    plugin.ircBots.get(bot).mute(channel, sender, args[i]);
                }
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
            }
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

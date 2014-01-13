/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleIRC;
import com.cnaude.purpleirc.Utilities.BotsAndChannels;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class UnMute implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([bot]) ([channel]) [user(s)]";
    private final String desc = "Unmute IRC user(s) in a channel.";
    private final String name = "unmute";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 

    /**
     *
     * @param plugin
     */
    public UnMute(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        BotsAndChannels bac;
        int idx;

        if (args.length >= 4) {
            bac = new BotsAndChannels(plugin, sender, args[1], args[2]);
            idx = 3;
        } else if (args.length == 2) {
            bac = new BotsAndChannels(plugin, sender);
            idx = 1;
        } else {
            sender.sendMessage(fullUsage);
            return;
        }
        if (bac.bot.size() > 0 && bac.channel.size() > 0) {
            for (String botName : bac.bot) {
                for (String channelName : plugin.ircBots.get(botName).botChannels) {
                    for (int i = idx; i < args.length; i++) {                        
                        plugin.ircBots.get(botName).unMute(channelName, sender, args[i]);
                    }
                }
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

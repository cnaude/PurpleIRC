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
public class Op implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[bot] [channel] [user(s)]";
    private final String desc = "Op an IRC user in a channel.";
    private final String name = "op";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 

    /**
     *
     * @param plugin
     */
    public Op(PurpleIRC plugin) {
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
            String bot = plugin.botify(args[1]);
            String channelName = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                for (int i = 3; i < args.length; i++) {
                    // #channel, user
                    plugin.ircBots.get(bot).op(channelName, args[i]);
                    sender.sendMessage("Giving operator status to " 
                            + ChatColor.WHITE + args[i] 
                            + ChatColor.RESET + " on " 
                            + ChatColor.WHITE + channelName);
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

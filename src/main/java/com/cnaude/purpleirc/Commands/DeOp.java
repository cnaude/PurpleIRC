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
public class DeOp {

    private final PurpleIRC plugin;

    public DeOp(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void dispatch(CommandSender sender, String[] args) {
        if (args.length >= 4) {
            String bot = args[1];
            String channelName = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                for (int i = 3; i < args.length; i++) {
                    // #channel, user
                    plugin.ircBots.get(bot).deOp(channelName, args[i]);
                    sender.sendMessage(ChatColor.WHITE + "De-opping " + args[i] + " in " + channelName + "...");
                }
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc deop [bot] [channel] [user(s)]");
        }
    }
}

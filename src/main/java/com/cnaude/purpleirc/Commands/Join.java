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
public class Join {

    private final PurpleIRC plugin;

    public Join(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void dispatch(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            String bot = args[1];
            String channelName = args[2];
            String password = "";
            if (args.length >= 4) {
                for (int i = 3; i < args.length; i++) {
                    password = password + " " + args[i];
                }
            }
            if (plugin.ircBots.containsKey(bot)) {
                // #channel, password
                plugin.ircBots.get(bot).bot.joinChannel(channelName, password);
                sender.sendMessage(ChatColor.WHITE + "Joining " + channelName + "...");
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc join [bot] [channel]");
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class ListOps {

    private final PurpleIRC plugin;

    public ListOps(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 3) {
            String bot = args[1];
            String channelName = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                if (plugin.ircBots.get(bot).opsList.containsKey(channelName)) {
                    sender.sendMessage(ChatColor.DARK_PURPLE + "-----[  " + ChatColor.WHITE + channelName
                            + ChatColor.DARK_PURPLE + " - " + ChatColor.WHITE + "Auto Op Masks" + ChatColor.DARK_PURPLE + " ]-----");
                    for (String userMask : plugin.ircBots.get(bot).opsList.get(channelName)) {
                        sender.sendMessage(" - " + userMask);
                    }
                } else {
                    sender.sendMessage(plugin.invalidChannel.replace("%CHANNEL%", channelName));
                }
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc listops [bot] [channel]");
        }
    }
}

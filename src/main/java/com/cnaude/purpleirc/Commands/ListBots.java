/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.pircbotx.Channel;

/**
 *
 * @author cnaude
 */
public class ListBots {

    private final PurpleIRC plugin;

    public ListBots(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void dispatch(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.DARK_PURPLE + "-----[  " + ChatColor.WHITE + "IRC Bots"
                + ChatColor.DARK_PURPLE + "   ]-----");
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            sender.sendMessage(ChatColor.DARK_PURPLE + "* " + ChatColor.WHITE + ircBot.bot.getName());
            for (Channel channel : ircBot.bot.getChannels()) {
                sender.sendMessage(ChatColor.DARK_PURPLE + "  - " + ChatColor.WHITE + channel.getName());
            }
        }
    }
}

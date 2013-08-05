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
public class ReloadBot {

    private final PurpleIRC plugin;

    public ReloadBot(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 2) {
            String bot = args[1];
            if (plugin.ircBots.containsKey(bot)) {
                plugin.ircBots.get(bot).reload(sender);
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc reloadbot [bot]");
        }
    }
}

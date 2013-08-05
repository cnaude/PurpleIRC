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
public class ReloadBotConfigs {

    private final PurpleIRC plugin;

    public ReloadBotConfigs(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 1) {
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                ircBot.reloadConfig(sender);
            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc reloadbotconfigs");
        }
    }
}

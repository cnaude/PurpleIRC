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
public class List {

    private final PurpleIRC plugin;

    public List(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 1) {
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                ircBot.sendUserList(sender);
            }
        } else if (args.length > 1) {
            String bot = args[1];
            if (plugin.ircBots.containsKey(bot)) {
                PurpleBot ircBot = plugin.ircBots.get(bot);
                if (args.length > 2) {
                    ircBot.sendUserList(sender, args[2]);
                } else {
                    ircBot.sendUserList(sender);
                }
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
            }
        }
    }
}

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
public class Whois {

    private final PurpleIRC plugin;

    public Whois(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 2) {
            String nick = args[1];
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                ircBot.sendUserWhois(sender, nick);
            }
        } else if (args.length == 3) {
            String bot = args[1];
            String nick = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                PurpleBot ircBot = plugin.ircBots.get(bot);
                ircBot.sendUserWhois(sender, nick);
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc whois [bot] [nick]");
        }
    }
}

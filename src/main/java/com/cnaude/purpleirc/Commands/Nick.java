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
public class Nick {

    private final PurpleIRC plugin;

    public Nick(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 3) {
            String bot = args[1];
            String nick = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                if (plugin.ircBots.containsKey(nick)) {
                    sender.sendMessage(ChatColor.RED + "There is already a bot with that nick!");
                } else {
                    plugin.ircBots.get(bot).changeNick(sender, nick);
                    PurpleBot ircBot = plugin.ircBots.remove(bot);
                    plugin.ircBots.put(nick, ircBot);
                    boolean isConnected = plugin.botConnected.remove(bot);
                    plugin.botConnected.put(nick, isConnected);
                }
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc nick [bot] [nick]");
        }
    }
}

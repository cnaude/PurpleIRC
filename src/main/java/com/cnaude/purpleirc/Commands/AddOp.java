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
public class AddOp {

    private final PurpleIRC plugin;

    public AddOp(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 4) {
            String bot = args[1];
            String channelName = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                // #channel, user
                plugin.ircBots.get(bot).addOp(channelName, args[3], sender);
                plugin.ircBots.get(bot).opFriends(channelName);
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc addop [bot] [channel] [user mask]");
        }
    }
}

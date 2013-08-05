/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.pircbotx.Channel;

/**
 *
 * @author cnaude
 */
public class Send {

    private final PurpleIRC plugin;

    public Send(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void dispatch(CommandSender sender, String[] args) {
        if (args.length >= 2) {
            int msgIdx = 1;
            String channelName = null;
            List<PurpleBot> myBots = new ArrayList<PurpleBot>();
            if (plugin.ircBots.containsKey(args[1])) {
                myBots.add(plugin.ircBots.get(args[1]));
                msgIdx = 2;
                if (args.length >= 3) {
                    if (plugin.ircBots.get(args[1]).botChannels.contains(args[2])) {
                        channelName = args[2];
                    }
                }
            } else {
                myBots.addAll(plugin.ircBots.values());
            }
            for (PurpleBot ircBot : myBots) {
                String msg = "";
                for (int i = msgIdx; i < args.length; i++) {
                    msg = msg + " " + args[i];
                }
                if (channelName == null) {
                    for (String c : ircBot.botChannels) {
                        if (sender instanceof Player) {
                            ircBot.gameChat((Player) sender, c, msg.substring(1));
                        } else {
                            ircBot.consoleChat(c, msg.substring(1));
                        }
                    }
                } else {
                    if (sender instanceof Player) {
                        ircBot.gameChat((Player) sender, channelName, msg.substring(1));
                    } else {
                        ircBot.consoleChat(channelName, msg.substring(1));
                    }
                }

            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc send ([bot]) ([channel]) [message]");
        }
    }
}

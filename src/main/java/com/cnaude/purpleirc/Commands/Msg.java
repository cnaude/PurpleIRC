/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.pircbotx.User;

/**
 *
 * @author cnaude
 */
public class Msg {

    private final PurpleIRC plugin;
    private final String usage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc msg ([bot]) [user] [message]";

    /**
     *
     * @param plugin
     */
    public Msg(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            plugin.logDebug("Dispatching msg command...");
            int msgIdx = 2;
            String nick;
            java.util.List<PurpleBot> myBots = new ArrayList<PurpleBot>();
            if (plugin.ircBots.containsKey(args[1])) {
                myBots.add(plugin.ircBots.get(args[1]));
                msgIdx = 3;
                nick = args[2];
            } else {
                myBots.addAll(plugin.ircBots.values());
                nick = args[1];
            }
            
            if (msgIdx == 3 && args.length <= 3) {
                sender.sendMessage(usage);
                return;
            }

            for (PurpleBot ircBot : myBots) {
                User user = ircBot.bot.getUser(nick);
                if (user != null) {
                    String msg = "";
                    for (int i = msgIdx; i < args.length; i++) {
                        msg = msg + " " + args[i];
                    }
                    if (sender instanceof Player) {
                        ircBot.msgPlayer((Player) sender, nick, msg.substring(1));
                    } else {
                        ircBot.consoleMsgPlayer(nick, msg.substring(1));
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid IRC nick: " + ChatColor.WHITE + nick);
                }
            }
        } else {
            sender.sendMessage(usage);
        }
    }
}

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
public class Notice {

    private final PurpleIRC plugin;
    private final String usage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc notice ([bot]) [nick|channel] [message]";

    public Notice(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void dispatch(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            plugin.logDebug("Dispatching notice command...");
            int msgIdx = 2;
            String target;
            java.util.List<PurpleBot> myBots = new ArrayList<PurpleBot>();
            if (plugin.ircBots.containsKey(args[1])) {
                myBots.add(plugin.ircBots.get(args[1]));
                msgIdx = 3;
                target = args[2];
            } else {
                myBots.addAll(plugin.ircBots.values());
                target = args[1];
            }

            if (msgIdx == 3 && args.length <= 3) {
                sender.sendMessage(usage);
                return;
            }

            for (PurpleBot ircBot : myBots) {
                String msg = "";
                for (int i = msgIdx; i < args.length; i++) {
                    msg = msg + " " + args[i];
                }
                ircBot.bot.sendNotice(target, msg.substring(1));
                sender.sendMessage("Sent notice message \"" + msg.substring(1) + "\" to \"" + target + "\"");
            }
        } else {
            sender.sendMessage(usage);
        }
    }
}

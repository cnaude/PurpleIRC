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
public class Topic {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public Topic(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 1) {
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                ircBot.sendTopic(sender);
                sender.sendMessage(ChatColor.WHITE + "To change the topic: " + ChatColor.GOLD + "/irc topic [bot] [channel] [topic]");
            }
        } else if (args.length >= 4) {
            String bot = args[1];
            String channelName = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                String topic = "";
                for (int i = 3; i < args.length; i++) {
                    topic = topic + " " + args[i];
                }
                plugin.ircBots.get(bot).changeTopic(channelName, topic.substring(1), sender);
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc topic [bot] [channel] [topic]");
        }
    }
}

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
public class Join implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[bot] [channel] ([password])";
    private final String desc = "Join IRC channel.";
    private final String name = "join";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 

    /**
     *
     * @param plugin
     */
    public Join(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length >= 3) {
            String bot = plugin.botify(args[1]);
            String channelName = args[2];
            String password = "";
            if (args.length >= 4) {
                for (int i = 3; i < args.length; i++) {
                    password = password + " " + args[i];
                }
            }
            if (plugin.ircBots.containsKey(bot)) {
                plugin.ircBots.get(bot).asyncJoinChannel(channelName, password);
                sender.sendMessage(ChatColor.WHITE + "Joining " + channelName + "...");
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
            }
        } else {
            sender.sendMessage(fullUsage);
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public String usage() {
        return usage;
    }
}

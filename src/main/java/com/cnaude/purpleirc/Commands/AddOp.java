/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.pircbotx.Channel;
import org.pircbotx.User;

/**
 *
 * @author cnaude
 */
public class AddOp implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[bot] [channel] [user mask]";
    private final String desc = "Add IRC users to IRC auto op list.";
    private final String name = "addop";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage;

    /**
     *
     * @param plugin
     */
    public AddOp(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 4) {
            String bot = args[1];
            String channelName = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                // #channel, user
                String nick = args[3];
                String mask = nick;
                Channel channel = plugin.ircBots.get(bot).getChannel(channelName);
                if (channel != null) {
                    for (User user : channel.getUsers()) {
                        if (user.getNick().equalsIgnoreCase(nick)) {
                            mask = "*!*" + user.getLogin() + "@" + user.getHostmask();
                        }
                    }
                }
                if (mask.split("[\\!\\@]", 3).length == 3) {
                    plugin.ircBots.get(bot).addOp(channelName, mask, sender);
                    plugin.ircBots.get(bot).opFriends(channelName);
                } else {
                    sender.sendMessage(ChatColor.RED + "Invalid user or mask: " 
                            + ChatColor.WHITE + mask);
                }
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

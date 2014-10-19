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
public class Server implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[bot] [server] ([true|false])";
    private final String desc = "Set IRC server hostname. Optionally set autoconnect.";
    private final String name = "server";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 

    /**
     *
     * @param plugin
     */
    public Server(PurpleIRC plugin) {
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
            String server = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                if (args.length == 3) {
                    plugin.ircBots.get(bot).setServer(sender, server);
                } else if (args.length == 4) {
                    plugin.ircBots.get(bot).setServer(sender, server, Boolean.parseBoolean(args[3]));
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

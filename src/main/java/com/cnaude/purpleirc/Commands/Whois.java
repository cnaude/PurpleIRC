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
public class Whois implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([bot]) [nick]";
    private final String desc = "Get whois info for IRC user.";
    private final String name = "whois";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 

    /**
     *
     * @param plugin
     */
    public Whois(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 2) {
            String nick = args[1];
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                ircBot.sendUserWhois(sender, nick);
            }
        } else if (args.length == 3) {
            String bot = plugin.botify(args[1]);
            String nick = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                PurpleBot ircBot = plugin.ircBots.get(bot);
                ircBot.sendUserWhois(sender, nick);
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

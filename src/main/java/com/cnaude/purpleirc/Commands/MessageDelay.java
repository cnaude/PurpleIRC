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
public class MessageDelay {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public MessageDelay(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 3) {
            if (args[2].matches("\\d+")) {
                String bot = args[1];
                if (plugin.ircBots.containsKey(bot)) {
                    long delay = Long.parseLong(args[2]);
                    plugin.ircBots.get(bot).setIRCDelay(sender, delay);
                } else {
                    sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
                }
            } else {
                sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc messagedelay [bot] ([milliseconds])");
            }
        } else if (args.length == 2) {
            String bot = args[1];
            if (plugin.ircBots.containsKey(bot)) {
                sender.sendMessage(ChatColor.WHITE + "IRC message delay is currently "
                        + plugin.ircBots.get(bot).bot.getMessageDelay() + " ms.");
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", bot));
            }
        } else {
            sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc messagedelay [bot] ([milliseconds])");
        }
    }
}

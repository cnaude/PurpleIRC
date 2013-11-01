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
public class Help implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([bot])";
    private final String desc = "Add IRC users to IRC auto op list.";
    private final String name = "connect";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 

    /**
     *
     * @param plugin
     */
    public Help(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {        
        String help = ChatColor.translateAlternateColorCodes('&', 
                "&5-----[  &fPurpleIRC&5 - &f"+ plugin.getServer().getPluginManager()
                .getPlugin("PurpleIRC").getDescription().getVersion()+"&5 ]-----"
                + "&5/irc reload &f- Reload the plugin (disable then enable)\n"
                + "&5/irc reloadconfig &f- Reload config.yml\n"
                + "&5/irc save &6([bot]) &f- Save bot configuration(s) to disk\n"
                + "&5/irc reloadbot &6[bot] &f- Reload the bot config and reconnect\n"
                + "&5/irc reloadbots &f- Reload all bot configs and reconnect\n"
                + "&5/irc reloadbotconfig &6[bot] &f- Reload bot config without reconnecting\n"
                + "&5/irc reloadbotconfigs &f- Reload all bot configs without reconnecting\n"
                + "&5/irc connect &6([bot]) &f- Connect to configured IRC server\n"
                + "&5/irc disconnect &6([bot]) &f- Disconnect from configured IRC server\n"
                + "&5/irc listbots &f- List loaded bots\n"
                + "&5/irc list &6([bot]) ([channel]) &f- List users in a channel\n"
                + "&5/irc msg &6([bot]) [player] [message] &f- Send private message to player\n"
                + "&5/irc op &6[bot] [channel] [user(s)] &f- Op user(s) in a channel\n"
                + "&5/irc kick &6[bot] [channel] [user(s)] &f- Kick user(s) from a channel\n"
                + "&5/irc listops &6[bot] [channel] &f- List auto ops for a channel\n"
                + "&5/irc deop &6[bot] [channel] [user(s)] &f- DeOp user(s) in a channel\n"
                + "&5/irc addop &6[bot] [channel] [user mask] &f- Add user mask to op list.\n"
                + "&5/irc removeop &6[bot] [channel] [user mask] &f- Remove user mask from op list.\n"
                + "&5/irc server &6[bot] [server] ([true|false]) &f- Set IRC server for bot. Optionally set autoconnect.\n"
                + "&5/irc mute &6[bot] [channel] [user(s)] &f- Mute user(s) in a channel.\n"
                + "&5/irc unmute &6[bot] [channel] [user(s)] &f- Unmute user(s) in a channel.\n"
                + "&5/irc nick &6[bot] [nick] &f- Change the bot's IRC nickname.\n"
                + "&5/irc login &6[bot] [login] &f- Change the bot's IRC login.\n"
                + "&5/irc join &6[bot] [channel] &f- Join a channel.\n"
                + "&5/irc leave &6[bot] [channel] ([reason)]- Leave a channel.\n"
                + "&5/irc whois &6([bot]) [user] &f- Get IRC user's info\n"
                + "&5/irc send &6([bot]) ([channel]) &f- Bypass valid-messages and send a message to the channel(s).\n"
                + "&5/irc sendraw &6([bot]) [message] &f- Send raw message to the IRC server.\n"
                + "&5/irc  &6 &f- \n"
                + "&5/irc  &6 &f- \n"
                + "&5/irc  &6 &f- ");
            sender.sendMessage(help);
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

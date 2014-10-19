package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class ListVoices implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "[bot] [channel]";
    private final String desc = "List IRC user mask in auto-voice list.";
    private final String name = "listvoices";
    private final String fullUsage = ChatColor.WHITE + "Usage: " + ChatColor.GOLD + "/irc " + name + " " + usage; 

    /**
     *
     * @param plugin
     */
    public ListVoices(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        if (args.length == 3) {
            String bot = plugin.botify(args[1]);
            String channelName = args[2];
            if (plugin.ircBots.containsKey(bot)) {
                if (plugin.ircBots.get(bot).voicesList.containsKey(channelName)) {
                    sender.sendMessage(ChatColor.DARK_PURPLE + "-----[  " + ChatColor.WHITE + channelName
                            + ChatColor.DARK_PURPLE + " - " + ChatColor.WHITE + "Auto Voice Masks" + ChatColor.DARK_PURPLE + " ]-----");
                    for (String userMask : plugin.ircBots.get(bot).voicesList.get(channelName)) {
                        sender.sendMessage(" - " + userMask);
                    }
                } else {
                    sender.sendMessage(plugin.invalidChannel.replace("%CHANNEL%", channelName));
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

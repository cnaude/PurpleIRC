package com.cnaude.purpleirc.Utilities;

import com.cnaude.purpleirc.PurpleIRC;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class BotsAndChannels {

    public ArrayList<String> bot = new ArrayList<>();
    public ArrayList<String> channel = new ArrayList<>();

    public BotsAndChannels(PurpleIRC plugin, CommandSender sender, String botName, String channelName) {
        if (plugin.ircBots.containsKey(botName)) {
            bot.add(botName);
            if (plugin.ircBots.get(botName).isValidChannel(channelName)) {           
                channel.add(channelName);
            } else {
                sender.sendMessage(plugin.invalidChannelName.replace("%CHANNEL%", channelName));
            }
        } else {
            sender.sendMessage(plugin.invalidBotName.replace("%BOT%", botName));
        }
    }

    public BotsAndChannels(PurpleIRC plugin, CommandSender sender) {
        for (String botName : plugin.ircBots.keySet()) {
            bot.add(botName);
            for (String channelName : plugin.ircBots.get(botName).botChannels) {
                channel.add(channelName);
            }
        }
    }

}

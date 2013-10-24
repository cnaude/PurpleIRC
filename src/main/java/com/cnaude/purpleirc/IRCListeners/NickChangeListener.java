/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.IRCListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.NickChangeEvent;

/**
 *
 * @author cnaude
 */
public class NickChangeListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public NickChangeListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onNickChange(NickChangeEvent event) {
        PircBotX bot = event.getBot();
        String newNick = event.getNewNick();
        String oldNick = event.getOldNick();
        plugin.logDebug("OLD: " + oldNick);
        plugin.logDebug("NEW: " + newNick);

        for (String channelName : ircBot.channelNicks.keySet()) {
            Channel channel = bot.getChannel(channelName);
            if (ircBot.enabledMessages.get(channelName).contains("irc-nickchange")) {
                plugin.getServer().broadcast(plugin.colorConverter.ircColorsToGame(plugin.ircNickChange
                        .replace("%NEWNICK%", newNick)
                        .replace("%OLDNICK%", oldNick)
                        .replace("%CHANNEL%", channelName)), "irc.message.nickchange");
            }
            if (plugin.netPackets != null) {
                plugin.netPackets.remFromTabList(oldNick);
                plugin.netPackets.addToTabList(newNick, ircBot, channel);
            }
            if (ircBot.channelNicks.get(channelName).contains(oldNick)) {
                ircBot.channelNicks.get(channelName).remove(oldNick);
                plugin.logDebug("Removing " + oldNick);
            }
            ircBot.channelNicks.get(channelName).add(newNick);
            plugin.logDebug("Adding " + newNick);
        }

    }
}

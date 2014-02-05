/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.IRCListeners;

import com.cnaude.purpleirc.TemplateName;
import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.pircbotx.Channel;
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
        String newNick = event.getNewNick();
        String oldNick = event.getOldNick();
        plugin.logDebug("OLD: " + oldNick);
        plugin.logDebug("NEW: " + newNick);

        for (String channelName : ircBot.channelNicks.keySet()) {
            Channel channel = ircBot.getChannel(channelName);
            if (channel != null) {
                if (ircBot.enabledMessages.get(channelName).contains(TemplateName.IRC_NICK_CHANGE)) {
                    plugin.getServer().broadcast(plugin.colorConverter.ircColorsToGame(
                            plugin.getMsgTemplate(ircBot.botNick, TemplateName.IRC_NICK_CHANGE)                            
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
}

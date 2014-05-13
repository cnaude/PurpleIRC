/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.IRCListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.QuitEvent;

/**
 *
 * @author cnaude
 */
public class QuitListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public QuitListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onQuit(QuitEvent event) {        
        String nick = event.getUser().getNick();
        for (String channelName : ircBot.channelNicks.keySet()) {
            if (ircBot.channelNicks.get(channelName).contains(nick)) {
                ircBot.broadcastIRCQuit(event.getUser(), ircBot.getChannel(channelName), event.getReason());
                if (plugin.netPackets != null) {
                    plugin.netPackets.remFromTabList(nick);
                }
            }
            ircBot.channelNicks.get(channelName).remove(nick);
        }
    }
}


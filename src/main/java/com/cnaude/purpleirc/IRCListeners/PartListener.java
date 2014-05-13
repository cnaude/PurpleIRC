/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.IRCListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.PartEvent;

/**
 *
 * @author cnaude
 */
public class PartListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public PartListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onPart(PartEvent event) {
        Channel channel = event.getChannel();
        User user = event.getUser();

        if (!ircBot.isValidChannel(channel.getName())) {
            ircBot.broadcastIRCPart(user, channel);
            if (plugin.netPackets != null) {
                plugin.netPackets.remFromTabList(user.getNick());
            }
        }
    }
}

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
import org.pircbotx.hooks.events.KickEvent;

/**
 *
 * @author cnaude
 */
public class KickListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public KickListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onKick(KickEvent event) {
        Channel channel = event.getChannel();
        User recipient = event.getRecipient();
        User user = event.getUser();

        if (ircBot.isValidChannel(channel.getName())) {
            ircBot.broadcastIRCKick(recipient.getNick(), user.getNick(), event.getReason(), channel.getName());
            if (plugin.netPackets != null) {
                plugin.netPackets.remFromTabList(recipient.getNick());
            }
        }
    }
}

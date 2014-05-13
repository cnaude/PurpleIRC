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
import org.pircbotx.hooks.events.ModeEvent;

/**
 *
 * @author cnaude
 */
public class ModeListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public ModeListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onMode(ModeEvent event) {
        Channel channel = event.getChannel();
        String mode = event.getMode();
        User user = event.getUser();

        if (!ircBot.isValidChannel(channel.getName())) {
            ircBot.broadcastIRCMode(user, mode, channel);
        }
    }
}

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
import org.pircbotx.hooks.events.MessageEvent;

/**
 *
 * @author cnaude
 */
public class MessageListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    public MessageListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    @Override
    public void onMessage(MessageEvent event) {

        String message = event.getMessage();
        Channel channel = event.getChannel();
        User user = event.getUser();

        plugin.logDebug("Message caught <" + user.getNick() + ">: " + message);

        ircBot.ircMessageHandler.processMessage(user, channel, message, false);
    }
}

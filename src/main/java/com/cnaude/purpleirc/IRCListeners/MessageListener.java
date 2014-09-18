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

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public MessageListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onMessage(MessageEvent event) {

        String message = event.getMessage();
        Channel channel = event.getChannel();
        User user = event.getUser();

        plugin.logDebug("Message caught <" + user.getNick() + ">: " + message);
                
        if (plugin.shortifyHook != null && ircBot.isShortifyEnabled(channel.getName())) {
            plugin.logDebug("Shortifying message (before): " + message);
            message = plugin.shortifyHook.shorten(message);
            plugin.logDebug("Shortifying message (after): " + message);
        } else {
            plugin.logDebug("Shortify: false");
        }

        if (ircBot.isValidChannel(channel.getName())) {
            plugin.ircMessageHandler.processMessage(ircBot, user, channel, message, false);
        }
    }
}

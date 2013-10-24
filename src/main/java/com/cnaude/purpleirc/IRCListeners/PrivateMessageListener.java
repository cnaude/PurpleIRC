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
import org.pircbotx.hooks.events.PrivateMessageEvent;

/**
 *
 * @author cnaude
 */
public class PrivateMessageListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;    

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public PrivateMessageListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onPrivateMessage(PrivateMessageEvent event) {

        String message = event.getMessage();        
        User user = event.getUser();
        Channel channel;

        plugin.logDebug("Private message caught <" + user.getNick() + ">: " + message);

        for (String myChannel : ircBot.botChannels) {
            channel = ircBot.bot.getChannel(myChannel);
            if (user.getChannels().contains(channel)) {
                ircBot.ircMessageHandler.processMessage(user, channel, message, true);
                return;
            }
            plugin.logDebug("Private message from " + user.getNick() + " ignored because not in valid channel.");
        }
    }
}

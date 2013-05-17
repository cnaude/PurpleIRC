/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.IRC;

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

    public KickListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    @Override
    public void onKick(KickEvent event) {
        Channel channel = event.getChannel();
        User recipient = event.getRecipient();
        User kicker = event.getSource();        
        
        if (!ircBot.botChannels.contains(channel.getName())) {
            return;
        }
        if (ircBot.enabledMessages.get(channel.getName()).contains("irc-kick")) {
            plugin.getServer().broadcast(plugin.colorConverter.ircColorsToGame(plugin.ircKick)
                    .replace("%NAME%", recipient.getNick())
                    .replace("%REASON%", event.getReason())
                    .replace("%KICKER%", kicker.getNick())
                    .replace("%CHANNEL%", channel.getName()), "irc.message.kick");
        }
    }
}

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
        
        if (!ircBot.botChannels.contains(channel.getName())) {
            return;
        }
        if (ircBot.enabledMessages.get(channel.getName()).contains("irc-part")) {
            plugin.getServer().broadcast(plugin.colorConverter.ircColorsToGame(plugin.ircPart)
                    .replace("%NAME%", user.getNick())
                    .replace("%CHANNEL%", channel.getName()), "irc.message.part");
            if (plugin.netPackets != null) {
                plugin.netPackets.remFromTabList(user.getNick());
            }
        }
    }
}

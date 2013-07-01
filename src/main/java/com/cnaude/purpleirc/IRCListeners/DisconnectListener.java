/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.IRCListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;

/**
 *
 * @author cnaude
 */
public class DisconnectListener extends ListenerAdapter {
    
    PurpleIRC plugin;
    PurpleBot ircBot;

    public DisconnectListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }
    
    @Override
    public void onDisconnect(DisconnectEvent event) {                
        PircBotX bot = event.getBot();
        plugin.botConnected.put(bot.getNick(), false);
        ircBot.broadcastIRCDisconnect();
    }
}

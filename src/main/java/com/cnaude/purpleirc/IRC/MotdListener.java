/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.IRC;

import com.cnaude.purpleirc.PurpleIRC;
import com.cnaude.purpleirc.PurpleBot;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MotdEvent;

/**
 *
 * @author cnaude
 */
public class MotdListener extends ListenerAdapter {
    
    PurpleIRC plugin;
    PurpleBot ircBot;

    public MotdListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }
    
    @Override
    public void onMotd(MotdEvent event) {
        if (ircBot.showMOTD) {
            plugin.logInfo(event.getMotd());
        }
    }
}

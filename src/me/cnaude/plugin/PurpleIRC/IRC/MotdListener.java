/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC.IRC;

import me.cnaude.plugin.PurpleIRC.PIRCMain;
import me.cnaude.plugin.PurpleIRC.PurpleBot;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MotdEvent;

/**
 *
 * @author cnaude
 */
public class MotdListener extends ListenerAdapter {
    
    PIRCMain plugin;
    PurpleBot ircBot;

    public MotdListener(PIRCMain plugin, PurpleBot ircBot) {
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

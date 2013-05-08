/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC.IRC;

import me.cnaude.plugin.PurpleIRC.PIRCMain;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.WhoisEvent;

/**
 *
 * @author cnaude
 */
public class WhoisListener extends ListenerAdapter {
    
    PIRCMain plugin;    

    public WhoisListener(PIRCMain plugin) {
        this.plugin = plugin;        
    }
    
    @Override
    public void onWhois(WhoisEvent event) {
        Long idleTime = event.getIdleSeconds();
        plugin.logInfo("Nick: " + event.getNick());
        plugin.logInfo("Idle: " + idleTime);
    }
}

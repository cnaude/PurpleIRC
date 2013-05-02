/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC.IRC;

import me.cnaude.plugin.PurpleIRC.PIRCMain;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.VersionEvent;

/**
 *
 * @author cnaude
 */
public class VersionListener extends ListenerAdapter {
    
    PIRCMain plugin;    

    public VersionListener(PIRCMain plugin) {
        this.plugin = plugin;        
    }
    
    @Override
    public void onVersion(VersionEvent event) {
        event.respond("[Name: " + plugin.getDescription().getFullName() + "]"
                + "[Desc: " + plugin.getDescription().getDescription() + "]"
                + "[Version: " + plugin.getDescription().getVersion() + "]"                
                + "[URL: " + plugin.getDescription().getWebsite() + "]");
    }
}

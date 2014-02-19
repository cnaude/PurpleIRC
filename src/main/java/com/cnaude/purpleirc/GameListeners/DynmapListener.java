/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.dynmap.DynmapWebChatEvent;

/**
 *
 * @author cnaude
 */
public class DynmapListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public DynmapListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onDynmapWebChatEvent(DynmapWebChatEvent event) {
        String message = event.getMessage();
        String name = event.getName();
        String source = event.getSource();
        plugin.logDebug("DynmapWebChat: " + source + " : " + name + ":" + message);
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (ircBot.isConnected()) {
                ircBot.dynmapWebChat(source, name, message);
            }
        }
    }
}

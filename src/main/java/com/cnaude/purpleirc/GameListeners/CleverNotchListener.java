/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.dotGaming.Endain.CleverNotch.CleverEvent;

/**
 *
 * @author cnaude
 */
public class CleverNotchListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public CleverNotchListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onCleverEvent(CleverEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (ircBot.isConnected()) {
                ircBot.cleverChat(event.getName(),event.getMessage());
            }
        }
    }
}

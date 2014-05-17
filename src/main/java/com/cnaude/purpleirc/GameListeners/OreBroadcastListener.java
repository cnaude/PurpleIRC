/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import be.bendem.orebroadcast.OreBroadcastEvent;
import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class OreBroadcastListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public OreBroadcastListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onOreBroadcastEvent(OreBroadcastEvent event) {
        plugin.logDebug("onOreBroadcastEvent caught");
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gameOreBroadcast(event.getPlayer(), event.getMessage());
        }
    }
}

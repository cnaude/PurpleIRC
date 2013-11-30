/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

/**
 *
 * @author cnaude
 */
public class GamePlayerKickListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GamePlayerKickListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKickEvent(PlayerKickEvent event) {        
        plugin.logDebug("KICK: " + event.getPlayer().getName());
        if (!plugin.kickedPlayers.contains(event.getPlayer().getName())) {
            plugin.kickedPlayers.add(event.getPlayer().getName());
        }
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (ircBot.isConnected()) {
                ircBot.gameKick(event.getPlayer(), event.getLeaveMessage(), event.getReason());                
            }
        }
    }

}

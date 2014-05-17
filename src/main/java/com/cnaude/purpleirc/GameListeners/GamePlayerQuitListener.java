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
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author cnaude
 */
public class GamePlayerQuitListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GamePlayerQuitListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        plugin.logDebug("QUIT: " + event.getPlayer().getName());
        if (plugin.kickedPlayers.contains(event.getPlayer().getName())) {
            plugin.kickedPlayers.remove(event.getPlayer().getName());
            plugin.logDebug("Player "
                    + event.getPlayer().getName()
                    + " was in the recently kicked list. Not sending quit message.");
            return;
        }
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gameQuit(event.getPlayer(), event.getQuitMessage());
            if (plugin.netPackets != null) {
                plugin.netPackets.updateTabList(event.getPlayer());
            }
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import java.io.IOException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.pircbotx.exception.IrcException;

/**
 *
 * @author cnaude
 */
public class GamePlayerJoinListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GamePlayerJoinListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        plugin.logDebug("PlayerJoinEvent: " + event.getPlayer().getDisplayName()
                + ": " + event.getPlayer().getCustomName());
        if (plugin.kickedPlayers.contains(event.getPlayer().getName())) {
            plugin.kickedPlayers.remove(event.getPlayer().getName());
            plugin.logDebug("Removing player "
                    + event.getPlayer().getName()
                    + " from the recently kicked list.");
        }
        plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.gameJoin(event.getPlayer(), event.getJoinMessage());
                    if (plugin.netPackets != null) {
                        plugin.netPackets.updateTabList(event.getPlayer());
                    }
                }
            }
        }, 20);

    }
}

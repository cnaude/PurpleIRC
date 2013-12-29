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
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author cnaude
 */
public class GamePlayerChatListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GamePlayerChatListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        plugin.logDebug("ChatFormat [" + event.isCancelled() + "]: " + event.getFormat());
        if (event.isCancelled() && !plugin.isFactionChatEnabled()) {
            plugin.logDebug("Ignore chat message due to event cancellation: " + event.getMessage());
            return;
        }
        if (event.getPlayer().hasPermission("irc.message.gamechat")) {
            plugin.logDebug("Player " + event.getPlayer().getName() + " has permission irc.message.gamechat");
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                if (ircBot.isConnected()) {
                    ircBot.gameChat(event.getPlayer(), event.getMessage());
                }
            }
        } else {
            plugin.logDebug("Player " + event.getPlayer().getName() + " does not have irc.message.gamechat permission.");
        }
    }
}

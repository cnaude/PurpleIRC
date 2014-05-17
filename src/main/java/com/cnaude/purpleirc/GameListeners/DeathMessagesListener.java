/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.cnaude.purpleirc.TemplateName;
import com.gmail.josemanuelgassin.DeathMessages.DeathMessageEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class DeathMessagesListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public DeathMessagesListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onDeathMessageEvent(DeathMessageEvent event) {
        plugin.logDebug("onDeathMessageEvent caught");
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gameDeath(event.getPlayer(), event.getDeathMessage(), TemplateName.DEATH_MESSAGES);
        }
    }
}

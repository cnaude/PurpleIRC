/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import net.ess3.api.IUser;
import net.ess3.api.events.AfkStatusChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class EssentialsListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public EssentialsListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onAfkStatusChangeEvent(AfkStatusChangeEvent event) {
        IUser user = event.getAffected();
        plugin.logDebug("AFK: " + user.getName() + ":" + user.isAfk());
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.essentialsAFK(user.getBase(), !user.isAfk());
        }
    }
}

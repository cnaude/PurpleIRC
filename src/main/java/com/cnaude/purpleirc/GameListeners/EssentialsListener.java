/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleIRC;
import com.earth2me.essentials.User;
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
        User user = event.getAffected();     
        plugin.logDebug("AFK: " + user.getName() + ":" + user.isAfk());
        for (String botName : plugin.ircBots.keySet()) {
            if (plugin.botConnected.get(botName)) {
                plugin.ircBots.get(botName).essentialsAFK(user.getBase(),user.isAfk());
            }
        }
    }
}

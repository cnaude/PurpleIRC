/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleIRC;
import com.nyancraft.reportrts.event.ReportCreateEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class ReportRTSListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public ReportRTSListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onReportCreateEvent(ReportCreateEvent event) {
        Player player = plugin.getServer().getPlayer(event.getRequest().getName());
        for (String botName : plugin.ircBots.keySet()) {
            if (plugin.botConnected.get(botName)) {
                plugin.ircBots.get(botName).reportRTSNotify(player, event.getRequest());
            }
        }
    }
}

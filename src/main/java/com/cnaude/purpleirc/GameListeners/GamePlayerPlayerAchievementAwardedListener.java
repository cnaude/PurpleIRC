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
import org.bukkit.event.player.PlayerAchievementAwardedEvent;

/**
 *
 * @author cnaude
 */
public class GamePlayerPlayerAchievementAwardedListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GamePlayerPlayerAchievementAwardedListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerAchievementAwardedEvent(PlayerAchievementAwardedEvent event) {
        plugin.logDebug("ACHIEVEMENT: " + event.getPlayer().getName() + " => " + event.getAchievement());
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gameAchievement(event.getPlayer(), event.getAchievement());
        }
    }
}

package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

/**
 *
 * @author cnaude
 */
public class GamePlayerGameModeChangeListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GamePlayerGameModeChangeListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerGameModeChangeEvent(PlayerGameModeChangeEvent event) {
        plugin.logDebug("GAMEMODE: " + event.getPlayer().getName() + " => " + event.getNewGameMode());
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gameModeChange(event.getPlayer(), event.getNewGameMode());
        }
    }
}

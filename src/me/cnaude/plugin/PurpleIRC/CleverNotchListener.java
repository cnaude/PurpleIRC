/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.dotGaming.Endain.CleverNotch.CleverEvent;

/**
 *
 * @author cnaude
 */
public class CleverNotchListener implements Listener {

    private final PIRCMain plugin;

    public CleverNotchListener(PIRCMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCleverEvent(CleverEvent event) {
        for (String botName : plugin.ircBots.keySet()) {
            if (plugin.botConnected.get(botName)) {
                plugin.ircBots.get(botName).cleverChat(event.getName(),event.getMessage());
            }
        }
    }
}

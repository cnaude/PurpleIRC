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
import org.bukkit.event.server.ServerCommandEvent;

/**
 *
 * @author cnaude
 */
public class GameServerCommandListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GameServerCommandListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onServerCommandEvent(ServerCommandEvent event) {
        String cmd = event.getCommand();
        if (cmd.startsWith("say ")) {
            String msg = cmd.split(" ", 2)[1];
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                if (ircBot.isConnected()) {
                    ircBot.consoleChat(msg);
                }
            }
        } else if (cmd.startsWith("broadcast ")) {
            String msg = cmd.split(" ", 2)[1];
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                if (ircBot.isConnected()) {
                    ircBot.consoleBroadcast(msg);
                }
            }
        } else {
            //plugin.logDebug("Invalid CE: " + cmd);
        }
    }
}

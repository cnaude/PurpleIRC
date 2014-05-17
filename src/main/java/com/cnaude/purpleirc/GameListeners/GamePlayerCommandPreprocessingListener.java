/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;

/**
 *
 * @author cnaude
 */
public class GamePlayerCommandPreprocessingListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GamePlayerCommandPreprocessingListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        String msg = event.getMessage();
        if (event.getPlayer().hasPermission("irc.message.gamechat")) {
            if (msg.startsWith("/me ")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.gameAction(event.getPlayer(), msg.replace("/me", ""));
                }
            } else if (msg.startsWith("/broadcast ")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.gameBroadcast(event.getPlayer(), msg.replace("/broadcast", ""));
                }
            }
        }
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (msg.startsWith("/")) {
                String cmd;
                String params = "";
                if (msg.contains(" ")) {
                    cmd = msg.split(" ", 2)[0];
                    params = msg.split(" ", 2)[1];
                } else {
                    cmd = msg;
                }
                cmd = cmd.substring(0);
                if (ircBot.channelCmdNotifyEnabled) {
                    ircBot.commandNotify(event.getPlayer(), cmd, params);
                }
            }
        }
    }
}

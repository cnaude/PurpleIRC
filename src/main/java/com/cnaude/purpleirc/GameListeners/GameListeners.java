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
public class GameListeners implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GameListeners(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
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

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        plugin.logDebug("QUIT: " + event.getPlayer().getName());
        if (plugin.kickedPlayers.contains(event.getPlayer().getName())) {
            plugin.kickedPlayers.remove(event.getPlayer().getName());
            plugin.logDebug("Player " 
                    + event.getPlayer().getName()
                    + " was in the recently kicked list. Not sending quit message.");
            return;
        }
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (ircBot.isConnected()) {                
                ircBot.gameQuit(event.getPlayer(), event.getQuitMessage());
                if (plugin.netPackets != null) {
                    plugin.netPackets.updateTabList(event.getPlayer());
                }
            }
        }
    }
    
    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKickEvent(PlayerKickEvent event) {        
        plugin.logDebug("KICK: " + event.getPlayer().getName());
        if (!plugin.kickedPlayers.contains(event.getPlayer().getName())) {
            plugin.kickedPlayers.add(event.getPlayer().getName());
        }
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (ircBot.isConnected()) {
                ircBot.gameKick(event.getPlayer(), event.getLeaveMessage(), event.getReason());                
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (plugin.kickedPlayers.contains(event.getPlayer().getName())) {
            plugin.kickedPlayers.remove(event.getPlayer().getName());
            plugin.logDebug("Removing player " 
                    + event.getPlayer().getName()
                    + " from the recently kicked list.");            
        }
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (ircBot.isConnected()) {
                ircBot.gameJoin(event.getPlayer(), event.getJoinMessage());
                if (plugin.netPackets != null) {
                    plugin.netPackets.updateTabList(event.getPlayer());
                }
            }
        }
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
                    if (ircBot.isConnected()) {
                        ircBot.gameAction(event.getPlayer(), msg.replace("/me", ""));
                    }
                }
            } else if (msg.startsWith("/broadcast ")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    if (ircBot.isConnected()) {
                        ircBot.gameBroadcast(event.getPlayer(), msg.replace("/broadcast", ""));
                    }
                }
            }
        }
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (ircBot.isConnected()) {
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
                    if (ircBot.isConnected()) {
                        if (ircBot.channelCmdNotifyEnabled) {
                            ircBot.commandNotify(event.getPlayer(), cmd, params);
                        }
                    }
                }
            }
        }

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
            plugin.logDebug("Invalid CE: " + cmd);
        }
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (ircBot.isConnected()) {
                ircBot.gameDeath((Player) event.getEntity(), event.getDeathMessage());
            }
        }
    }
}

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
            for (String botName : plugin.ircBots.keySet()) {
                if (plugin.botConnected.get(botName)) {
                    plugin.ircBots.get(botName).gameChat(event.getPlayer(), event.getMessage());
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
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        for (String botName : plugin.ircBots.keySet()) {
            if (plugin.botConnected.get(botName)) {
                plugin.ircBots.get(botName).gameQuit(event.getPlayer(), event.getQuitMessage());
            }
        }
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        for (String botName : plugin.ircBots.keySet()) {
            if (plugin.botConnected.get(botName)) {
                plugin.ircBots.get(botName).gameJoin(event.getPlayer(), event.getJoinMessage());
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
                for (String botName : plugin.ircBots.keySet()) {
                    if (plugin.botConnected.get(botName)) {
                        plugin.ircBots.get(botName).gameAction(event.getPlayer(), msg.replace("/me", ""));
                    }
                }
            } else if (msg.startsWith("/broadcast ")) {
                for (String botName : plugin.ircBots.keySet()) {
                    if (plugin.botConnected.get(botName)) {
                        plugin.ircBots.get(botName).gameBroadcast(event.getPlayer(), msg.replace("/broadcast", ""));
                    }
                }
            }
        }
        for (String botName : plugin.ircBots.keySet()) {
            if (plugin.botConnected.get(botName)) {
                PurpleBot ircBot = plugin.ircBots.get(botName);
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
                    if (plugin.botConnected.get(botName)) {
                        if (ircBot.channelCmdNotifyEnabled) {
                            ircBot.commandNotify(event.getPlayer(),cmd,params);                                
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
        plugin.logDebug("CE: " + cmd);
        if (cmd.startsWith("say ")) {
            String msg = cmd.split(" ", 2)[1];
            for (String botName : plugin.ircBots.keySet()) {
                if (plugin.botConnected.get(botName)) {
                    plugin.ircBots.get(botName).consoleChat(msg);
                }
            }
        } else if (cmd.startsWith("broadcast ")) {
            String msg = cmd.split(" ", 2)[1];
            for (String botName : plugin.ircBots.keySet()) {
                if (plugin.botConnected.get(botName)) {
                    plugin.ircBots.get(botName).consoleBroadcast(msg);
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
        for (String botName : plugin.ircBots.keySet()) {
            if (plugin.botConnected.get(botName)) {
                plugin.ircBots.get(botName).gameDeath((Player) event.getEntity(), event.getDeathMessage());
            }
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author cnaude
 */
public class PIRCListener implements Listener {

    private final PIRCMain plugin;

    public PIRCListener(PIRCMain plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPlayer().hasPermission("irc.message.gamechat")) {
            for (String botName : plugin.ircBots.keySet()) {
                if (plugin.botConnected.get(botName)) {
                    PIRCBot ircBot = plugin.ircBots.get(botName);
                    plugin.logDebug("YES-CHAT: " + event.getPlayer().getName() + " => " + event.getMessage());
                    ircBot.gameChat(event.getPlayer(), event.getMessage());
                } else {
                    plugin.logDebug("NO-CHAT: " + event.getPlayer().getName() + " => " + event.getMessage());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        for (String botName : plugin.ircBots.keySet()) {
            if (plugin.botConnected.get(botName)) {
                PIRCBot ircBot = plugin.ircBots.get(botName);
                ircBot.gameQuit(event.getPlayer(), event.getQuitMessage());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        for (String botName : plugin.ircBots.keySet()) {
            if (plugin.botConnected.get(botName)) {
                PIRCBot ircBot = plugin.ircBots.get(botName);
                ircBot.gameJoin(event.getPlayer(), event.getJoinMessage());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPlayer().hasPermission("irc.message.gamechat")) {
            String msg = event.getMessage();
            if (msg.startsWith("/me ")) {
                for (String botName : plugin.ircBots.keySet()) {
                    if (plugin.botConnected.get(botName)) {
                        PIRCBot ircBot = plugin.ircBots.get(botName);
                        ircBot.gameAction(event.getPlayer(), msg.replaceAll("/me", ""));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        for (String botName : plugin.ircBots.keySet()) {
            if (plugin.botConnected.get(botName)) {
                PIRCBot ircBot = plugin.ircBots.get(botName);
                ircBot.gameDeath((Player) event.getEntity(), event.getDeathMessage());
            }
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC;

import java.util.regex.Matcher;
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
public class GameListeners implements Listener {

    private final PIRCMain plugin;

    public GameListeners(PIRCMain plugin) {
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
                    plugin.ircBots.get(botName).gameChat(event.getPlayer(), Matcher.quoteReplacement(event.getMessage()));
                } 
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        for (String botName : plugin.ircBots.keySet()) {
            if (plugin.botConnected.get(botName)) {                
                plugin.ircBots.get(botName).gameQuit(event.getPlayer(), Matcher.quoteReplacement(event.getQuitMessage()));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        for (String botName : plugin.ircBots.keySet()) {
            if (plugin.botConnected.get(botName)) {                
                plugin.ircBots.get(botName).gameJoin(event.getPlayer(), Matcher.quoteReplacement(event.getJoinMessage()));
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPlayer().hasPermission("irc.message.gamechat")) {
            String msg = Matcher.quoteReplacement(event.getMessage());
            if (msg.startsWith("/me ")) {
                for (String botName : plugin.ircBots.keySet()) {
                    if (plugin.botConnected.get(botName)) {                        
                        plugin.ircBots.get(botName).gameAction(event.getPlayer(), msg.replaceAll("/me", ""));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        for (String botName : plugin.ircBots.keySet()) {
            if (plugin.botConnected.get(botName)) {                
                plugin.ircBots.get(botName).gameDeath((Player) event.getEntity(), Matcher.quoteReplacement(event.getDeathMessage()));
            }
        }
    }
}

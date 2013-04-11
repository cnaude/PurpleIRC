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
        Player player = event.getPlayer();
        for (PIRCBot ircBot : plugin.ircBots.values()) {
            ircBot.message(player, event.getMessage());
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (PIRCBot ircBot : plugin.ircBots.values()) {
            ircBot.quitMessage(player, event.getQuitMessage());
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (PIRCBot ircBot : plugin.ircBots.values()) {
            ircBot.joinMessage(player, event.getJoinMessage());
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocessEvent (PlayerCommandPreprocessEvent  event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
        String msg = event.getMessage();
        if (msg.startsWith("/me ")) {
            for (PIRCBot ircBot : plugin.ircBots.values()) {
                ircBot.action(player, msg.replaceAll("/me", "*** " + player.getName()));
            }
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeathEvent (PlayerDeathEvent  event) {        
        for (PIRCBot ircBot : plugin.ircBots.values()) {
            ircBot.death((Player)event.getEntity(), event.getDeathMessage());
        }
    }
}

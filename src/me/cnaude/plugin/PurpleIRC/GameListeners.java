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
import org.bukkit.event.server.ServerCommandEvent;

/**
 *
 * @author cnaude
 */
public class GameListeners implements Listener {

    private final PIRCMain plugin;
    
    private String lastMessage;
    private String lastChatter;
    private int messageCounter;
    private int chatterCounter;

    public GameListeners(PIRCMain plugin) {
        this.plugin = plugin;
        lastMessage = "";
        messageCounter = 0;
        chatterCounter = 0;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPlayer().hasPermission("irc.message.gamechat")) {
            for (String botName : plugin.ircBots.keySet()) {
                if (plugin.botConnected.get(botName)) { 
                    /*
                    String pName = event.getPlayer().getName();
                    String message = pName + ":" + event.getMessage();   
                    
                    if (message.equals(lastMessage)) {
                        messageCounter++;
                    } else {
                        messageCounter = 0;
                    }
                    lastMessage = message;
                    plugin.logDebug(String.format("[%d] Caught Message: %s", messageCounter,message));
                    if (messageCounter >= 5) {                        
                        plugin.logInfo("Cancelling chat message from " + event.getPlayer().getName() + " due to spamming.");
                        event.setCancelled(true);
                        return;
                    } 
                    
                    if (pName.equals(lastChatter)) {
                        chatterCounter++;
                    } else {
                        chatterCounter = 0;
                    }
                    lastChatter = pName;
                    plugin.logDebug(String.format("[%d] Chat Counter: %s", chatterCounter,pName));
                    if (chatterCounter >= 8) {                        
                        plugin.logInfo("Cancelling chat message from " + event.getPlayer().getName() + " due to spamming.");
                        event.setCancelled(true);
                        return;
                    } 
                    */
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
    public void onServerCommandEvent(ServerCommandEvent event) {        
        String cmd = event.getCommand();                    
        plugin.logDebug("CE: " + cmd);
        if (cmd.startsWith("say ")) {            
            String msg = cmd.split(" ",2)[1];            
            for (String botName : plugin.ircBots.keySet()) {
                if (plugin.botConnected.get(botName)) {                        
                    plugin.ircBots.get(botName).consoleChat(msg);
                }
            }
        } else {
            plugin.logDebug("Invalid CE: " + cmd);
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

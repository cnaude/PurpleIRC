/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import java.util.regex.Matcher;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class HeroChatListener implements Listener {
    
    PIRCMain plugin;

    public HeroChatListener(PIRCMain plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onChannelChatEvent(ChannelChatEvent event) {
        Chatter chatter = event.getSender();
        Player player = chatter.getPlayer();
        if (player.hasPermission("irc.message.gamechat")) {
            for (String botName : plugin.ircBots.keySet()) {
                if (plugin.botConnected.get(botName)) { 
                    plugin.ircBots.get(botName).gameChat(chatter, Matcher.quoteReplacement(event.getMessage()));
                } 
            }
        }
    }
    
}

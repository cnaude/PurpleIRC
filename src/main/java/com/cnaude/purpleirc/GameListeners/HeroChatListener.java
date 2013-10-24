/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleIRC;
import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class HeroChatListener implements Listener {

    final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public HeroChatListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onChannelChatEvent(ChannelChatEvent event) {
        Chatter chatter = event.getSender();        
        ChatColor chatColor = event.getChannel().getColor();        
        Player player = chatter.getPlayer();
        if (player.hasPermission("irc.message.gamechat")
                && chatter.getChannels().contains(event.getChannel())) {
            for (String botName : plugin.ircBots.keySet()) {
                if (plugin.botConnected.get(botName)) {
                    plugin.ircBots.get(botName).heroChat(chatter, chatColor, event.getMessage());
                }
            }
        }
    }
    
}

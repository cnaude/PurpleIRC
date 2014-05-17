/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.titankingdoms.dev.titanchat.core.participant.Participant;
import com.titankingdoms.dev.titanchat.event.ChannelChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class TitanChatListener implements Listener {

    final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public TitanChatListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onChannelChatEvent(ChannelChatEvent event) {
        Participant participant = event.getSender();
        Player player = plugin.getServer().getPlayer(participant.getName());
        String tChannel = event.getChannel().getName();
        String tColor = event.getChannel().getDisplayColour();
        if (player.hasPermission("irc.message.gamechat")) {
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                ircBot.titanChat(participant, tChannel, tColor, event.getMessage());
            }
        }
    }
}

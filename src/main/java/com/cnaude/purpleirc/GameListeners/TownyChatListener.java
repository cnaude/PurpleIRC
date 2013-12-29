/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class TownyChatListener implements Listener {

    final PurpleIRC plugin;

    public TownyChatListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncChatHookEvent(AsyncChatHookEvent event) {
        if (event.getAsyncPlayerChatEvent().isCancelled()) {
            plugin.logDebug("Ignore TC chat due to event is cancelled.");
            return;
        }
        Channel townyChannel = event.getChannel();

        plugin.logDebug("TC Format[1]: " + event.getFormat());

        Player player = event.getPlayer();
        if (player.hasPermission("irc.message.gamechat")) {
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                ircBot.townyChat(player, townyChannel, event.getMessage());
            }
        }
        event.getAsyncPlayerChatEvent().setMessage("[[townytag]]" + event.getMessage());
    }
}

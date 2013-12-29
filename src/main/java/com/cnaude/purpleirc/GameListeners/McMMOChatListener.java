/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.gmail.nossr50.events.chat.McMMOAdminChatEvent;
import com.gmail.nossr50.events.chat.McMMOChatEvent;
import com.gmail.nossr50.events.chat.McMMOPartyChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class McMMOChatListener implements Listener {

    final PurpleIRC plugin;

    public McMMOChatListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMcMMOChatEvent(McMMOChatEvent event) {
        plugin.logDebug("McMMOChatEvent caught");
        String sender = event.getSender();
        Player player = plugin.getServer().getPlayer(sender);
        if (player != null) {
            if (player.hasPermission("irc.message.gamechat")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    if (ircBot.isConnected()) {
                        ircBot.mcMMOChat(player, event.getMessage());
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onMcMMOAdminChatEvent(McMMOAdminChatEvent event) {
        plugin.logDebug("McMMOAdminChatEvent caught");
        String sender = event.getSender();
        Player player = plugin.getServer().getPlayer(sender);
        if (player != null) {
            if (player.hasPermission("irc.message.gamechat")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    if (ircBot.isConnected()) {
                        ircBot.mcMMOAdminChat(player, event.getMessage());
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onMcMMOPartyChatEvent(McMMOPartyChatEvent event) {
        plugin.logDebug("onMcMMOPartyChatEvent caught");
        String sender = event.getSender();
        Player player = plugin.getServer().getPlayer(sender);
        String party = event.getParty();
        if (player != null) {
            if (player.hasPermission("irc.message.gamechat")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    if (ircBot.isConnected()) {
                        ircBot.mcMMOPartyChat(player, party, event.getMessage());
                    }
                }
            }
        }
    }
}

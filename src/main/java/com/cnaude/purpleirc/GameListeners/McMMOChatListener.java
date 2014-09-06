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
        event.setMessage(event.getMessage().replace("[[townytag]]", ""));
        String sender = event.getSender();
        Player player = plugin.getServer().getPlayer(sender);
        plugin.logDebug("McMMOChatEvent caught: " + sender);
        if (player != null && !sender.isEmpty()) {
            if (player.hasPermission("irc.message.gamechat")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.mcMMOChat(player, event.getMessage());
                }
            }
        }
    }

    @EventHandler
    public void onMcMMOAdminChatEvent(McMMOAdminChatEvent event) {
        event.setMessage(event.getMessage().replace("[[townytag]]", ""));
        String sender = event.getSender();
        Player player = plugin.getServer().getPlayer(sender);
        plugin.logDebug("McMMOAdminChatEvent caught: " + sender);
        if (player != null && !sender.isEmpty()) {
            if (player.hasPermission("irc.message.gamechat")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.mcMMOAdminChat(player, event.getMessage());
                }
            }
        }
    }

    @EventHandler
    public void onMcMMOPartyChatEvent(McMMOPartyChatEvent event) {
        event.setMessage(event.getMessage().replace("[[townytag]]", ""));
        String sender = event.getSender();
        Player player = plugin.getServer().getPlayer(sender);
        String party = event.getParty();
        plugin.logDebug("onMcMMOPartyChatEvent caught: " + sender);
        if (player != null && !sender.isEmpty()) {
            if (player.hasPermission("irc.message.gamechat")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.mcMMOPartyChat(player, party, event.getMessage());
                }
            }
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
import java.lang.reflect.Method;
import nz.co.lolnet.james137137.FactionChat.ChatMode;
import nz.co.lolnet.james137137.FactionChat.FactionChatAPI;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class FactionChatHook {

    private final PurpleIRC plugin;
    FactionChatAPI api;

    public FactionChatHook(PurpleIRC plugin) {
        this.plugin = plugin;
        api = new FactionChatAPI();
    }
    
    public String getChatMode(Player player) {
        String playerChatMode = null;
        try {
            playerChatMode = api.getChatMode(player);
            plugin.logDebug("fcHook => [CM: " + playerChatMode + "] [" + player.getName() + "]");
        } catch (Exception ex) {
            plugin.logError("fcHook ERROR: " + ex.getMessage());
        }
        if (playerChatMode == null) {
            playerChatMode = "unknown";
        }
        return playerChatMode.toLowerCase();
    }
    
    public String getFactionName(Player player) {
        String factionName = null;
        try {
            factionName = api.getFactionName(player);
            plugin.logDebug("fcHook => [FN: " + factionName + "] [" + player.getName() + "]");
        } catch (Exception ex) {
            plugin.logError("fcHook ERROR: " + ex.getMessage());
        }
        if (factionName == null) {
            factionName = "unknown";
        }
        return factionName.toLowerCase();
    }
}

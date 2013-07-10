/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
import java.lang.reflect.Method;
import nz.co.lolnet.james137137.FactionChat.ChatMode;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class FactionChatHook {

    private final PurpleIRC plugin;

    public FactionChatHook(PurpleIRC plugin) {        
        this.plugin = plugin;
    }

    // Ugh this is ugly. We'll use reflection until FactionChat gets a real public API.
    public String getChatMode(Player player) {     
        String cm = "unknown";
        try {
        Method getCM = ChatMode.class.getDeclaredMethod("getChatMode", Player.class);
        getCM.setAccessible(true);
        cm = (String)getCM.invoke(ChatMode.class,player);
        plugin.logDebug("fcHook => [CM: " + cm + "] [" + player.getName() + "]");
        } catch (Exception ex) {
            plugin.logError("fcHook ERROR: " + ex.getMessage());
        }
        return cm.toLowerCase();
    }
}

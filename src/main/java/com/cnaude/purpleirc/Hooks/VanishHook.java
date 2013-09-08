/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

/**
 *
 * @author cnaude
 */
public class VanishHook {
    
    final PurpleIRC plugin;
    
    public VanishHook(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public boolean isVanished(Player player) {
        if (player.hasMetadata("vanished")) {
            plugin.logDebug("Player " + player.getName() + " has vanished metadata.");
            MetadataValue md = player.getMetadata("vanished").get(0);
            if (md.asBoolean()) {
                plugin.logDebug("Player " + player.getName() + " is invisible.");
                return true;
            }
        }
        return false;
    }
}

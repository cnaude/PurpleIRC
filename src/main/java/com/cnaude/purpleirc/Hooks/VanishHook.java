/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
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
                plugin.logDebug("Player " + player.getName() + " is vanished.");
                return true;
            } else {
                plugin.logDebug("Player " + player.getName() + " is NOT vanished.");
            }
        } else {
            plugin.logDebug("Player " + player.getName() + " has NO vanished metadata.");
        }
        return false;
    }
}

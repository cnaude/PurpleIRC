/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 *
 * @author cnaude
 */
public class VaultHook {
    
    final PurpleIRC plugin;
    
    /**
     *
     */
    public Permission permission;    

    /**
     *
     */
    public Chat chat;
    
    /**
     *
     * @param plugin
     */
    public VaultHook(PurpleIRC plugin) {
        this.plugin = plugin;
        
        setupPermissions();
        setupChat();
    }
    
    private void setupPermissions() {
        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }        
    }

    private void setupChat() {
        RegisteredServiceProvider<Chat> chatProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
        }        
    }   
}

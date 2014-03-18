/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
import org.dynmap.DynmapCommonAPI;

/**
 *
 * @author cnaude
 */
public class DynmapHook {

    private final PurpleIRC plugin;
    private final DynmapCommonAPI dynmapAPI;

    /**
     *
     * @param plugin
     */
    public DynmapHook(PurpleIRC plugin) {
        this.plugin = plugin;
        this.dynmapAPI = (DynmapCommonAPI)plugin.getServer().getPluginManager().getPlugin("dynmap");
    }
    
    /**
     *
     * @param nick
     * @param message
     */
    public void sendMessage(String nick, String message) {
         dynmapAPI.sendBroadcastToWeb(nick, message);
    }
}

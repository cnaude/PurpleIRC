/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class Reload {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public Reload(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    public void dispatch(CommandSender sender, String[] args) {
        sender.sendMessage("Disabling PurpleIRC...");
        plugin.getServer().getPluginManager().disablePlugin(plugin);
        sender.sendMessage("Enabling PurpleIRC...");
        plugin.getServer().getPluginManager().enablePlugin(plugin);
    }
}

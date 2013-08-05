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
public class ReloadConfig {

    private final PurpleIRC plugin;

    public ReloadConfig(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void dispatch(CommandSender sender) {
        plugin.reloadMainConfig(sender);
    }
}

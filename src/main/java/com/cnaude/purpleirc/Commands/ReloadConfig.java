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
public class ReloadConfig implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "";
    private final String desc = "Reload PurpleIRC/config.yml.";
    private final String name = "reloadconfig";    

    /**
     *
     * @param plugin
     */
    public ReloadConfig(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        plugin.reloadMainConfig(sender);
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String desc() {
        return desc;
    }

    @Override
    public String usage() {
        return usage;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class ReloadBotConfigs implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "";
    private final String desc = "Reload all bot configurations.";
    private final String name = "reloadbotconfigs";

    /**
     *
     * @param plugin
     */
    public ReloadBotConfigs(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reloadConfig(sender);
        }
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

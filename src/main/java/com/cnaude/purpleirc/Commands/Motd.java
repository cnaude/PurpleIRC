/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Commands;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import java.util.ArrayList;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class Motd implements IRCCommandInterface {

    private final PurpleIRC plugin;
    private final String usage = "([bot])";
    private final String desc = "Get server motd.";
    private final String name = "motd";

    /**
     *
     * @param plugin
     */
    public Motd(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param sender
     * @param args
     */
    @Override
    public void dispatch(CommandSender sender, String[] args) {
        java.util.List<PurpleBot> myBots = new ArrayList<PurpleBot>();
        if (args.length >= 2) {
            if (plugin.ircBots.containsKey(args[1])) {
                myBots.add(plugin.ircBots.get(args[1]));
                
            } else {
                sender.sendMessage(plugin.invalidBotName.replace("%BOT%", args[1]));
            }
        } else {
            myBots.addAll(plugin.ircBots.values());
        }

        for (PurpleBot ircBot : myBots) {
            String motd = ircBot.getMotd();
            if (motd != null) {
                sender.sendMessage(motd);
            } else {
                sender.sendMessage("No MOTD found.");
            }
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

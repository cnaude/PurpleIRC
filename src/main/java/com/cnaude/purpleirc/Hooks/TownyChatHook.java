/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
import com.cnaude.purpleirc.Utilities.CaseInsensitiveMap;
import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.channels.channelTypes;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import java.util.ArrayList;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class TownyChatHook {

    private final PurpleIRC plugin;
    private final Chat chat;
    private final ArrayList<channelTypes> townyChannelTypes;

    /**
     *
     * @param plugin
     */
    public TownyChatHook(PurpleIRC plugin) {
        this.plugin = plugin;
        chat = (Chat) plugin.getServer().getPluginManager().getPlugin("TownyChat");
        townyChannelTypes = new ArrayList<channelTypes>();
        townyChannelTypes.add(channelTypes.TOWN);
        townyChannelTypes.add(channelTypes.GLOBAL);
        townyChannelTypes.add(channelTypes.NATION);
        townyChannelTypes.add(channelTypes.DEFAULT);
    }

    public String getTown(Player player) {
        String town = "";
        Resident resident;
        try {
            resident = TownyUniverse.getDataSource().getResident(player.getName());
        } catch (NotRegisteredException ex) {
            resident = null;
        }

        if (resident != null) {
            try {
                town = resident.getTown().getName();
            } catch (NotRegisteredException ex) {
                town = "";
            }
        }
        return town;
    }

    public String getNation(Player player) {
        String nation = "";
        Resident resident;
        try {
            resident = TownyUniverse.getDataSource().getResident(player.getName());
        } catch (NotRegisteredException ex) {
            resident = null;
        }
        if (resident != null) {
            try {
                nation = resident.getTown().getNation().getName();
            } catch (NotRegisteredException ex) {
                nation = "";
            }
        }
        return nation;
    }

    public String getTitle(Player player) {
        Resident resident;
        try {
            resident = TownyUniverse.getDataSource().getResident(player.getName());
        } catch (NotRegisteredException ex) {
            resident = null;
        }
        String title = "";
        if (resident != null) {
            title = resident.getTitle();
        }
        return title;
    }

    public void sendMessage(String townyChannel, String message) {
        plugin.logDebug("TownyChatHook called...");
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            plugin.logDebug("P: " + player.getName());
            for (channelTypes ct : townyChannelTypes) {
                plugin.logDebug("CT: " + ct.name());
                String townyChannelName = chat.getChannelsHandler().getActiveChannel(player, ct).getName();
                if (townyChannel.equalsIgnoreCase(townyChannelName)) {
                    plugin.logDebug("TC ["+townyChannelName+"]: Sending message to " + player + ": " + message);
                    player.sendMessage(message);
                    break;
                } else {
                    plugin.logDebug("TC "+townyChannelName+"]: invalid TC channel name for " + player);
                }
                    
            }
        }
    }
}

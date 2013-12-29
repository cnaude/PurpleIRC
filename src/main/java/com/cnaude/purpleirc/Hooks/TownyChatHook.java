/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class TownyChatHook {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public TownyChatHook(PurpleIRC plugin) {
        this.plugin = plugin;
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
}

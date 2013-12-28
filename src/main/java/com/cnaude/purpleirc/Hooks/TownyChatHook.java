/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;

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

    public String getTown(Resident resident) {
        String town = "";
        if (resident != null) {
            try {
                town = resident.getTown().getName();
            } catch (NotRegisteredException ex) {
                town = "";
            }
        }
        return town;
    }
    
    public String getNation(Resident resident) {
        String nation = "";
        if (resident != null) {
            try {
                nation = resident.getTown().getNation().getName();
            } catch (NotRegisteredException ex) {
                nation = "";
            }
        }
        return nation;
    }
    
    public String getTitle(Resident resident) {
        String title = "";
        if (resident != null) {
            title = resident.getTitle();
        }
        return title;
    }
}

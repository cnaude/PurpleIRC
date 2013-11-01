/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.cnaude.purpleirc.Commands;

import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public interface IRCCommandInterface {
    void dispatch(CommandSender sender, String[] args);
    String name();
    String desc();
    String usage();    
}

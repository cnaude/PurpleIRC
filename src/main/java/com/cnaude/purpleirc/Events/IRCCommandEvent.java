/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Events;

import com.cnaude.purpleirc.IRCCommand;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author cnaude
 * Event listener for plugins that want to catch command events from PurpleIRC
 */
public class IRCCommandEvent extends Event {

    private static final HandlerList handlers = new HandlerList();    
    private IRCCommand ircCommand;

    public IRCCommandEvent(IRCCommand ircCommand) {
        this.ircCommand = ircCommand;             
    }
    
    public IRCCommand getIRCCommand() {
        return this.ircCommand;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
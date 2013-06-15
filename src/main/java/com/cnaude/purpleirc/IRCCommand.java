/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc;

/**
 *
 * @author cnaude
 */
public class IRCCommand {
    final private IRCCommandSender sender;
    final private String command;
    
    public IRCCommand(IRCCommandSender sender, String command) {
        this.sender = sender;
        this.command = command;
    }
    
    public IRCCommandSender getIRCCommandSender() {
        return sender;
    }
    
    public String getGameCommand() {
        return command;
    }
}

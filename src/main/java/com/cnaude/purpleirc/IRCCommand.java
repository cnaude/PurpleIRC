package com.cnaude.purpleirc;

/**
 *
 * @author cnaude
 */
public class IRCCommand {
    final private IRCCommandSender sender;
    final private String command;
    
    /**
     *
     * @param sender
     * @param command
     */
    public IRCCommand(IRCCommandSender sender, String command) {
        this.sender = sender;
        this.command = command;
    }
    
    /**
     *
     * @return
     */
    public IRCCommandSender getIRCCommandSender() {
        return sender;
    }
    
    /**
     *
     * @return
     */
    public String getGameCommand() {
        return command;
    }
}

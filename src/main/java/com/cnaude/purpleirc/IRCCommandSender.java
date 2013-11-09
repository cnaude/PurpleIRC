package com.cnaude.purpleirc;

import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.pircbotx.PircBotX;

/**
 *
 * @author Chris Naude
 * We have to implement our own CommandSender so that we can
 * receive output from the command dispatcher.
 */
public class IRCCommandSender implements CommandSender {    
    private final PurpleBot ircBot;
    private final String target;
    private final PurpleIRC plugin;
    private final boolean ctcpResponse;
    
    /**
     *
     * @param message
     */
    @Override
    public void sendMessage(String message) {     
        if (ctcpResponse) {
            ircBot.asyncCTCPMessage(target, plugin.colorConverter.gameColorsToIrc(message));                
        } else {            
            ircBot.asyncIRCMessage(target, plugin.colorConverter.gameColorsToIrc(message));                        
        }
    }
    
    /**
     *
     * @param messages
     */
    @Override 
    public void sendMessage(String[] messages) {  
        for (String message : messages) {
            if (ctcpResponse) {
                ircBot.asyncCTCPMessage(target, plugin.colorConverter.gameColorsToIrc(message));  
            } else {
                ircBot.asyncCTCPMessage(target, plugin.colorConverter.gameColorsToIrc(message));  
            }
        }
    }
    
    /**
     *
     * @param ircBot
     * @param target
     * @param plugin
     * @param ctcpResponse
     */
    public IRCCommandSender(PurpleBot ircBot, String target, PurpleIRC plugin, boolean ctcpResponse) {        
        this.target = target;
        this.ircBot = ircBot;        
        this.plugin = plugin;
        this.ctcpResponse = ctcpResponse;
    }
    
    /**
     *
     * @return
     */
    @Override 
    public Server getServer() {
        return Bukkit.getServer();
    }
    
    /**
     *
     * @return
     */
    @Override 
    public String getName() {
        return "";
    }
    
    /**
     *
     * @return
     */
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    /**
     *
     * @param arg0
     * @return
     */
    @Override
    public boolean hasPermission(final String arg0) {
        return true;
    }

    /**
     *
     * @param arg0
     * @return
     */
    @Override
    public boolean hasPermission(final Permission arg0) {
        return true;
    }

    /**
     *
     * @param arg0
     * @return
     */
    @Override
    public boolean isPermissionSet(final String arg0) {
        return true;
    }

    /**
     *
     * @param arg0
     * @return
     */
    @Override
    public boolean isPermissionSet(final Permission arg0) {
        return true;
    }

    /**
     *
     */
    @Override
    public void recalculatePermissions() {
    }

    /**
     *
     * @param arg0
     */
    @Override
    public void removeAttachment(final PermissionAttachment arg0) {
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isOp() {
        return true;
    }

    /**
     *
     * @param op
     */
    @Override
    public void setOp(final boolean op) {
    }

    /**
     *
     * @param arg0
     * @return
     */
    @Override
    public PermissionAttachment addAttachment(final Plugin arg0) {
        return null;
    }

    /**
     *
     * @param arg0
     * @param arg1
     * @return
     */
    @Override
    public PermissionAttachment addAttachment(final Plugin arg0, final int arg1) {
        return null;
    }

    /**
     *
     * @param arg0
     * @param arg1
     * @param arg2
     * @return
     */
    @Override
    public PermissionAttachment addAttachment(final Plugin arg0, final String arg1, final boolean arg2) {
        return null;
    }

    /**
     *
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @return
     */
    @Override
    public PermissionAttachment addAttachment(final Plugin arg0, final String arg1, final boolean arg2, final int arg3) {
        return null;
    }
}

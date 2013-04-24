/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC;

import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author cnaude
 */
public class PIRCCommandSender implements CommandSender {
    private final PIRCBot ircBot;
    private String channel;
    
    @Override
    public void sendMessage(String message) {       
        ircBot.sendMessage(channel, message);        
    }
    
    @Override 
    public void sendMessage(String[] messages) {        
    }
    
    public PIRCCommandSender(PIRCBot ircBot, String channel) {
        this.ircBot = ircBot;
        this.channel = channel;
    }
    
    @Override 
    public Server getServer() {
        return Bukkit.getServer();
    }
    
    @Override 
    public String getName() {
        return ircBot.getName();                
    }
    
    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return null;
    }

    @Override
    public boolean hasPermission(final String arg0) {
        return true;
    }

    @Override
    public boolean hasPermission(final Permission arg0) {
        return true;
    }

    @Override
    public boolean isPermissionSet(final String arg0) {
        return true;
    }

    @Override
    public boolean isPermissionSet(final Permission arg0) {
        return true;
    }

    @Override
    public void recalculatePermissions() {
    }

    @Override
    public void removeAttachment(final PermissionAttachment arg0) {
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(final boolean op) {
    }

    @Override
    public PermissionAttachment addAttachment(final Plugin arg0) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(final Plugin arg0, final int arg1) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(final Plugin arg0, final String arg1, final boolean arg2) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(final Plugin arg0, final String arg1, final boolean arg2, final int arg3) {
        return null;
    }
}

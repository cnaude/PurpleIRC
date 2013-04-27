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
 * @author Chris Naude
 * We have to implement our own CommandSender so that we can
 * receive output from the command dispatcher.
 */
public class PIRCCommandSender implements CommandSender {
    private final PIRCBot ircBot;
    private String target;
    private final PIRCMain plugin;
    
    @Override
    public void sendMessage(String message) {             
        ircBot.sendMessage(target, plugin.gameColorsToIrc(message));                
    }
    
    @Override 
    public void sendMessage(String[] messages) {  
        for (String message : messages) {
            ircBot.sendMessage(target, plugin.gameColorsToIrc(message));  
        }
    }
    
    public PIRCCommandSender(PIRCBot ircBot, String target, PIRCMain plugin) {
        this.ircBot = ircBot;
        this.target = target;
        this.plugin = plugin;
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

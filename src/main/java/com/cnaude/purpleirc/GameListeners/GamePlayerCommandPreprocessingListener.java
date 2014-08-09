package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author cnaude
 */
public class GamePlayerCommandPreprocessingListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public GamePlayerCommandPreprocessingListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {         
            return;
        }        
        String msg = event.getMessage();
        if (event.getPlayer().hasPermission("irc.message.gamechat")) {
            if (msg.startsWith("/me ")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.gameAction(event.getPlayer(), msg.replace("/me", ""));
                }
            } else if (msg.startsWith("/broadcast ")) {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    ircBot.gameBroadcast(event.getPlayer(), msg.replace("/broadcast", ""));
                }
            }
        }
        if (plugin.isPluginEnabled("Essentials")) {            
            if (msg.startsWith("/helpop ") || msg.startsWith("/amsg ") || msg.startsWith("/ac ")) {                
                if (msg.contains(" ")) {                    
                    String message = msg.split(" ", 2)[1];                    
                    for (PurpleBot ircBot : plugin.ircBots.values()) {                        
                        ircBot.essHelpOp(event.getPlayer(), message);
                    }
                }
            }
        }
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            if (msg.startsWith("/")) {
                String cmd;
                String params = "";
                if (msg.contains(" ")) {
                    cmd = msg.split(" ", 2)[0];
                    params = msg.split(" ", 2)[1];
                } else {
                    cmd = msg;
                }
                cmd = cmd.substring(0);
                if (ircBot.channelCmdNotifyEnabled && !ircBot.channelCmdNotifyIgnore.contains(cmd)) {
                    ircBot.commandNotify(event.getPlayer(), cmd, params);
                }
            }
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC.IRC;

import me.cnaude.plugin.PurpleIRC.PurpleBot;
import me.cnaude.plugin.PurpleIRC.PIRCMain;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;

/**
 *
 * @author cnaude
 */
public class DisconnectListener extends ListenerAdapter {
    
    PIRCMain plugin;
    PurpleBot ircBot;

    public DisconnectListener(PIRCMain plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }
    
    @Override
    public void onDisconnect(DisconnectEvent event) {                
        PircBotX bot = event.getBot();
        plugin.botConnected.put(bot.getNick(), false);
        plugin.getServer().broadcast("[" + bot.getNick() + "] Disconnected from IRC server.", "irc.message.disconnect");
    }
}

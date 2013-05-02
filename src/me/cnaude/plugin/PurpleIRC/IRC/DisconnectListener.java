/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC.IRC;

import java.util.regex.Matcher;
import me.cnaude.plugin.PurpleIRC.PIRCBot;
import me.cnaude.plugin.PurpleIRC.PIRCMain;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.PartEvent;

/**
 *
 * @author cnaude
 */
public class DisconnectListener extends ListenerAdapter {
    
    PIRCMain plugin;
    PIRCBot ircBot;

    public DisconnectListener(PIRCMain plugin, PIRCBot ircBot) {
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

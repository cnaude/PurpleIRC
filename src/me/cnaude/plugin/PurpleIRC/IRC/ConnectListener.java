/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC.IRC;

import me.cnaude.plugin.PurpleIRC.PIRCBot;
import me.cnaude.plugin.PurpleIRC.PIRCMain;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;

/**
 *
 * @author cnaude
 */
public class ConnectListener extends ListenerAdapter {
    
    PIRCMain plugin;
    PIRCBot ircBot;

    public ConnectListener(PIRCMain plugin, PIRCBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }
    
    @Override
    public void onConnect(ConnectEvent event) {
        PircBotX bot = event.getBot();
        
        plugin.botConnected.put(ircBot.botNick, true);
        for (String channel : ircBot.botChannels.keySet()) {
            if (ircBot.channelAutoJoin.containsKey(channel)) {
                if (ircBot.channelAutoJoin.get(channel)) {
                    plugin.logInfo("Auto joining channel " + ircBot.botChannels.get(channel));
                    bot.joinChannel(ircBot.botChannels.get(channel));
                } else {
                    plugin.logInfo("Not auto joining channel " + ircBot.botChannels.get(channel));
                }
            }
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC.IRC;

import me.cnaude.plugin.PurpleIRC.PurpleBot;
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
    PurpleBot ircBot;

    public ConnectListener(PIRCMain plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }
    
    @Override
    public void onConnect(ConnectEvent event) {
        PircBotX bot = event.getBot();
        
        plugin.botConnected.put(ircBot.botNick, true);
        for (String channelName : ircBot.botChannels) {
            if (ircBot.channelAutoJoin.containsKey(channelName)) {
                if (ircBot.channelAutoJoin.get(channelName)) {
                    plugin.logInfo("Auto joining channel " + channelName);
                    if (ircBot.channelPassword.get(channelName).isEmpty()) {
                        bot.joinChannel(channelName);
                    } else {
                        bot.joinChannel(channelName,ircBot.channelPassword.get(channelName));
                    }
                } else {
                    plugin.logInfo("Not auto joining channel " + channelName);
                }
            }
        }
    }
}

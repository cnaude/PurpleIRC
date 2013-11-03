/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.IRCListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;

/**
 *
 * @author cnaude
 */
public class ConnectListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public ConnectListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onConnect(ConnectEvent event) {
        PircBotX bot = event.getBot();

        plugin.botConnected.put(ircBot.botNick, true);
        if (!ircBot.botIdentPassword.isEmpty()) {
            plugin.logInfo("Sending ident password to NickServ...");
            bot.sendIRC().identify(ircBot.botIdentPassword);
        }
        if (ircBot.sendRawMessageOnConnect) {
            plugin.logInfo("Sending raw message to server");            
            event.respond(ircBot.rawMessage);            
        }
        for (String channelName : ircBot.botChannels) {
            if (ircBot.channelAutoJoin.containsKey(channelName)) {
                if (ircBot.channelAutoJoin.get(channelName)) {
                    String connectMessage = "Automatically joining IRC channel " + channelName;
                    plugin.logInfo(connectMessage);
                    ircBot.broadcastIRCConnect();
                    if (ircBot.channelPassword.get(channelName).isEmpty()) {
                        bot.sendIRC().joinChannel(channelName);
                    } else {
                        bot.sendIRC().joinChannel(channelName, ircBot.channelPassword.get(channelName));
                    }
                } else {
                    plugin.logInfo("Not automatically joining IRC channel " + channelName);
                }
            }
        }
    }
}

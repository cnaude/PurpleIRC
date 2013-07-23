/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.IRCListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.QuitEvent;

/**
 *
 * @author cnaude
 */
public class QuitListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    public QuitListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    @Override
    public void onQuit(QuitEvent event) {
        User user = event.getUser();
        for (String channelName : ircBot.channelNicks.keySet()) {
            if (ircBot.channelNicks.containsKey(user.getNick())) {
                if (ircBot.enabledMessages.get(channelName).contains("irc-quit")) {
                    plugin.getServer().broadcast(plugin.colorConverter.ircColorsToGame(plugin.ircQuit)
                            .replace("%NAME%", user.getNick())
                            .replace("%REASON%", event.getReason())
                            .replace("%CHANNEL%", channelName), "irc.message.quit");
                    if (plugin.netPackets != null) {
                        plugin.netPackets.remFromTabList(user.getNick());
                    }
                }
                ircBot.channelNicks.get(channelName).remove(user.getNick());
            }
        }
    }
}

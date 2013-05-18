/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.IRCListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;

/**
 *
 * @author cnaude
 */
public class ActionListener extends ListenerAdapter {
    
    PurpleIRC plugin;
    PurpleBot ircBot;

    public ActionListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }
        
    
    @Override
    public void onAction(ActionEvent event) {        
        Channel channel = event.getChannel();
        User user = event.getUser();
        PircBotX bot = event.getBot();
        if (!ircBot.botChannels.contains(channel.getName())) {
            return;
        }
        if (ircBot.enabledMessages.get(channel.getName()).contains("irc-action")) {
            plugin.getServer().broadcast(plugin.colorConverter.ircColorsToGame(plugin.ircAction)
                    .replace("%NAME%", user.getNick())
                    .replace("%MESSAGE%", event.getAction())
                    .replace("%CHANNEL%", channel.getName()), "irc.message.action");
        }
    }
}

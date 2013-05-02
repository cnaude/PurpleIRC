/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC.IRC;

import java.util.regex.Matcher;
import me.cnaude.plugin.PurpleIRC.PurpleBot;
import me.cnaude.plugin.PurpleIRC.PIRCMain;
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
    
    PIRCMain plugin;
    PurpleBot ircBot;

    public ActionListener(PIRCMain plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }
        
    
    @Override
    public void onAction(ActionEvent event) {        
        Channel channel = event.getChannel();
        User user = event.getUser();
        PircBotX bot = event.getBot();
        if (!ircBot.botChannels.containsValue(channel.getName())) {
            return;
        }
        if (ircBot.enabledMessages.get(ircBot.channelKeys.get(channel.getName())).contains("irc-action")) {
            plugin.getServer().broadcast(plugin.colorConverter.ircColorsToGame(Matcher.quoteReplacement(plugin.ircAction)
                    .replaceAll("%NAME%", user.getNick())
                    .replaceAll("%MESSAGE%", event.getAction())
                    .replaceAll("%CHANNEL%", channel.getName())), "irc.message.action");
        }
    }
}

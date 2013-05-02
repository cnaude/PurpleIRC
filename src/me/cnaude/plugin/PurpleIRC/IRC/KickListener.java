/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC.IRC;

import java.util.regex.Matcher;
import me.cnaude.plugin.PurpleIRC.PurpleBot;
import me.cnaude.plugin.PurpleIRC.PIRCMain;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.KickEvent;

/**
 *
 * @author cnaude
 */
public class KickListener extends ListenerAdapter {
    
    PIRCMain plugin;
    PurpleBot ircBot;

    public KickListener(PIRCMain plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    @Override
    public void onKick(KickEvent event) {
        Channel channel = event.getChannel();
        User recipient = event.getRecipient();
        User kicker = event.getSource();        
        
        if (!ircBot.botChannels.contains(channel.getName())) {
            return;
        }
        if (ircBot.enabledMessages.get(channel.getName()).contains("irc-kick")) {
            plugin.getServer().broadcast(plugin.colorConverter.ircColorsToGame(Matcher.quoteReplacement(plugin.ircKick)
                    .replaceAll("%NAME%", recipient.getNick())
                    .replaceAll("%REASON%", event.getReason())
                    .replaceAll("%KICKER%", kicker.getNick())
                    .replaceAll("%CHANNEL%", channel.getName())), "irc.message.kick");
        }
    }
}

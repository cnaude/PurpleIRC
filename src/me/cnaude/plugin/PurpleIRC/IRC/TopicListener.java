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
import org.pircbotx.hooks.events.TopicEvent;

/**
 *
 * @author cnaude
 */
public class TopicListener extends ListenerAdapter {
    
    PIRCMain plugin;
    PIRCBot ircBot;

    public TopicListener(PIRCMain plugin, PIRCBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }
    

    
    @Override
    public void onTopic(TopicEvent event) {
        Channel channel = event.getChannel();
        User user = event.getUser();        
        
        if (!ircBot.botChannels.containsValue(channel.getName())) {
            return;
        }
        ircBot.fixTopic(channel, event.getTopic(), event.getUser().getNick());
        if (event.isChanged()) {
            if (ircBot.enabledMessages.get(ircBot.channelKeys.get(channel.getName())).contains("irc-topic")) {
                plugin.getServer().broadcast(plugin.colorConverter.ircColorsToGame(Matcher.quoteReplacement(plugin.ircTopic)
                        .replaceAll("%NAME%", user.getNick())
                        .replaceAll("%TOPIC%", event.getTopic())
                        .replaceAll("%CHANNEL%", channel.getName())), "irc.message.topic");
            }
        }
        ircBot.activeTopic.put(ircBot.channelKeys.get(channel.getName()), event.getTopic());
    }
}

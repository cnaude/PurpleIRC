/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC.IRC;

import me.cnaude.plugin.PurpleIRC.PurpleBot;
import me.cnaude.plugin.PurpleIRC.PIRCMain;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.TopicEvent;

/**
 *
 * @author cnaude
 */
public class TopicListener extends ListenerAdapter {
    
    PIRCMain plugin;
    PurpleBot ircBot;

    public TopicListener(PIRCMain plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }
        
    @Override
    public void onTopic(TopicEvent event) {
        Channel channel = event.getChannel();
        User user = event.getUser();        
        
        if (!ircBot.botChannels.contains(channel.getName())) {
            return;
        }
        ircBot.fixTopic(channel, event.getTopic(), event.getUser().getNick());
        if (event.isChanged()) {
            if (ircBot.enabledMessages.get(channel.getName()).contains("irc-topic")) {
                plugin.getServer().broadcast(plugin.colorConverter.ircColorsToGame(plugin.ircTopic)
                        .replace("%NAME%", user.getNick()
                        .replace("%TOPIC%", event.getTopic())
                        .replace("%CHANNEL%", channel.getName())), "irc.message.topic");
            }
        }
        ircBot.activeTopic.put(channel.getName(), event.getTopic());
    }
}

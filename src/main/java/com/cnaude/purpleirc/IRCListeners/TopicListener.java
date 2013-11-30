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
import org.pircbotx.hooks.events.TopicEvent;

/**
 *
 * @author cnaude
 */
public class TopicListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public TopicListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onTopic(TopicEvent event) {
        Channel channel = event.getChannel();
        String channelName = channel.getName();
        User user = event.getUser();

        if (!ircBot.isValidChannel(channelName)) {
            ircBot.fixTopic(channel, event.getTopic(), event.getUser().getNick());
            if (event.isChanged()) {
                if (ircBot.enabledMessages.get(channelName).contains("irc-topic")) {
                    plugin.getServer().broadcast(plugin.colorConverter.ircColorsToGame(plugin.ircTopic)
                            .replace("%NAME%", user.getNick()
                                    .replace("%TOPIC%", event.getTopic())
                                    .replace("%CHANNEL%", channel.getName())), "irc.message.topic");
                }
            }
            ircBot.activeTopic.put(channelName, event.getTopic());
        }
    }
}

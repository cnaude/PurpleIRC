package com.cnaude.purpleirc.IRCListeners;

import com.cnaude.purpleirc.TemplateName;
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
        User user = event.getUser();

        if (ircBot.isValidChannel(channel.getName())) {
            ircBot.fixTopic(channel, event.getTopic(), event.getUser().getNick());
            if (event.isChanged()) {
                if (ircBot.enabledMessages.get(channel.getName()).contains(TemplateName.IRC_TOPIC)) {
                    String message = plugin.colorConverter.ircColorsToGame(
                            plugin.getMsgTemplate(ircBot.botNick, TemplateName.IRC_TOPIC)
                            .replace("%NAME%", user.getNick())
                            .replace("%TOPIC%", event.getTopic())
                            .replace("%CHANNEL%", channel.getName()));
                    plugin.logDebug("Sending topic notification due to " 
                            + TemplateName.IRC_TOPIC + " being true: " + message);
                    plugin.getServer().broadcast(message, "irc.message.topic");
                }
            }
            ircBot.activeTopic.put(channel.getName(), event.getTopic());
        }
    }
}

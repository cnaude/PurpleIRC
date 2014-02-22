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
import org.pircbotx.hooks.events.JoinEvent;

/**
 *
 * @author cnaude
 */
public class JoinListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public JoinListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onJoin(JoinEvent event) {
        Channel channel = event.getChannel();
        User user = event.getUser();

        if (!ircBot.isValidChannel(channel.getName())) {
            plugin.logDebug("Invalid channel: " + channel.getName());
            plugin.logDebug("Part if invalid: " + ircBot.partInvalidChannels);
            plugin.logDebug("Nick: " + user.getNick());
            if (user.getNick().equals(ircBot.botNick)
                    && ircBot.partInvalidChannels) {
                plugin.logInfo("Leaving invalid channel: " + channel.getName());
                channel.send().part(ircBot.partInvalidChannelsMsg);
            }
            return;
        }
        ircBot.broadcastIRCJoin(user.getNick(), channel.getName());
        ircBot.opFriends(channel, user);
        if (user.getNick().equals(ircBot.botNick)) {
            plugin.logInfo("Joining channel: " + channel.getName());
            plugin.logDebug("Setting channel modes: " + channel.getName() + " => "
                    + ircBot.channelModes.get(channel.getName()));
            channel.send().setMode(ircBot.channelModes.get(channel.getName()));
            ircBot.fixTopic(channel, channel.getTopic(), channel.getTopicSetter());
            ircBot.updateNickList(channel);
        }
        if (plugin.netPackets != null) {
            plugin.netPackets.addToTabList(user.getNick(), ircBot, channel);
        }
            }
        }

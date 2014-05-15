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
        String channelName = channel.getName();
        User user = event.getUser();

        if (!ircBot.isValidChannel(channel.getName())) {
            plugin.logDebug("Invalid channel: " + channelName);
            plugin.logDebug("Part if invalid: " + ircBot.partInvalidChannels);
            plugin.logDebug("Nick: " + user.getNick());
            if (user.getNick().equals(ircBot.botNick)
                    && ircBot.partInvalidChannels) {
                plugin.logInfo("Leaving invalid channel: " + channel.getName());
                channel.send().part(ircBot.partInvalidChannelsMsg);
            }
            return;
        }
        ircBot.broadcastIRCJoin(user, channel);
        ircBot.opFriends(channel, user);
        ircBot.voiceFriends(channel, user);
        if (user.getNick().equals(ircBot.botNick)) {
            plugin.logInfo("Joining channel: " + channelName);
            plugin.logDebug("Setting channel modes: " + channelName + " => "
                    + ircBot.channelModes.get(channel.getName()));
            channel.send().setMode(ircBot.channelModes.get(channelName));
            ircBot.fixTopic(channel, channel.getTopic(), channel.getTopicSetter());
            ircBot.updateNickList(channel);
            if (ircBot.msgOnJoin.containsKey(channelName) && ircBot.joinMsg.containsKey(channelName)) {
                if (ircBot.msgOnJoin.get(channelName) && !ircBot.joinMsg.get(channelName).isEmpty()) {
                    plugin.logDebug("Sending on join message to IRC server: " + ircBot.joinMsg.get(channelName));
                    ircBot.asyncRawlineNow(ircBot.joinMsg.get(channelName));
                }
            }
        }
        if (plugin.netPackets != null) {
            plugin.netPackets.addToTabList(user.getNick(), ircBot, channel);
        }
    }
}

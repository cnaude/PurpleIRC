package com.cnaude.purpleirc;

import org.pircbotx.Channel;

/**
 *
 * @author Chris Naude
 * This thread checks each for users and updates the internal lists.
 */
public class ChannelWatcher {
    
    private final PurpleIRC plugin;
    private int taskID;
    
    public ChannelWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;

        taskID = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                //plugin.logDebug("Checking connection status of IRC bots.");
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    if (plugin.botConnected.get(ircBot.botNick)) {
                        for (Channel channel : ircBot.bot.getChannels()) {
                            ircBot.updateNickList(channel);
                        }
                    }
                }
            }
        }, plugin.ircChannelCheckInterval, plugin.ircChannelCheckInterval);
    }
    
    public void cancel() {
        this.plugin.getServer().getScheduler().cancelTask(taskID);        
    }
}

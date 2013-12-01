package com.cnaude.purpleirc.Watchers;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.scheduler.BukkitTask;
import org.pircbotx.Channel;

/**
 *
 * @author Chris Naude
 * This thread checks each for users and updates the internal lists.
 */
public class ChannelWatcher {
    
    private final PurpleIRC plugin;
    private final BukkitTask bt;
    
    /**
     *
     * @param plugin
     */
    public ChannelWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;

        bt = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    if (ircBot.isConnected()) {
                        for (Channel channel : ircBot.getChannels()) {
                            ircBot.updateNickList(channel);
                        }
                    }
                }
            }
        }, plugin.ircChannelCheckInterval, plugin.ircChannelCheckInterval);
    }
    
    /**
     *
     */
    public void cancel() {
        bt.cancel();
    }
}

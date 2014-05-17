package com.cnaude.purpleirc;

import org.bukkit.scheduler.BukkitTask;

/** This thread checks each bot for connectivity and reconnects when appropriate.
 *
 * @author Chris Naude
 * 
 */
public class BotWatcher {
    
    private final PurpleIRC plugin;
    private final BukkitTask bt;
    
    /**
     *
     * @param plugin
     */
    public BotWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;

        bt = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                //plugin.logDebug("Checking connection status of IRC bots.");
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    if (ircBot.isConnectedBlocking()) {
                        plugin.logDebug("[" + ircBot.botNick + "] CONNECTED");
                        ircBot.setConnected(true);
                    } else {
                        ircBot.setConnected(false);
                        if (ircBot.autoConnect) {
                            plugin.logInfo("[" + ircBot.botNick + "] NOT CONNECTED");
                            ircBot.reload();
                        }
                    }
                }
            }
        }, plugin.ircConnCheckInterval, plugin.ircConnCheckInterval);
    }
    
    /**
     *
     */
    public void cancel() {
        bt.cancel();
    }

}
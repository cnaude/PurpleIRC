package com.cnaude.purpleirc;

/**
 *
 * @author Chris Naude
 * This thread checks each bot for connectivity and reconnects when appropriate.
 */
public class BotWatcher {
    
    private final PurpleIRC plugin;
    private final int taskID;
    
    public BotWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;

        taskID = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                plugin.logDebug("Checking connection status of IRC bots.");
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    if (!plugin.botConnected.get(ircBot.botNick)) {
                        if (ircBot.autoConnect) {
                            plugin.logInfo("IRC bot '" + ircBot.bot.getName() + "' is not connected! Attempting reconnect...");
                            ircBot.asyncReConnect();
                        }
                    } else {
                        plugin.logDebug("IRC bot '" + ircBot.bot.getName() + "' is connected!");
                    }
                }
            }
        }, plugin.ircConnCheckInterval, plugin.ircConnCheckInterval);
    }
    
    public void cancel() {
        this.plugin.getServer().getScheduler().cancelTask(taskID);        
    }

}

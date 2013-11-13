package com.cnaude.purpleirc;

/** This thread checks each bot for connectivity and reconnects when appropriate.
 *
 * @author Chris Naude
 * 
 */
public class BotWatcher {
    
    private final PurpleIRC plugin;
    private final int taskID;
    
    /**
     *
     * @param plugin
     */
    public BotWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;

        taskID = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                plugin.logDebug("Checking connection status of IRC bots.");
                for (PurpleBot ircBot : plugin.ircBots.values()) {
                    if (ircBot.isConnected()) {
                        plugin.logDebug("[" + ircBot.botNick + "] CONNECTED");
                    } else {
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
        this.plugin.getServer().getScheduler().cancelTask(taskID);        
    }

}
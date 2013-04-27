package me.cnaude.plugin.PurpleIRC;

/**
 *
 * @author Chris Naude
 * This thread checks each bot for connectivity and reconnects when appropriate.
 */
public class PIRCBotWatcher {
    
    private final PIRCMain plugin;
    private int taskID;
    
    public PIRCBotWatcher(final PIRCMain plugin) {
        this.plugin = plugin;

        taskID = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                plugin.logDebug("Checking connection status of IRC bots.");
                for (PIRCBot ircBot : plugin.ircBots.values()) {
                    if (!ircBot.isConnected()) {
                        if (ircBot.autoConnect) {
                            plugin.logInfo("IRC bot '" + ircBot.getName() + "' is not connected! Attempting reconnect...");
                            ircBot.asyncConnect(plugin.getServer().getConsoleSender(), true);
                        }
                    } else {
                        plugin.logDebug("IRC bot '" + ircBot.getName() + "' is connected!");
                    }
                }
            }
        }, plugin.ircConnCheckInterval, plugin.ircConnCheckInterval);
    }
    
    public void cancel() {
        this.plugin.getServer().getScheduler().cancelTask(taskID);        
    }

}

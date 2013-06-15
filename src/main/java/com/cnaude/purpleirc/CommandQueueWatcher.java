package com.cnaude.purpleirc;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Chris Naude
 * Poll the command queue and dispatch to Bukkit
 */
public class CommandQueueWatcher {
    
    private final PurpleIRC plugin;
    private int taskID;
    private Queue<IRCCommand> queue = new ConcurrentLinkedQueue<IRCCommand>();
    
    public CommandQueueWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;

        taskID = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                plugin.logDebug("Checking IRC command queue.");
                IRCCommand ircCommand = queue.poll();
                if (ircCommand == null) {
                    plugin.logDebug("Command queue is empty.");
                } else {
                    plugin.getServer().dispatchCommand(ircCommand.getIRCCommandSender(), ircCommand.getGameCommand());
                    plugin.logDebug("Dispatching command from queue: " + ircCommand.getGameCommand());
                }
                
            }
        }, 20, 20);
    }
    
    public void cancel() {
        this.plugin.getServer().getScheduler().cancelTask(taskID);        
    }
    
    public void add(IRCCommand command) {
        queue.offer(command);
    }

}

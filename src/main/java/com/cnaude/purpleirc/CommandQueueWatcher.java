package com.cnaude.purpleirc;

import com.cnaude.purpleirc.Events.IRCCommandEvent;
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
                IRCCommand ircCommand = queue.poll();
                if (ircCommand != null) {
                    plugin.getServer().dispatchCommand(ircCommand.getIRCCommandSender(), ircCommand.getGameCommand());
                    plugin.getServer().getPluginManager().callEvent(new IRCCommandEvent(ircCommand));
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

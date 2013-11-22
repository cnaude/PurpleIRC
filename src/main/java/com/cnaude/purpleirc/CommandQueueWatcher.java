package com.cnaude.purpleirc;

import com.cnaude.purpleirc.Events.IRCCommandEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Chris Naude
 * Poll the command queue and dispatch to Bukkit
 */
public class CommandQueueWatcher {
    
    private final PurpleIRC plugin;
    private final BukkitTask bt;
    private Queue<IRCCommand> queue = new ConcurrentLinkedQueue<IRCCommand>();
    
    /**
     *
     * @param plugin
     */
    public CommandQueueWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;

        bt = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new Runnable() {        
            @Override
            public void run() {                
                IRCCommand ircCommand = queue.poll();
                if (ircCommand != null) {                   
                    plugin.getServer().dispatchCommand(ircCommand.getIRCCommandSender(), ircCommand.getGameCommand());                   
                    plugin.getServer().getPluginManager().callEvent(new IRCCommandEvent(ircCommand));                    
                }
            }
        }, 0, 60);
    }
    
    /**
     *
     */
    public void cancel() {
        bt.cancel();
    }
    
    public String clearQueue() {
        int size = queue.size();
        if (!queue.isEmpty()) {          
            queue.clear();
        } 
        return "Elements removed from command queue: " + size;
    }
    
    /**
     *
     * @param command
     */
    public void add(IRCCommand command) {
        queue.offer(command);
    }

}

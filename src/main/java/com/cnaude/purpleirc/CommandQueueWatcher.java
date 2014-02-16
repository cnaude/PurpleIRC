package com.cnaude.purpleirc;

import com.cnaude.purpleirc.Events.IRCCommandEvent;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Chris Naude Poll the command queue and dispatch to Bukkit
 */
public class CommandQueueWatcher {

    private final PurpleIRC plugin;
    private int bt;
    private final Queue<IRCCommand> queue = new ConcurrentLinkedQueue<IRCCommand>();

    /**
     *
     * @param plugin
     */
    public CommandQueueWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;
        startWatcher();
    }

    private void startWatcher() {
        plugin.logDebug("Starting command queue");
        bt = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                queueAndSend();
            }
        }, 0, 5);
    }

    private void queueAndSend() {
        IRCCommand ircCommand = queue.poll();
        if (ircCommand != null) {
            plugin.getServer().dispatchCommand(ircCommand.getIRCCommandSender(), ircCommand.getGameCommand());
            plugin.getServer().getPluginManager().callEvent(new IRCCommandEvent(ircCommand));
        }
    }

    /**
     *
     */
    public void cancel() {
        this.plugin.getServer().getScheduler().cancelTask(bt);
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
        plugin.logDebug("Adding command to queue: " + command.getGameCommand());
        queue.offer(command);        
    }
}

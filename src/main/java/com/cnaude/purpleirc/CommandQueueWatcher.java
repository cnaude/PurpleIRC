package com.cnaude.purpleirc;

import com.cnaude.purpleirc.Events.IRCCommandEvent;
import com.cnaude.purpleirc.IRCCommand;
import com.cnaude.purpleirc.PurpleIRC;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Chris Naude Poll the command queue and dispatch to Bukkit
 */
public class CommandQueueWatcher {

    private final PurpleIRC plugin;
    private BukkitTask bt;
    private final BlockingQueue<IRCCommand> queue = new LinkedBlockingQueue<IRCCommand>();

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
        bt = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new Runnable() {
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
        plugin.logDebug("Adding command to queue: " + command.getGameCommand());
        queue.offer(command);        
    }
}

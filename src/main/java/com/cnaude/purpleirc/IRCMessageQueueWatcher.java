package com.cnaude.purpleirc;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author Chris Naude Poll the command queue and dispatch to Bukkit
 */
public class IRCMessageQueueWatcher {

    private final PurpleIRC plugin;
    private final BukkitTask bt;
    private Queue<IRCMessage> queue = new ConcurrentLinkedQueue<IRCMessage>();

    /**
     *
     * @param plugin
     */
    public IRCMessageQueueWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;

        bt = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                IRCMessage ircMessage = queue.poll();   
                if (ircMessage != null) {
                    if (ircMessage.ctcpResponse) {
                        ircMessage.ircBot.blockingCTCPMessage(ircMessage.target, ircMessage.message);
                    } else {
                        ircMessage.ircBot.blockingIRCMessage(ircMessage.target, ircMessage.message);                        
                    }
                }
            }
        }, 5L, 5L);
    }

    /**
     *
     */
    public void cancel() {
        bt.cancel();
    }

    /**
     *
     * @param ircMessage
     */
    public void add(IRCMessage ircMessage) {
        queue.offer(ircMessage);
        plugin.logDebug("[" + queue.size() + "] Adding message to queue.");
    }

}

package com.cnaude.purpleirc;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Chris Naude Poll the command queue and dispatch to Bukkit
 */
public class IRCMessageQueueWatcher {

    private final PurpleIRC plugin;
    private final int taskID;
    private Queue<IRCMessage> queue = new ConcurrentLinkedQueue<IRCMessage>();

    /**
     *
     * @param plugin
     */
    public IRCMessageQueueWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;

        taskID = this.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable() {
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
        }, 20, 20);
    }

    /**
     *
     */
    public void cancel() {
        this.plugin.getServer().getScheduler().cancelTask(taskID);
    }

    /**
     *
     * @param ircMessage
     */
    public void add(IRCMessage ircMessage) {
        queue.offer(ircMessage);
    }

}

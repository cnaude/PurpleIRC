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
    private final PurpleBot ircBot;
    private BukkitTask bt;
    private final Queue<IRCMessage> queue = new ConcurrentLinkedQueue<IRCMessage>();

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public IRCMessageQueueWatcher(final PurpleBot ircBot, final PurpleIRC plugin) {
        this.plugin = plugin;
        this.ircBot = ircBot;
        startWatcher();
    }

    private void startWatcher() {
        bt = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new Runnable() {
            @Override
            public void run() {
                queueAndSend();
            }
        }, 0, 5);
    }

    private void queueAndSend() {
        IRCMessage ircMessage = queue.poll();
        if (ircMessage != null) {
            if (ircMessage.ctcpResponse) {
                ircBot.blockingCTCPMessage(ircMessage.target, ircMessage.message);
            } else {
                ircBot.blockingIRCMessage(ircMessage.target, ircMessage.message);
            }
            try {
                Thread.sleep(ircBot.chatDelay);
            } catch (InterruptedException ex) {
                plugin.logError(ex.getMessage());
            }
        }

    }

    public void cancel() {
        bt.cancel();
    }

    public String clearQueue() {
        int size = queue.size();
        if (!queue.isEmpty()) {
            queue.clear();
        }
        return "Elements removed from message queue: " + size;
    }

    /**
     *
     * @param ircMessage
     */
    public void add(IRCMessage ircMessage) {
        queue.offer(ircMessage);
        queueAndSend();
    }
}

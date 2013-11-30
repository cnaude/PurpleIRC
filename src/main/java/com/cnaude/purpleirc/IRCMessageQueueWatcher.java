package com.cnaude.purpleirc;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author Chris Naude Poll the command queue and dispatch to Bukkit
 */
public class IRCMessageQueueWatcher {

    private final PurpleIRC plugin;
    private final PurpleBot ircBot;
    private final Queue<IRCMessage> queue = new ConcurrentLinkedQueue<IRCMessage>();
    Timer timer;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public IRCMessageQueueWatcher(final PurpleBot ircBot, final PurpleIRC plugin) {
        this.plugin = plugin;
        this.ircBot = ircBot;
        timer = new Timer();
        timer.schedule(new WatcherTask(), 0, ircBot.chatDelay);
        plugin.logDebug("Message queue initialized for bot " + ircBot.botNick
                + ". Interval: " + ircBot.chatDelay + "ms");
    }

    class WatcherTask extends TimerTask {

        @Override
        public void run() {
            queueAndSend();
        }
    }

    private void queueAndSend() {
        IRCMessage ircMessage = queue.poll();
        if (ircMessage != null) {
            if (ircMessage.ctcpResponse) {
                ircBot.blockingCTCPMessage(ircMessage.target, ircMessage.message);
            } else {
                ircBot.blockingIRCMessage(ircMessage.target, ircMessage.message);
            }
        }
    }

    /**
     *
     */
    public void cancel() {
        timer.cancel();
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
        plugin.logDebug("[" + queue.size() + "] Adding message to queue.");
        if (queue.size() == 1) {
            queueAndSend();
        }
    }

}

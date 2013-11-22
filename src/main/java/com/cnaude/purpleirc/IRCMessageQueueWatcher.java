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
    private final Queue<IRCMessage> queue = new ConcurrentLinkedQueue<IRCMessage>();
    Timer timer;

    /**
     *
     * @param plugin
     */
    public IRCMessageQueueWatcher(final PurpleIRC plugin) {
        this.plugin = plugin;
        timer = new Timer();
        timer.schedule(new WatcherTask(), 0, 1000);
    }

    class WatcherTask extends TimerTask {

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
    }

}

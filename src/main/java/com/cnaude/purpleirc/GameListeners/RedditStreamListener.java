package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import io.github.wolf_359.RedditBroadcastEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class RedditStreamListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public RedditStreamListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onRedditBroadcastEvent(RedditBroadcastEvent event) {
        plugin.logDebug("onRedditBroadcastEvent caught");
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.redditStreamBroadcast(event.getMessage());
        }
    }
}

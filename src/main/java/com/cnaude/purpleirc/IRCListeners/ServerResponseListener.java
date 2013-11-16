/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.IRCListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.pircbotx.ReplyConstants;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ServerResponseEvent;

/**
 *
 * @author cnaude
 */
public class ServerResponseListener extends ListenerAdapter {

    PurpleIRC plugin;
    PurpleBot ircBot;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public ServerResponseListener(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    /**
     *
     * @param event
     */
    @Override
    public void onServerResponse(ServerResponseEvent event) {
        int serverReply = event.getCode();
        
        if (serverReply == ReplyConstants.ERR_BADCHANNELKEY) {
            plugin.logInfo("Bad channel password.");
        }
        
        if (serverReply == ReplyConstants.ERR_BANNEDFROMCHAN) {
            plugin.logInfo("Banned from the channel.");
        }
        
        //plugin.logDebug("Server response: " + event.getRawLine());
        
    }
}

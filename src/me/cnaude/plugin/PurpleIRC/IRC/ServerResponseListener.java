/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC.IRC;

import me.cnaude.plugin.PurpleIRC.PIRCMain;
import me.cnaude.plugin.PurpleIRC.PurpleBot;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ServerResponseEvent;
import org.pircbotx.ReplyConstants;

/**
 *
 * @author cnaude
 */
public class ServerResponseListener extends ListenerAdapter {

    PIRCMain plugin;
    PurpleBot ircBot;

    public ServerResponseListener(PIRCMain plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    @Override
    public void onServerResponse(ServerResponseEvent event) {
        int serverReply = event.getCode();
        
        if (serverReply == ReplyConstants.ERR_BADCHANNELKEY) {
            plugin.logInfo("Bad channel password.");
        }
        
        if (serverReply == ReplyConstants.ERR_BANNEDFROMCHAN) {
            plugin.logInfo("Banned from the channel.");
        }        
    }
}

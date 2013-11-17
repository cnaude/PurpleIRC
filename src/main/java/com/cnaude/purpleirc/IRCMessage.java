package com.cnaude.purpleirc;

/**
 *
 * @author cnaude
 */
public class IRCMessage {

    PurpleBot ircBot;
    PurpleIRC plugin;
    String target;
    String message;
    boolean ctcpResponse;

    public IRCMessage(PurpleBot ircBot, PurpleIRC plugin, String target,
            String message, boolean ctcpResponse) {
        this.ircBot = ircBot;
        this.plugin = plugin;
        this.target = target;
        this.message = message;
        this.ctcpResponse = ctcpResponse;
    }
}

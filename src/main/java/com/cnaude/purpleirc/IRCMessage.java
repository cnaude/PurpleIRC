package com.cnaude.purpleirc;

/**
 *
 * @author cnaude
 */
public class IRCMessage {

    String target;
    String message;
    boolean ctcpResponse;

    public IRCMessage(String target, String message, boolean ctcpResponse) {
        this.target = target;
        this.message = message;
        this.ctcpResponse = ctcpResponse;
    }
}

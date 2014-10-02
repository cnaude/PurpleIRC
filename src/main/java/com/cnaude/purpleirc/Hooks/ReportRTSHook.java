package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.PurpleIRC;
import com.nyancraft.reportrts.RTSFunctions;
import com.nyancraft.reportrts.util.Message;

/**
 *
 * @author cnaude
 */
public class ReportRTSHook {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public ReportRTSHook(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public void modBroadcast(String name, String message) {
        RTSFunctions.messageMods(Message.parse("broadcastMessage", name, message), false);
        plugin.logDebug("RTSMB: " + message);
    }
}

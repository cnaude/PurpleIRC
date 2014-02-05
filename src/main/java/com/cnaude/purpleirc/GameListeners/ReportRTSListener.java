/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.nyancraft.reportrts.event.ReportAssignEvent;
import com.nyancraft.reportrts.event.ReportClaimEvent;
import com.nyancraft.reportrts.event.ReportCompleteEvent;
import com.nyancraft.reportrts.event.ReportCreateEvent;
import com.nyancraft.reportrts.event.ReportHoldEvent;
import com.nyancraft.reportrts.event.ReportModBroadcastEvent;
import com.nyancraft.reportrts.event.ReportReopenEvent;
import com.nyancraft.reportrts.event.ReportUnclaimEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class ReportRTSListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public ReportRTSListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onReportCreateEvent(ReportCreateEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getRequest().getName(),
                    event.getRequest(), ircBot.botNick, "rts-notify");
        }
    }

    @EventHandler
    public void onReportCompleteEvent(ReportCompleteEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getRequest().getName(),
                    event.getRequest(), ircBot.botNick, "rts-complete");
        }
    }

    @EventHandler
    public void onReportClaimEvent(ReportClaimEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getRequest().getName(),
                    event.getRequest(), ircBot.botNick, "rts-claim");
        }
    }

    @EventHandler
    public void onReportUnclaimEvent(ReportUnclaimEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getRequest().getName(),
                    event.getRequest(), ircBot.botNick, "rts-unclaim");
        }
    }

    @EventHandler
    public void onReportHoldEvent(ReportHoldEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getRequest().getName(),
                    event.getRequest(), ircBot.botNick, "rts-held");
        }
    }

    @EventHandler
    public void onReportAssignEvent(ReportAssignEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getRequest().getName(),
                    event.getRequest(), ircBot.botNick, "rts-assign");
        }
    }

    @EventHandler
    public void onReportReopenEvent(ReportReopenEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getRequest().getName(),
                    event.getRequest(), ircBot.botNick, "rts-reopen");
        }
    }

    @EventHandler
    public void onModBroadcastEvent(ReportModBroadcastEvent event) {
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.reportRTSNotify(event.getSender(),
                    event.getMessage(), ircBot.botNick, "rts-modbroadcast");
        }
    }
}

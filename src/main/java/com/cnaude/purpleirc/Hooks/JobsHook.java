/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.TemplateName;
import com.cnaude.purpleirc.PurpleIRC;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import me.zford.jobs.Jobs;
import me.zford.jobs.container.Job;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class JobsHook {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public JobsHook(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    public String getPlayerJob(Player player, boolean shortName) {
        return getPlayerJob(player.getName(), shortName);
    }

    public String getPlayerJob(String player, boolean shortName) {
        ArrayList<String> j = new ArrayList<String>();
        if (plugin.isJobsEnabled()) {
            for (Job job : Jobs.getJobs()) {
                if (Jobs.getPlayerManager().getJobsPlayer(player)
                        .isInJob(job)) {
                    if (shortName) {
                        j.add(job.getShortName());
                    } else {
                        j.add(job.getName());
                    }
                }
            }
            if (!j.isEmpty()) {
                return Joiner.on(plugin.getMsgTemplate(TemplateName.JOBS_SEPARATOR)).join(j);
            }
        }
        return "";
    }
}

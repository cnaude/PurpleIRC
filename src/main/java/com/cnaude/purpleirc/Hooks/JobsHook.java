/*
 * Copyright (C) 2014 cnaude
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cnaude.purpleirc.Hooks;

import com.cnaude.purpleirc.TemplateName;
import com.cnaude.purpleirc.PurpleIRC;
import com.google.common.base.Joiner;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import me.zford.jobs.Jobs;
import me.zford.jobs.container.Job;
import me.zford.jobs.container.JobsPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class JobsHook {

    private final PurpleIRC plugin;
    private final Method getJobsPlayer;

    /**
     *
     * @param plugin
     */
    public JobsHook(PurpleIRC plugin) {
        this.plugin = plugin;
        Method m = null;
        try {
            m = Jobs.class.getMethod("getJobsPlayer");
        } catch (NoSuchMethodException | SecurityException e) {
            // doesn't matter
        }
        getJobsPlayer = m;
    }

    public String getPlayerJob(Player player, boolean shortName) {
        if (player != null) {
            ArrayList<String> j = new ArrayList<>();
            if (plugin.isPluginEnabled("Jobs")) {
                try {
                    for (Job job : Jobs.getJobs()) {
                        if (((JobsPlayer) getJobsPlayer.invoke(player)).isInJob(job)) {
                            if (shortName) {
                                j.add(job.getShortName());
                            } else {
                                j.add(job.getName());
                            }
                        }
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    plugin.logError("Problem with Jobs hook: " + ex.getMessage());
                }
                if (!j.isEmpty()) {
                    return Joiner.on(plugin.getMsgTemplate(TemplateName.JOBS_SEPARATOR)).join(j);
                }
            }
        }
        return "";
    }
}

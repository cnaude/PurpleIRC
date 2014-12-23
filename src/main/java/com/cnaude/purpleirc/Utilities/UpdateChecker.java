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
package com.cnaude.purpleirc.Utilities;

import com.cnaude.purpleirc.PurpleIRC;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author cnaude
 */
public class UpdateChecker {

    PurpleIRC plugin;

    private BukkitTask bt;
    private int newVersion = 0;
    private int currentVersion = 0;
    private String currentVersionTitle = "";
    private String newVersionTitle = "";

    /**
     *
     * @param plugin
     */
    public UpdateChecker(PurpleIRC plugin) {
        this.plugin = plugin;
        currentVersionTitle = plugin.getDescription().getVersion();
        try {
            currentVersion = Integer.valueOf(currentVersionTitle.split("-")[1]);
        } catch (NumberFormatException e) {
            currentVersion = 0;
        }
        startUpdateChecker();
    }

    private void startUpdateChecker() {
        plugin.logDebug("Starting update checker");
        bt = this.plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.logInfo("Checking for Updates ... ");
                newVersion = updateCheck(currentVersion);
                if (newVersion > currentVersion) {
                    plugin.logInfo("Stable Version: " + newVersionTitle + " is out!" + " You are still running version: " + currentVersionTitle);
                    plugin.logInfo("Update at: http://dev.bukkit.org/server-mods/purpleirc");
                } else if (currentVersion > newVersion) {
                    plugin.logInfo("Stable Version: " + newVersionTitle + " | Current Version: " + currentVersionTitle);
                } else {
                    plugin.logInfo("No new version available");
                }
            }
        }, 0, 432000);
    }

    public int updateCheck(int currentVersion) {
        try {
            URL url = new URL("https://api.curseforge.com/servermods/files?projectids=56773");
            URLConnection conn = url.openConnection();
            conn.setReadTimeout(5000);
            conn.addRequestProperty("User-Agent", "PurpleIRC Update Checker");
            conn.setDoOutput(true);
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            final String response = reader.readLine();
            final JSONArray array = (JSONArray) JSONValue.parse(response);
            if (array.size() == 0) {
                plugin.logInfo("No files found, or Feed URL is bad.");
                return currentVersion;
            }
            newVersionTitle = ((String) ((JSONObject) array.get(array.size() - 1)).get("name")).trim();
            plugin.logDebug("newVersionTitle: " + newVersionTitle);
            int t = Integer.valueOf(newVersionTitle.split("-")[1]);
            plugin.logDebug("t: " + t);
            return t;
        } catch (IOException | NumberFormatException e) {
            plugin.logInfo("There was an issue attempting to check for the latest version: " + e.getMessage());
        }
        return currentVersion;
    }

    public void cancel() {
        bt.cancel();
    }
}

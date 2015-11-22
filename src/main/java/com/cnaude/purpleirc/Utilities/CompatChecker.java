/*
 * Copyright (C) 2015 cnaude
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

/**
 *
 * @author Chris Naude
 */
public class CompatChecker {
    public static boolean isCompatible(PurpleIRC plugin) {
        if (plugin.getServer().getVersion().contains("Spigot") && plugin.getServer().getVersion().contains("MC: 1.8")) {
            plugin.logError("This plugin is not compatible with Spigot 1.8. Please download the Spigot version from the Spigot site.");            
            return false;
        }
        return true;
    }
}

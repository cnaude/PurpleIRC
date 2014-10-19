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

import com.cnaude.purpleirc.PurpleIRC;
import com.nullblock.vemacs.Shortify.common.CommonConfiguration;
import com.nullblock.vemacs.Shortify.common.ShortifyException;
import com.nullblock.vemacs.Shortify.util.ShortifyUtility;
import java.io.File;

/**
 *
 * @author cnaude
 */
public class ShortifyHook {

    private final PurpleIRC plugin;
    private final CommonConfiguration configuration;

    /**
     *
     * @param plugin
     */
    public ShortifyHook(PurpleIRC plugin) {
        this.plugin = plugin;
        configuration = ShortifyUtility.loadCfg(new File("plugins/Shortify/config.yml"));
    }

    public String shorten(String message) {
        String m = message;
        try {
            m = ShortifyUtility.shortenAll(message, Integer.valueOf(
                    configuration.getString("minlength", "20")), ShortifyUtility.getShortener(configuration), "");
        } catch (ShortifyException ex) {
            plugin.logError(ex.getMessage());
        }
        return m;
    }

}

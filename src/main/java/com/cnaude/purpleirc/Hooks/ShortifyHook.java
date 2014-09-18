/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
                    configuration.getString("minlength", "20")),
                    ShortifyUtility.getShortener(configuration), "");
        } catch (ShortifyException ex) {
            plugin.logError(ex.getMessage());
        }
        return m;
    }

}

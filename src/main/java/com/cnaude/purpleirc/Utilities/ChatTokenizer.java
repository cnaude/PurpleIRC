/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Utilities;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import org.bukkit.entity.Player;

/**
 *
 * @author cnaude
 */
public class ChatTokenizer {

    PurpleIRC plugin;
    PurpleBot ircBot;

    public ChatTokenizer(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    public String chatIRCTokenizer(String nick, String channelName, String template) {
        return ircBot.plugin.colorConverter.ircColorsToGame(template
                .replace("%NAME%", nick).replace("%CHANNEL%", channelName));
    }

    public String chatIRCTokenizer(String nick, String channelName, String template, String message) {
        return ircBot.plugin.colorConverter.ircColorsToGame(template
                .replace("%NAME%", nick).replace("%MESSAGE%", message).replace("%CHANNEL%", channelName));
    }
    
        public String chatTokenizer(String pName, String template, String message) {
        return plugin.colorConverter.gameColorsToIrc(template
                .replace("%NAME%", pName)
                .replace("%MESSAGE%", plugin.colorConverter.gameColorsToIrc(message)));
    }

    public String chatTokenizer(Player player, String template, String message) {
        String pPrefix = plugin.getPlayerPrefix(player);
        if (pPrefix == null) {
            pPrefix = "";
        }
        String gPrefix = plugin.getGroupPrefix(player);
        if (gPrefix == null) {
            gPrefix = "";
        }
        String group = plugin.getPlayerGroup(player);
        if (group == null) {
            group = "";
        }
        return plugin.colorConverter.gameColorsToIrc(template
                .replace("%NAME%", player.getName())
                .replace("%GROUP%", group)
                .replace("%MESSAGE%", message)
                .replace("%PLAYERPREFIX%", pPrefix)
                .replace("%GROUPPREFIX%", gPrefix)
                .replace("%WORLD%", player.getWorld().getName()));
    }

    public String chatMcMMOTokenizer(Player player, String template, String message, String partyName) {
        return chatTokenizer(player, template, message)
                .replace("%PARTY%", partyName);
    }

    public String chatFactionTokenizer(Player player, String message, String chatTag, String chatMode) {
        String template;
        if (chatMode.equals("public")) {
            template = plugin.factionPublicChat;
        } else if (chatMode.equals("ally")) {
            template = plugin.factionAllyChat;
        } else if (chatMode.equals("enemy")) {
            template = plugin.factionEnemyChat;
        } else {
            return "";
        }
        return chatTokenizer(player, template, message)
                .replace("%FACTIONTAG%", chatTag)
                .replace("%FACTIONMODE%", chatMode);
    }

    public String chatHeroTokenizer(Player player, String message, String hChannel, String hNick) {
        return chatTokenizer(player, plugin.heroChat, message)
                .replace("%HEROCHANNEL%", hChannel)
                .replace("%HERONICK%", hNick)
                .replace("%CHANNEL%", hChannel);
    }

    public String chatTokenizer(String template, String message) {
        return plugin.colorConverter.gameColorsToIrc(template
                .replace("%MESSAGE%", message));
    }
    
    
}

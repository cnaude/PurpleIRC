/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Utilities;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.dthielke.herochat.ChannelManager;
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

    // IRC to game chat tokenizer without a message
    public String chatIRCTokenizer(String nick, String channelName, String template) {
        return ircBot.plugin.colorConverter.ircColorsToGame(template
                .replace("%NAME%", nick)
                .replace("%CHANNEL%", channelName));
    }

    // IRC to Hero chat tokenizer without a message
    public String ircChatToHeroChatTokenizer(String nick, String channelName, String template, ChannelManager channelManager, String hChannel) {
        return ircBot.plugin.colorConverter.ircColorsToGame(template
                .replace("%HEROCHANNEL%", hChannel)
                .replace("%HERONICK%", channelManager.getChannel(hChannel).getNick())
                .replace("%HEROCOLOR%", channelManager.getChannel(hChannel).getColor().toString())
                .replace("%NAME%", nick)
                .replace("%CHANNEL%", channelName));
    }

    // Normal IRC to game chat tokenizer
    public String ircChatToGameTokenizer(String nick, String channelName, String template, String message) {
        return ircBot.plugin.colorConverter.ircColorsToGame(template
                .replace("%NAME%", nick)
                .replace("%MESSAGE%", message)
                .replace("%CHANNEL%", channelName));
    }

    // IRC to Hero chat channel tokenizer
    public String ircChatToHeroChatTokenizer(String ircNick, String ircChannelName, String template, String message, ChannelManager channelManager, String hChannel) {
        return ircBot.plugin.colorConverter.ircColorsToGame(template
                .replace("%HEROCHANNEL%", hChannel)
                .replace("%HERONICK%", channelManager.getChannel(hChannel).getNick())
                .replace("%HEROCOLOR%", channelManager.getChannel(hChannel).getColor().toString())
                .replace("%NAME%", ircNick)
                .replace("%MESSAGE%", message)
                .replace("%CHANNEL%", ircChannelName));
    }

    // Kick message
    public String ircKickToHeroChatTokenizer(String recipient, String kicker, String reason, String channelName, String template) {
        return ircBot.plugin.colorConverter.ircColorsToGame(template
                .replace("%NAME%", recipient)
                .replace("%REASON%", reason)
                .replace("%KICKER%", kicker)
                .replace("%CHANNEL%", channelName));
    }

    // IRC to hero kick message
    public String chatIRCTokenizer(String recipient, String kicker, String reason, String channelName, String template, ChannelManager channelManager, String hChannel) {
        return ircBot.plugin.colorConverter.ircColorsToGame(template
                .replace("%HEROCHANNEL%", hChannel)
                .replace("%HERONICK%", channelManager.getChannel(hChannel).getNick())                
                .replace("%HEROCOLOR%", channelManager.getChannel(hChannel).getColor().toString())
                .replace("%NAME%", recipient)
                .replace("%REASON%", reason)
                .replace("%KICKER%", kicker)
                .replace("%CHANNEL%", channelName));
    }

    public String gameChatToIRCTokenizer(String pName, String template, String message) {
        return plugin.colorConverter.gameColorsToIrc(template
                .replace("%NAME%", pName)
                .replace("%MESSAGE%", plugin.colorConverter.gameColorsToIrc(message)));
    }

    public String gameChatToIRCTokenizer(Player player, String template, String message) {
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

    public String mcMMOChatToIRCTokenizer(Player player, String template, String message, String partyName) {
        return gameChatToIRCTokenizer(player, template, message)
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
        return gameChatToIRCTokenizer(player, template, message)
                .replace("%FACTIONTAG%", chatTag)
                .replace("%FACTIONMODE%", chatMode);
    }

    public String chatHeroTokenizer(Player player, String message, String hColor, String hChannel, String hNick) {
        return gameChatToIRCTokenizer(player, plugin.heroChat, message)
                .replace("%HEROCHANNEL%", hChannel)
                .replace("%HERONICK%", hNick)
                .replace("%HEROCOLOR%", plugin.colorConverter.gameColorsToIrc(hColor))
                .replace("%CHANNEL%", hChannel);
    }

    public String gameChatToIRCTokenizer(String template, String message) {
        return plugin.colorConverter.gameColorsToIrc(template
                .replace("%MESSAGE%", message));
    }
}

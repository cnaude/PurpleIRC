/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Utilities;

import com.cnaude.purpleirc.PurpleIRC;
import com.dthielke.herochat.ChannelManager;
import com.nyancraft.reportrts.data.HelpRequest;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import org.bukkit.entity.Player;

/**
 * Main class containing all message template token expanding methods
 *
 * @author cnaude
 */
public class ChatTokenizer {

    PurpleIRC plugin;

    /**
     * Class initializer
     *
     * @param plugin
     */
    public ChatTokenizer(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     * IRC to game chat tokenizer without a message
     *
     * @param nick
     * @param channelName
     * @param template
     * @return
     */
    public String chatIRCTokenizer(String nick, String channelName, String template) {
        return plugin.colorConverter.ircColorsToGame(template
                .replace("%NAME%", nick)
                .replace("%CHANNEL%", channelName));
    }

    /**
     * IRC to Hero chat tokenizer without a message
     *
     * @param nick
     * @param channelName
     * @param template
     * @param channelManager
     * @param hChannel
     * @return
     */
    public String ircChatToHeroChatTokenizer(String nick, String channelName, String template, ChannelManager channelManager, String hChannel) {
        String tmpl;
        Player player = this.getPlayer(nick);
        if (player != null) {
            plugin.logDebug("ircChatToHeroChatTokenizer: player not null ");
            tmpl = playerTokenizer(player, template);
        } else {
            tmpl = playerTokenizer(nick, template);
        }
        return plugin.colorConverter.ircColorsToGame(tmpl
                .replace("%HEROCHANNEL%", hChannel)
                .replace("%HERONICK%", channelManager.getChannel(hChannel).getNick())
                .replace("%HEROCOLOR%", channelManager.getChannel(hChannel).getColor().toString())
                .replace("%NAME%", nick)
                .replace("%CHANNEL%", channelName));
    }

    /**
     * Normal IRC to game chat tokenizer
     *
     * @param nick
     * @param channelName
     * @param template
     * @param message
     * @return
     */
    public String ircChatToGameTokenizer(String nick, String channelName, String template, String message) {
        String tmpl;
        Player player = this.getPlayer(nick);
        if (player != null) {
            tmpl = playerTokenizer(player, template);
        } else {
            plugin.logDebug("ircChatToGameTokenizer: null player: " + nick);
            tmpl = playerTokenizer(nick, template);
        }
        return plugin.colorConverter.ircColorsToGame(tmpl
                .replace("%NAME%", nick)
                .replace("%MESSAGE%", message)
                .replace("%CHANNEL%", channelName));
    }

    /**
     * IRC to Hero chat channel tokenizer
     *
     * @param ircNick
     * @param ircChannelName
     * @param template
     * @param message
     * @param channelManager
     * @param hChannel
     * @return
     */
    public String ircChatToHeroChatTokenizer(String ircNick, String ircChannelName, String template, String message, ChannelManager channelManager, String hChannel) {
        String tmpl;
        Player player = this.getPlayer(ircNick);
        if (player != null) {
            tmpl = playerTokenizer(player, template);
        } else {
            tmpl = playerTokenizer(ircNick, template);
        }
        return plugin.colorConverter.ircColorsToGame(tmpl
                .replace("%HEROCHANNEL%", hChannel)
                .replace("%HERONICK%", channelManager.getChannel(hChannel).getNick())
                .replace("%HEROCOLOR%", channelManager.getChannel(hChannel).getColor().toString())
                .replace("%NAME%", ircNick)
                .replace("%MESSAGE%", message)
                .replace("%CHANNEL%", ircChannelName));
    }

    /**
     * IRC kick message to game
     *
     * @param recipient
     * @param kicker
     * @param reason
     * @param channelName
     * @param template
     * @return
     */
    public String ircKickTokenizer(String recipient, String kicker, String reason, String channelName, String template) {
        return plugin.colorConverter.ircColorsToGame(template
                .replace("%NAME%", recipient)
                .replace("%REASON%", reason)
                .replace("%KICKER%", kicker)
                .replace("%CHANNEL%", channelName));
    }

    /**
     * IRC to hero kick message
     *
     * @param recipient
     * @param kicker
     * @param reason
     * @param channelName
     * @param template
     * @param channelManager
     * @param hChannel
     * @return
     */
    public String ircKickToHeroChatTokenizer(String recipient, String kicker, String reason, String channelName, String template, ChannelManager channelManager, String hChannel) {
        return plugin.colorConverter.ircColorsToGame(template
                .replace("%HEROCHANNEL%", hChannel)
                .replace("%HERONICK%", channelManager.getChannel(hChannel).getNick())
                .replace("%HEROCOLOR%", channelManager.getChannel(hChannel).getColor().toString())
                .replace("%NAME%", recipient)
                .replace("%REASON%", reason)
                .replace("%KICKER%", kicker)
                .replace("%CHANNEL%", channelName));
    }

    /**
     * IRC mode change messages
     *
     * @param nick
     * @param mode
     * @param myChannel
     * @param template
     * @return
     */
    public String ircModeTokenizer(String nick, String mode, String myChannel, String template) {
        return plugin.colorConverter.ircColorsToGame(template
                .replace("%NAME%", nick)
                .replace("%MODE%", mode)
                .replace("%CHANNEL%", myChannel));
    }

    /**
     * IRC notice change messages
     *
     * @param nick
     * @param message
     * @param notice
     * @param myChannel
     * @param template
     * @return
     */
    public String ircNoticeTokenizer(String nick, String message, String notice, String myChannel, String template) {
        return plugin.colorConverter.ircColorsToGame(template
                .replace("%NAME%", nick)
                .replace("%MESSAGE%", message)
                .replace("%NOTICE%", notice)
                .replace("%CHANNEL%", myChannel));
    }

    /**
     * Game chat to IRC
     *
     * @param pName
     * @param template
     *
     * @param message
     * @return
     */
    public String gameChatToIRCTokenizer(String pName, String template, String message) {
        return plugin.colorConverter.gameColorsToIrc(template
                .replace("%NAME%", pName)
                .replace("%MESSAGE%", plugin.colorConverter.gameColorsToIrc(message)));
    }

    /**
     * Game chat to IRC
     *
     * @param player
     * @param template
     *
     * @param message
     * @return
     */
    public String gameChatToIRCTokenizer(Player player, String template, String message) {
        if (message == null) {
            message = "";
        }
        return plugin.colorConverter.gameColorsToIrc(playerTokenizer(player, template)
                .replace("%MESSAGE%", message));
    }

    /**
     * Game player AFK to IRC
     *
     * @param player
     * @param template
     *
     * @return
     */
    public String gamePlayerAFKTokenizer(Player player, String template) {
        return plugin.colorConverter.gameColorsToIrc(playerTokenizer(player, template));
    }

    /**
     * mcMMO chat to IRC
     *
     * @param player
     * @param template
     *
     * @param message
     * @param partyName
     * @return
     */
    public String mcMMOChatToIRCTokenizer(Player player, String template, String message, String partyName) {
        return gameChatToIRCTokenizer(player, template, message)
                .replace("%PARTY%", partyName);
    }

    /**
     * FactionChat to IRC
     *
     * @param player
     * @param message
     * @param chatTag
     * @param chatMode
     * @return
     */
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
        return plugin.colorConverter.gameColorsToIrc(
                gameChatToIRCTokenizer(player, template, message)
                .replace("%FACTIONTAG%", chatTag)
                .replace("%FACTIONMODE%", chatMode));
    }

    /**
     * Herochat to IRC
     *
     * @param player
     * @param message
     * @param hColor
     * @param hChannel
     * @param hNick
     * @param template
     * @return
     */
    public String chatHeroTokenizer(Player player, String message, String hColor, String hChannel, String hNick, String template) {
        return gameChatToIRCTokenizer(player, template, message)
                .replace("%HEROCHANNEL%", hChannel)
                .replace("%HERONICK%", hNick)
                .replace("%HEROCOLOR%", plugin.colorConverter.gameColorsToIrc(hColor))
                .replace("%CHANNEL%", hChannel);
    }

    public String chatTownyTokenizer(Player player, com.palmergames.bukkit.TownyChat.channels.Channel townyChannel, String message) {
        Resident resident;
        String town = "";
        String nation = "";
        String title = "";
        try {
            resident = TownyUniverse.getDataSource().getResident(player.getName());
        } catch (NotRegisteredException ex) {
            resident = null;
        }
        if (resident != null) {
            try {
                town = resident.getTown().getName();
            } catch (NotRegisteredException ex) {
                town = "";
            }
            try {
                nation = resident.getTown().getNation().getName();
            } catch (NotRegisteredException ex) {
                nation = "";
            }
            title = resident.getTitle();
        }

        return gameChatToIRCTokenizer(player, plugin.townyChat, message)
                .replace("%TOWNYCHANNEL%", townyChannel.getName())
                .replace("%TOWNYCHANNELTAG%", townyChannel.getChannelTag())
                .replace("%TOWNYMSGCOLOR%", townyChannel.getMessageColour())
                .replace("%TOWN%", town)
                .replace("%NATION%", nation)
                .replace("%TITLE%", title);
    }

    /**
     * TitanChat to IRC
     *
     * @param player
     * @param tChannel
     * @param tColor
     * @param message
     * @return
     */
    public String titanChatTokenizer(Player player, String tChannel, String tColor, String message) {
        return gameChatToIRCTokenizer(player, plugin.titanChat, message)
                .replace("%TITANCHANNEL%", tChannel)
                .replace("%TITANCOLOR%", plugin.colorConverter.gameColorsToIrc(tColor))
                .replace("%CHANNEL%", tChannel);
    }

    /**
     * Game chat to IRC
     *
     * @param template
     * @param message
     * @return
     */
    public String gameChatToIRCTokenizer(String template, String message) {
        return plugin.colorConverter.gameColorsToIrc(template
                .replace("%MESSAGE%", message));
    }

    /**
     * Game kick message to IRC
     *
     * @param player
     * @param reason
     * @param message
     * @return
     */
    public String gameKickTokenizer(Player player, String message, String reason) {
        return plugin.colorConverter.gameColorsToIrc(
                gameChatToIRCTokenizer(player, plugin.gameKick, message)
                .replace("%MESSAGE%", message)
                .replace("%REASON%", reason));
    }

    /**
     * ReportRTS notifications to IRC
     *
     * @param player
     * @param template
     * @param request
     * @return
     */
    public String reportRTSTokenizer(Player player, String template, HelpRequest request) {
        String message = request.getMessage();
        String modName = request.getModName();
        String name = request.getName();
        String world = request.getWorld();
        int id = request.getId();
        if (message == null) {
            message = "";
        }
        if (modName == null) {
            modName = "";
        }
        if (name == null) {
            name = "";
        }
        if (world == null) {
            world = "";
        }
        return gameChatToIRCTokenizer(player, template, message)
                .replace("%MESSAGE%", message)
                .replace("%MODNAME%", modName)
                .replace("%TICKETNUMBER%", String.valueOf(id))
                .replace("%RTSNAME%", name)
                .replace("%RTSWORLD%", world);
    }

    public String playerTokenizer(Player player, String message) {
        String pName = player.getName();
        plugin.logDebug("Tokenizing " + pName + "(O: " + player.isOnline() + ")");
        String pSuffix = plugin.getPlayerSuffix(player);
        String pPrefix = plugin.getPlayerPrefix(player);
        String gPrefix = plugin.getGroupPrefix(player);
        String gSuffix = plugin.getGroupSuffix(player);
        String group = plugin.getPlayerGroup(player);
        String displayName = player.getDisplayName();
        String worldName = "";
        String worldAlias = "";
        String worldColor = "";
        if (pSuffix == null) {
            pSuffix = "";
        }
        if (pPrefix == null) {
            pPrefix = "";
        }
        if (gSuffix == null) {
            gSuffix = "";
        }
        if (gPrefix == null) {
            gPrefix = "";
        }
        if (group == null) {
            group = "";
        }
        if (displayName == null) {
            displayName = "";
        }
        if (player.getWorld() != null) {
            worldName = player.getWorld().getName();
            worldAlias = plugin.getWorldAlias(worldName);
            worldColor = plugin.getWorldColor(worldName);
        }
        plugin.logDebug("[P]Raw message: " + message);
        return message.replace("%DISPLAYNAME%", displayName)
                .replace("%NAME%", pName)
                .replace("%GROUP%", group)
                .replace("%PLAYERPREFIX%", pPrefix)
                .replace("%PLAYERSUFFIX%", pSuffix)
                .replace("%GROUPPREFIX%", gPrefix)
                .replace("%GROUPSUFFIX%", gSuffix)
                .replace("%WORLDALIAS%", worldAlias)
                .replace("%WORLDCOLOR%", worldColor)
                .replace("%WORLD%", worldName);
    }

    private String playerTokenizer(String player, String message) {
        plugin.logDebug("Tokenizing " + player);
        String worldName = plugin.defaultPlayerWorld;
        String pSuffix = plugin.getPlayerSuffix(worldName, player);
        String pPrefix = plugin.getPlayerPrefix(worldName, player);
        String gPrefix = plugin.getGroupPrefix(worldName, player);
        String gSuffix = plugin.getGroupSuffix(worldName, player);
        String group = plugin.getPlayerGroup(worldName, player);
        String worldAlias = "";
        String worldColor = "";
        if (!worldName.isEmpty()) {
            worldAlias = plugin.getWorldAlias(worldName);
            worldColor = plugin.getWorldColor(worldName);
        }
        if (pSuffix == null) {
            pSuffix = plugin.defaultPlayerSuffix;
        }
        if (pPrefix == null) {
            pPrefix = plugin.defaultPlayerPrefix;
        }
        if (gSuffix == null) {
            gSuffix = plugin.defaultGroupSuffix;
        }
        if (gPrefix == null) {
            gPrefix = plugin.defaultGroupPrefix;
        }
        if (group == null) {
            group = plugin.defaultPlayerGroup;
        }

        plugin.logDebug("[S]Raw message: " + message);
        return message.replace("%DISPLAYNAME%", player)
                .replace("%NAME%", player)
                .replace("%GROUP%", group)
                .replace("%PLAYERPREFIX%", pPrefix)
                .replace("%PLAYERSUFFIX%", pSuffix)
                .replace("%GROUPSUFFIX%", gSuffix)
                .replace("%GROUPPREFIX%", gPrefix)
                .replace("%WORLDALIAS%", worldAlias)
                .replace("%WORLDCOLOR%", worldColor)
                .replace("%WORLD%", worldName);
    }

    private Player getPlayer(String name) {
        Player player;
        if (plugin.exactNickMatch) {
            plugin.logDebug("Checking for exact player matching " + name);
            player = plugin.getServer().getPlayerExact(name);
        } else {
            plugin.logDebug("Checking for player matching " + name);
            player = plugin.getServer().getPlayer(name);
        }
        return player;
    }

    /**
     *
     * @param player
     * @param template
     * @param cmd
     * @param params
     * @return
     */
    public String gameCommandToIRCTokenizer(Player player, String template, String cmd, String params) {
        return plugin.colorConverter.gameColorsToIrc(playerTokenizer(player, template)
                .replace("%COMMAND%", cmd)
                .replace("%PARAMS%", params));
    }

    public String targetChatResponseTokenizer(String target, String message, String template) {
        return plugin.colorConverter.gameColorsToIrc(template
                .replace("%TARGET%", target)
                .replace("%MESSAGE%", message)
        );
    }

    public String msgChatResponseTokenizer(String target, String message, String template) {
        return plugin.colorConverter.ircColorsToGame(template
                .replace("%TARGET%", target)
                .replace("%MESSAGE%", message)
        );
    }
}

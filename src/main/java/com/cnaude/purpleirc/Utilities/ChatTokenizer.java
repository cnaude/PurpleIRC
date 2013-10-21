/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Utilities;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.dthielke.herochat.ChannelManager;
import com.nyancraft.reportrts.data.HelpRequest;
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
        String tmpl;
        Player player = this.getPlayer(nick);
        if (player != null) {
            plugin.logDebug("ircChatToHeroChatTokenizer: player not null ");
            tmpl = playerTokenizer(player, template);
        } else {
            tmpl = playerTokenizer(nick, template);
        }
        return ircBot.plugin.colorConverter.ircColorsToGame(tmpl
                .replace("%HEROCHANNEL%", hChannel)
                .replace("%HERONICK%", channelManager.getChannel(hChannel).getNick())
                .replace("%HEROCOLOR%", channelManager.getChannel(hChannel).getColor().toString())
                .replace("%NAME%", nick)
                .replace("%CHANNEL%", channelName));
    }

    // Normal IRC to game chat tokenizer
    public String ircChatToGameTokenizer(String nick, String channelName, String template, String message) {
        String tmpl;
        Player player = this.getPlayer(nick);
        if (player != null) {
            tmpl = playerTokenizer(player, template);
        } else {
            plugin.logDebug("ircChatToGameTokenizer: null player: " + nick);
            tmpl = playerTokenizer(nick, template);
        }
        return ircBot.plugin.colorConverter.ircColorsToGame(tmpl
                .replace("%NAME%", nick)
                .replace("%MESSAGE%", message)
                .replace("%CHANNEL%", channelName));
    }

    // IRC to Hero chat channel tokenizer
    public String ircChatToHeroChatTokenizer(String ircNick, String ircChannelName, String template, String message, ChannelManager channelManager, String hChannel) {
        String tmpl;
        Player player = this.getPlayer(ircNick);
        if (player != null) {
            tmpl = playerTokenizer(player, template);
        } else {
            tmpl = playerTokenizer(ircNick, template);
        }
        return ircBot.plugin.colorConverter.ircColorsToGame(tmpl
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
        if (message == null) {
            message = "";
        }
        return plugin.colorConverter.gameColorsToIrc(playerTokenizer(player, template)
                .replace("%MESSAGE%", message));
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

    public String titanChatTokenizer(Player player, String tChannel, String tColor, String message) {
        return gameChatToIRCTokenizer(player, plugin.titanChat, message)
                .replace("%TITANCHANNEL%", tChannel)
                .replace("%TITANCOLOR%", plugin.colorConverter.gameColorsToIrc(tColor))
                .replace("%CHANNEL%", tChannel);
    }

    public String gameChatToIRCTokenizer(String template, String message) {
        return plugin.colorConverter.gameColorsToIrc(template
                .replace("%MESSAGE%", message));
    }

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

    private String playerTokenizer(Player player, String message) {
        String pName = player.getName();
        plugin.logDebug("Tokenizing " + pName + "(O: " + player.isOnline() + ")");
        String pSuffix = plugin.getPlayerSuffix(player);
        String pPrefix = plugin.getPlayerPrefix(player);
        String gPrefix = plugin.getGroupPrefix(player);
        String group = plugin.getPlayerGroup(player);
        String displayName = player.getDisplayName();
        String worldName = "";
        String worldAlias = "";
        if (pSuffix == null) {
            pSuffix = "";
        }
        if (pPrefix == null) {
            pPrefix = "";
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
        }
        return message.replace("%DISPLAYNAME%", displayName)
                .replace("%NAME%", pName)
                .replace("%GROUP%", group)
                .replace("%PLAYERPREFIX%", pPrefix)
                .replace("%PLAYERSUFFIX%", pSuffix)
                .replace("%GROUPPREFIX%", gPrefix)
                .replace("%WORLDALIAS%", worldAlias)
                .replace("%WORLD%", worldName);
    }
    
    private String playerTokenizer(String player, String message) {
        plugin.logDebug("Tokenizing " + player);
        String worldName = plugin.defaultPlayerWorld;
        String pSuffix = plugin.getPlayerSuffix(worldName,player);
        String pPrefix = plugin.getPlayerPrefix(worldName,player);
        String gPrefix = plugin.getGroupPrefix(worldName,player);
        String group = plugin.getPlayerGroup(worldName,player);       
        String worldAlias = plugin.getWorldAlias(worldName);        
        if (pSuffix == null) {            
            pSuffix = plugin.defaultPlayerSuffix;            
        }        
        if (pPrefix == null) {            
            pPrefix = plugin.defaultPlayerPrefix;            
        }        
        if (gPrefix == null) {         
            gPrefix = plugin.defaultGroupPrefix;
        }        
        if (group == null) {
            group = plugin.defaultPlayerGroup;
        }       
        
        plugin.logDebug("Message:" + message);
        return message.replace("%DISPLAYNAME%", player)
                .replace("%NAME%", player)
                .replace("%GROUP%", group)
                .replace("%PLAYERPREFIX%", pPrefix)
                .replace("%PLAYERSUFFIX%", pSuffix)
                .replace("%GROUPPREFIX%", gPrefix)
                .replace("%WORLDALIAS%", worldAlias)
                .replace("%WORLD%", worldName);
    }

    private String playerTokenizer(String message) {
        return message.replace("%DISPLAYNAME%", "")
                .replace("%GROUP%", "")
                .replace("%PLAYERPREFIX%", "")
                .replace("%PLAYERSUFFIX%", "")
                .replace("%GROUPPREFIX%", "")
                .replace("%WORLDALIAS%", "")
                .replace("%WORLD%", "");
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

    public String gameCommandToIRCTokenizer(Player player, String template, String cmd, String params) {
        return plugin.colorConverter.gameColorsToIrc(playerTokenizer(player, template)
                .replace("%COMMAND%", cmd)
                .replace("%PARAMS%", params));
    }
}

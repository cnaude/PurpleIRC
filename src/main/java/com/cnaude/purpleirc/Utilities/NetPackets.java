/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Utilities;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.PacketConstructor;
import org.bukkit.entity.Player;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

/**
 *
 * @author cnaude
 */
public class NetPackets {

    PurpleIRC plugin;
    private ProtocolManager protocolManager;
    private PacketConstructor playerListConstructor;

    public NetPackets(PurpleIRC plugin) {
        this.plugin = plugin;
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void addToTabList(String name, PurpleBot ircBot, Channel channel) {   
        String channelName = channel.getName();
        if (ircBot.tabIgnoreNicks.containsKey(channelName)) {
            if (ircBot.tabIgnoreNicks.get(channelName).contains(name)) {
                plugin.logDebug("Not adding " + name + " to tab list.");
                return;
            }
        }
        playerListConstructor = protocolManager.createPacketConstructor(Packets.Server.PLAYER_INFO, "", false, (int) 0);
        try {     
            
            PacketContainer packet = playerListConstructor.createPacket(
                    truncateName(plugin.customTabPrefix + name), true, 0);
            for (Player reciever : plugin.getServer().getOnlinePlayers()) {
                if (reciever.hasPermission("irc.tablist")) {
                    protocolManager.sendServerPacket(reciever, packet);
                }
            }
        } catch (Exception e) {
            plugin.logError(e.getMessage());
        }
    }
    
    public void remFromTabList(String name) {
        playerListConstructor = protocolManager.createPacketConstructor(Packets.Server.PLAYER_INFO, "", false, (int) 0);
        try {
            PacketContainer packet = playerListConstructor.createPacket(
                    truncateName(plugin.customTabPrefix + name), false, 0);
            for (Player reciever : plugin.getServer().getOnlinePlayers()) {
                if (reciever.hasPermission("irc.tablist")) {
                    protocolManager.sendServerPacket(reciever, packet);
                }
            }
        } catch (Exception e) {
            plugin.logError(e.getMessage());
        }
    }

    public void updateTabList(Player player, final PurpleBot ircBot, final String channelName) {
        final PircBotX bot = ircBot.bot;
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                Channel channel = bot.getChannel(channelName);
                for (User user : bot.getUsers(channel)) {                    
                    String nick = user.getNick();
                    /*
                    if (user.isIrcop()) {
                        nick = "~" + nick;
                    } else if (user.getChannelsSuperOpIn().contains(channel)) {
                        nick = "&" + nick;
                    } else if (user.getChannelsOpIn().contains(channel)) {
                        nick = "@" + nick;
                    } else if (user.getChannelsHalfOpIn().contains(channel)) {
                        nick = "%" + nick;
                    } else if (user.getChannelsVoiceIn().contains(channel)) {
                        nick = "+" + nick;
                    }
                    if (nick.equals(bot.getNick())) {
                        nick = ChatColor.DARK_PURPLE + nick;
                    }*/
                    addToTabList(nick, ircBot, channel);
                }
            }
        }, 5);

    }
    
    private String truncateName(String name) {
        if (name.length() > 16) {
            return name.substring(0, 15);
        } else {
            return name;
        }
    }
}

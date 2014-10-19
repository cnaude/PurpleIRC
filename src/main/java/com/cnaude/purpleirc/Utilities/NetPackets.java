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

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.comphenix.protocol.Packets;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.injector.PacketConstructor;
import com.comphenix.protocol.reflect.FieldAccessException;
import java.lang.reflect.InvocationTargetException;
import org.bukkit.entity.Player;
import org.pircbotx.Channel;
import org.pircbotx.User;

/**
 *
 * @author cnaude
 */
public class NetPackets {

    PurpleIRC plugin;
    private final ProtocolManager protocolManager;
    private PacketConstructor playerListConstructor;

    /**
     *
     * @param plugin
     */
    public NetPackets(PurpleIRC plugin) {
        this.plugin = plugin;
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    /**
     *
     * @param name
     * @param ircBot
     * @param channel
     */
    public void addToTabList(String name, PurpleBot ircBot, Channel channel) {
        if (! plugin.customTabList) {
            return;
        }
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
        } catch (FieldAccessException | InvocationTargetException e) {
            plugin.logError(e.getMessage());
        }
    }

    /**
     *
     * @param name
     */
    public void remFromTabList(String name) {
        if (! plugin.customTabList) {
            return;
        }
        playerListConstructor = protocolManager.createPacketConstructor(Packets.Server.PLAYER_INFO, "", false, (int) 0);
        try {
            PacketContainer packet = playerListConstructor.createPacket(
                    truncateName(plugin.customTabPrefix + name), false, 0);
            for (Player reciever : plugin.getServer().getOnlinePlayers()) {
                if (reciever.hasPermission("irc.tablist")) {
                    protocolManager.sendServerPacket(reciever, packet);
                }
            }
        } catch (FieldAccessException | InvocationTargetException e) {
            plugin.logError(e.getMessage());
        }
    }

    /**
     *
     * @param player
     * @param ircBot
     * @param channel
     */
    public void updateTabList(Player player, final PurpleBot ircBot, final Channel channel) {
        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
            @Override
            public void run() {
                for (User user : channel.getUsers()) {
                    String nick = user.getNick();
                    addToTabList(nick, ircBot, channel);
                }
            }
        }, 5);

    }

    /**
     *
     * @param player
     */
    public void updateTabList(Player player) {
        if (player.hasPermission("irc.tablist")) {
            for (PurpleBot ircBot : plugin.ircBots.values()) {
                if (ircBot.isConnected()) {
                    for (Channel channel : ircBot.getChannels()) {
                        if (ircBot.isValidChannel(channel.getName())) {
                            updateTabList(player, ircBot, channel);
                        }
                    }
                }
            }
        }
    }

    private String truncateName(String name) {
        if (name.length() > 16) {
            return name.substring(0, 15);
        } else {
            return name;
        }
    }
}

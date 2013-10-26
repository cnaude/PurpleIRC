/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Utilities;

import com.cnaude.purpleirc.IRCCommand;
import com.cnaude.purpleirc.IRCCommandSender;
import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.google.common.base.Joiner;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

/**
 *
 * @author cnaude
 */
public class IRCMessageHandler {

    PurpleIRC plugin;
    PurpleBot ircBot;
    PircBotX bot;

    /**
     *
     * @param plugin
     * @param ircBot
     */
    public IRCMessageHandler(PurpleIRC plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
        this.bot = this.ircBot.bot;
    }

    /**
     *
     * @param user
     * @param channel
     * @param message
     * @param privateMessage
     */
    public void processMessage(User user, Channel channel, String message, boolean privateMessage) {
        if (!ircBot.botChannels.contains(channel.getName())) {
            plugin.logDebug("Invalid IRC channel (" + channel.getName() + "). Ignoring message from " + user.getNick() + ": " + message);
            return;
        }
        String myChannel = channel.getName();
        if (ircBot.muteList.get(myChannel).contains(user.getNick())) {
            plugin.logDebug("User is muted. Ignoring message from " + user.getNick() + ": " + message);
            return;
        }
        if (message.startsWith(ircBot.commandPrefix) && (!message.startsWith(ircBot.commandPrefix + ircBot.commandPrefix))) {
            String command = message.split(" ")[0].substring(ircBot.commandPrefix.length());

            String commandArgs = null;
            if (message.contains(" ")) {
                commandArgs = message.split(" ", 2)[1];
            }
            plugin.logDebug("IRC command detected: " + command);

            plugin.logDebug(message);
            String target = channel.getName();
            if (ircBot.commandMap.get(myChannel).containsKey(command)) {
                boolean privateListen = Boolean.parseBoolean(ircBot.commandMap.get(myChannel).get(command).get("private_listen"));
                boolean channelListen = Boolean.parseBoolean(ircBot.commandMap.get(myChannel).get(command).get("channel_listen"));
                plugin.logDebug("privateListen: " + privateListen);
                plugin.logDebug("channelListen: " + channelListen);
                if (privateMessage && !privateListen) {
                    plugin.logDebug("This is a private message but privateListen is false. Ignoring...");
                    return;
                }
                if (!privateMessage && !channelListen) {
                    plugin.logDebug("This is a channel message but channelListen is false. Ignoring...");
                    return;
                }

                String gameCommand = (String) ircBot.commandMap.get(myChannel).get(command).get("game_command");
                String modes = (String) ircBot.commandMap.get(myChannel).get(command).get("modes");
                boolean privateCommand = Boolean.parseBoolean(ircBot.commandMap.get(myChannel).get(command).get("private"));
                boolean ctcpResponse = Boolean.parseBoolean(ircBot.commandMap.get(myChannel).get(command).get("ctcp"));

                plugin.logDebug(gameCommand + ":" + modes + ":" + privateCommand);

                if (privateCommand || privateMessage) {
                    target = user.getNick();
                }
                
                plugin.logDebug("Target: " + target);

                boolean modeOkay = false;
                if (modes.equals("*")) {
                    modeOkay = true;
                }
                if (modes.contains("o") && !modeOkay) {
                    modeOkay = user.getChannelsOpIn().contains(channel);
                }
                if (modes.contains("v") && !modeOkay) {
                    modeOkay = user.getChannelsVoiceIn().contains(channel);
                }
                if (modes.contains("h") && !modeOkay) {
                    modeOkay = user.getChannelsHalfOpIn().contains(channel);
                }
                if (modes.contains("q") && !modeOkay) {
                    modeOkay = user.getChannelsOwnerIn().contains(channel);
                }
                if (modes.contains("s") && !modeOkay) {
                    modeOkay = user.getChannelsSuperOpIn().contains(channel);
                }

                if (modeOkay) {
                    if (gameCommand.equals("@list")) {
                        this.sendMessage(bot, target, plugin.getMCPlayers(), ctcpResponse);
                    } else if (gameCommand.equals("@uptime")) {
                        this.sendMessage(bot, target, plugin.getMCUptime(), ctcpResponse);
                    } else if (gameCommand.equals("@help")) {
                        this.sendMessage(bot, target, getCommands(ircBot.commandMap, myChannel), ctcpResponse);
                    } else if (gameCommand.equals("@chat")) {
                        ircBot.broadcastChat(user.getNick(), myChannel, commandArgs, false);
                    } else if (gameCommand.equals("@ochat")) {
                        ircBot.broadcastChat(user.getNick(), myChannel, commandArgs, true);
                    } else if (gameCommand.equals("@hchat")) {                        
                        ircBot.broadcastHeroChat(user.getNick(), myChannel, target, commandArgs);
                    } else if (gameCommand.equals("@msg")) {
                        ircBot.playerChat(user.getNick(), myChannel, target, commandArgs);
                    } else {
                        if (commandArgs == null) {
                            commandArgs = "";
                        }
                        if (gameCommand.contains("%ARGS%")) {
                            gameCommand = gameCommand.replace("%ARGS%", commandArgs);
                        }
                        if (gameCommand.contains("%NAME%")) {
                            gameCommand = gameCommand.replace("%NAME%", user.getNick());
                        }
                        plugin.logDebug("GM: \"" + gameCommand.trim() + "\"");
                        ircBot.commandQueue.add(new IRCCommand(new IRCCommandSender(bot, target, plugin, ctcpResponse), gameCommand.trim()));
                    }
                } else {
                    plugin.logDebug("User '" + user.getNick() + "' mode not okay.");
                    bot.sendMessage(target, plugin.noPermForIRCCommand.replace("%NICK%", user.getNick())
                            .replace("%CMDPREFIX%", ircBot.commandPrefix));
                }
            } else {
                if (privateMessage) {
                    target = user.getNick();
                }
                bot.sendMessage(target, plugin.invalidIRCCommand.replace("%NICK%", user.getNick())
                        .replace("%CMDPREFIX%", ircBot.commandPrefix));
            }
        } else {
            if (ircBot.ignoreIRCChat.get(myChannel)) {
                plugin.logDebug("Message NOT dispatched for broadcast due to \"ignore-irc-chat\" being true ...");
                return;
            }
            if (privateMessage && !ircBot.relayPrivateChat) {
                plugin.logDebug("Message NOT dispatched for broadcast due to \"relay-private-chat\" being false and this is a private message ...");
                return;
            }
            plugin.logDebug("Message dispatched for broadcast...");
            ircBot.broadcastChat(user.getNick(), myChannel, message, false);
        }
    }

    private void sendMessage(PircBotX bot, String target, String message, boolean ctcpResponse) {
        if (ctcpResponse) {
            bot.sendCTCPResponse(target, message);
        } else {
            bot.sendMessage(target, message);
        }
    }

    private String getCommands(Map<String, Map<String, Map<String, String>>> commandMap, String myChannel) {
        if (commandMap.containsKey(myChannel)) {
            List<String> sortedCommands = new ArrayList<String>();
            for (String command : commandMap.get(myChannel).keySet()) {
                sortedCommands.add(command);
            }
            Collections.sort(sortedCommands, Collator.getInstance());

            return "Valid commands: " + Joiner.on(", ").join(sortedCommands);
        }
        return "";
    }
}

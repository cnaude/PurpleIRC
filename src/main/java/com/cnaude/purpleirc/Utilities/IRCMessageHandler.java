/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Utilities;

import com.cnaude.purpleirc.IRCCommand;
import com.cnaude.purpleirc.IRCCommandSender;
import com.cnaude.purpleirc.TemplateName;
import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.google.common.base.Joiner;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.pircbotx.Channel;
import org.pircbotx.User;

/**
 *
 * @author cnaude
 */
public class IRCMessageHandler {

    PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public IRCMessageHandler(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param ircBot
     * @param user
     * @param channel
     * @param message
     * @param privateMessage
     */
    public void processMessage(PurpleBot ircBot, User user, Channel channel, String message, boolean privateMessage) {
        plugin.logDebug("processMessage: " + message);
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
                boolean privateListen = Boolean.parseBoolean(ircBot.commandMap
                        .get(myChannel).get(command).get("private_listen"));
                boolean channelListen = Boolean.parseBoolean(ircBot.commandMap
                        .get(myChannel).get(command).get("channel_listen"));
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
                        sendMessage(ircBot, target, plugin.getMCPlayers(ircBot, myChannel), ctcpResponse);
                    } else if (gameCommand.equals("@uptime")) {
                        sendMessage(ircBot, target, plugin.getMCUptime(), ctcpResponse);
                    } else if (gameCommand.equals("@help")) {
                        sendMessage(ircBot, target, getCommands(ircBot.commandMap, myChannel), ctcpResponse);
                    } else if (gameCommand.equals("@chat")) {
                        ircBot.broadcastChat(user.getNick(), myChannel, commandArgs, false);
                    } else if (gameCommand.equals("@ochat")) {
                        ircBot.broadcastChat(user.getNick(), myChannel, commandArgs, true);
                    } else if (gameCommand.equals("@hchat")) {
                        ircBot.broadcastHeroChat(user.getNick(), myChannel, target, commandArgs);
                    } else if (gameCommand.equals("@motd")) {
                        sendMessage(ircBot, target, plugin.getServerMotd(), ctcpResponse);
                    } else if (gameCommand.equals("@msg")) {
                        ircBot.playerChat(user.getNick(), myChannel, target, commandArgs);
                    } else if (gameCommand.equals("@clearqueue")) {
                        sendMessage(ircBot, target, plugin.commandQueue.clearQueue(), ctcpResponse);
                        sendMessage(ircBot, target, ircBot.messageQueue.clearQueue(), ctcpResponse);
                    } else if (gameCommand.equals("@query")) {
                        sendMessage(ircBot, target, plugin.getRemotePlayers(commandArgs), ctcpResponse);
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
                        try {
                            plugin.commandQueue.add(new IRCCommand(new IRCCommandSender(ircBot, target, plugin, ctcpResponse), gameCommand.trim()));
                        } catch (Exception ex) {
                            plugin.logError(ex.getMessage());
                        }
                    }
                } else {
                    plugin.logDebug("User '" + user.getNick() + "' mode not okay.");
                    ircBot.asyncIRCMessage(target, plugin.getMsgTemplate(
                            ircBot.botNick, TemplateName.NO_PERM_FOR_IRC_COMMAND)
                            .replace("%NICK%", user.getNick())
                            .replace("%CMDPREFIX%", ircBot.commandPrefix));
                }
            } else {
                if (privateMessage || ircBot.invalidCommandPrivate.get(myChannel)) {
                    target = user.getNick();
                }
                plugin.logDebug("Invalid command: " + command);
                if (ircBot.invalidCommandCTCP.get(myChannel)) {
                    ircBot.blockingCTCPMessage(target, plugin.getMsgTemplate(
                            ircBot.botNick, TemplateName.INVALID_IRC_COMMAND)
                            .replace("%NICK%", user.getNick())
                            .replace("%CMDPREFIX%", ircBot.commandPrefix));
                } else {
                    ircBot.asyncIRCMessage(target, plugin.getMsgTemplate(
                            ircBot.botNick, TemplateName.INVALID_IRC_COMMAND)
                            .replace("%NICK%", user.getNick())
                            .replace("%CMDPREFIX%", ircBot.commandPrefix));
                }
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

    private void sendMessage(PurpleBot ircBot, String target, String message, boolean ctcpResponse) {
        if (ctcpResponse) {
            plugin.logDebug("Sending message to target: " + target + " => " + message);
            ircBot.asyncCTCPMessage(target, message);
        } else {
            plugin.logDebug("Sending message to target: " + target + " => " + message);
            ircBot.asyncIRCMessage(target, message);
        }
    }

    private String getCommands(CaseInsensitiveMap<CaseInsensitiveMap<CaseInsensitiveMap<String>>> commandMap, String myChannel) {
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

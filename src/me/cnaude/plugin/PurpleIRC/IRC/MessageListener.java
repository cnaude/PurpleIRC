/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC.IRC;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import me.cnaude.plugin.PurpleIRC.PIRCBot;
import me.cnaude.plugin.PurpleIRC.PIRCCommandSender;
import me.cnaude.plugin.PurpleIRC.PIRCMain;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.MessageEvent;

/**
 *
 * @author cnaude
 */
public class MessageListener extends ListenerAdapter {

    PIRCMain plugin;
    PIRCBot ircBot;

    public MessageListener(PIRCMain plugin, PIRCBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }

    @Override
    public void onMessage(MessageEvent event) {
        String message = event.getMessage();
        Channel channel = event.getChannel();
        User user = event.getUser();
        PircBotX bot = event.getBot();

        if (!ircBot.botChannels.containsValue(channel.getName())) {
            return;
        }
        String myChannel = ircBot.channelKeys.get(channel.getName());
        if (ircBot.muteList.get(myChannel).contains(user.getNick())) {
            return;
        }
        if (message.startsWith(ircBot.commandPrefix) && (!message.startsWith(ircBot.commandPrefix + ircBot.commandPrefix))) {
            String command = message.split(" ")[0].substring(1);
            plugin.logDebug("IRC command detected: " + command);
            if (ircBot.commandMap.get(myChannel).containsKey(command)) {
                String gameCommand = (String) ircBot.commandMap.get(myChannel).get(command).get("game_command");
                String modes = (String) ircBot.commandMap.get(myChannel).get(command).get("modes");
                boolean privateCommand = Boolean.parseBoolean(ircBot.commandMap.get(myChannel).get(command).get("private"));
                plugin.logDebug(gameCommand + ":" + modes + ":" + privateCommand);
                String target = channel.getName();
                if (privateCommand) {
                    target = user.getNick();
                }
                boolean modeOkay = false;
                if (modes.equals("*")) {
                    modeOkay = true;
                } else if (modes.contains("o")) {
                    modeOkay = user.getChannelsOpIn().contains(channel);
                } else if (modes.contains("v")) {
                    modeOkay = user.getChannelsVoiceIn().contains(channel);
                }

                if (modeOkay) {
                    if (gameCommand.equals("@list")) {
                        bot.sendMessage(target, plugin.getMCPlayers());                        
                    } else if (gameCommand.equals("@uptime")) {
                        bot.sendMessage(target, plugin.getMCPlayers());
                    } else if (gameCommand.equals("@help")) {
                        bot.sendMessage(user, getCommands(ircBot.commandMap,myChannel));                     
                    } else {
                        plugin.getServer().dispatchCommand(new PIRCCommandSender(event.getBot(), target, plugin), gameCommand);
                    }
                } else {
                    plugin.logDebug("User '" + user.getNick() + "' mode not okay.");
                }
            } else {
                event.respond("I'm sorry " + user.getNick() + " I can't do that. Type \""
                        + ircBot.commandPrefix + "help\" for a list of commands I might respond to");

            }
        } else {
            if (ircBot.enabledMessages.get(myChannel).contains("irc-chat")) {
                plugin.getServer().broadcast(plugin.colorConverter.ircColorsToGame(Matcher.quoteReplacement(plugin.ircChat)
                        .replaceAll("%NAME%", user.getNick())
                        .replaceAll("%MESSAGE%", Matcher.quoteReplacement(message))
                        .replaceAll("%CHANNEL%", channel.getName())), "irc.message.chat");
            }
        }
    }
    
    private String getCommands(Map<String, Map<String, Map<String, String>>> commandMap, String myChannel) {        
        if (commandMap.containsKey(myChannel)) {
            List<String> sortedCommands = new ArrayList<String>();
            for (String command : commandMap.get(myChannel).keySet()) {
                sortedCommands.add(command);
            }
            Collections.sort(sortedCommands, Collator.getInstance());

            String commands = "";
            for (String command : sortedCommands) {
                commands = commands + ", " + command;
            }
            return "Valid commands:" + commands.substring(1);
        }
        return "";
    }
}

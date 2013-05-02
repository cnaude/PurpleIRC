/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC.IRC;

import java.util.regex.Matcher;
import me.cnaude.plugin.PurpleIRC.PurpleBot;
import me.cnaude.plugin.PurpleIRC.PIRCMain;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.JoinEvent;

/**
 *
 * @author cnaude
 */
public class JoinListener extends ListenerAdapter {
    
    PIRCMain plugin;
    PurpleBot ircBot;

    public JoinListener(PIRCMain plugin, PurpleBot ircBot) {
        this.plugin = plugin;
        this.ircBot = ircBot;
    }
    
    @Override
    public void onJoin(JoinEvent event) {                
        Channel channel = event.getChannel();
        User user = event.getUser();
        PircBotX bot = event.getBot();
        
        if (!ircBot.botChannels.containsValue(channel.getName())) {
            return;
        }
        if (ircBot.enabledMessages.get(ircBot.channelKeys.get(channel.getName())).contains("irc-join")) {
            plugin.getServer().broadcast(plugin.colorConverter.ircColorsToGame(Matcher.quoteReplacement(plugin.ircJoin)
                    .replaceAll("%NAME%", user.getNick())
                    .replaceAll("%CHANNEL%", channel.getName())), "irc.message.join");
        }
        opFriends(channel, user);
        if (user.getNick().equals(ircBot.botNick)) {
            plugin.logDebug("Setting channel modes: " + channel.getName() + " => " 
                    + ircBot.channelModes.get(ircBot.channelKeys.get(channel.getName())));
            bot.setMode(channel, ircBot.channelModes.get(ircBot.channelKeys.get(channel.getName())));
        }
    }
    
    private void opFriends(Channel channel, User user) {
        if (user.getNick().equals(ircBot.botNick)) {
            return;
        }
        String myChannel = ircBot.channelKeys.get(channel.getName());
        for (String opsUser : ircBot.opsList.get(myChannel)) {
            plugin.logDebug("OP => " + user);
            //sender!*login@hostname            
            String mask[] = opsUser.split("[\\!\\@]", 3);
            if (mask.length == 3) {
                String gUser = createRegexFromGlob(mask[0]);
                String gLogin = createRegexFromGlob(mask[1]);
                String gHost = createRegexFromGlob(mask[2]);
                String sender = user.getNick();
                String login = user.getLogin();
                String hostname = user.getHostmask();
                plugin.logDebug("Nick: " + sender + " =~ " + gUser + " = " + sender.matches(gUser));
                plugin.logDebug("Name: " + login + " =~ " + gLogin + " = " + login.matches(gLogin));
                plugin.logDebug("Hostname: " + hostname + " =~ " + gHost + " = " + hostname.matches(gHost));
                if (sender.matches(gUser) && login.matches(gLogin) && hostname.matches(gHost)) {
                    plugin.logInfo("Auto-opping " + sender + " on " + channel);
                    user.getBot().op(channel, user);
                    // leave after our first match
                    return;
                } else {
                    plugin.logDebug("No match: " + sender + "!" + login + "@" + hostname + " != " + user);
                }
            } else {
                plugin.logInfo("Invalid op mask: " + user);
            }
        }
    }
    
    //http://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
    private static String createRegexFromGlob(String glob) {
        String out = "^";
        for (int i = 0; i < glob.length(); ++i) {
            final char c = glob.charAt(i);
            switch (c) {
                case '*':
                    out += ".*";
                    break;
                case '?':
                    out += '.';
                    break;
                case '.':
                    out += "\\.";
                    break;
                case '\\':
                    out += "\\\\";
                    break;
                default:
                    out += c;
            }
        }
        out += '$';
        return out;
    }
}

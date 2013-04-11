/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cnaude.plugin.PurpleIRC;

import java.io.File;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jibble.pircbot.PircBot;

/**
 *
 * @author cnaude
 */
public class PIRCBot extends PircBot {
    private ArrayList<String> botChannels;
    private String botServer;
    private String botNick;
    private String botServerPass;
    private int botServerPort;
    private final PIRCMain plugin;
    private YamlConfiguration config;
    
    private void loadConfig(File file) {
        try {
            config.load(file);
            botNick = config.getString("nick","");
            botServer = config.getString("server","");
            botServerPort  = config.getInt("port");
            botServerPass = config.getString("password","");
            plugin.logInfo("Nick   => " + botNick);
            plugin.logInfo("Server => " + botServer);
            plugin.logInfo("Port   => " + botServerPort);
            plugin.logDebug("Pass   => " + botServerPass);                    
            botChannels = new ArrayList<String>();
            for (String s : config.getStringList("channels")) {    
                plugin.logInfo("Channel  => " + s);
                botChannels.add(s);
            }
        } catch (Exception ex) {
            plugin.logError(ex.getMessage());
        }
    }
    
    public PIRCBot(File file, PIRCMain plugin) {
        this.plugin = plugin;     
        config = new YamlConfiguration();
        loadConfig(file);
        this.setName(botNick);

        try {
            this.plugin.logInfo("Connecting to " + botServer + " as " + getName());
            this.connect(botServer, botServerPort, botServerPass);
        } catch (Exception ex) {
            this.plugin.logError("Problem connecting to " + botServer + " => " 
                    + " as " + getName() + " [Error: " + ex.getMessage() + "]");
        }
    }
    
    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        if (!botChannels.contains(channel)) {
            return;
        }
        if (message.startsWith(".list")) {              
            sendMessage(channel, geMCPlayers());
        } else {
            plugin.getServer().broadcast("[IRC]<" + sender + "> " + message, "irc.message.chat");
        }
    }
    
    public void message(Player player, String message) {
        String world = player.getLocation().getWorld().getName();
        String pName = player.getName();
        for (String channel : botChannels) {
            this.sendMessage(channel, "["+world+"]<"+pName+"> " + message);
        }
    }
    
    public void joinMessage(Player player, String message) {
        String world = player.getLocation().getWorld().getName();
        for (String channel : botChannels) {
            this.sendMessage(channel, "["+world+"] " + ChatColor.stripColor(message));
        }
    }
        
    public void quitMessage(Player player, String message) {
        String world = player.getLocation().getWorld().getName();
        for (String channel : botChannels) {
            this.sendMessage(channel, "["+world+"] " + ChatColor.stripColor(message));
        }
    }
    
    public void action(Player player, String message) {
        String world = player.getLocation().getWorld().getName();
        for (String channel : botChannels) {
            this.sendMessage(channel, "["+world+"] " + message);
        }
    }
    
    public void death(Player player, String message) {
        String world = player.getLocation().getWorld().getName();
        for (String channel : botChannels) {
            this.sendMessage(channel, "["+world+"] " + message);
        }
    }
    
    @Override 
    public void onJoin(String channel, String sender, String login, String hostname) {
        if (!botChannels.contains(channel)) {
            return;
        }
        plugin.getServer().broadcast("[IRC] " + sender + " has entered the room.", "irc.message.join");
    }
    
    @Override
    public void onAction(String sender, String login, String hostname, String target, String action) {
        if (!botChannels.contains(target)) {
            return;
        }
        plugin.getServer().broadcast("[IRC] *** " + sender + " " + action, "irc.message.action");
    }
    
    @Override 
    public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        if (!botChannels.contains(channel)) {
            return;
        }
        plugin.getServer().broadcast("[IRC] " + recipientNick + " was kicked by " + kickerNick + ". (Reason: " + reason + ")", "irc.message.kick");
    }
    
    @Override
    public void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
        if (!botChannels.contains(channel)) {
            return;
        }
        plugin.getServer().broadcast("[IRC] " + setBy + " changed the topic: " + topic, "irc.message.topic");
    }
    
    @Override
    public void onVersion(String sourceNick, String sourceLogin, String sourceHostname, String target) {
        this.sendMessage(sourceNick, 
                "[Name: " + plugin.getDescription().getFullName() + "]"
                + "[Desc: " + plugin.getDescription().getDescription() + "]"
                + "[Version: " + plugin.getDescription().getVersion() + "]"
                + "[URL: " + plugin.getDescription().getWebsite() + "]"
                );
    }
    
    @Override
    public void onDisconnect() {
        plugin.getServer().broadcast("[" + botNick + "] Disconnected from IRC server.", "irc.message.disconnect");        
    }
    
    @Override
    public void onPart(String channel, String sender, String login, String hostname) {
        if (!botChannels.contains(channel)) {
            return;
        }
        plugin.getServer().broadcast("[IRC] " + sender + " has left the room.", "irc.message.part");
    }
    
    @Override
    public void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        plugin.getServer().broadcast("[IRC] " + sourceNick + " has quit (Reason: " + reason + ")", "irc.message.quit");
    }
        
    @Override
    public void onConnect() {
        for (String channel : this.botChannels) {
            this.plugin.logInfo("Joining channel " + channel);
            this.joinChannel(channel);
        }
    }
    
    private String geMCPlayers() {
        String msg = "Players currently online(" 
                + plugin.getServer().getOnlinePlayers().length
                + "/" + plugin.getServer().getMaxPlayers() + "): ";
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            msg = msg + p.getName()+ ", ";
        }
        msg = msg.substring(0, msg.length() - 1);
        return msg;
    }
}

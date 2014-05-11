/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 *//*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Utilities;

import com.cnaude.purpleirc.PurpleIRC;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.ChatColor;
import org.pircbotx.Colors;

/**
 *
 * @author cnaude
 */
public class ColorConverter {

    PurpleIRC plugin;
    private final boolean stripGameColors;
    private final boolean stripIRCColors;
    private final boolean stripIRCBackgroundColors;
    private final EnumMap<ChatColor, String> ircColorMap = new EnumMap<ChatColor, String>(ChatColor.class);
    private final HashMap<String, ChatColor> gameColorMap = new HashMap<String, ChatColor>();
    private final Pattern pattern;
    private final Pattern pattern2;

    /**
     *
     * @param plugin
     * @param stripGameColors
     * @param stripIRCColors
     * @param stripIRCBackgroundColors
     */
    public ColorConverter(PurpleIRC plugin, boolean stripGameColors, boolean stripIRCColors, boolean stripIRCBackgroundColors) {
        this.stripGameColors = stripGameColors;
        this.stripIRCColors = stripIRCColors;
        this.stripIRCBackgroundColors = stripIRCBackgroundColors;
        this.plugin = plugin;
        buildDefaultColorMaps();
        this.pattern = Pattern.compile("((\\u0003\\d+),\\d+)");
        this.pattern2 = Pattern.compile("((\\u0003)(\\d))\\D+");
    }

    /**
     *
     * @param message
     * @return
     */
    public String gameColorsToIrc(String message) {
        if (stripGameColors) {
            return ChatColor.stripColor(message);
        } else {
            String newMessage = message;
            for (ChatColor gameColor : ircColorMap.keySet()) {
                newMessage = newMessage.replace(gameColor.toString(), ircColorMap.get(gameColor));
            }
            // We return the message with the remaining MC color codes stripped out
            return ChatColor.stripColor(newMessage);
        }
    }

    /**
     *
     * @param message
     * @return
     */
    public String ircColorsToGame(String message) {
        try {
            Matcher m2 = pattern2.matcher(message);
            while (m2.find()) {
                plugin.logDebug("Single to double: " + m2.group(2) + "0" + m2.group(3));
                message = message.replace(m2.group(1), m2.group(2) + "0" + m2.group(3));
            }
        } catch (Exception ex) {
            plugin.logDebug(ex.getMessage());
        }
        if (stripIRCBackgroundColors) {
            Matcher m = pattern.matcher(message);
            while (m.find()) {
                message = message.replace(m.group(1), m.group(2));
            }
        }
        if (stripIRCColors) {
            return Colors.removeFormattingAndColors(message);
        } else {
            String newMessage = message;
            for (String ircColor : gameColorMap.keySet()) {
                newMessage = newMessage.replace(ircColor, gameColorMap.get(ircColor).toString());
            }
            // We return the message with the remaining IRC color codes stripped out
            return Colors.removeFormattingAndColors(newMessage);
        }
    }

    public void addIrcColorMap(String gameColor, String ircColor) {
        ChatColor chatColor;
        try {
            chatColor = ChatColor.valueOf(gameColor.toUpperCase());
        } catch (Exception ex) {
            plugin.logError("Invalid game color: " + gameColor);
            return;
        }
        if (chatColor != null) {
            plugin.logDebug("addIrcColorMap: " + gameColor + " => " + ircColor);
            ircColorMap.put(chatColor, getIrcColor(ircColor));
        }
    }

    public void addGameColorMap(String ircColor, String gameColor) {
        ChatColor chatColor;
        try {
            chatColor = ChatColor.valueOf(gameColor.toUpperCase());
        } catch (Exception ex) {
            plugin.logError("Invalid game color: " + gameColor);
            return;
        }
        plugin.logDebug("addGameColorMap: " + ircColor + " => " + gameColor);
        gameColorMap.put(getIrcColor(ircColor), chatColor);
    }

    private String getIrcColor(String ircColor) {
        String s = "";
        try {
            s = (String) Colors.class.getField(ircColor.toUpperCase()).get(null);
        } catch (NoSuchFieldException ex) {
            plugin.logError(ex.getMessage());
        } catch (SecurityException ex) {
            plugin.logError(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            plugin.logError(ex.getMessage());
        } catch (IllegalAccessException ex) {
            plugin.logError(ex.getMessage());
        }
        if (s.isEmpty()) {
            plugin.logError("Invalid IRC color: " + ircColor);
        }
        return s;
    }

    private void buildDefaultColorMaps() {
        ircColorMap.put(ChatColor.AQUA, Colors.CYAN);
        ircColorMap.put(ChatColor.BLACK, Colors.BLACK);
        ircColorMap.put(ChatColor.BLUE, Colors.BLUE);
        ircColorMap.put(ChatColor.BOLD, Colors.BOLD);
        ircColorMap.put(ChatColor.DARK_AQUA, Colors.TEAL);
        ircColorMap.put(ChatColor.DARK_BLUE, Colors.DARK_BLUE);
        ircColorMap.put(ChatColor.DARK_GRAY, Colors.DARK_GRAY);
        ircColorMap.put(ChatColor.DARK_GREEN, Colors.DARK_GREEN);
        ircColorMap.put(ChatColor.DARK_PURPLE, Colors.PURPLE);
        ircColorMap.put(ChatColor.DARK_RED, Colors.RED);
        ircColorMap.put(ChatColor.GOLD, Colors.OLIVE);
        ircColorMap.put(ChatColor.GRAY, Colors.LIGHT_GRAY);
        ircColorMap.put(ChatColor.GREEN, Colors.GREEN);
        ircColorMap.put(ChatColor.LIGHT_PURPLE, Colors.MAGENTA);
        ircColorMap.put(ChatColor.RED, Colors.RED);
        ircColorMap.put(ChatColor.UNDERLINE, Colors.UNDERLINE);
        ircColorMap.put(ChatColor.YELLOW, Colors.YELLOW);
        ircColorMap.put(ChatColor.WHITE, Colors.WHITE);
        ircColorMap.put(ChatColor.RESET, Colors.NORMAL);

        gameColorMap.put(Colors.BLACK, ChatColor.BLACK);
        gameColorMap.put(Colors.BLUE, ChatColor.BLUE);
        gameColorMap.put(Colors.BOLD, ChatColor.BOLD);
        gameColorMap.put(Colors.BROWN, ChatColor.GRAY);
        gameColorMap.put(Colors.CYAN, ChatColor.AQUA);
        gameColorMap.put(Colors.DARK_BLUE, ChatColor.DARK_BLUE);
        gameColorMap.put(Colors.DARK_GRAY, ChatColor.DARK_GRAY);
        gameColorMap.put(Colors.DARK_GREEN, ChatColor.DARK_GREEN);
        gameColorMap.put(Colors.GREEN, ChatColor.GREEN);
        gameColorMap.put(Colors.LIGHT_GRAY, ChatColor.GRAY);
        gameColorMap.put(Colors.MAGENTA, ChatColor.LIGHT_PURPLE);
        gameColorMap.put(Colors.NORMAL, ChatColor.RESET);
        gameColorMap.put(Colors.OLIVE, ChatColor.GOLD);
        gameColorMap.put(Colors.PURPLE, ChatColor.DARK_PURPLE);
        gameColorMap.put(Colors.RED, ChatColor.RED);
        gameColorMap.put(Colors.TEAL, ChatColor.DARK_AQUA);
        gameColorMap.put(Colors.UNDERLINE, ChatColor.UNDERLINE);
        gameColorMap.put(Colors.WHITE, ChatColor.WHITE);
        gameColorMap.put(Colors.YELLOW, ChatColor.YELLOW);
    }
}

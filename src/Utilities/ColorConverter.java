/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utilities;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.regex.Matcher;
import org.bukkit.ChatColor;
import org.pircbotx.Colors;

/**
 *
 * @author cnaude
 */
public class ColorConverter {
    
    private boolean stripGameColors;    
    private boolean stripIRCColors;    
    private EnumMap<ChatColor, String> ircColorMap = new EnumMap<ChatColor, String>(ChatColor.class);
    private HashMap<String, ChatColor> gameColorMap = new HashMap<String, ChatColor>();
    
    public ColorConverter(boolean stripGameColors, boolean stripIRCColors) {
        this.stripGameColors = stripGameColors;
        this.stripIRCColors = stripIRCColors;
        buildColorMaps();
    }
    
    public String gameColorsToIrc(String message) {
        if (stripGameColors) {
            return ChatColor.stripColor(message);
        } else {
            String newMessage = message;
            for (ChatColor gameColor : ircColorMap.keySet()) {
                newMessage = Matcher.quoteReplacement(newMessage).replaceAll(gameColor.toString(), ircColorMap.get(gameColor));
            }
            // We return the message with the remaining MC color codes stripped out
            return ChatColor.stripColor(newMessage);
        }
    }

    public String ircColorsToGame(String message) {
        if (stripIRCColors) {
            return Colors.removeFormattingAndColors(message);
        } else {
            String newMessage = message;
            for (String ircColor : gameColorMap.keySet()) {
                newMessage = Matcher.quoteReplacement(newMessage).replaceAll(ircColor.toString(), gameColorMap.get(ircColor).toString());
            }
            // We return the message with the remaining IRC color codes stripped out
            return Colors.removeFormattingAndColors(message);
        }
    }
    
    
    private void buildColorMaps() {
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
        ircColorMap.put(ChatColor.GOLD, Colors.YELLOW);
        ircColorMap.put(ChatColor.GRAY, Colors.DARK_GRAY);
        ircColorMap.put(ChatColor.GREEN, Colors.GREEN);
        ircColorMap.put(ChatColor.LIGHT_PURPLE, Colors.MAGENTA);
        ircColorMap.put(ChatColor.RED, Colors.RED);
        ircColorMap.put(ChatColor.YELLOW, Colors.YELLOW);
        ircColorMap.put(ChatColor.WHITE, Colors.WHITE);
        
        for (ChatColor chatColor : ircColorMap.keySet()) {
            gameColorMap.put(ircColorMap.get(chatColor), chatColor);
        }
    }
    
}

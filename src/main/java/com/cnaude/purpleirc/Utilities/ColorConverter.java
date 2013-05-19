/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cnaude.purpleirc.Utilities;

import java.util.EnumMap;
import java.util.HashMap;
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
                newMessage = newMessage.replace(gameColor.toString(), ircColorMap.get(gameColor));
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
                //System.out.println(ircColor + " => " + gameColorMap.get(ircColor).toString());
                newMessage = newMessage.replace(ircColor, gameColorMap.get(ircColor).toString());
            }
            // We return the message with the remaining IRC color codes stripped out
            return Colors.removeFormattingAndColors(newMessage);
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
        ircColorMap.put(ChatColor.UNDERLINE, Colors.UNDERLINE);
        ircColorMap.put(ChatColor.YELLOW, Colors.YELLOW);
        ircColorMap.put(ChatColor.WHITE, Colors.WHITE);
        ircColorMap.put(ChatColor.RESET, Colors.NORMAL);        
        
        gameColorMap.put(Colors.BLACK,      ChatColor.BLACK);
        gameColorMap.put(Colors.BLUE,       ChatColor.BLUE);        
        gameColorMap.put(Colors.BOLD,       ChatColor.BOLD);
        gameColorMap.put(Colors.BROWN,      ChatColor.GRAY);
        gameColorMap.put(Colors.CYAN,       ChatColor.AQUA);                        
        gameColorMap.put(Colors.DARK_BLUE,  ChatColor.DARK_BLUE);
        gameColorMap.put(Colors.DARK_GRAY,  ChatColor.DARK_GRAY);                
        gameColorMap.put(Colors.DARK_GREEN, ChatColor.DARK_GREEN);
        gameColorMap.put(Colors.GREEN,      ChatColor.GREEN);
        gameColorMap.put(Colors.LIGHT_GRAY, ChatColor.GRAY);
        gameColorMap.put(Colors.MAGENTA,    ChatColor.LIGHT_PURPLE);
        gameColorMap.put(Colors.NORMAL,     ChatColor.RESET);
        gameColorMap.put(Colors.PURPLE,     ChatColor.DARK_PURPLE);
        gameColorMap.put(Colors.RED,        ChatColor.RED);
        gameColorMap.put(Colors.TEAL,       ChatColor.DARK_AQUA);
        gameColorMap.put(Colors.UNDERLINE,  ChatColor.UNDERLINE);
        gameColorMap.put(Colors.WHITE,      ChatColor.WHITE);
        gameColorMap.put(Colors.YELLOW,     ChatColor.YELLOW);
                                                            
    }
    
}

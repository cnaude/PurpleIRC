package com.cnaude.purpleirc.GameListeners;

import com.cnaude.purpleirc.PurpleBot;
import com.cnaude.purpleirc.PurpleIRC;
import com.cnaude.purpleirc.TemplateName;
import me.botsko.prism.events.PrismBlocksDrainEvent;
import me.botsko.prism.events.PrismBlocksExtinguishEvent;
import me.botsko.prism.events.PrismBlocksRollbackEvent;
import me.botsko.prism.events.PrismCustomPlayerActionEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 *
 * @author cnaude
 */
public class PrismListener implements Listener {

    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public PrismListener(PurpleIRC plugin) {
        this.plugin = plugin;
    }

    /**
     *
     * @param event
     */
    @EventHandler
    public void onPrismBlocksRollbackEvent(PrismBlocksRollbackEvent event) {
        plugin.logDebug("onPrismBlocksRollbackEvent caught");
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gamePrismRollback(event.onBehalfOf(), event.getQueryParameters());
        }
    }
    
     /**
     *
     * @param event
     */
    @EventHandler
    public void onPrismBlocksDrainEvent(PrismBlocksDrainEvent event) {
        plugin.logDebug("onPrismBlocksDrainEvent caught");
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gamePrismDrainOrExtinguish(TemplateName.PRISM_DRAIN, event.onBehalfOf(), event.getRadius(), event.getBlockStateChanges());
        }
    }
    
     /**
     *
     * @param event
     */
    @EventHandler
    public void onPrismBlocksExtinguishEvent(PrismBlocksExtinguishEvent event) {
        plugin.logDebug("onPrismBlocksExtinguishEvent caught");
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gamePrismDrainOrExtinguish(TemplateName.PRISM_EXTINGUISH, event.onBehalfOf(), event.getRadius(), event.getBlockStateChanges());
        }
    }
    
    /**
     *
     * @param event
     */
    @EventHandler
    public void onPrismCustomPlayerActionEvent(PrismCustomPlayerActionEvent event) {
        plugin.logDebug("onPrismCustomPlayerActionEvent caught");
        for (PurpleBot ircBot : plugin.ircBots.values()) {
            ircBot.gamePrismCustom(event.getPlayer(), event.getActionTypeName(), 
                    event.getMessage(), event.getPluginName());
        }
    }
}

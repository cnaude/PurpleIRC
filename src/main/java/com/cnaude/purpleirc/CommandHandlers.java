package com.cnaude.purpleirc;

import com.cnaude.purpleirc.Commands.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class CommandHandlers implements CommandExecutor {

    HashMap<String, Object> commands = new HashMap<String, Object>();
    private final PurpleIRC plugin;

    /**
     *
     * @param plugin
     */
    public CommandHandlers(PurpleIRC plugin) {

        this.plugin = plugin;

        commands.put("addop", new AddOp(plugin));
        commands.put("connect", new Connect(plugin));
        commands.put("ctcp", new CTCP(plugin));
        commands.put("deop", new DeOp(plugin));
        commands.put("debug", new Debug(plugin));
        commands.put("disconnect", new Disconnect(plugin));
        commands.put("join", new Join(plugin));
        commands.put("kick", new Kick(plugin));
        commands.put("leave", new Leave(plugin));
        commands.put("list", new List(plugin));
        commands.put("listbots", new ListBots(plugin));
        commands.put("listops", new ListOps(plugin));
        commands.put("login", new Login(plugin));
        commands.put("messagedelay", new MessageDelay(plugin));
        commands.put("msg", new Msg(plugin));
        commands.put("mute", new Mute(plugin));
        commands.put("nick", new Nick(plugin));
        commands.put("notice", new Notice(plugin));
        commands.put("op", new Op(plugin));
        commands.put("reload", new Reload(plugin));
        commands.put("reloadbot", new ReloadBot(plugin));
        commands.put("reloadbotconfig", new ReloadBotConfig(plugin));
        commands.put("reloadbotconfigs", new ReloadBotConfigs(plugin));
        commands.put("reloadbots", new ReloadBots(plugin));
        commands.put("reloadconfig", new ReloadConfig(plugin));
        commands.put("removeop", new RemoveOp(plugin));
        commands.put("save", new Save(plugin));
        commands.put("say", new Say(plugin));
        commands.put("send", new Send(plugin));
        commands.put("sendraw", new SendRaw(plugin));
        commands.put("server", new Server(plugin));
        commands.put("topic", new Topic(plugin));
        commands.put("whois", new Whois(plugin));
    }

    /**
     *
     * @param sender
     * @param cmd
     * @param commandLabel
     * @param args
     * @return
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (args.length >= 1) {
            String subCmd = args[0].toLowerCase();
            if (commands.containsKey(subCmd)) {
                if (!sender.hasPermission("irc." + subCmd)) {
                    sender.sendMessage(plugin.noPermission);
                    return true;
                }
                try {
                    Method method = commands.get(subCmd).getClass().getMethod("dispatch", CommandSender.class, String[].class);
                    method.invoke(commands.get(subCmd), sender, args);
                    return true;
                } catch (NoSuchMethodException ex) {
                    plugin.logError(ex.getMessage());
                } catch (IllegalAccessException ex) {
                    plugin.logError(ex.getMessage());
                } catch (InvocationTargetException ex) {
                    plugin.logError(ex.getMessage());
                }
            }
        }
        return false;
    }
}

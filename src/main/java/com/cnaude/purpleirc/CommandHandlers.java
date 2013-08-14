package com.cnaude.purpleirc;

import com.cnaude.purpleirc.Commands.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cnaude
 */
public class CommandHandlers implements CommandExecutor {

    private PurpleIRC plugin;
    private final AddOp addOp;
    private final Connect connect;
    private final DeOp deOp;
    private final Debug debug;
    private final Disconnect disconnect;
    private final Join join;
    private final Kick kick;
    private final Leave leave;
    private final List list;
    private final ListBots listBots;
    private final ListOps listOps;
    private final Login login;
    private final MessageDelay messageDelay;
    private final Msg msg;
    private final Mute mute;
    private final Nick nick;
    private final Op op;
    private final Reload reload;
    private final ReloadBot reloadBot;
    private final ReloadBotConfig reloadBotConfig;
    private final ReloadBotConfigs reloadBotConfigs;
    private final ReloadBots reloadBots;
    private final ReloadConfig reloadConfig;
    private final RemoveOp removeOp;
    private final Save save;
    private final Say say;
    private final Send send;
    private final Server server;
    private final Topic topic;
    private final Whois whois;

    public CommandHandlers(PurpleIRC plugin) {
        this.plugin = plugin;
        this.addOp = new AddOp(plugin);
        this.connect = new Connect(plugin);
        this.deOp = new DeOp(plugin);
        this.debug = new Debug(plugin);
        this.disconnect = new Disconnect(plugin);
        this.join = new Join(plugin);
        this.kick = new Kick(plugin);
        this.leave = new Leave(plugin);
        this.list = new List(plugin);
        this.listBots = new ListBots(plugin);
        this.listOps = new ListOps(plugin);
        this.login = new Login(plugin);
        this.messageDelay = new MessageDelay(plugin);
        this.msg = new Msg(plugin);
        this.mute = new Mute(plugin);
        this.nick = new Nick(plugin);
        this.op = new Op(plugin);
        this.reload = new Reload(plugin);
        this.reloadBot = new ReloadBot(plugin);
        this.reloadBotConfig = new ReloadBotConfig(plugin);
        this.reloadBotConfigs = new ReloadBotConfigs(plugin);
        this.reloadBots = new ReloadBots(plugin);
        this.reloadConfig = new ReloadConfig(plugin);
        this.removeOp = new RemoveOp(plugin);
        this.save = new Save(plugin);
        this.say = new Say(plugin);
        this.send = new Send(plugin);
        this.server = new Server(plugin);
        this.topic = new Topic(plugin);
        this.whois = new Whois(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        /*
        String[] subArgs = null;
        plugin.logDebug("ARGS: " + args.length);
        if (args.length >= 2) {
            subArgs = new String[args.length - 2];
            for (int i = 2; i < args.length; i++) {
                subArgs[i - 2] = args[i];
                plugin.logDebug("a[" + i + "] " + args[i]);
            }
        } else if (args.length >= 1) {
            subArgs = new String[1];
                subArgs[i - 1] = args[i];
                plugin.logDebug("a[" + i + "] " + args[i]);
        } else {
            subArgs = args;
        }
        for (String s1 : subArgs) {
            plugin.logDebug(s1);
        }*/

        if (args.length >= 1) {
            String botName = "";

            if (plugin.ircBots.size() == 1) {
                botName = (String) plugin.ircBots.keySet().toArray()[0];
            } else {
                if (args.length >= 2) {
                    for (String s : plugin.ircBots.keySet()) {
                        if (s.equalsIgnoreCase(args[1])) {
                            botName = s;
                            break;
                        }
                    }
                }
            }

            String subCmd = args[0].toLowerCase();
            if (!sender.hasPermission("irc." + subCmd)) {
                sender.sendMessage(plugin.noPermission);
                return true;
            }
            if (subCmd.equalsIgnoreCase("listbots")) {
                listBots.dispatch(sender);
                return true;
            }
            if (subCmd.equalsIgnoreCase("debug")) {
                debug.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("connect")) {
                connect.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("reload")) {
                reload.dispatch(sender);
                return true;
            }
            if (subCmd.equalsIgnoreCase("reloadbot")) {
                reloadBot.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("reloadbots")) {
                reloadBots.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("reloadbotconfig")) {
                reloadBotConfig.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("reloadbotconfigs")) {
                reloadBotConfigs.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("reloadconfig")) {
                reloadConfig.dispatch(sender);
                return true;
            }
            if (subCmd.equalsIgnoreCase("disconnect")) {
                disconnect.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("topic")) {
                topic.dispatch(sender, args);
                return true;
            }
            /*
            if (subCmd.equalsIgnoreCase("msg")) {
                msg.dispatch(sender, args);
                return true;
            }*/
            if (subCmd.equalsIgnoreCase("say")) {
                say.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("send")) {
                send.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("op")) {
                op.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("deop")) {
                deOp.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("kick")) {
                kick.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("addop")) {
                addOp.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("listops")) {
                listOps.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("removeop")) {
                removeOp.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("mute")) {
                mute.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("save")) {
                save.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("server")) {
                server.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("nick")) {
                nick.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("messagedelay")) {
                messageDelay.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("login")) {
                login.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("list")) {
                list.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("whois")) {
                whois.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("join")) {
                join.dispatch(sender, args);
                return true;
            }
            if (subCmd.equalsIgnoreCase("leave")) {
                leave.dispatch(sender, args);
                return true;
            }
        }
        return false;
    }

    private String getBotName(String[] args) {
        String botName = "";
        if (plugin.ircBots.size() == 1) {
            botName = (String) plugin.ircBots.keySet().toArray()[0];
        } else {
            for (String s : plugin.ircBots.keySet()) {
                if (s.equalsIgnoreCase(args[1])) {
                    botName = s;
                    break;
                }
            }
        }
        return botName;
    }
}

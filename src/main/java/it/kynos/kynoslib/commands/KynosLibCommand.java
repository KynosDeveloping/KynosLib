package it.kynos.kynoslib.commands;

import it.kynos.kynoslib.KynosLib;
import it.kynos.kynoslib.menu.KynosLibGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KynosLibCommand extends KynosCommand {

    public KynosLibCommand(KynosLib plugin) {
        super(plugin, "kynoslib");
    }

    @Override
    protected boolean execute(CommandSender sender, String[] args) {
        final KynosLib pl = KynosLib.getInstance();
        final String version = pl.getDescription().getVersion();

        sender.sendMessage(this.msg.get("Messages.KynosLibInfo.Separator"));
        sender.sendMessage(this.msg.get("Messages.KynosLibInfo.Title"));
        sender.sendMessage(this.msg.get("Messages.KynosLibInfo.Version", "{version}", version));
        sender.sendMessage(this.msg.get("Messages.KynosLibInfo.Author"));
        sender.sendMessage(this.msg.get("Messages.KynosLibInfo.Status"));

        if (sender.hasPermission("kynoslib.admin.gui")) {
            sender.sendMessage(this.msg.get("Messages.KynosLibInfo.HelpHint"));
        }
        sender.sendMessage(this.msg.get("Messages.KynosLibInfo.Separator"));
        return true;
    }

    @SubCommand(name = "gui", permission = "kynoslib.admin.gui", playerOnly = true)
    public boolean onGui(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            new KynosLibGui().open(player);
        }
        return true;
    }

    @SubCommand(name = "reload", permission = "kynoslib.admin.reload")
    public boolean onReload(CommandSender sender, String[] args) {
        KynosLib.getInstance().reloadPlugin();
        sender.sendMessage(this.msg.get("Messages.ReloadSuccess"));
        return true;
    }

    @SubCommand(name = "help")
    public boolean onHelp(CommandSender sender, String[] args) {
        sender.sendMessage(this.msg.get("Messages.KynosLibHelp.Separator"));
        sender.sendMessage(this.msg.get("Messages.KynosLibHelp.Title"));
        sender.sendMessage(this.msg.get("Messages.KynosLibHelp.CmdInfo"));

        if (sender.hasPermission("kynoslib.admin.gui")) {
            sender.sendMessage(this.msg.get("Messages.KynosLibHelp.CmdGui"));
        }
        if (sender.hasPermission("kynoslib.admin.reload")) {
            sender.sendMessage(this.msg.get("Messages.KynosLibHelp.CmdReload"));
        }
        if (sender.hasPermission("kynoslib.admin.stopplugin")) {
            sender.sendMessage(this.msg.get("Messages.KynosLibHelp.CmdStopPlugin"));
        }
        sender.sendMessage(this.msg.get("Messages.KynosLibHelp.Separator"));
        return true;
    }
}
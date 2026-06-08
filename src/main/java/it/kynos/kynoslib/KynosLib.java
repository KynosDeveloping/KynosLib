package it.kynos.kynoslib;

import it.kynos.kynoslib.commands.KynosLibCommand;
import it.kynos.kynoslib.commands.StartPluginCommand;
import it.kynos.kynoslib.commands.StopPluginCommand;
import it.kynos.kynoslib.listeners.CommandProtectionListener;
import it.kynos.kynoslib.listeners.PlayerJoinListener;
import it.kynos.kynoslib.listeners.UpdateJoinListener;
import it.kynos.kynoslib.managers.ModuleManager;
import it.kynos.kynoslib.menu.GuiListener;
import it.kynos.kynoslib.utils.ColorUtils;
import it.kynos.kynoslib.utils.GitHubUpdater;
import org.bukkit.plugin.java.JavaPlugin;

public final class KynosLib extends JavaPlugin {

    private static KynosLib instance;
    private ModuleManager moduleManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        this.moduleManager = new ModuleManager();

        // Initialize WorldGuard Hook
        it.kynos.kynoslib.utils.KynosWorldGuard.init();

        printBanner(true);

        // Register main command
        new KynosLibCommand(this);

        // GUI Manager module
        if (this.moduleManager.isEnabled("GuiManager")) {
            getServer().getPluginManager().registerEvents(new GuiListener(), this);
            ColorUtils.log("§7[KynosLib] §aGUI Manager §7loaded.");
        }

        // Plugin Disabler module
        if (this.moduleManager.isEnabled("PluginDisabler")) {
            new StopPluginCommand(this);
            ColorUtils.log("§7[KynosLib] §aPlugin Disabler §7loaded.");
        }

        // Plugin Enabler module
        if (this.moduleManager.isEnabled("PluginEnabler")) {
            new StartPluginCommand(this);
            ColorUtils.log("§7[KynosLib] §aPlugin Enabler §7loaded.");
        }

        // PlayerJoin Listener module
        if (getConfig().getBoolean("Listeners.PlayerJoin.Enabled", true)) {
            getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
            ColorUtils.log("§7[KynosLib] §aPlayerJoin Listener §7loaded.");
        }

        // Always-active listener for protection rules
        getServer().getPluginManager().registerEvents(new CommandProtectionListener(), this);
        getServer().getPluginManager().registerEvents(new UpdateJoinListener(this), this);

        new GitHubUpdater(this, "KynosDeveloping", "KynosLib").checkForUpdate().thenAccept(latestVersion -> {
            if (latestVersion != null) {
                getLogger().warning("A new update (v" + latestVersion + ") is available on GitHub!");
            } else {
                getLogger().info("KynosLib is up to date.");
            }
        });

        ColorUtils.log("§7[KynosLib] §aKynosLib v" + getDescription().getVersion() + " §7enabled successfully.");
    }

    @Override
    public void onDisable() {
        printBanner(false);
    }

    public void reloadPlugin() {
        reloadConfig();
        this.moduleManager.reload();
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public static KynosLib getInstance() {
        return instance;
    }

    private void printBanner(boolean enabled) {
        final String status = enabled ? "§aSuccessfully Enabled" : "§cSuccessfully Disabled";
        ColorUtils.log("§c▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄");
        ColorUtils.log("§c█ §7########################################################################## §c█");
        ColorUtils.log("§c█ §7#                                                                        # §c█");
        ColorUtils.log("§c█ §7# §b██╗  ██╗██╗   ██╗███╗  ██╗ ██████╗ ███████╗██╗     ██╗██████╗       §7# §c█");
        ColorUtils.log("§c█ §7# §b██║ ██╔╝╚██╗ ██╔╝████╗ ██║██╔═══██╗██╔════╝██║     ██║██╔══██╗      §7# §c█");
        ColorUtils.log("§c█ §7# §b█████╔╝  ╚████╔╝ ██╔██╗██║██║   ██║███████╗██║     ██║██████╔╝      §7# §c█");
        ColorUtils.log("§c█ §7# §b██╔═██╗   ╚██╔╝  ██║╚████║██║   ██║╚════██║██║     ██║██╔══██╗      §7# §c█");
        ColorUtils.log("§c█ §7# §b██║  ██╗   ██║   ██║ ╚███║╚██████╔╝███████║███████╗██║██████╔╝      §7# §c█");
        ColorUtils.log("§c█ §7# §b╚═╝  ╚═╝   ╚═╝   ╚═╝  ╚══╝ ╚═════╝ ╚══════╝╚══════╝╚═╝╚═════╝      §7# §c█");
        ColorUtils.log("§c█ §7#                                                                        # §c█");
        ColorUtils.log("§c█ §7#                         " + status + "                              §7# §c█");
        ColorUtils.log("§c█ §7########################################################################## §c█");
        ColorUtils.log("§c▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀");
    }
}
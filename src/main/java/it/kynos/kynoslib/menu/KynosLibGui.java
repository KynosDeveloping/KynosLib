package it.kynos.kynoslib.menu;

import it.kynos.kynoslib.KynosLib;
import it.kynos.kynoslib.managers.ModuleManager;
import it.kynos.kynoslib.utils.ColorUtils;
import it.kynos.kynoslib.utils.CooldownManager;
import it.kynos.kynoslib.utils.MessageUtils;
import it.kynos.kynoslib.utils.SoundManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KynosLibGui extends KynosGui {

    private static final int SLOT_FILE_MANAGER    = 10;
    private static final int SLOT_GUI_MANAGER     = 11;
    private static final int SLOT_PLUGIN_DISABLER = 13;
    private static final int SLOT_PLUGIN_ENABLER  = 14;
    private static final int SLOT_PLAYER_JOIN     = 16;
    private static final int SLOT_RELOAD          = 22;

    private final Map<UUID, Long> clickCooldowns = new HashMap<>();
    private final MessageUtils msg;

    public KynosLibGui() {
        super(27, "§8⚙ KynosLib Management");
        this.msg = new MessageUtils(KynosLib.getInstance());
        render();
    }

    public void render() {
        inventory.clear();
        fillBorder(Material.BLACK_STAINED_GLASS_PANE);

        ModuleManager mm = KynosLib.getInstance().getModuleManager();
        boolean pjEnabled = KynosLib.getInstance().getConfig().getBoolean("Listeners.PlayerJoin.Enabled", true);

        inventory.setItem(SLOT_FILE_MANAGER,    moduleItem("FileManager",    mm.isEnabled("FileManager")));
        inventory.setItem(SLOT_GUI_MANAGER,     moduleItem("GUI Manager",    mm.isEnabled("GuiManager")));
        inventory.setItem(SLOT_PLUGIN_DISABLER, moduleItem("Plugin Disabler", mm.isEnabled("PluginDisabler")));
        inventory.setItem(SLOT_PLUGIN_ENABLER,  moduleItem("Plugin Enabler",  mm.isEnabled("PluginEnabler")));
        inventory.setItem(SLOT_PLAYER_JOIN,     listenerItem("PlayerJoin",   pjEnabled));

        inventory.setItem(SLOT_RELOAD, buildItem(Material.SUNFLOWER, "&e&lReload Config",
                List.of("&7Reload configuration from disk.", "&7No server restart required.")));
    }

    @Override
    public void handleSlotClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        int slot = event.getSlot();
        if (slot != SLOT_FILE_MANAGER && slot != SLOT_GUI_MANAGER &&
                slot != SLOT_PLUGIN_DISABLER && slot != SLOT_PLUGIN_ENABLER &&
                slot != SLOT_PLAYER_JOIN && slot != SLOT_RELOAD) {
            return;
        }

        // Sistema di controllo Cooldown locale via Manager condivisibile
        if (!CooldownManager.checkAndSet(player, "guiclick", 3000)) {
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        KynosLib plugin = KynosLib.getInstance();
        ModuleManager mm = plugin.getModuleManager();

        String toggledModule = null;
        boolean newState = false;

        switch (slot) {
            case SLOT_FILE_MANAGER -> {
                mm.toggle("FileManager");
                toggledModule = "FileManager";
                newState = mm.isEnabled("FileManager");
            }
            case SLOT_GUI_MANAGER -> {
                mm.toggle("GuiManager");
                toggledModule = "GuiManager";
                newState = mm.isEnabled("GuiManager");
            }
            case SLOT_PLUGIN_DISABLER -> {
                mm.toggle("PluginDisabler");
                toggledModule = "Plugin Disabler";
                newState = mm.isEnabled("PluginDisabler");
            }
            case SLOT_PLUGIN_ENABLER -> {
                mm.toggle("PluginEnabler");
                toggledModule = "Plugin Enabler";
                newState = mm.isEnabled("PluginEnabler");
            }
            case SLOT_PLAYER_JOIN -> {
                boolean cur = plugin.getConfig().getBoolean("Listeners.PlayerJoin.Enabled", true);
                plugin.getConfig().set("Listeners.PlayerJoin.Enabled", !cur);
                plugin.saveConfig();
                toggledModule = "PlayerJoin";
                newState = !cur;
            }
            case SLOT_RELOAD -> {
                plugin.reloadPlugin();
                player.sendMessage(ColorUtils.translate(msg.get("Messages.ReloadSuccess")));
                render();
                SoundManager.playFromConfig(player, plugin.getConfig(), "Messages.GUI");
                return;
            }
            default -> { return; }
        }

        // Riproduce il suono configurato dopo un cambio di stato valido
        SoundManager.playFromConfig(player, plugin.getConfig(), "Messages.GUI");

        if (toggledModule != null) {
            String stateKey = newState ? "Messages.GUI.StateEnabled" : "Messages.GUI.StateDisabled";
            String stateLabel = msg.get(stateKey);
            player.sendMessage(ColorUtils.translate(
                    msg.get("Messages.GUI.ModuleToggled", "{module}", toggledModule, "{state}", stateLabel)
            ));
        }

        // Forza il rinfresco visivo degli inventari
        render();
    }

    private org.bukkit.inventory.ItemStack moduleItem(String name, boolean enabled) {
        Material mat = enabled ? Material.GREEN_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE;
        String stateKey = enabled ? "Messages.GUI.StateEnabled" : "Messages.GUI.StateDisabled";
        String stateLabel = msg.get(stateKey);
        String action = enabled ? "&7Click to &cdisable" : "&7Click to &aenable";
        return buildItem(mat, "&b" + name,
                List.of("&7Status: " + stateLabel, "", action,
                        "", "&8Some changes may require a restart."));
    }

    private org.bukkit.inventory.ItemStack listenerItem(String name, boolean enabled) {
        Material mat = enabled ? Material.LIME_DYE : Material.GRAY_DYE;
        String stateKey = enabled ? "Messages.GUI.StateEnabled" : "Messages.GUI.StateDisabled";
        String stateLabel = msg.get(stateKey);
        String action = enabled ? "&7Click to &cdisable" : "&7Click to &aenable";
        return buildItem(mat, "&b" + name + " Listener",
                List.of("&7Status: " + stateLabel, "", action));
    }
}
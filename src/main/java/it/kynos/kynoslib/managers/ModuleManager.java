package it.kynos.kynoslib.managers;

import it.kynos.kynoslib.KynosLib;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handles runtime toggle states for all internal framework modules.
 */
public class ModuleManager {

    // Optimized unmodifiable storage allocation boundaries
    private static final Map<String, Boolean> DEFAULTS = Map.of(
            "GuiManager", true,
            "PluginDisabler", true,
            "PluginEnabler", true,
            "FileManager", true
    );

    private final Map<String, Boolean> states = new HashMap<>(DEFAULTS.size());

    public ModuleManager() {
        reload();
    }

    /**
     * Synchronizes runtime maps with current configuration parameters.
     */
    public void reload() {
        final KynosLib lib = KynosLib.getInstance();
        for (Map.Entry<String, Boolean> entry : DEFAULTS.entrySet()) {
            this.states.put(entry.getKey(),
                    lib.getConfig().getBoolean("Modules." + entry.getKey(), entry.getValue()));
        }
    }

    public boolean isEnabled(String module) {
        return this.states.getOrDefault(module, false);
    }

    public void setEnabled(String module, boolean value) {
        if (!DEFAULTS.containsKey(module)) return;
        this.states.put(module, value);

        final KynosLib lib = KynosLib.getInstance();
        lib.getConfig().set("Modules." + module, value);
        lib.saveConfig();
    }

    public void toggle(String module) {
        setEnabled(module, !isEnabled(module));
    }

    public Set<String> getModules() {
        return DEFAULTS.keySet();
    }
}
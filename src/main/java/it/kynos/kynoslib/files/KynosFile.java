package it.kynos.kynoslib.files;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Utility class to manage custom YAML files within KynosLib framework plugins.
 */
public class KynosFile {

    private final JavaPlugin plugin;
    private final File file;
    private FileConfiguration config;

    /**
     * Initializes or loads a YAML configuration file within the plugin's data folder.
     *
     * @param plugin   The owner plugin instance.
     * @param fileName The name of the target file (e.g., "data.yml" or "subfolder/data.yml").
     */
    public KynosFile(JavaPlugin plugin, String fileName) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            final File parent = file.getParentFile();
            if (parent != null) {
                parent.mkdirs();
            }

            final InputStream resource = plugin.getResource(fileName);
            if (resource != null) {
                plugin.saveResource(fileName, false);
            } else {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().severe("Could not create custom configuration file " + fileName + ": " + e.getMessage());
                }
            }
        }
        load();
    }

    private void load() {
        this.config = YamlConfiguration.loadConfiguration(file);

        final InputStream resource = plugin.getResource(file.getName());
        if (resource != null) {
            try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8)) {
                final YamlConfiguration defaults = YamlConfiguration.loadConfiguration(reader);
                this.config.setDefaults(defaults);
            } catch (IOException e) {
                plugin.getLogger().warning("Failed to properly close resource reader for " + file.getName());
            }
        }
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public File getFile() {
        return this.file;
    }

    public void save() {
        try {
            this.config.save(this.file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save configuration data to " + this.file.getName() + ": " + e.getMessage());
        }
    }

    public void reload() {
        load();
    }

    public void saveAndReload() {
        save();
        reload();
    }

    public boolean contains(String path) {
        return this.config.contains(path);
    }

    public void remove(String path) {
        this.config.set(path, null);
        save();
    }
}
package it.kynos.kynoslib.menu;

import it.kynos.kynoslib.utils.ColorUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract core structure representing highly-optimized internal framework user interfaces.
 */
public abstract class KynosGui implements InventoryHolder {

    protected final Inventory inventory;

    protected KynosGui(final int size, final String title) {
        this.inventory = Bukkit.createInventory(this, size, ColorUtils.translateToString(title));
    }

    /**
     * Fired automatically when a validated slot click bypasses safety cancel criteria.
     *
     * @param event The triggered InventoryClickEvent instance context.
     */
    public abstract void handleSlotClick(final InventoryClickEvent event);

    @Override
    public final Inventory getInventory() {
        return this.inventory;
    }

    public final void open(final Player player) {
        player.openInventory(this.inventory);
    }

    protected final ItemStack buildItem(final Material material, final String name, final String... lore) {
        return buildItem(material, 1, name, Arrays.asList(lore));
    }

    protected final ItemStack buildItem(final Material material, final String name, final List<String> lore) {
        return buildItem(material, 1, name, lore);
    }

    protected final ItemStack buildItem(final Material material, final int amount, final String name, final List<String> lore) {
        final ItemStack item = new ItemStack(material, amount);
        final ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ColorUtils.translateToString(name));
            meta.setLore(ColorUtils.translateListToString(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Dynamically renders a continuous filler border using localized inventory metrics.
     */
    protected final void fillBorder(final Material material) {
        final int size = this.inventory.getSize();
        final int rows = size / 9;
        final ItemStack borderFiller = buildItem(material, " ");

        for (int i = 0; i < 9; i++) this.inventory.setItem(i, borderFiller);
        for (int i = size - 9; i < size; i++) this.inventory.setItem(i, borderFiller);

        for (int row = 1; row < rows - 1; row++) {
            this.inventory.setItem(row * 9, borderFiller);
            this.inventory.setItem(row * 9 + 8, borderFiller);
        }
    }

    /**
     * Efficiently scans and populates completely empty storage layouts with a specific pattern framework.
     */
    protected final void fillEmpty(final Material material) {
        final ItemStack filler = buildItem(material, " ");
        final int size = this.inventory.getSize();

        for (int i = 0; i < size; i++) {
            final ItemStack current = this.inventory.getItem(i);
            if (current == null || current.getType() == Material.AIR) {
                this.inventory.setItem(i, filler);
            }
        }
    }
}
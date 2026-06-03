package it.kynos.kynoslib.menu;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

/**
 * Handles inventory interactions and routes click inputs directly to active KynosGui instances.
 */
public class GuiListener implements Listener {

    @EventHandler
    public void onGuiClick(final InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof KynosGui gui) {
            event.setCancelled(true);

            final Inventory clickedInventory = event.getClickedInventory();
            if (clickedInventory != null && clickedInventory.getHolder() instanceof KynosGui) {
                gui.handleSlotClick(event);
            }
        }
    }

    @EventHandler
    public void onGuiDrag(final InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof KynosGui) {
            event.setCancelled(true);
        }
    }
}
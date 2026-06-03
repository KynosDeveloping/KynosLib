package it.kynos.kynoslib.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

/**
 * Ensures the main /kynoslib command structure bypasses early cancellations from third-party plugins.
 */
public class CommandProtectionListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        final String msg = event.getMessage().toLowerCase().trim();
        if (isKynosLibCommand(msg)) {
            event.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(ServerCommandEvent event) {
        final String cmd = event.getCommand().toLowerCase().trim();
        if (cmd.equals("kynoslib") || cmd.startsWith("kynoslib ")) {
            event.setCancelled(false);
        }
    }

    private boolean isKynosLibCommand(String message) {
        return message.equals("/kynoslib")
                || message.startsWith("/kynoslib ")
                || message.equals("/kynoslib:kynoslib")
                || message.startsWith("/kynoslib:kynoslib ");
    }
}
package it.kynos.kynoslib.listeners;

import it.kynos.kynoslib.KynosLib;
import it.kynos.kynoslib.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.kyori.adventure.text.Component;

public class PlayerJoinListener implements Listener {

    private final MessageUtils messageUtils;

    public PlayerJoinListener() {
        this.messageUtils = new MessageUtils(KynosLib.getInstance());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final KynosLib plugin = KynosLib.getInstance();
        if (!plugin.getConfig().getBoolean("Listeners.PlayerJoin.Enabled", true)) {
            return;
        }

        final Player player = event.getPlayer();
        final String rawMessage = plugin.getConfig().getString("Messages.Listeners.PlayerJoinEvent.message");

        if (rawMessage == null || rawMessage.isEmpty()) {
            event.joinMessage(Component.empty());
            return;
        }

        // Resolves placeholders, colors and passes the optimized component context to the event flow
        final String formattedMessage = this.messageUtils.get(player, "Messages.Listeners.PlayerJoinEvent.message");
        event.joinMessage(Component.text(formattedMessage));
    }
}
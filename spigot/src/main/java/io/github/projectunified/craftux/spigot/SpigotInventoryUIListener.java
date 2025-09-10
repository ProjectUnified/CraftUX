package io.github.projectunified.craftux.spigot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;

/**
 * The listener for {@link SpigotInventoryUI}.
 * Must be registered so that {@link SpigotInventoryUI} works
 */
public final class SpigotInventoryUIListener implements Listener {
    private final Plugin plugin;

    /**
     * Create the listener
     *
     * @param plugin the plugin
     */
    public SpigotInventoryUIListener(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Register the listener
     */
    public void register() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Unregister the listener
     */
    public void unregister() {
        HandlerList.unregisterAll(this);
    }

    private SpigotInventoryUI getUI(InventoryEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof SpigotInventoryUI) {
            return (SpigotInventoryUI) holder;
        }
        return null;
    }

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        SpigotInventoryUI ui = getUI(event);
        if (ui == null) return;
        ui.onOpen(event);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        SpigotInventoryUI ui = getUI(event);
        if (ui == null) return;

        boolean wasCancelled = event.isCancelled();
        event.setCancelled(true);

        ui.handleClick(event);

        if (!wasCancelled && !event.isCancelled()) {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        SpigotInventoryUI ui = getUI(event);
        if (ui == null) return;
        ui.onClose(event);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        SpigotInventoryUI ui = getUI(event);
        if (ui == null) return;

        boolean wasCancelled = event.isCancelled();
        event.setCancelled(true);

        ui.handleDrag(event);

        if (!wasCancelled && !event.isCancelled()) {
            event.setCancelled(false);
        }
    }
}

package io.github.projectunified.craftux.spigot;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Mask;
import io.github.projectunified.craftux.common.Position;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The inventory UI for Spigot
 */
public class SpigotInventoryUI implements InventoryHolder {
    private final UUID viewerId;
    private final Inventory inventory;
    private final AtomicReference<Map<Integer, Consumer<Object>>> eventConsumerMapRef = new AtomicReference<>();
    private Mask mask;
    private boolean moveItemOnBottom = false;

    /**
     * Create a new inventory UI
     *
     * @param viewerId          the viewer's UUID
     * @param inventoryFunction the function to create the inventory
     */
    public SpigotInventoryUI(UUID viewerId, Function<InventoryHolder, Inventory> inventoryFunction) {
        this.viewerId = viewerId;
        this.inventory = inventoryFunction.apply(this);
    }

    /**
     * Create a new Chest inventory UI
     *
     * @param viewerId the viewer's UUID
     * @param title    the title of the inventory
     * @param size     the size of the inventory (must be a multiple of 9)
     */
    public SpigotInventoryUI(UUID viewerId, String title, int size) {
        this(viewerId, holder -> Bukkit.createInventory(holder, size, title));
    }

    /**
     * Create a new Chest inventory UI
     *
     * @param viewerId the viewer's UUID
     * @param title    the title of the inventory
     * @param type     the type of the inventory
     */
    public SpigotInventoryUI(UUID viewerId, String title, InventoryType type) {
        this(viewerId, holder -> Bukkit.createInventory(holder, type, title));
    }

    /**
     * Register the inventory listener. Must be called once.
     *
     * @param plugin the plugin
     */
    public static void register(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(new InventoryListener(), plugin);
    }

    /**
     * Convert a position to a slot index
     *
     * @param position the position
     * @return the slot index
     */
    protected int toSlot(Position position) {
        int slotPerRow;
        switch (inventory.getType()) {
            case CHEST:
            case ENDER_CHEST:
            case SHULKER_BOX:
                slotPerRow = 9;
                break;
            case DISPENSER:
            case DROPPER:
            case HOPPER:
                slotPerRow = 3;
                break;
            default:
                slotPerRow = inventory.getSize();
                break;
        }
        return position.getX() + position.getY() * slotPerRow;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Open the inventory for the player
     *
     * @param player the player
     */
    public void open(Player player) {
        player.openInventory(inventory);
    }

    /**
     * Open the inventory for the viewer
     */
    public void open() {
        Player player = Bukkit.getPlayer(viewerId);
        if (player != null) {
            open(player);
        }
    }

    /**
     * Get the viewer's UUID
     *
     * @return the viewer's UUID
     */
    public UUID getViewerId() {
        return viewerId;
    }

    /**
     * Get the mask
     *
     * @return the mask
     */
    public Mask getMask() {
        return mask;
    }

    /**
     * Set the mask
     *
     * @param mask the mask
     */
    public void setMask(Mask mask) {
        this.mask = mask;
    }

    /**
     * Whether to allow moving items in the bottom inventory (player inventory)
     *
     * @param moveItemOnBottom true to allow moving items in the bottom inventory
     */
    public void setMoveItemOnBottom(boolean moveItemOnBottom) {
        this.moveItemOnBottom = moveItemOnBottom;
    }

    /**
     * Update the inventory
     */
    public void update() {
        Map<Position, Consumer<ActionItem>> positionActionItemMap = mask != null ? mask.apply(viewerId) : null;
        if (positionActionItemMap == null) {
            inventory.clear();
            eventConsumerMapRef.set(null);
            return;
        }

        Map<Integer, Consumer<ActionItem>> newItemMap = positionActionItemMap.entrySet().stream()
                .collect(Collectors.toMap(entry -> toSlot(entry.getKey()), Map.Entry::getValue));

        Map<Integer, Consumer<Object>> consumerMap = new HashMap<>();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ActionItem actionItem = new ActionItem();
            Consumer<ActionItem> actionItemConsumer = newItemMap.get(slot);
            if (actionItemConsumer != null) {
                actionItemConsumer.accept(actionItem);
            }

            ItemStack item = actionItem.getItem(ItemStack.class);
            inventory.setItem(slot, item);

            Consumer<Object> consumer = actionItem.getAction();
            if (consumer != null) {
                consumerMap.put(slot, consumer);
            }
        }

        eventConsumerMapRef.set(consumerMap);
    }

    void handleClick(InventoryClickEvent event) {
        if (moveItemOnBottom) {
            if (event.getClickedInventory() != event.getInventory()) {
                switch (event.getAction()) {
                    case DROP_ALL_SLOT:
                    case DROP_ONE_SLOT:
                    case PICKUP_ALL:
                    case PICKUP_HALF:
                    case PICKUP_ONE:
                    case PICKUP_SOME:
                    case HOTBAR_MOVE_AND_READD:
                    case PLACE_ALL:
                    case PLACE_ONE:
                    case PLACE_SOME:
                    case HOTBAR_SWAP:
                    case SWAP_WITH_CURSOR:
                        event.setCancelled(false);
                        break;
                    default:
                        break;
                }
            }
        }

        this.onClick(event);

        Map<Integer, Consumer<Object>> consumerMap = eventConsumerMapRef.get();
        if (consumerMap == null) return;
        Consumer<Object> consumer = consumerMap.get(event.getRawSlot());
        if (consumer == null) return;

        consumer.accept(event);
    }

    void handleDrag(InventoryDragEvent event) {
        boolean slotInInventory = false;
        for (int slot : event.getRawSlots()) {
            if (slot < inventory.getSize()) {
                slotInInventory = true;
                break;
            }
        }
        if (!slotInInventory) {
            event.setCancelled(false);
        }

        this.onDrag(event);
    }

    /**
     * Called when the inventory is opened. Override to add custom behavior.
     *
     * @param event the event
     */
    protected void onOpen(InventoryOpenEvent event) {
    }

    /**
     * Called when the inventory is clicked. Override to add custom behavior.
     *
     * @param event the event
     */
    protected void onClick(InventoryClickEvent event) {
    }

    /**
     * Called when the inventory is closed. Override to add custom behavior.
     *
     * @param event the event
     */
    protected void onClose(InventoryCloseEvent event) {
    }

    /**
     * Called when the inventory is dragged. Override to add custom behavior.
     *
     * @param event the event
     */
    protected void onDrag(InventoryDragEvent event) {
    }

    /**
     * The inventory listener
     */
    public static class InventoryListener implements Listener {
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
}

package io.github.projectunified.craftux.spigot;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Mask;
import io.github.projectunified.craftux.common.Position;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents an inventory-based user interface for Spigot (Minecraft) servers.
 * Manages the display and interaction of GUI elements in a player's inventory.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SpigotInventoryUI ui = new SpigotInventoryUI(playerUUID, "My GUI", 27); // 3 rows
 * ui.setMask(myMask);
 * ui.setDefaultButton(new SimpleButton(new ItemStack(Material.BARRIER), event -> {}));
 * ui.update(); // Update inventory contents
 * ui.open(player); // Open for player
 * }</pre>
 */
public class SpigotInventoryUI implements InventoryHolder {
    private final UUID viewerId;
    private final Inventory inventory;
    private final AtomicReference<Map<Integer, Consumer<Object>>> eventConsumerMapRef = new AtomicReference<>();
    private Mask mask;
    private Button defaultButton;
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
     * Get the default button. This button is used when an item is not explicitly defined in the mask.
     *
     * @return the default button
     */
    public Button getDefaultButton() {
        return defaultButton;
    }

    /**
     * Set the default button. This button is used when an item is not explicitly defined in the mask.
     *
     * @param defaultButton the default button
     */
    public void setDefaultButton(Button defaultButton) {
        this.defaultButton = defaultButton;
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
                .collect(Collectors.toMap(entry -> SpigotInventoryUtil.toSlot(entry.getKey(), inventory.getType()), Map.Entry::getValue));

        Consumer<ActionItem> defaultActionItemConsumer = defaultButton == null ? null : defaultButton.apply(viewerId);

        Map<Integer, Consumer<Object>> consumerMap = new HashMap<>();
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            ActionItem actionItem = this.createActionItem();

            if (defaultActionItemConsumer != null) {
                defaultActionItemConsumer.accept(actionItem);
            }

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

        if (!this.onClick(event)) {
            return;
        }

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
     * Create an action item. Override this to add default logic to the item.
     *
     * @return the action item
     */
    protected ActionItem createActionItem() {
        return new ActionItem();
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
     * @return true if the action in the inventory can be performed
     */
    protected boolean onClick(InventoryClickEvent event) {
        return true;
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
}

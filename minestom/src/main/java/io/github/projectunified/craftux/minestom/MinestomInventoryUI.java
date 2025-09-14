package io.github.projectunified.craftux.minestom;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Mask;
import io.github.projectunified.craftux.common.Position;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryOpenEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The inventory UI for Minestom
 */
public class MinestomInventoryUI {
    private final UUID viewerId;
    private final Inventory inventory;
    private final AtomicReference<Map<Integer, Consumer<Object>>> eventConsumerMapRef = new AtomicReference<>();
    private final EventNode<@NotNull InventoryEvent> eventNode;
    private Mask mask;
    private Button defaultButton;

    /**
     * Create a new inventory UI
     *
     * @param viewerId  the viewer's UUID
     * @param inventory the inventory
     */
    public MinestomInventoryUI(UUID viewerId, Inventory inventory) {
        this.viewerId = viewerId;
        this.inventory = inventory;

        eventNode = EventNode.event("inventory-" + UUID.randomUUID(), EventFilter.INVENTORY, event -> Objects.equals(event.getInventory(), inventory));
        eventNode.addListener(InventoryOpenEvent.class, this::onOpen);
        eventNode.addListener(InventoryPreClickEvent.class, event -> {
            boolean wasCancelled = event.isCancelled();
            event.setCancelled(true);
            handleClick(event);
            if (!wasCancelled && !event.isCancelled()) {
                event.setCancelled(false);
            }
        });
        eventNode.addListener(InventoryCloseEvent.class, this::onClose);
    }

    /**
     * Create a new inventory UI
     *
     * @param viewerId      the viewer's UUID
     * @param inventoryType the inventory type
     * @param title         the title of the inventory
     */
    public MinestomInventoryUI(UUID viewerId, InventoryType inventoryType, Component title) {
        this(viewerId, new Inventory(inventoryType, title));
    }

    /**
     * Register the inventory UI event node to the global event handler
     */
    public void register() {
        MinecraftServer.getGlobalEventHandler().addChild(eventNode);
    }

    /**
     * Unregister the inventory UI event node from the global event handler
     */
    public void unregister() {
        MinecraftServer.getGlobalEventHandler().removeChild(eventNode);
    }

    /**
     * Get the inventory
     *
     * @return the inventory
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Get the event node of the inventory UI
     *
     * @return the event node
     */
    public EventNode<@NotNull InventoryEvent> getEventNode() {
        return eventNode;
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
        Player player = MinecraftServer.getConnectionManager().getOnlinePlayerByUuid(viewerId);
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
                .collect(Collectors.toMap(entry -> MinestomInventoryUtil.toSlot(entry.getKey(), inventory.getInventoryType()), Map.Entry::getValue));

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
            inventory.setItemStack(slot, item == null ? ItemStack.AIR : item);

            Consumer<Object> consumer = actionItem.getAction();
            if (consumer != null) {
                consumerMap.put(slot, consumer);
            }
        }

        eventConsumerMapRef.set(consumerMap);
    }

    private void handleClick(InventoryPreClickEvent event) {
        this.onClick(event);

        Map<Integer, Consumer<Object>> consumerMap = eventConsumerMapRef.get();
        if (consumerMap == null) return;
        Consumer<Object> consumer = consumerMap.get(event.getSlot());
        if (consumer == null) return;

        consumer.accept(event);
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
     */
    protected void onClick(InventoryPreClickEvent event) {
    }

    /**
     * Called when the inventory is closed. Override to add custom behavior.
     *
     * @param event the event
     */
    protected void onClose(InventoryCloseEvent event) {
    }
}

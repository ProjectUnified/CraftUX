package io.github.projectunified.craftux.spigot.button;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Element;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * A button that stores an input item per player and allows swapping it with the cursor item on click.
 * Useful for creating input slots where players can place items.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * InputButton inputButton = new InputButton();
 * inputButton.setDisplayItemFunction((uuid, item) -> {
 *     if (item == null) return new ItemStack(Material.BARRIER); // Show barrier if empty
 *     return item;
 * });
 * }</pre>
 */
public class InputButton implements Element, Button {
    private final Map<UUID, ItemStack> map = new IdentityHashMap<>();
    private BiFunction<@NotNull UUID, @Nullable ItemStack, @Nullable ItemStack> displayItemFunction = (uuid, item) -> item;

    @Override
    public boolean apply(@NotNull UUID uuid, @NotNull ActionItem actionItem) {
        actionItem.setItem(displayItemFunction.apply(uuid, getInputItem(uuid)));
        actionItem.setAction(InventoryClickEvent.class, event -> {
            ItemStack cursorItem = Optional.ofNullable(event.getCursor())
                    .filter(itemStack -> itemStack.getType() != Material.AIR)
                    .map(ItemStack::clone)
                    .orElse(null);
            ItemStack storeItem = getInputItem(uuid);
            event.getWhoClicked().setItemOnCursor(storeItem);
            setInputItem(uuid, cursorItem);
        });
        return true;
    }

    @Override
    public void stop() {
        map.clear();
    }

    /**
     * Set the input item for the unique id
     *
     * @param uuid      the unique id
     * @param itemStack the item, or null to remove the input item
     */
    public void setInputItem(@NotNull UUID uuid, @Nullable ItemStack itemStack) {
        map.compute(uuid, (uuid1, item) -> itemStack);
    }

    /**
     * Get the input item for the unique id
     *
     * @param uuid the unique id
     * @return the item
     */
    @Nullable
    public ItemStack getInputItem(@NotNull UUID uuid) {
        return map.get(uuid);
    }

    /**
     * Set the function to display the item on the GUI
     *
     * @param displayItemFunction the function
     * @return this instance
     */
    @Contract("_ -> this")
    public InputButton setDisplayItemFunction(@NotNull BiFunction<UUID, ItemStack, ItemStack> displayItemFunction) {
        this.displayItemFunction = displayItemFunction;
        return this;
    }
}

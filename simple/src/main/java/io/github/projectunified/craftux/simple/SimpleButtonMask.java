package io.github.projectunified.craftux.simple;

import io.github.projectunified.craftux.common.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

/**
 * A simple mask that associates buttons with multiple positions in the GUI grid.
 * Allows placing the same button in multiple slots or different buttons in various positions.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * SimpleButtonMask mask = new SimpleButtonMask();
 * mask.setButton(Position.of(0, 0), new SimpleButton(new ItemStack(Material.GRASS_BLOCK)));
 * mask.setButton(Position.of(1, 1), new SimpleButton(new ItemStack(Material.STONE)));
 * mask.setButton(Position.of(0, 1), new SimpleButton(new ItemStack(Material.GRASS_BLOCK))); // Same button in multiple positions
 * Map<Position, Consumer<ActionItem>> actions = mask.apply(playerUUID);
 * }</pre>
 */
public class SimpleButtonMask implements Element, Mask {
    private final Map<Button, Collection<Position>> buttonSlotMap = new LinkedHashMap<>();

    /**
     * Set the button
     *
     * @param position the position
     * @param button   the button
     */
    public void setButton(Position position, @NotNull Button button) {
        buttonSlotMap.computeIfAbsent(button, b -> new ArrayList<>()).add(position);
    }

    /**
     * Get the button-to-slot map
     *
     * @return the button-to-slot map
     */
    public Map<Button, Collection<Position>> getButtonSlotMap() {
        return Collections.unmodifiableMap(buttonSlotMap);
    }

    @Override
    public void init() {
        Element.handleIfElement(buttonSlotMap.keySet(), Element::init);
    }

    @Override
    public void stop() {
        Element.handleIfElement(buttonSlotMap.keySet(), Element::stop);
        buttonSlotMap.clear();
    }

    @Override
    public @NotNull Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        Map<Position, Consumer<ActionItem>> map = new HashMap<>();

        buttonSlotMap.forEach(
                (button, positions) -> {
                    Consumer<ActionItem> consumer = button.apply(uuid);
                    positions.forEach(position -> map.merge(
                            position,
                            consumer,
                            Consumer::andThen
                    ));
                }
        );

        return map;
    }
}

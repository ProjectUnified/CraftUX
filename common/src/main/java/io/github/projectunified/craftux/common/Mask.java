package io.github.projectunified.craftux.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The mask that maps positions to action item consumers
 */
public interface Mask {
    /**
     * Get a map of positions to action item consumers
     *
     * @param uuid the uuid of the player
     * @return the map, or null if no items should be displayed. Can return null in a conditional case (e.g. predicate mask)
     */
    @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid);

    /**
     * Get a map of positions to action items
     *
     * @param uuid the uuid of the player
     * @return the map, or null if no items should be displayed. Can return null in a conditional case (e.g. predicate mask)
     */
    default @Nullable Map<Position, ActionItem> getActionMap(UUID uuid) {
        Map<Position, Consumer<ActionItem>> map = apply(uuid);
        if (map == null) return null;
        return map.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                    ActionItem actionItem = new ActionItem();
                    entry.getValue().accept(actionItem);
                    return actionItem;
                }));
    }
}

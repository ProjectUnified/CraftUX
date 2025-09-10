package io.github.projectunified.craftux.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

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
}

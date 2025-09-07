package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The mask with a list of child masks
 */
public class ListMask extends MultiMask<Map<Position, Consumer<ActionItem>>> {
    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        for (Function<@NotNull UUID, @Nullable Map<Position, Consumer<ActionItem>>> mask : elements) {
            Map<Position, Consumer<ActionItem>> itemMap = mask.apply(uuid);
            if (itemMap != null) {
                return itemMap;
            }
        }
        return null;
    }
}

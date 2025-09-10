package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Mask;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * The mask with a list of child masks
 */
public class ListMask extends MultiMask<Mask> {
    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        for (Mask mask : elements) {
            Map<Position, Consumer<ActionItem>> itemMap = mask.apply(uuid);
            if (itemMap != null) {
                return itemMap;
            }
        }
        return null;
    }
}

package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * The mask that views multiple masks
 */
public class HybridMask extends MultiMask<Map<Position, ActionItem>> {
    @Override
    public @NotNull Map<Position, ActionItem> apply(@NotNull UUID uuid) {
        Map<Position, ActionItem> itemMap = new HashMap<>();
        for (Function<@NotNull UUID, @Nullable Map<Position, ActionItem>> mask : elements) {
            Map<Position, ActionItem> map = mask.apply(uuid);
            if (map != null) {
                for (Map.Entry<Position, ActionItem> entry : map.entrySet()) {
                    ActionItem current = itemMap.getOrDefault(entry.getKey(), new ActionItem());
                    current.apply(entry.getValue());
                    itemMap.put(entry.getKey(), current);
                }
            }
        }
        return itemMap;
    }
}

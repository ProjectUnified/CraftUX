package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Mask;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * The mask that views multiple masks
 */
public class HybridMask extends MultiMask<Mask> {
    @Override
    public @NotNull Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        Map<Position, Consumer<ActionItem>> itemMap = new HashMap<>();
        for (Mask mask : elements) {
            Map<Position, Consumer<ActionItem>> map = mask.apply(uuid);
            if (map != null) {
                for (Map.Entry<Position, Consumer<ActionItem>> entry : map.entrySet()) {
                    itemMap.merge(entry.getKey(), entry.getValue(), Consumer::andThen);
                }
            }
        }
        return itemMap;
    }
}

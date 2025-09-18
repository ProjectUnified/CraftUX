package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Mask;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * The mask with a list of child masks
 */
public class ListMask extends MultiMask<Mask> {
    private final Map<UUID, Integer> currentIndexMap = new ConcurrentHashMap<>();
    private boolean keepCurrentIndex = false;

    /**
     * Should the mask keep the current index for the unique id?
     *
     * @return true if it should
     */
    public boolean isKeepCurrentIndex() {
        return keepCurrentIndex;
    }

    /**
     * Should the mask keep the current index for the unique id?
     *
     * @param keepCurrentIndex true if it should
     */
    public void setKeepCurrentIndex(boolean keepCurrentIndex) {
        this.keepCurrentIndex = keepCurrentIndex;
    }

    /**
     * Remove the current index for the unique id
     *
     * @param uuid the unique id
     */
    public void removeCurrentIndex(UUID uuid) {
        this.currentIndexMap.remove(uuid);
    }

    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        if (keepCurrentIndex && currentIndexMap.containsKey(uuid)) {
            return elements.get(currentIndexMap.get(uuid)).apply(uuid);
        }

        for (int i = 0; i < elements.size(); i++) {
            Map<Position, Consumer<ActionItem>> itemMap = elements.get(i).apply(uuid);
            if (itemMap != null) {
                currentIndexMap.put(uuid, i);
                return itemMap;
            }
        }
        return null;
    }

    @Override
    public void stop() {
        super.stop();
        currentIndexMap.clear();
    }
}

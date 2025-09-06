package io.github.projectunified.craftux.button;

import io.github.projectunified.craftux.common.ActionItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * The button that loops through the list of child buttons
 */
public class ListButton extends MultiButton {
    private final Map<UUID, Integer> currentIndexMap = new ConcurrentHashMap<>();
    private boolean keepCurrentIndex = false;

    /**
     * Should the button keep the current index for the unique id?
     *
     * @return true if it should
     */
    public boolean isKeepCurrentIndex() {
        return keepCurrentIndex;
    }

    /**
     * Should the button keep the current index for the unique id?
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
    public @Nullable ActionItem apply(@NotNull UUID uuid) {
        if (keepCurrentIndex && currentIndexMap.containsKey(uuid)) {
            return buttons.get(currentIndexMap.get(uuid)).apply(uuid);
        }

        for (int i = 0; i < getButtons().size(); i++) {
            Function<@NotNull UUID, @Nullable ActionItem> button = buttons.get(i);
            ActionItem item = button.apply(uuid);
            if (item != null) {
                currentIndexMap.put(uuid, i);
                return item;
            }
        }

        return null;
    }
}

package io.github.projectunified.craftux.button;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A button that applies actions from a list of child buttons, cycling through them.
 * It can optionally remember the current button index per player UUID.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * ListButton listButton = new ListButton();
 * listButton.addButton(
 *     new SimpleButton(new ItemStack(Material.STONE)),
 *     new SimpleButton(new ItemStack(Material.COBBLESTONE))
 * );
 * listButton.setKeepCurrentIndex(true); // Remember choice per player
 * }</pre>
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
    public boolean apply(@NotNull UUID uuid, @NotNull ActionItem actionItem) {
        if (keepCurrentIndex && currentIndexMap.containsKey(uuid)) {
            return buttons.get(currentIndexMap.get(uuid)).apply(uuid, actionItem);
        }

        for (int i = 0; i < buttons.size(); i++) {
            Button button = buttons.get(i);
            if (button.apply(uuid, actionItem)) {
                currentIndexMap.put(uuid, i);
                return true;
            }
        }

        return false;
    }

    @Override
    public void stop() {
        super.stop();
        currentIndexMap.clear();
    }
}

package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * A button paginated mask, where each button is a page
 */
public abstract class SequencePaginatedMask extends PaginatedMask {
    protected final Function<UUID, List<Position>> maskPositionFunction;

    /**
     * Create a new mask
     *
     * @param maskPositionFunction the mask position function
     */
    protected SequencePaginatedMask(@NotNull Function<UUID, List<Position>> maskPositionFunction) {
        this.maskPositionFunction = maskPositionFunction;
    }

    /**
     * Get the mask position function
     *
     * @return the mask position
     */
    @NotNull
    public Function<UUID, List<Position>> getMaskPositionFunction() {
        return this.maskPositionFunction;
    }

    /**
     * Get the buttons for the unique id
     *
     * @param uuid the unique id
     * @return the buttons
     */
    @NotNull
    public abstract List<@NotNull Function<@NotNull UUID, @Nullable ActionItem>> getButtons(UUID uuid);

    @Override
    protected @Nullable Map<@NotNull Position, @NotNull ActionItem> getItemMap(@NotNull UUID uuid, int pageNumber) {
        List<Position> positions = this.maskPositionFunction.apply(uuid);
        List<Function<@NotNull UUID, @Nullable ActionItem>> buttons = getButtons(uuid);
        if (buttons.isEmpty() || positions.isEmpty()) return null;

        int pageAmount = buttons.size();
        pageAmount = this.getAndSetExactPage(uuid, pageNumber, pageAmount);

        Map<Position, ActionItem> map = new HashMap<>();
        int basePage = this.getPage(uuid);
        int buttonsSize = buttons.size();
        int positionSize = positions.size();

        for (int i = 0; i < positionSize; i++) {
            int index = i + basePage;
            if (this.cycle) {
                index = this.getExactPage(index, pageAmount);
            } else if (index >= buttonsSize) {
                break;
            }
            ActionItem actionItem = buttons.get(index).apply(uuid);
            if (actionItem != null) {
                map.put(positions.get(i), actionItem);
            }
        }

        return map;
    }

    @Override
    public void stop() {
        this.pageNumberMap.clear();
    }
}

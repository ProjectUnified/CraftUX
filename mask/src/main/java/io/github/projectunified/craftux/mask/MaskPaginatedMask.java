package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * The mask paginated mask, those with a long list of masks divided into pages.
 */
public abstract class MaskPaginatedMask extends PaginatedMask {
    /**
     * Get the masks for the unique id
     *
     * @param uuid the unique id
     * @return the masks
     */
    @NotNull
    public abstract List<@NotNull Function<@NotNull UUID, @Nullable Map<Position, ActionItem>>> getMasks(@NotNull UUID uuid);

    @Override
    protected @Nullable Map<@NotNull Position, @NotNull ActionItem> getItemMap(@NotNull UUID uuid, int pageNumber) {
        List<Function<@NotNull UUID, @Nullable Map<Position, ActionItem>>> masks = getMasks(uuid);
        if (masks.isEmpty()) {
            return null;
        }
        int pageAmount = masks.size();
        pageNumber = getAndSetExactPage(uuid, pageNumber, pageAmount);
        return masks.get(pageNumber).apply(uuid);
    }

    @Override
    public void stop() {
        this.pageNumberMap.clear();
    }
}

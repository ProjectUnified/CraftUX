package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Mask;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * An abstract mask that displays one mask at a time from a list, allowing pagination through multiple masks.
 * Each page shows the actions from a single mask in the list.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * public class MyMaskPaginatedMask extends MaskPaginatedMask {
 *     @Override
 *     public List<Mask> getMasks(UUID uuid) {
 *         return Arrays.asList(
 *             new SingleMask(Position.of(0, 0), new SimpleButton(new ItemStack(Material.APPLE))),
 *             new SingleMask(Position.of(0, 0), new SimpleButton(new ItemStack(Material.BREAD))),
 *             new SingleMask(Position.of(0, 0), new SimpleButton(new ItemStack(Material.COOKED_BEEF)))
 *         );
 *     }
 * }
 * }</pre>
 */
public abstract class MaskPaginatedMask extends PaginatedMask {
    /**
     * Get the masks for the unique id
     *
     * @param uuid the unique id
     * @return the masks
     */
    @NotNull
    public abstract List<Mask> getMasks(@NotNull UUID uuid);

    @Override
    protected @Nullable Map<Position, Consumer<ActionItem>> getItemMap(@NotNull UUID uuid, int pageNumber) {
        List<Mask> masks = getMasks(uuid);
        if (masks.isEmpty()) {
            return null;
        }
        int pageAmount = masks.size();
        pageNumber = getAndSetExactPage(uuid, pageNumber, pageAmount);
        return masks.get(pageNumber).apply(uuid);
    }

    @Override
    protected int getPageAmount(@NotNull UUID uuid) {
        List<Mask> masks = getMasks(uuid);
        return masks.size();
    }

    /**
     * Clears the page number mappings for all users.
     */
    @Override
    public void stop() {
        this.pageNumberMap.clear();
    }
}

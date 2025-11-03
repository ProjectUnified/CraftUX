package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An abstract mask that displays a paginated list of buttons across multiple pages.
 * Subclasses define the positions and button lists, while this class handles pagination logic.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * public class MyButtonPaginatedMask extends ButtonPaginatedMask {
 *     public MyButtonPaginatedMask() {
 *         super(uuid -> Arrays.asList(Position.of(0, 0), Position.of(1, 1)));
 *     }
 *
 *     @Override
 *     public List<Button> getButtons(UUID uuid) {
 *         return Arrays.asList(
 *             new SimpleButton(new ItemStack(Material.WOODEN_SWORD)),
 *             new SimpleButton(new ItemStack(Material.STONE_SWORD))
 *         );
 *     }
 * }
 * }</pre>
 */
public abstract class ButtonPaginatedMask extends PaginatedMask {
    private final Function<UUID, List<Position>> maskPositionFunction;

    /**
     * Create a new mask
     *
     * @param maskPositionFunction the mask position function
     */
    protected ButtonPaginatedMask(@NotNull Function<UUID, List<Position>> maskPositionFunction) {
        this.maskPositionFunction = maskPositionFunction;
    }

    /**
     * Get the mask position function
     *
     * @return the mask position function
     */
    @NotNull
    public Function<UUID, List<Position>> getMaskPositionFunction() {
        return maskPositionFunction;
    }

    /**
     * Get the buttons for the unique id
     *
     * @param uuid the unique id
     * @return the buttons
     */
    @NotNull
    public abstract List<Button> getButtons(@NotNull UUID uuid);

    @Override
    protected @Nullable Map<Position, Consumer<ActionItem>> getItemMap(@NotNull UUID uuid, int pageNumber) {
        List<Position> positions = this.maskPositionFunction.apply(uuid);
        List<Button> buttons = getButtons(uuid);
        if (buttons.isEmpty() || positions.isEmpty()) return null;

        int pageAmount = (int) Math.ceil((double) buttons.size() / positions.size());
        pageNumber = this.getAndSetExactPage(uuid, pageNumber, pageAmount);

        Map<Position, Consumer<ActionItem>> map = new HashMap<>();
        int positionSize = positions.size();
        int offset = pageNumber * positionSize;
        int buttonsSize = buttons.size();

        for (int i = 0; i < positionSize; i++) {
            int index = i + offset;
            if (index >= buttonsSize) {
                break;
            }
            Button button = buttons.get(index);
            map.put(positions.get(i), button.apply(uuid));
        }

        return map;
    }

    /**
     * Clears the page number mappings for all users.
     */
    @Override
    public void stop() {
        this.pageNumberMap.clear();
    }
}

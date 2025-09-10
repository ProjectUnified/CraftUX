package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The masks with multiple positions
 */
public class MultiPositionMask extends MultiMask<Button> {
    protected final Function<UUID, List<Position>> maskPositionFunction;

    /**
     * Create a new mask
     *
     * @param maskPositionFunction the mask position function
     */
    public MultiPositionMask(@NotNull Function<UUID, List<Position>> maskPositionFunction) {
        this.maskPositionFunction = maskPositionFunction;
    }

    /**
     * Get the mask position function
     *
     * @return the mask position
     */
    @NotNull
    public Function<UUID, List<Position>> getMaskPositionFunction() {
        return maskPositionFunction;
    }

    @Override
    public @NotNull Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        Map<Position, Consumer<ActionItem>> map = new HashMap<>();
        List<Position> positions = this.maskPositionFunction.apply(uuid);
        if (!this.elements.isEmpty() && !positions.isEmpty()) {
            int positionSize = positions.size();
            int buttonsSize = this.elements.size();
            for (int i = 0; i < positionSize; i++) {
                map.put(positions.get(i), this.elements.get(i % buttonsSize).apply(uuid));
            }
        }
        return map;
    }
}

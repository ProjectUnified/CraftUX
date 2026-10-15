package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An abstract mask that fills a list of positions with two buttons according to a progress value.
 * The first positions get the complete button, the remaining ones get the incomplete button.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * public class MyProgressMask extends ProgressMask {
 *     public MyProgressMask() {
 *         super(uuid -> Arrays.asList(Position.of(0, 0), Position.of(1, 0), Position.of(2, 0)));
 *         setCompleteButton(new SimpleButton(new ItemStack(Material.LIME_DYE)));
 *         setIncompleteButton(new SimpleButton(new ItemStack(Material.GRAY_DYE)));
 *     }
 *
 *     @Override
 *     protected Value getValue(UUID uuid) {
 *         return new Value(1, 3); // 1 of 3, so one complete position
 *     }
 * }
 * }</pre>
 */
public abstract class ProgressMask implements Element, Mask {
    private final Function<UUID, List<Position>> maskPositionFunction;
    private Button completeButton = null;
    private Button incompleteButton = null;

    /**
     * Create a new mask
     *
     * @param maskPositionFunction the mask position function
     */
    protected ProgressMask(Function<UUID, List<Position>> maskPositionFunction) {
        this.maskPositionFunction = maskPositionFunction;
    }

    /**
     * Get the mask position function
     *
     * @return the mask position function
     */
    public Function<UUID, List<Position>> getMaskPositionFunction() {
        return maskPositionFunction;
    }

    /**
     * Set the button for the complete positions
     *
     * @param completeButton the complete button
     */
    public void setCompleteButton(@Nullable Button completeButton) {
        this.completeButton = completeButton;
    }

    /**
     * Set the button for the incomplete positions
     *
     * @param incompleteButton the incomplete button
     */
    public void setIncompleteButton(@Nullable Button incompleteButton) {
        this.incompleteButton = incompleteButton;
    }

    /**
     * Get the current progress value for the unique id
     *
     * @param uuid the unique id
     * @return the progress value
     */
    protected abstract @NotNull Value getValue(@NotNull UUID uuid);

    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        List<Position> positions = this.maskPositionFunction.apply(uuid);
        Value value = this.getValue(uuid);

        double max = value.max;
        double current = value.current;

        int positionSize = positions.size();
        int completeSize = max <= 0 || current < 0 ? 0 : (int) Math.round(current / max * positionSize);
        completeSize = Math.min(completeSize, positionSize);

        Map<Position, Consumer<ActionItem>> buttonMap = new HashMap<>();
        if (completeButton != null && completeSize > 0) {
            Consumer<ActionItem> completeConsumer = completeButton.apply(uuid);
            for (int i = 0; i < completeSize; i++) {
                buttonMap.put(positions.get(i), completeConsumer);
            }
        }
        if (incompleteButton != null && completeSize < positionSize) {
            Consumer<ActionItem> incompleteConsumer = incompleteButton.apply(uuid);
            for (int i = completeSize; i < positionSize; i++) {
                buttonMap.put(positions.get(i), incompleteConsumer);
            }
        }
        return buttonMap;
    }

    /**
     * The progress value of a mask, holding the current value out of the maximum value
     */
    public static final class Value {
        /**
         * The current value
         */
        public final double current;

        /**
         * The maximum value
         */
        public final double max;

        /**
         * Create a new value
         *
         * @param current the current value
         * @param max     the maximum value
         */
        public Value(double current, double max) {
            this.current = current;
            this.max = max;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Value value = (Value) o;
            return Double.compare(current, value.current) == 0 && Double.compare(max, value.max) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(current, max);
        }
    }
}

package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.animation.Animation;
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
 * A mask that animates through child masks once and then optionally displays the last frame or nothing.
 * Useful for one-time animations like introductions or transitions.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * OneTimeAnimatedMask mask = new OneTimeAnimatedMask();
 * mask.add(
 *     new SingleMask(Position.of(0, 0), new SimpleButton(new ItemStack(Material.COAL))),
 *     new SingleMask(Position.of(0, 0), new SimpleButton(new ItemStack(Material.IRON_INGOT))),
 *     new SingleMask(Position.of(0, 0), new SimpleButton(new ItemStack(Material.DIAMOND)))
 * );
 * mask.setPeriodMillis(500); // 500ms per frame
 * mask.setViewLast(true); // Show last frame after animation
 * }</pre>
 */
public class OneTimeAnimatedMask extends MultiMask<Mask> {
    private final Map<UUID, Animation<Mask>> animationMap = new ConcurrentHashMap<>();
    private boolean viewLast = false;
    private long periodMillis = 50L;

    /**
     * Sets the period of the animation between frame changes.
     *
     * @param periodMillis the period in milliseconds
     * @throws IllegalArgumentException if periodMillis is not positive
     */
    public void setPeriodMillis(long periodMillis) {
        if (periodMillis <= 0) {
            throw new IllegalArgumentException("Period must be positive");
        }
        this.periodMillis = periodMillis;
    }

    /**
     * Set whether to view the last frame when the animation is finished
     *
     * @param viewLast true to view the last frame
     */
    public void setViewLast(boolean viewLast) {
        this.viewLast = viewLast;
    }

    /**
     * Reset the animation for the unique id
     *
     * @param uuid the unique id
     */
    public void reset(@NotNull UUID uuid) {
        getAnimation(uuid).reset();
    }

    private Animation<Mask> getAnimation(@NotNull UUID uuid) {
        return animationMap.computeIfAbsent(uuid, key -> new Animation<>(elements, periodMillis));
    }

    @Override
    public void stop() {
        this.animationMap.clear();
        super.stop();
    }

    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        if (elements.isEmpty()) return null;

        Animation<Mask> animation = getAnimation(uuid);
        long currentMillis = System.currentTimeMillis();
        if (animation.isFirstRun(currentMillis)) {
            return animation.getCurrentFrame(currentMillis).apply(uuid);
        } else if (viewLast) {
            return elements.get(elements.size() - 1).apply(uuid);
        } else {
            return null;
        }
    }
}

package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.animation.Animation;
import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The animated mask with child masks as frames, but only run once
 */
public class OneTimeAnimatedMask extends MultiMask<Map<Position, Consumer<ActionItem>>> {
    private final Map<UUID, Animation<Function<@NotNull UUID, @Nullable Map<Position, Consumer<ActionItem>>>>> animationMap = new ConcurrentHashMap<>();
    private boolean viewLast = false;
    private long periodMillis = 50L;

    @Override
    protected boolean requireChildElements() {
        return true;
    }

    /**
     * Set the period of the animation
     *
     * @param periodMillis the period in milliseconds
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

    private Animation<Function<@NotNull UUID, @Nullable Map<Position, Consumer<ActionItem>>>> getAnimation(@NotNull UUID uuid) {
        return animationMap.computeIfAbsent(uuid, key -> new Animation<>(elements, periodMillis));
    }

    @Override
    public void stop() {
        this.animationMap.clear();
        super.stop();
    }

    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        Animation<Function<@NotNull UUID, @Nullable Map<Position, Consumer<ActionItem>>>> animation = getAnimation(uuid);
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

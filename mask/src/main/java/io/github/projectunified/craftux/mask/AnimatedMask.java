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
 * The animated mask with child masks as frames
 */
public class AnimatedMask extends MultiMask<Map<Position, Consumer<ActionItem>>> {
    private final Map<UUID, Animation<Function<@NotNull UUID, @Nullable Map<Position, Consumer<ActionItem>>>>> animationMap = new ConcurrentHashMap<>();
    private long periodMillis = 50;

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

    private Animation<Function<@NotNull UUID, @Nullable Map<Position, Consumer<ActionItem>>>> getAnimation(@NotNull UUID uuid) {
        return animationMap.computeIfAbsent(uuid, k -> new Animation<>(elements, periodMillis));
    }

    @Override
    public void stop() {
        this.animationMap.clear();
        super.stop();
    }

    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        return getAnimation(uuid).getCurrentFrame().apply(uuid);
    }
}

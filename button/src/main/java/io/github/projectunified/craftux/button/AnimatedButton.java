package io.github.projectunified.craftux.button;

import io.github.projectunified.craftux.animation.Animation;
import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The animated button with child buttons as frames
 */
public class AnimatedButton extends MultiButton {
    private final Map<UUID, Animation<Button>> animationMap = new ConcurrentHashMap<>();
    private long periodMillis = 50L;

    @Override
    protected boolean requireChildButtons() {
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

    private Animation<Button> getAnimation(UUID uuid) {
        return animationMap.computeIfAbsent(uuid, key -> new Animation<>(buttons, periodMillis));
    }

    @Override
    public void stop() {
        this.animationMap.clear();
        super.stop();
    }

    @Override
    public boolean apply(@NotNull UUID uuid, @NotNull ActionItem actionItem) {
        return getAnimation(uuid).getCurrentFrame().apply(uuid, actionItem);
    }
}

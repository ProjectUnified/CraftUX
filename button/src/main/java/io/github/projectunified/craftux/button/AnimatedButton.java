package io.github.projectunified.craftux.button;

import io.github.projectunified.craftux.animation.Animation;
import io.github.projectunified.craftux.animation.AnimationMode;
import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Button;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A button that cycles through a list of buttons as animation frames over time.
 * Each frame is displayed for a configurable period, creating an animated effect.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * AnimatedButton animatedButton = new AnimatedButton();
 * animatedButton.addButton(
 *     new SimpleButton(new ItemStack(Material.DIAMOND)),
 *     new SimpleButton(new ItemStack(Material.EMERALD))
 * );
 * animatedButton.setPeriodMillis(100); // 100ms per frame
 * }</pre>
 */
public class AnimatedButton extends MultiButton {
    private final Map<UUID, Animation<Button>> animationMap = new ConcurrentHashMap<>();
    private long periodMillis = 50L;
    private AnimationMode mode = AnimationMode.REPEAT;

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
     * Set the mode of the animation
     *
     * @param mode the mode of the animation
     */
    public void setMode(AnimationMode mode) {
        this.mode = mode;
    }

    /**
     * Reset the animation for the unique id
     *
     * @param uuid the unique id
     */
    public void reset(UUID uuid) {
        Animation<Button> animation = animationMap.get(uuid);
        if (animation != null) {
            animation.reset();
        }
    }

    private Animation<Button> getAnimation(UUID uuid) {
        return animationMap.computeIfAbsent(uuid, key -> new Animation<>(buttons, periodMillis, mode));
    }

    @Override
    public void stop() {
        this.animationMap.clear();
        super.stop();
    }

    @Override
    public boolean apply(@NotNull UUID uuid, @NotNull ActionItem actionItem) {
        if (this.buttons.isEmpty()) return false;
        return getAnimation(uuid).getCurrentFrame().apply(uuid, actionItem);
    }
}

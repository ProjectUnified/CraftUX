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
 * A mask that cycles through a list of child masks as animation frames over time.
 * Each frame is displayed for a configurable period, creating an animated GUI layout.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * AnimatedMask animatedMask = new AnimatedMask();
 * animatedMask.add(
 *     new SingleMask(Position.of(0, 0), new SimpleButton(new ItemStack(Material.REDSTONE))),
 *     new SingleMask(Position.of(0, 0), new SimpleButton(new ItemStack(Material.LAPIS_LAZULI))),
 *     new SingleMask(Position.of(0, 0), new SimpleButton(new ItemStack(Material.GOLD_INGOT)))
 * );
 * animatedMask.setPeriodMillis(1000); // 1 second per frame
 * Map<Position, Consumer<ActionItem>> actions = animatedMask.apply(playerUUID);
 * }</pre>
 */
public class AnimatedMask extends MultiMask<Mask> {
    private final Map<UUID, Animation<Mask>> animationMap = new ConcurrentHashMap<>();
    private long periodMillis = 50;

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

    private Animation<Mask> getAnimation(@NotNull UUID uuid) {
        return animationMap.computeIfAbsent(uuid, k -> new Animation<>(elements, periodMillis));
    }

    @Override
    public void stop() {
        this.animationMap.clear();
        super.stop();
    }

    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        if (elements.isEmpty()) return null;
        return getAnimation(uuid).getCurrentFrame().apply(uuid);
    }
}

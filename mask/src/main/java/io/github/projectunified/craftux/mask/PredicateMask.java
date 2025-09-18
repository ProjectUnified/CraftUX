package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Element;
import io.github.projectunified.craftux.common.Mask;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * The mask with predicate
 */
public class PredicateMask implements Element, Mask {
    private @Nullable Predicate<UUID> viewPredicate = null;
    private @Nullable Mask mask = uuid -> null;
    private @Nullable Mask fallbackMask = uuid -> null;

    /**
     * Set the view predicate
     *
     * @param viewPredicate the view predicate
     */
    public void setViewPredicate(@Nullable Predicate<@NotNull UUID> viewPredicate) {
        this.viewPredicate = viewPredicate;
    }

    /**
     * Get the mask
     *
     * @return the mask
     */
    @Nullable
    public Mask getMask() {
        return mask;
    }

    /**
     * Set the mask
     *
     * @param mask the mask
     */
    public void setMask(@Nullable Mask mask) {
        this.mask = mask;
    }

    /**
     * Get the fallback mask
     *
     * @return the fallback mask
     */
    @Nullable
    public Mask getFallbackMask() {
        return fallbackMask;
    }

    /**
     * Set the fallback mask
     *
     * @param fallbackMask the fallback mask
     */
    public void setFallbackMask(@Nullable Mask fallbackMask) {
        this.fallbackMask = fallbackMask;
    }

    @Override
    public void init() {
        Element.handleIfElement(mask, Element::init);
        Element.handleIfElement(fallbackMask, Element::init);
    }

    @Override
    public void stop() {
        Element.handleIfElement(mask, Element::stop);
        Element.handleIfElement(fallbackMask, Element::stop);
    }

    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        Mask maskToUse = viewPredicate == null || viewPredicate.test(uuid) ? mask : fallbackMask;
        return maskToUse == null ? null : maskToUse.apply(uuid);
    }
}

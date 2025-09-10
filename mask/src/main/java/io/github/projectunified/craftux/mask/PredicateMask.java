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
    private Predicate<UUID> viewPredicate = uuid -> true;
    private Mask mask = uuid -> null;
    private Mask fallbackMask = uuid -> null;

    /**
     * Set the view predicate
     *
     * @param viewPredicate the view predicate
     */
    public void setViewPredicate(@NotNull Predicate<@NotNull UUID> viewPredicate) {
        this.viewPredicate = viewPredicate;
    }

    /**
     * Get the mask
     *
     * @return the mask
     */
    @NotNull
    public Mask getMask() {
        return mask;
    }

    /**
     * Set the mask
     *
     * @param mask the mask
     */
    public void setMask(@NotNull Mask mask) {
        this.mask = mask;
    }

    /**
     * Get the fallback mask
     *
     * @return the fallback mask
     */
    @NotNull
    public Mask getFallbackMask() {
        return fallbackMask;
    }

    /**
     * Set the fallback mask
     *
     * @param fallbackMask the fallback mask
     */
    public void setFallbackMask(@NotNull Mask fallbackMask) {
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
        if (viewPredicate.test(uuid)) {
            return mask.apply(uuid);
        } else {
            return fallbackMask.apply(uuid);
        }
    }
}

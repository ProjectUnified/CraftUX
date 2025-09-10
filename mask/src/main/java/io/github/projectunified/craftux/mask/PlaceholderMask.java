package io.github.projectunified.craftux.mask;

import io.github.projectunified.craftux.common.ActionItem;
import io.github.projectunified.craftux.common.Element;
import io.github.projectunified.craftux.common.Mask;
import io.github.projectunified.craftux.common.Position;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * The placeholder mask <br> Used for per-user masks
 */
public class PlaceholderMask implements Element, Mask {
    protected final Map<UUID, Mask> userMasks = new ConcurrentHashMap<>();
    protected Mask defaultMask = context -> null;

    @Override
    public void init() {
        Element.handleIfElement(defaultMask, Element::init);
    }

    @Override
    public void stop() {
        Element.handleIfElement(defaultMask, Element::stop);
        this.userMasks.clear();
    }

    @Override
    public @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid) {
        return this.userMasks.getOrDefault(uuid, this.defaultMask).apply(uuid);
    }

    /**
     * Set the mask for the unique id
     *
     * @param uuid the unique id
     * @param mask the mask
     */
    public void setMask(@NotNull UUID uuid, @Nullable Mask mask) {
        if (mask == null) {
            this.userMasks.remove(uuid);
        } else {
            this.userMasks.put(uuid, mask);
        }
    }

    /**
     * Get the mask for the unique id
     *
     * @param uuid the unique id
     * @return the mask
     */
    @Nullable
    public Mask getMask(@NotNull UUID uuid) {
        return this.userMasks.get(uuid);
    }

    /**
     * Get the default mask
     *
     * @return the default mask
     */
    @NotNull
    public Mask getDefaultMask() {
        return defaultMask;
    }

    /**
     * Set the default mask
     *
     * @param defaultMask the default mask
     */
    public void setDefaultMask(@NotNull Mask defaultMask) {
        this.defaultMask = defaultMask;
    }

    /**
     * Get the user-mask map
     *
     * @return the user-mask map
     */
    @NotNull
    public Map<UUID, Mask> getUserMasks() {
        return Collections.unmodifiableMap(this.userMasks);
    }
}

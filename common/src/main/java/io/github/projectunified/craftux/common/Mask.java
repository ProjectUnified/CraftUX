package io.github.projectunified.craftux.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface Mask {
    @Nullable Map<Position, Consumer<ActionItem>> apply(@NotNull UUID uuid);
}

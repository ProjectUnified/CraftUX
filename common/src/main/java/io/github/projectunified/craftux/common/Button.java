package io.github.projectunified.craftux.common;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Consumer;

public interface Button {
    boolean apply(@NotNull UUID uuid, @NotNull ActionItem actionItem);

    default Consumer<ActionItem> apply(@NotNull UUID uuid) {
        return actionItem -> apply(uuid, actionItem);
    }
}

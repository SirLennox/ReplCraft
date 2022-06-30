package dev.sirlennox.replcraftclient.context;

import java.util.Arrays;
import java.util.Objects;

public enum OpenCause {
    LOGIN("login"), ITEM_ATTACK("itemAttack"), ITEM_BREAK_BLOCK("itemBreakBlock"), ITEM_INTERACT_BLOCK("itemInteractBlock"), ITEM_INTERACT_AIR("itemInteractAir");

    private final String id;

    OpenCause(final String id) {
        this.id = id;
    }

    public final String getId() {
        return this.id;
    }

    public static OpenCause getById(final String id) {
        return Arrays.stream(OpenCause.values()).filter(cause -> Objects.equals(cause.getId(), id)).findFirst().orElse(null);
    }
}
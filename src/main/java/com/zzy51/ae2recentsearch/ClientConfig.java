package com.zzy51.ae2recentsearch;

import net.neoforged.neoforge.common.ModConfigSpec;

public final class ClientConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.IntValue MAX_VISIBLE_ENTRIES = BUILDER
            .comment("Maximum number of recent search entries shown in the terminal.")
            .defineInRange("maxVisibleEntries", 10, 1, Integer.MAX_VALUE);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private ClientConfig() {
    }
}

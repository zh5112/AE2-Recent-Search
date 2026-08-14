package com.zzy51.ae2recentsearch;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(AE2RecentSearch.MOD_ID)
public final class AE2RecentSearch {
    public static final String MOD_ID = "ae2_recent_search";

    public AE2RecentSearch(ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }
}

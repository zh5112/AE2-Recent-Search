package com.zzy51.ae2recentsearch.client;

import com.zzy51.ae2recentsearch.AE2RecentSearch;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = AE2RecentSearch.MOD_ID, dist = Dist.CLIENT)
public final class AE2RecentSearchClient {
    public AE2RecentSearchClient(IEventBus modEventBus) {
        modEventBus.addListener(RecentSearchKeyMappings::registerKeyMappings);
    }
}

package com.zzy51.ae2recentsearch.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.widgets.VerticalButtonBar;

@Mixin(AEBaseScreen.class)
public interface AEBaseScreenAccessor {
    @Accessor("verticalToolbar")
    VerticalButtonBar ae2RecentSearch$getVerticalToolbar();
}

package com.zzy51.ae2recentsearch.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import appeng.client.gui.widgets.VerticalButtonBar;
import net.minecraft.client.gui.components.Button;

@Mixin(VerticalButtonBar.class)
public interface VerticalButtonBarAccessor {
    @Accessor("buttons")
    List<Button> ae2RecentSearch$getButtons();
}

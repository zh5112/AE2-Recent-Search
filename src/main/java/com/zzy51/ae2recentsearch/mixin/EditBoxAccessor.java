package com.zzy51.ae2recentsearch.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.components.EditBox;

@Mixin(EditBox.class)
public interface EditBoxAccessor {
    @Accessor("responder")
    Consumer<String> ae2RecentSearch$getResponder();

    @Accessor("responder")
    void ae2RecentSearch$setResponder(Consumer<String> responder);
}

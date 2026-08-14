package com.zzy51.ae2recentsearch.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.client.gui.AEBaseScreen;

import com.zzy51.ae2recentsearch.client.RecentSearchOverlay;
import com.zzy51.ae2recentsearch.client.RecentSearchScreenAccess;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.Slot;

@Mixin(AEBaseScreen.class)
public abstract class AEBaseScreenMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void ae2RecentSearch$renderOverlay(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick,
            CallbackInfo ci) {
        if (!((Object) this instanceof RecentSearchScreenAccess access)) {
            return;
        }

        var pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(0.0F, 0.0F, 400.0F);
        RecentSearchOverlay.renderScreen(guiGraphics, Minecraft.getInstance().font, access.ae2RecentSearch$getSearchField(),
                mouseX, mouseY);
        pose.popPose();
    }

    @Inject(method = "isHovering(Lnet/minecraft/world/inventory/Slot;DD)Z", at = @At("HEAD"), cancellable = true)
    private void ae2RecentSearch$hideSlotsBelowRecentSearch(
            Slot slot,
            double mouseX,
            double mouseY,
            CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof RecentSearchScreenAccess access
                && RecentSearchOverlay.isMouseOver(access.ae2RecentSearch$getSearchField(), mouseX, mouseY)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "renderTooltips", at = @At("HEAD"), cancellable = true)
    private void ae2RecentSearch$hideTooltipsOverRecentSearch(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            CallbackInfo ci) {
        if ((Object) this instanceof RecentSearchScreenAccess access
                && RecentSearchOverlay.isMouseOverSearchOrOverlay(access.ae2RecentSearch$getSearchField(), mouseX, mouseY)) {
            ci.cancel();
        }
    }
}

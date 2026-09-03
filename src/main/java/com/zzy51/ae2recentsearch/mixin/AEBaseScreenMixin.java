package com.zzy51.ae2recentsearch.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.client.gui.AEBaseScreen;

import com.zzy51.ae2recentsearch.client.RecentSearchActions;
import com.zzy51.ae2recentsearch.client.RecentSearchOverlay;
import com.zzy51.ae2recentsearch.client.RecentSearchScreenAccess;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.inventory.Slot;

@Mixin(AEBaseScreen.class)
public abstract class AEBaseScreenMixin {
    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void ae2RecentSearch$handleRecentSearchScroll(
            double mouseX,
            double mouseY,
            double scrollX,
            double scrollY,
            CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof RecentSearchScreenAccess access
                && RecentSearchOverlay.scroll(access.ae2RecentSearch$getSearchField(), mouseX, mouseY, scrollY)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseDragged", at = @At("HEAD"), cancellable = true)
    private void ae2RecentSearch$handleRecentSearchDrag(
            double mouseX,
            double mouseY,
            int button,
            double dragX,
            double dragY,
            CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof RecentSearchScreenAccess access
                && RecentSearchOverlay.drag(access.ae2RecentSearch$getSearchField(), mouseX, mouseY, button)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void ae2RecentSearch$handleRecentSearchRelease(
            double mouseX,
            double mouseY,
            int button,
            CallbackInfoReturnable<Boolean> cir) {
        if (!((Object) this instanceof RecentSearchScreenAccess access)) {
            return;
        }

        var searchField = access.ae2RecentSearch$getSearchField();
        if (RecentSearchOverlay.releaseDrag(searchField, mouseX, mouseY, button)) {
            cir.setReturnValue(true);
            return;
        }

        var releaseTarget = RecentSearchOverlay.releaseClick(searchField, button);
        if (releaseTarget != null) {
            RecentSearchActions.handleTarget((AEBaseScreen<?>) (Object) this, searchField, releaseTarget);
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE",
            target = "Lappeng/client/gui/AEBaseScreen;renderTooltips(Lnet/minecraft/client/gui/GuiGraphics;II)V"))
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

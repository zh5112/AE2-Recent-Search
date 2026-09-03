package com.zzy51.ae2recentsearch.mixin;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.me.common.MEStorageScreen;
import appeng.client.gui.widgets.AETextField;
import appeng.menu.me.common.MEStorageMenu;

import com.zzy51.ae2recentsearch.client.RecentSearchActions;
import com.zzy51.ae2recentsearch.client.RecentSearchOverlay;
import com.zzy51.ae2recentsearch.client.RecentSearchKeyboardNavigation;
import com.zzy51.ae2recentsearch.client.RecentSearchOverlay.ClickTargetType;
import com.zzy51.ae2recentsearch.client.RecentSearchScreenAccess;
import com.zzy51.ae2recentsearch.client.SearchHistoryStore;

import net.minecraft.client.gui.GuiGraphics;

@Mixin(MEStorageScreen.class)
public abstract class MEStorageScreenMixin<C extends MEStorageMenu> implements RecentSearchScreenAccess {
    @Shadow
    @Final
    private AETextField searchField;

    @Override
    public AETextField ae2RecentSearch$getSearchField() {
        return searchField;
    }

    @Unique
    @SuppressWarnings("unchecked")
    private AEBaseScreen<C> ae2RecentSearch$screen() {
        return (AEBaseScreen<C>) (Object) this;
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void ae2RecentSearch$handleRecentSearchClick(
            double x,
            double y,
            int button,
            CallbackInfoReturnable<Boolean> cir) {
        var target = RecentSearchOverlay.getClickedTarget(searchField, x, y);
        if (target != null) {
            if (button == 0) {
                if (target.type() == ClickTargetType.ENTRY && SearchHistoryStore.isFavorite(target.value())) {
                    if (!RecentSearchOverlay.beginDrag(searchField, x, y, button)) {
                        RecentSearchActions.handleTarget(ae2RecentSearch$screen(), searchField, target);
                    }
                } else {
                    RecentSearchActions.handleTarget(ae2RecentSearch$screen(), searchField, target);
                }
            }
            cir.setReturnValue(true);
            return;
        }

        if (RecentSearchOverlay.isMouseOver(searchField, x, y)) {
            cir.setReturnValue(true);
            return;
        }

        if (searchField != null
                && searchField.isFocused()
                && searchField.isMouseOver(x, y)) {
            RecentSearchKeyboardNavigation.clear(searchField);
        }

        if (searchField != null
                && searchField.isFocused()
                && !searchField.isMouseOver(x, y)
                && !RecentSearchOverlay.isMouseOver(searchField, x, y)) {
            RecentSearchKeyboardNavigation.clear(searchField);
            RecentSearchActions.recordCurrentSearch(searchField);
            RecentSearchActions.clearFocus(ae2RecentSearch$screen(), searchField);
        }
    }

    @Inject(method = "mouseScrolled", at = @At("HEAD"), cancellable = true)
    private void ae2RecentSearch$handleRecentSearchScroll(
            double mouseX,
            double mouseY,
            double scrollX,
            double scrollY,
            CallbackInfoReturnable<Boolean> cir) {
        if (RecentSearchOverlay.scroll(searchField, mouseX, mouseY, scrollY)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void ae2RecentSearch$hideTooltipBelowRecentSearch(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            CallbackInfo ci) {
        if (RecentSearchOverlay.isMouseOver(searchField, mouseX, mouseY)) {
            ci.cancel();
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void ae2RecentSearch$recordOnEnter(
            int keyCode,
            int scanCode,
            int modifiers,
            CallbackInfoReturnable<Boolean> cir) {
        if (searchField == null || !searchField.isFocused()) {
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_DOWN
                && RecentSearchKeyboardNavigation.moveSelection(searchField, 1)) {
            cir.setReturnValue(true);
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_UP
                && RecentSearchKeyboardNavigation.moveSelection(searchField, -1)) {
            cir.setReturnValue(true);
            return;
        }

        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            var target = RecentSearchKeyboardNavigation.selectedTarget(searchField);
            if (target != null) {
                RecentSearchActions.handleTarget(ae2RecentSearch$screen(), searchField, target);
                cir.setReturnValue(true);
                return;
            }

            RecentSearchActions.recordCurrentSearch(searchField);
            return;
        }

        RecentSearchKeyboardNavigation.clear(searchField);
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void ae2RecentSearch$recordOnRemoved(CallbackInfo ci) {
        RecentSearchOverlay.clearInteraction(searchField);
        RecentSearchKeyboardNavigation.clear(searchField);
        RecentSearchActions.recordCurrentSearch(searchField);
    }
}

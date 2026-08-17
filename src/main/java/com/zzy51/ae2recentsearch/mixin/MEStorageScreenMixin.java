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
import appeng.core.AEConfig;
import appeng.integration.abstraction.ItemListMod;
import appeng.menu.me.common.MEStorageMenu;

import com.zzy51.ae2recentsearch.client.RecentSearchOverlay;
import com.zzy51.ae2recentsearch.client.RecentSearchKeyboardNavigation;
import com.zzy51.ae2recentsearch.client.RecentSearchOverlay.ClickTarget;
import com.zzy51.ae2recentsearch.client.RecentSearchOverlay.ClickTargetType;
import com.zzy51.ae2recentsearch.client.RecentSearchScreenAccess;
import com.zzy51.ae2recentsearch.client.SearchHistoryStore;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;

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
    private void ae2RecentSearch$recordCurrentSearch() {
        if (searchField != null) {
            SearchHistoryStore.record(searchField.getValue());
        }
    }

    @Unique
    private void ae2RecentSearch$setValueWithoutSearch(String value) {
        var editBox = (EditBoxAccessor) searchField;
        var responder = editBox.ae2RecentSearch$getResponder();
        editBox.ae2RecentSearch$setResponder(null);
        searchField.setValue(value);
        editBox.ae2RecentSearch$setResponder(responder);
    }

    @Unique
    @SuppressWarnings("unchecked")
    private void ae2RecentSearch$setScreenFocus(GuiEventListener listener) {
        ((AEBaseScreen<C>) (Object) this).setFocused(listener);
    }

    @Unique
    private void ae2RecentSearch$syncExternalSearch(String value) {
        if (SearchHistoryStore.isSyncExternalSearch()
                && AEConfig.instance().isSyncWithExternalSearch()
                && ItemListMod.isEnabled()) {
            ItemListMod.setSearchText(value);
        }
    }

    @Unique
    private void ae2RecentSearch$handleTarget(ClickTarget target) {
        if (target.type() == ClickTargetType.DELETE) {
            SearchHistoryStore.remove(target.value());
            RecentSearchKeyboardNavigation.clear(searchField);
        } else if (target.type() == ClickTargetType.SEARCH_FAVORITE) {
            SearchHistoryStore.toggleFavoriteForSearch(target.value());
            RecentSearchKeyboardNavigation.clear(searchField);
            searchField.setFocused(true);
            ae2RecentSearch$setScreenFocus(searchField);
        } else {
            SearchHistoryStore.record(target.value());
            RecentSearchKeyboardNavigation.clear(searchField);
            if (SearchHistoryStore.isApplyOnClick()) {
                searchField.setValue(target.value());
                ae2RecentSearch$syncExternalSearch(target.value());
                searchField.setFocused(false);
                ae2RecentSearch$setScreenFocus(null);
            } else {
                ae2RecentSearch$setValueWithoutSearch(target.value());
                searchField.setFocused(true);
                ae2RecentSearch$setScreenFocus(searchField);
            }
        }
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
                ae2RecentSearch$handleTarget(target);
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
            ae2RecentSearch$recordCurrentSearch();
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
                ae2RecentSearch$handleTarget(target);
                cir.setReturnValue(true);
                return;
            }

            ae2RecentSearch$recordCurrentSearch();
            return;
        }

        RecentSearchKeyboardNavigation.clear(searchField);
    }

    @Inject(method = "removed", at = @At("HEAD"))
    private void ae2RecentSearch$recordOnRemoved(CallbackInfo ci) {
        RecentSearchKeyboardNavigation.clear(searchField);
        ae2RecentSearch$recordCurrentSearch();
    }
}

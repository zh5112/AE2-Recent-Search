package com.zzy51.ae2recentsearch.client;

import appeng.client.gui.AEBaseScreen;
import appeng.client.gui.widgets.AETextField;
import appeng.core.AEConfig;
import appeng.integration.abstraction.ItemListMod;

import com.zzy51.ae2recentsearch.mixin.EditBoxAccessor;

public final class RecentSearchActions {
    private RecentSearchActions() {
    }

    public static void recordCurrentSearch(AETextField searchField) {
        if (searchField != null) {
            SearchHistoryStore.record(searchField.getValue());
        }
    }

    public static void handleTarget(
            AEBaseScreen<?> screen,
            AETextField searchField,
            RecentSearchOverlay.ClickTarget target) {
        if (target.type() == RecentSearchOverlay.ClickTargetType.DELETE) {
            SearchHistoryStore.remove(target.value());
            RecentSearchKeyboardNavigation.clear(searchField);
        } else if (target.type() == RecentSearchOverlay.ClickTargetType.SEARCH_FAVORITE) {
            SearchHistoryStore.toggleFavoriteForSearch(target.value());
            RecentSearchKeyboardNavigation.clear(searchField);
            focusSearchField(screen, searchField);
        } else {
            SearchHistoryStore.record(target.value());
            RecentSearchKeyboardNavigation.clear(searchField);
            if (SearchHistoryStore.isApplyOnClick()) {
                searchField.setValue(target.value());
                syncExternalSearch(target.value());
                clearFocus(screen, searchField);
            } else {
                setValueWithoutSearch(searchField, target.value());
                focusSearchField(screen, searchField);
            }
        }
    }

    public static void clearFocus(AEBaseScreen<?> screen, AETextField searchField) {
        if (searchField != null) {
            searchField.setFocused(false);
        }
        screen.setFocused(null);
    }

    private static void focusSearchField(AEBaseScreen<?> screen, AETextField searchField) {
        searchField.setFocused(true);
        screen.setFocused(searchField);
    }

    private static void setValueWithoutSearch(AETextField searchField, String value) {
        var editBox = (EditBoxAccessor) searchField;
        var responder = editBox.ae2RecentSearch$getResponder();
        editBox.ae2RecentSearch$setResponder(null);
        searchField.setValue(value);
        editBox.ae2RecentSearch$setResponder(responder);
    }

    private static void syncExternalSearch(String value) {
        if (SearchHistoryStore.isSyncExternalSearch()
                && AEConfig.instance().isSyncWithExternalSearch()
                && ItemListMod.isEnabled()) {
            ItemListMod.setSearchText(value);
        }
    }
}

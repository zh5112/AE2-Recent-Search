package com.zzy51.ae2recentsearch.client;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import appeng.client.gui.widgets.AETextField;

public final class RecentSearchKeyboardNavigation {
    private static final Map<AETextField, String> SELECTED_VALUES = new WeakHashMap<>();

    private RecentSearchKeyboardNavigation() {
    }

    public static boolean moveSelection(AETextField searchField, int direction) {
        if (!isNavigationAvailable(searchField)) {
            clear(searchField);
            return false;
        }

        List<SearchHistoryStore.SearchEntry> entries = SearchHistoryStore.getVisibleEntries();
        if (entries.isEmpty()) {
            clear(searchField);
            return false;
        }

        int currentIndex = selectedIndex(entries, SELECTED_VALUES.get(searchField));
        int nextIndex;
        if (currentIndex < 0) {
            nextIndex = direction > 0 ? 0 : entries.size() - 1;
        } else {
            nextIndex = Math.floorMod(currentIndex + direction, entries.size());
        }

        SELECTED_VALUES.put(searchField, entries.get(nextIndex).value());
        return true;
    }

    public static RecentSearchOverlay.ClickTarget selectedTarget(AETextField searchField) {
        String value = selectedValue(searchField);
        return value == null
                ? null
                : new RecentSearchOverlay.ClickTarget(RecentSearchOverlay.ClickTargetType.ENTRY, value);
    }

    public static String selectedValue(AETextField searchField) {
        if (!isNavigationAvailable(searchField)) {
            clear(searchField);
            return null;
        }

        List<SearchHistoryStore.SearchEntry> entries = SearchHistoryStore.getVisibleEntries();
        String value = SELECTED_VALUES.get(searchField);
        if (selectedIndex(entries, value) < 0) {
            clear(searchField);
            return null;
        }

        return value;
    }

    public static void clear(AETextField searchField) {
        if (searchField != null) {
            SELECTED_VALUES.remove(searchField);
        }
    }

    private static boolean isNavigationAvailable(AETextField searchField) {
        return SearchHistoryStore.isKeyboardNavigationEnabled()
                && RecentSearchOverlay.shouldShow(searchField);
    }

    private static int selectedIndex(List<SearchHistoryStore.SearchEntry> entries, String value) {
        if (value == null) {
            return -1;
        }

        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).value().equals(value)) {
                return i;
            }
        }

        return -1;
    }
}

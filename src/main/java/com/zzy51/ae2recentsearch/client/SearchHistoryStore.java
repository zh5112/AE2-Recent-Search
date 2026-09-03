package com.zzy51.ae2recentsearch.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zzy51.ae2recentsearch.AE2RecentSearch;
import com.zzy51.ae2recentsearch.ClientConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;

public final class SearchHistoryStore {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String UNKNOWN_PLAYER = "unknown";

    private static final Map<String, PlayerHistory> HISTORY_BY_PLAYER = new HashMap<>();
    private static String loadedPlayerKey;
    private static boolean loaded;

    private SearchHistoryStore() {
    }

    public static List<SearchEntry> getVisibleEntries() {
        var allEntries = getAllVisibleEntries();
        var visibleCount = Math.min(getMaxVisibleEntries(), allEntries.size());
        return List.copyOf(allEntries.subList(0, visibleCount));
    }

    public static int getMaxVisibleEntries() {
        return Math.max(1, ClientConfig.MAX_VISIBLE_ENTRIES.getAsInt());
    }

    public static List<SearchEntry> getAllVisibleEntries() {
        ensureLoaded();
        var history = currentHistory().entries();
        var visibleEntries = currentHistory().favoritesEnabled()
                ? favoriteEntriesFirst(history)
                : history;
        return List.copyOf(visibleEntries);
    }

    public static boolean isEnabled() {
        ensureLoaded();
        return currentHistory().enabled();
    }

    public static void setEnabled(boolean enabled) {
        ensureLoaded();
        currentHistory().setEnabled(enabled);
        save();
    }

    public static boolean isApplyOnClick() {
        ensureLoaded();
        return currentHistory().applyOnClick();
    }

    public static void setApplyOnClick(boolean applyOnClick) {
        ensureLoaded();
        currentHistory().setApplyOnClick(applyOnClick);
        save();
    }

    public static boolean isSyncExternalSearch() {
        ensureLoaded();
        return currentHistory().syncExternalSearch();
    }

    public static void setSyncExternalSearch(boolean syncExternalSearch) {
        ensureLoaded();
        currentHistory().setSyncExternalSearch(syncExternalSearch);
        save();
    }

    public static boolean isDeleteButtonsEnabled() {
        ensureLoaded();
        return currentHistory().deleteButtonsEnabled();
    }

    public static void setDeleteButtonsEnabled(boolean deleteButtonsEnabled) {
        ensureLoaded();
        currentHistory().setDeleteButtonsEnabled(deleteButtonsEnabled);
        save();
    }

    public static boolean isFavoritesEnabled() {
        ensureLoaded();
        return currentHistory().favoritesEnabled();
    }

    public static void setFavoritesEnabled(boolean favoritesEnabled) {
        ensureLoaded();
        currentHistory().setFavoritesEnabled(favoritesEnabled);
        save();
    }

    public static boolean isKeyboardNavigationEnabled() {
        ensureLoaded();
        return currentHistory().keyboardNavigationEnabled();
    }

    public static void setKeyboardNavigationEnabled(boolean keyboardNavigationEnabled) {
        ensureLoaded();
        currentHistory().setKeyboardNavigationEnabled(keyboardNavigationEnabled);
        save();
    }

    public static boolean isMouseScrollEnabled() {
        ensureLoaded();
        return currentHistory().mouseScrollEnabled();
    }

    public static void setMouseScrollEnabled(boolean mouseScrollEnabled) {
        ensureLoaded();
        currentHistory().setMouseScrollEnabled(mouseScrollEnabled);
        save();
    }

    public static boolean isFavoriteDragEnabled() {
        ensureLoaded();
        return currentHistory().favoriteDragEnabled();
    }

    public static void setFavoriteDragEnabled(boolean favoriteDragEnabled) {
        ensureLoaded();
        currentHistory().setFavoriteDragEnabled(favoriteDragEnabled);
        save();
    }

    public static void record(String value) {
        if (!isEnabled() || value == null || value.isBlank()) {
            return;
        }

        ensureLoaded();
        var history = currentHistory().entries();
        var favorite = false;
        for (int i = 0; i < history.size(); i++) {
            var entry = history.get(i);
            if (entry.value().equals(value)) {
                favorite = entry.favorite();
                if (favorite) {
                    return;
                }
                history.remove(i);
                break;
            }
        }

        history.add(0, new SearchEntry(value, favorite));
        save();
    }

    public static void remove(String value) {
        if (value == null || value.isBlank()) {
            return;
        }

        ensureLoaded();
        if (removeEntry(value)) {
            save();
        }
    }

    public static void setFavorite(String value, boolean favorite) {
        if (value == null || value.isBlank()) {
            return;
        }

        ensureLoaded();
        var history = currentHistory().entries();
        for (int i = 0; i < history.size(); i++) {
            var entry = history.get(i);
            if (!entry.value().equals(value)) {
                continue;
            }

            if (entry.favorite() == favorite) {
                return;
            }

            history.set(i, new SearchEntry(value, favorite));
            if (favorite) {
                var updated = history.remove(i);
                history.add(0, updated);
            }
            save();
            return;
        }
    }

    public static void toggleFavorite(String value) {
        if (value == null || value.isBlank()) {
            return;
        }

        ensureLoaded();
        var history = currentHistory().entries();
        for (int i = 0; i < history.size(); i++) {
            var entry = history.get(i);
            if (!entry.value().equals(value)) {
                continue;
            }

            if (entry.favorite()) {
                history.set(i, new SearchEntry(value, false));
            } else {
                history.remove(i);
                history.add(0, new SearchEntry(value, true));
            }
            save();
            return;
        }
    }

    public static void toggleFavoriteForSearch(String value) {
        if (value == null || value.isBlank()) {
            return;
        }

        ensureLoaded();
        var history = currentHistory().entries();
        for (int i = 0; i < history.size(); i++) {
            var entry = history.get(i);
            if (!entry.value().equals(value)) {
                continue;
            }

            if (entry.favorite()) {
                history.set(i, new SearchEntry(value, false));
            } else {
                history.remove(i);
                history.add(0, new SearchEntry(value, true));
            }
            save();
            return;
        }

        history.add(0, new SearchEntry(value, true));
        save();
    }

    public static void moveFavorite(String value, String beforeValue) {
        if (value == null || value.isBlank() || value.equals(beforeValue)) {
            return;
        }

        ensureLoaded();
        var history = currentHistory().entries();
        var favoriteEntries = new ArrayList<SearchEntry>();
        SearchEntry movingEntry = null;
        for (var entry : history) {
            if (!entry.favorite()) {
                continue;
            }

            if (entry.value().equals(value)) {
                movingEntry = entry;
            } else {
                favoriteEntries.add(entry);
            }
        }

        if (movingEntry == null) {
            return;
        }

        var insertIndex = favoriteEntries.size();
        if (beforeValue != null) {
            for (int i = 0; i < favoriteEntries.size(); i++) {
                if (favoriteEntries.get(i).value().equals(beforeValue)) {
                    insertIndex = i;
                    break;
                }
            }
        }

        favoriteEntries.add(insertIndex, movingEntry);

        var reordered = new ArrayList<SearchEntry>(history.size());
        var favoriteIndex = 0;
        for (var entry : history) {
            if (entry.favorite()) {
                reordered.add(favoriteEntries.get(favoriteIndex++));
            } else {
                reordered.add(entry);
            }
        }

        history.clear();
        history.addAll(reordered);
        save();
    }

    public static boolean isFavorite(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }

        ensureLoaded();
        for (var entry : currentHistory().entries()) {
            if (entry.value().equals(value)) {
                return entry.favorite();
            }
        }

        return false;
    }

    public static void clear() {
        ensureLoaded();
        currentHistory().entries().clear();
        save();
    }

    private static PlayerHistory currentHistory() {
        return HISTORY_BY_PLAYER.computeIfAbsent(currentPlayerKey(), key -> new PlayerHistory());
    }

    private static List<SearchEntry> favoriteEntriesFirst(List<SearchEntry> entries) {
        var ordered = new ArrayList<SearchEntry>(entries.size());
        for (var entry : entries) {
            if (entry.favorite()) {
                ordered.add(entry);
            }
        }
        for (var entry : entries) {
            if (!entry.favorite()) {
                ordered.add(entry);
            }
        }
        return ordered;
    }

    private static void ensureLoaded() {
        var playerKey = currentPlayerKey();
        if (loaded && playerKey.equals(loadedPlayerKey)) {
            return;
        }

        loaded = true;
        loadedPlayerKey = playerKey;
        HISTORY_BY_PLAYER.clear();
        load();
    }

    private static String currentPlayerKey() {
        User user = Minecraft.getInstance().getUser();
        if (user == null) {
            return UNKNOWN_PLAYER;
        }

        UUID profileId = user.getProfileId();
        return profileId != null ? profileId.toString() : user.getName();
    }

    private static Path filePath() {
        return Minecraft.getInstance().gameDirectory.toPath()
                .resolve("config")
                .resolve(AE2RecentSearch.MOD_ID + "_history.json");
    }

    private static void load() {
        var path = filePath();
        if (!Files.isRegularFile(path)) {
            return;
        }

        try {
            var root = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8)).getAsJsonObject();
            var players = root.getAsJsonObject("players");
            if (players == null) {
                return;
            }

            for (var entry : players.entrySet()) {
                if (!entry.getValue().isJsonArray()) {
                    continue;
                }

                var history = new PlayerHistory();
                var values = new LinkedHashMap<String, SearchEntry>();
                for (JsonElement element : entry.getValue().getAsJsonArray()) {
                    var parsed = parseEntry(element);
                    if (parsed == null) {
                        continue;
                    }

                    var existing = values.get(parsed.value());
                    if (existing == null || (!existing.favorite() && parsed.favorite())) {
                        values.put(parsed.value(), parsed);
                    }
                }

                history.entries().addAll(values.values());
                HISTORY_BY_PLAYER.put(entry.getKey(), history);
            }

            var settings = root.getAsJsonObject("settings");
            if (settings != null) {
                for (var entry : settings.entrySet()) {
                    if (!entry.getValue().isJsonObject()) {
                        continue;
                    }

                    var state = entry.getValue().getAsJsonObject();
                    var history = HISTORY_BY_PLAYER.computeIfAbsent(entry.getKey(), key -> new PlayerHistory());
                    if (state.has("enabled")) {
                        history.setEnabled(state.get("enabled").getAsBoolean());
                    }
                    if (state.has("applyOnClick")) {
                        history.setApplyOnClick(state.get("applyOnClick").getAsBoolean());
                    }
                    if (state.has("syncExternalSearch")) {
                        history.setSyncExternalSearch(state.get("syncExternalSearch").getAsBoolean());
                    }
                    if (state.has("deleteButtonsEnabled")) {
                        history.setDeleteButtonsEnabled(state.get("deleteButtonsEnabled").getAsBoolean());
                    }
                    if (state.has("favoritesEnabled")) {
                        history.setFavoritesEnabled(state.get("favoritesEnabled").getAsBoolean());
                    }
                    if (state.has("keyboardNavigationEnabled")) {
                        history.setKeyboardNavigationEnabled(state.get("keyboardNavigationEnabled").getAsBoolean());
                    }
                    if (state.has("mouseScrollEnabled")) {
                        history.setMouseScrollEnabled(state.get("mouseScrollEnabled").getAsBoolean());
                    }
                    if (state.has("favoriteDragEnabled")) {
                        history.setFavoriteDragEnabled(state.get("favoriteDragEnabled").getAsBoolean());
                    }
                }
            }
        } catch (Exception ignored) {
            // A malformed local history should never prevent the game from starting.
        }
    }

    private static SearchEntry parseEntry(JsonElement element) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            return new SearchEntry(element.getAsString(), false);
        }

        if (!element.isJsonObject()) {
            return null;
        }

        var object = element.getAsJsonObject();
        if (!object.has("value")
                || !object.get("value").isJsonPrimitive()
                || !object.get("value").getAsJsonPrimitive().isString()) {
            return null;
        }

        var value = object.get("value").getAsString();
        var favorite = object.has("favorite") && object.get("favorite").getAsBoolean();
        return new SearchEntry(value, favorite);
    }

    private static void save() {
        var path = filePath();
        try {
            Files.createDirectories(path.getParent());

            var players = new JsonObject();
            var settings = new JsonObject();
            for (var entry : HISTORY_BY_PLAYER.entrySet()) {
                var values = new JsonArray();
                for (var historyEntry : entry.getValue().entries()) {
                    var value = new JsonObject();
                    value.addProperty("value", historyEntry.value());
                    value.addProperty("favorite", historyEntry.favorite());
                    values.add(value);
                }
                players.add(entry.getKey(), values);

                var state = new JsonObject();
                state.addProperty("enabled", entry.getValue().enabled());
                state.addProperty("applyOnClick", entry.getValue().applyOnClick());
                state.addProperty("syncExternalSearch", entry.getValue().syncExternalSearch());
                state.addProperty("deleteButtonsEnabled", entry.getValue().deleteButtonsEnabled());
                state.addProperty("favoritesEnabled", entry.getValue().favoritesEnabled());
                state.addProperty("keyboardNavigationEnabled", entry.getValue().keyboardNavigationEnabled());
                state.addProperty("mouseScrollEnabled", entry.getValue().mouseScrollEnabled());
                state.addProperty("favoriteDragEnabled", entry.getValue().favoriteDragEnabled());
                settings.add(entry.getKey(), state);
            }

            var root = new JsonObject();
            root.add("players", players);
            root.add("settings", settings);
            Files.writeString(path, GSON.toJson(root), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // Search history is optional local convenience data.
        }
    }

    private static boolean removeEntry(String value) {
        var history = currentHistory().entries();
        for (int i = 0; i < history.size(); i++) {
            if (history.get(i).value().equals(value)) {
                history.remove(i);
                return true;
            }
        }
        return false;
    }

    private static final class PlayerHistory {
        private final List<SearchEntry> entries = new ArrayList<>();
        private boolean enabled = true;
        private boolean applyOnClick = true;
        private boolean syncExternalSearch = true;
        private boolean deleteButtonsEnabled = true;
        private boolean favoritesEnabled = true;
        private boolean keyboardNavigationEnabled = true;
        private boolean mouseScrollEnabled = true;
        private boolean favoriteDragEnabled = true;

        List<SearchEntry> entries() {
            return entries;
        }

        boolean enabled() {
            return enabled;
        }

        void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        boolean applyOnClick() {
            return applyOnClick;
        }

        void setApplyOnClick(boolean applyOnClick) {
            this.applyOnClick = applyOnClick;
        }

        boolean syncExternalSearch() {
            return syncExternalSearch;
        }

        void setSyncExternalSearch(boolean syncExternalSearch) {
            this.syncExternalSearch = syncExternalSearch;
        }

        boolean deleteButtonsEnabled() {
            return deleteButtonsEnabled;
        }

        void setDeleteButtonsEnabled(boolean deleteButtonsEnabled) {
            this.deleteButtonsEnabled = deleteButtonsEnabled;
        }

        boolean favoritesEnabled() {
            return favoritesEnabled;
        }

        void setFavoritesEnabled(boolean favoritesEnabled) {
            this.favoritesEnabled = favoritesEnabled;
        }

        boolean keyboardNavigationEnabled() {
            return keyboardNavigationEnabled;
        }

        void setKeyboardNavigationEnabled(boolean keyboardNavigationEnabled) {
            this.keyboardNavigationEnabled = keyboardNavigationEnabled;
        }

        boolean mouseScrollEnabled() {
            return mouseScrollEnabled;
        }

        void setMouseScrollEnabled(boolean mouseScrollEnabled) {
            this.mouseScrollEnabled = mouseScrollEnabled;
        }

        boolean favoriteDragEnabled() {
            return favoriteDragEnabled;
        }

        void setFavoriteDragEnabled(boolean favoriteDragEnabled) {
            this.favoriteDragEnabled = favoriteDragEnabled;
        }

    }

    public record SearchEntry(String value, boolean favorite) {
    }
}

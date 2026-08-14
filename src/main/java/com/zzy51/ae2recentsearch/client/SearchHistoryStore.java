package com.zzy51.ae2recentsearch.client;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
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

    public static List<String> getVisibleEntries() {
        ensureLoaded();
        var history = currentHistory().entries();
        var visibleCount = Math.min(ClientConfig.MAX_VISIBLE_ENTRIES.getAsInt(), history.size());
        return List.copyOf(history.subList(0, visibleCount));
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

    public static void record(String value) {
        if (!isEnabled() || value == null || value.isBlank()) {
            return;
        }

        ensureLoaded();
        var history = currentHistory().entries();
        history.remove(value);
        history.add(0, value);
        save();
    }

    public static void clear() {
        ensureLoaded();
        currentHistory().entries().clear();
        save();
    }

    private static PlayerHistory currentHistory() {
        return HISTORY_BY_PLAYER.computeIfAbsent(currentPlayerKey(), key -> new PlayerHistory());
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
                var values = new LinkedHashSet<String>();
                for (JsonElement element : entry.getValue().getAsJsonArray()) {
                    if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
                        values.add(element.getAsString());
                    }
                }
                history.entries().addAll(values);
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
                }
            }
        } catch (Exception ignored) {
            // A malformed local history should never prevent the game from starting.
        }
    }

    private static void save() {
        var path = filePath();
        try {
            Files.createDirectories(path.getParent());

            var players = new JsonObject();
            var settings = new JsonObject();
            for (var entry : HISTORY_BY_PLAYER.entrySet()) {
                var values = new JsonArray();
                entry.getValue().entries().forEach(values::add);
                players.add(entry.getKey(), values);

                var state = new JsonObject();
                state.addProperty("enabled", entry.getValue().enabled());
                state.addProperty("applyOnClick", entry.getValue().applyOnClick());
                state.addProperty("syncExternalSearch", entry.getValue().syncExternalSearch());
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

    private static final class PlayerHistory {
        private final List<String> entries = new ArrayList<>();
        private boolean enabled = true;
        private boolean applyOnClick = true;
        private boolean syncExternalSearch = true;

        List<String> entries() {
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
    }
}

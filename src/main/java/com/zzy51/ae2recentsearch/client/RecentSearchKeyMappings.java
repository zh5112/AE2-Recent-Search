package com.zzy51.ae2recentsearch.client;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

public final class RecentSearchKeyMappings {
    private static final String CATEGORY = "key.categories.ae2_recent_search";

    private static final KeyMapping SELECT_PREVIOUS = new KeyMapping(
            "key.ae2_recent_search.select_previous",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_UP,
            CATEGORY);

    private static final KeyMapping SELECT_NEXT = new KeyMapping(
            "key.ae2_recent_search.select_next",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_DOWN,
            CATEGORY);

    private RecentSearchKeyMappings() {
    }

    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SELECT_PREVIOUS);
        event.register(SELECT_NEXT);
    }

    public static boolean matchesSelectPrevious(int keyCode, int scanCode) {
        return SELECT_PREVIOUS.matches(keyCode, scanCode);
    }

    public static boolean matchesSelectNext(int keyCode, int scanCode) {
        return SELECT_NEXT.matches(keyCode, scanCode);
    }
}

package com.zzy51.ae2recentsearch.client;

import appeng.client.gui.widgets.AETextField;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public final class RecentSearchOverlay {
    private static final int PADDING = 2;
    private static final int ROW_HEIGHT = 12;
    private static final int TEXT_COLOR = 0xFF303040;
    private static final int TEXT_HOVER_COLOR = 0xFF101020;
    private static final int BACKGROUND_COLOR = 0xFFE1E4F0;
    private static final int INNER_BACKGROUND_COLOR = 0xFFD2D6E6;
    private static final int HIGHLIGHT_BORDER_COLOR = 0xFFFFFFFF;
    private static final int SHADOW_BORDER_COLOR = 0xFF6F7488;
    private static final int HOVER_COLOR = 0xFFC1C8DE;
    private static final int SEPARATOR_COLOR = 0xFFB6BCCF;

    private RecentSearchOverlay() {
    }

    public static boolean shouldShow(AETextField searchField) {
        return SearchHistoryStore.isEnabled()
                && searchField != null
                && searchField.visible
                && searchField.isFocused()
                && !SearchHistoryStore.getVisibleEntries().isEmpty();
    }

    public static void renderScreen(GuiGraphics graphics, Font font, AETextField searchField, int mouseX, int mouseY) {
        if (!shouldShow(searchField)) {
            return;
        }

        var entries = SearchHistoryStore.getVisibleEntries();
        var x = screenX(searchField);
        var y = screenY(searchField);
        var width = width(searchField);
        var height = entries.size() * ROW_HEIGHT + 2 * PADDING;

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, INNER_BACKGROUND_COLOR);
        graphics.hLine(x, x + width - 1, y, HIGHLIGHT_BORDER_COLOR);
        graphics.vLine(x, y, y + height - 1, HIGHLIGHT_BORDER_COLOR);
        graphics.hLine(x, x + width - 1, y + height - 1, SHADOW_BORDER_COLOR);
        graphics.vLine(x + width - 1, y, y + height - 1, SHADOW_BORDER_COLOR);

        for (int i = 0; i < entries.size(); i++) {
            var rowY = y + PADDING + i * ROW_HEIGHT;
            var hovered = isRowHovered(searchField, i, mouseX, mouseY);
            if (hovered) {
                graphics.fill(x + 1, rowY, x + width - 1, rowY + ROW_HEIGHT, HOVER_COLOR);
            }

            var value = font.plainSubstrByWidth(entries.get(i), width - 2 * PADDING);
            graphics.drawString(font, value, x + PADDING, rowY + 2, hovered ? TEXT_HOVER_COLOR : TEXT_COLOR, false);
            graphics.hLine(x + 2, x + width - 3, rowY + ROW_HEIGHT - 1, SEPARATOR_COLOR);
        }
    }

    public static boolean isMouseOver(AETextField searchField, double mouseX, double mouseY) {
        if (!shouldShow(searchField)) {
            return false;
        }

        var entries = SearchHistoryStore.getVisibleEntries();
        var x = screenX(searchField);
        var y = screenY(searchField);
        var width = width(searchField);
        var height = entries.size() * ROW_HEIGHT + 2 * PADDING;
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static boolean isMouseOverSearchOrOverlay(AETextField searchField, double mouseX, double mouseY) {
        return shouldShow(searchField)
                && (searchField.isMouseOver(mouseX, mouseY) || isMouseOver(searchField, mouseX, mouseY));
    }

    public static String getClickedValue(AETextField searchField, double mouseX, double mouseY) {
        if (!isMouseOver(searchField, mouseX, mouseY)) {
            return null;
        }

        var row = ((int) mouseY - screenY(searchField) - PADDING) / ROW_HEIGHT;
        var entries = SearchHistoryStore.getVisibleEntries();
        return row >= 0 && row < entries.size() ? entries.get(row) : null;
    }

    private static boolean isRowHovered(AETextField searchField, int row, int mouseX, int mouseY) {
        var y = screenY(searchField) + PADDING + row * ROW_HEIGHT;
        return mouseX >= screenX(searchField)
                && mouseX < screenX(searchField) + width(searchField)
                && mouseY >= y
                && mouseY < y + ROW_HEIGHT;
    }

    private static int screenX(AETextField searchField) {
        return searchField.getX();
    }

    private static int screenY(AETextField searchField) {
        return searchField.getY() + searchField.getHeight() + 3;
    }

    private static int width(AETextField searchField) {
        return searchField.getWidth();
    }
}

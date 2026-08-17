package com.zzy51.ae2recentsearch.client;

import java.util.ArrayList;
import java.util.List;

import appeng.client.gui.widgets.AETextField;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public final class RecentSearchOverlay {
    private static final int PADDING = 2;
    private static final int ROW_HEIGHT = 14;
    private static final int ACTION_BUTTON_SIZE = 10;
    private static final int TEXT_BUTTON_GAP = 4;
    private static final int SEARCH_FAVORITE_HIT_SIZE = 12;
    private static final int SEARCH_FAVORITE_RIGHT_OFFSET = 4;
    private static final int OVERLAY_X_OFFSET = -1;
    private static final int OVERLAY_WIDTH_OFFSET = 8;
    private static final int FAVORITE_BUTTON_X_OFFSET = 11;
    private static final int FAVORITE_BUTTON_Y_OFFSET = -1;

    private static final int TEXT_COLOR = 0xFF303040;
    private static final int TEXT_HOVER_COLOR = 0xFF101020;
    private static final int BACKGROUND_COLOR = 0xFFE1E4F0;
    private static final int INNER_BACKGROUND_COLOR = 0xFFD2D6E6;
    private static final int HIGHLIGHT_BORDER_COLOR = 0xFFFFFFFF;
    private static final int SHADOW_BORDER_COLOR = 0xFF6F7488;
    private static final int HOVER_COLOR = 0xFFC1C8DE;
    private static final int KEYBOARD_SELECTED_COLOR = 0xFFAEB8D0;
    private static final int KEYBOARD_SELECTED_MARKER_COLOR = 0xFF5E6F99;
    private static final int SEPARATOR_COLOR = 0xFFB6BCCF;
    private static final int GROUP_SEPARATOR_COLOR = 0xFF9298AC;

    private static final int ACTION_BACKGROUND_COLOR = 0xFFDCE1EE;
    private static final int ACTION_HOVER_BACKGROUND_COLOR = 0xFFF0F3F9;
    private static final int ACTION_BORDER_LIGHT = 0xFFFFFFFF;
    private static final int ACTION_BORDER_DARK = 0xFF7A7F93;
    private static final int FAVORITE_ICON_COLOR = 0xFFE0B84C;
    private static final int FAVORITE_HOVER_COLOR = 0xFFFFD86A;
    private static final int FAVORITE_INACTIVE_COLOR = 0xFF8A8F9E;
    private static final int DELETE_ICON_COLOR = 0xFFC85E68;
    private static final int DELETE_HOVER_COLOR = 0xFFE9757D;

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
        renderSearchFavoriteButton(graphics, searchField, mouseX, mouseY);
        if (!shouldShow(searchField)) {
            return;
        }

        var groupedEntries = groupEntries(SearchHistoryStore.getVisibleEntries());
        var x = screenX(searchField);
        var y = screenY(searchField);
        var width = width(searchField);
        var height = overlayHeight(groupedEntries);
        var selectedValue = RecentSearchKeyboardNavigation.selectedValue(searchField);

        graphics.fill(x, y, x + width, y + height, BACKGROUND_COLOR);
        graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, INNER_BACKGROUND_COLOR);
        graphics.hLine(x, x + width - 1, y, HIGHLIGHT_BORDER_COLOR);
        graphics.vLine(x, y, y + height - 1, HIGHLIGHT_BORDER_COLOR);
        graphics.hLine(x, x + width - 1, y + height - 1, SHADOW_BORDER_COLOR);
        graphics.vLine(x + width - 1, y, y + height - 1, SHADOW_BORDER_COLOR);

        var rowY = y + PADDING;
        rowY = renderEntries(graphics, font, x, width, rowY, groupedEntries.favorites(), selectedValue, mouseX, mouseY);
        if (groupedEntries.hasSeparator()) {
            graphics.hLine(x + 3, x + width - 4, rowY + 1, GROUP_SEPARATOR_COLOR);
            rowY += 4;
        }
        renderEntries(graphics, font, x, width, rowY, groupedEntries.recents(), selectedValue, mouseX, mouseY);
    }

    public static boolean isMouseOver(AETextField searchField, double mouseX, double mouseY) {
        if (!shouldShow(searchField)) {
            return false;
        }

        var groupedEntries = groupEntries(SearchHistoryStore.getVisibleEntries());
        var x = screenX(searchField);
        var y = screenY(searchField);
        var width = width(searchField);
        var height = overlayHeight(groupedEntries);
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    public static boolean isMouseOverSearchOrOverlay(AETextField searchField, double mouseX, double mouseY) {
        return (isFavoriteButtonVisible(searchField) && isSearchFavoriteHovered(searchField, mouseX, mouseY))
                || (shouldShow(searchField)
                && (searchField.isMouseOver(mouseX, mouseY) || isMouseOver(searchField, mouseX, mouseY)));
    }

    public static ClickTarget getClickedTarget(AETextField searchField, double mouseX, double mouseY) {
        if (isFavoriteButtonVisible(searchField) && isSearchFavoriteHovered(searchField, mouseX, mouseY)) {
            return new ClickTarget(ClickTargetType.SEARCH_FAVORITE, searchField.getValue());
        }

        if (!shouldShow(searchField)) {
            return null;
        }

        var groupedEntries = groupEntries(SearchHistoryStore.getVisibleEntries());
        var x = screenX(searchField);
        var y = screenY(searchField);
        var width = width(searchField);
        var showDelete = SearchHistoryStore.isDeleteButtonsEnabled();

        var rowY = y + PADDING;
        var target = getGroupClickTarget(groupedEntries.favorites(), x, width, rowY, mouseX, mouseY, showDelete);
        if (target != null) {
            return target;
        }

        rowY += groupedEntries.favorites().size() * ROW_HEIGHT;
        if (groupedEntries.hasSeparator()) {
            rowY += 4;
        }

        return getGroupClickTarget(groupedEntries.recents(), x, width, rowY, mouseX, mouseY, showDelete);
    }

    public static String getClickedValue(AETextField searchField, double mouseX, double mouseY) {
        var target = getClickedTarget(searchField, mouseX, mouseY);
        return target != null && target.type() == ClickTargetType.ENTRY ? target.value() : null;
    }

    private static int renderEntries(
            GuiGraphics graphics,
            Font font,
            int x,
            int width,
            int rowY,
            List<SearchHistoryStore.SearchEntry> entries,
            String selectedValue,
            int mouseX,
            int mouseY) {
        var showDelete = SearchHistoryStore.isDeleteButtonsEnabled();
        for (var entry : entries) {
            drawEntryRow(graphics, font, x, width, rowY, entry, selectedValue, mouseX, mouseY, showDelete);
            rowY += ROW_HEIGHT;
        }
        return rowY;
    }

    private static void drawEntryRow(
            GuiGraphics graphics,
            Font font,
            int x,
            int width,
            int rowY,
            SearchHistoryStore.SearchEntry entry,
            String selectedValue,
            int mouseX,
            int mouseY,
            boolean showDelete) {
        var layout = buttonLayout(x, width, showDelete);
        var hovered = isRowHovered(x, width, rowY, mouseX, mouseY);
        var selected = entry.value().equals(selectedValue);
        if (selected) {
            graphics.fill(x + 1, rowY, x + width - 1, rowY + ROW_HEIGHT, KEYBOARD_SELECTED_COLOR);
            graphics.fill(x + 1, rowY + 1, x + 3, rowY + ROW_HEIGHT - 1, KEYBOARD_SELECTED_MARKER_COLOR);
        }
        if (hovered) {
            graphics.fill(x + 1, rowY, x + width - 1, rowY + ROW_HEIGHT, HOVER_COLOR);
        }

        var textWidth = Math.max(0, width - 2 * PADDING - layout.textReserveWidth());
        var value = font.plainSubstrByWidth(entry.value(), textWidth);
        graphics.drawString(font, value, x + PADDING, rowY + 3,
                hovered || selected ? TEXT_HOVER_COLOR : TEXT_COLOR, false);

        if (showDelete) {
            var deleteHovered = isButtonHovered(layout.deleteX(), rowY, mouseX, mouseY);
            drawDeleteButton(graphics, layout.deleteX(), rowY + 2, deleteHovered);
        }

        graphics.hLine(x + 2, x + width - 3, rowY + ROW_HEIGHT - 1, SEPARATOR_COLOR);
    }

    private static void renderSearchFavoriteButton(
            GuiGraphics graphics,
            AETextField searchField,
            int mouseX,
            int mouseY) {
        if (!isFavoriteButtonVisible(searchField)) {
            return;
        }

        var x = searchFavoriteX(searchField);
        var y = searchFavoriteY(searchField);
        var hovered = isSearchFavoriteHovered(searchField, mouseX, mouseY);
        var value = searchField.getValue();
        var favorite = SearchHistoryStore.isFavorite(value);
        var iconColor = hovered ? FAVORITE_HOVER_COLOR : favorite ? FAVORITE_ICON_COLOR : FAVORITE_INACTIVE_COLOR;
        drawPixelStar(graphics, x + 2, y + 2, iconColor);
    }

    private static void drawDeleteButton(GuiGraphics graphics, int x, int y, boolean hovered) {
        drawSmallButtonFrame(graphics, x, y, hovered);
        drawPixelX(graphics, x + 2, y + 2, hovered ? DELETE_HOVER_COLOR : DELETE_ICON_COLOR);
    }

    private static void drawSmallButtonFrame(GuiGraphics graphics, int x, int y, boolean hovered) {
        graphics.fill(x, y, x + ACTION_BUTTON_SIZE, y + ACTION_BUTTON_SIZE,
                hovered ? ACTION_HOVER_BACKGROUND_COLOR : ACTION_BACKGROUND_COLOR);
        graphics.hLine(x, x + ACTION_BUTTON_SIZE - 1, y, ACTION_BORDER_LIGHT);
        graphics.vLine(x, y, y + ACTION_BUTTON_SIZE - 1, ACTION_BORDER_LIGHT);
        graphics.hLine(x, x + ACTION_BUTTON_SIZE - 1, y + ACTION_BUTTON_SIZE - 1, ACTION_BORDER_DARK);
        graphics.vLine(x + ACTION_BUTTON_SIZE - 1, y, y + ACTION_BUTTON_SIZE - 1, ACTION_BORDER_DARK);
    }

    private static void drawPixelX(GuiGraphics graphics, int x, int y, int color) {
        for (int i = 0; i < 6; i++) {
            graphics.fill(x + i, y + i, x + i + 1, y + i + 1, color);
            graphics.fill(x + 5 - i, y + i, x + 6 - i, y + i + 1, color);
        }
    }

    private static void drawPixelStar(GuiGraphics graphics, int x, int y, int color) {
        graphics.fill(x + 3, y, x + 4, y + 1, color);
        graphics.fill(x + 3, y + 1, x + 4, y + 2, color);
        graphics.fill(x + 1, y + 2, x + 6, y + 3, color);
        graphics.fill(x + 2, y + 3, x + 5, y + 4, color);
        graphics.fill(x + 1, y + 4, x + 6, y + 5, color);
        graphics.fill(x + 1, y + 5, x + 3, y + 6, color);
        graphics.fill(x + 4, y + 5, x + 6, y + 6, color);
    }

    private static boolean isRowHovered(int x, int width, int rowY, int mouseX, int mouseY) {
        return mouseX >= x
                && mouseX < x + width
                && mouseY >= rowY
                && mouseY < rowY + ROW_HEIGHT;
    }

    private static boolean isButtonHovered(int buttonX, int rowY, double mouseX, double mouseY) {
        return buttonX >= 0
                && mouseX >= buttonX
                && mouseX < buttonX + ACTION_BUTTON_SIZE
                && mouseY >= rowY + 2
                && mouseY < rowY + 2 + ACTION_BUTTON_SIZE;
    }

    private static ClickTarget getGroupClickTarget(
            List<SearchHistoryStore.SearchEntry> entries,
            int x,
            int width,
            int rowY,
            double mouseX,
            double mouseY,
            boolean showDelete) {
        for (var entry : entries) {
            var layout = buttonLayout(x, width, showDelete);
            if (showDelete && isButtonHovered(layout.deleteX(), rowY, mouseX, mouseY)) {
                return new ClickTarget(ClickTargetType.DELETE, entry.value());
            }

            if (mouseX >= x
                    && mouseX < x + width
                    && mouseY >= rowY
                    && mouseY < rowY + ROW_HEIGHT) {
                return new ClickTarget(ClickTargetType.ENTRY, entry.value());
            }

            rowY += ROW_HEIGHT;
        }

        return null;
    }

    private static GroupedEntries groupEntries(List<SearchHistoryStore.SearchEntry> entries) {
        if (!SearchHistoryStore.isFavoritesEnabled()) {
            return new GroupedEntries(List.of(), entries);
        }

        var favorites = new ArrayList<SearchHistoryStore.SearchEntry>();
        var recents = new ArrayList<SearchHistoryStore.SearchEntry>();
        for (var entry : entries) {
            if (entry.favorite()) {
                favorites.add(entry);
            } else {
                recents.add(entry);
            }
        }

        return new GroupedEntries(List.copyOf(favorites), List.copyOf(recents));
    }

    private static int overlayHeight(GroupedEntries groupedEntries) {
        var rowCount = groupedEntries.favorites().size() + groupedEntries.recents().size();
        var separatorHeight = groupedEntries.hasSeparator() ? 4 : 0;
        return rowCount * ROW_HEIGHT + separatorHeight + 2 * PADDING;
    }

    private static ButtonLayout buttonLayout(int x, int width, boolean showDelete) {
        if (!showDelete) {
            return new ButtonLayout(-1, 0);
        }

        return new ButtonLayout(x + width - PADDING - ACTION_BUTTON_SIZE, ACTION_BUTTON_SIZE + TEXT_BUTTON_GAP);
    }

    private static boolean isFavoriteButtonVisible(AETextField searchField) {
        return SearchHistoryStore.isEnabled()
                && SearchHistoryStore.isFavoritesEnabled()
                && searchField != null
                && searchField.visible;
    }

    private static boolean isSearchFavoriteHovered(AETextField searchField, double mouseX, double mouseY) {
        var x = searchFavoriteX(searchField);
        var y = searchFavoriteY(searchField);
        return mouseX >= x
                && mouseX < x + SEARCH_FAVORITE_HIT_SIZE
                && mouseY >= y
                && mouseY < y + SEARCH_FAVORITE_HIT_SIZE;
    }

    private static int searchFavoriteX(AETextField searchField) {
        return searchField.getX()
                + searchField.getWidth()
                - SEARCH_FAVORITE_HIT_SIZE
                - SEARCH_FAVORITE_RIGHT_OFFSET
                + FAVORITE_BUTTON_X_OFFSET;
    }

    private static int searchFavoriteY(AETextField searchField) {
        return searchField.getY()
                + Math.max(1, (searchField.getHeight() - SEARCH_FAVORITE_HIT_SIZE) / 2)
                + FAVORITE_BUTTON_Y_OFFSET;
    }

    private static int screenX(AETextField searchField) {
        return searchField.getX() + OVERLAY_X_OFFSET;
    }

    private static int screenY(AETextField searchField) {
        return searchField.getY() + searchField.getHeight() + 3;
    }

    private static int width(AETextField searchField) {
        return Math.max(40, searchField.getWidth() + OVERLAY_WIDTH_OFFSET);
    }

    public record ClickTarget(ClickTargetType type, String value) {
    }

    public enum ClickTargetType {
        ENTRY,
        SEARCH_FAVORITE,
        DELETE
    }

    private record GroupedEntries(List<SearchHistoryStore.SearchEntry> favorites,
                                  List<SearchHistoryStore.SearchEntry> recents) {
        boolean hasSeparator() {
            return !favorites.isEmpty() && !recents.isEmpty();
        }
    }

    private record ButtonLayout(int deleteX, int textReserveWidth) {
    }
}

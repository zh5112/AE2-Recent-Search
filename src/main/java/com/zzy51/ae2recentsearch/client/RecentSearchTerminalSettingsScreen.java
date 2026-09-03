package com.zzy51.ae2recentsearch.client;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import appeng.client.gui.AESubScreen;
import appeng.client.gui.Icon;
import appeng.client.gui.me.common.TerminalSettingsScreen;
import appeng.client.gui.widgets.AE2Button;
import appeng.client.gui.widgets.TabButton;
import appeng.menu.SlotSemantics;
import appeng.menu.me.common.MEStorageMenu;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class RecentSearchTerminalSettingsScreen<C extends MEStorageMenu>
        extends AESubScreen<C, TerminalSettingsScreen<C>> {
    private AE2Button enabledButton;
    private AE2Button deleteButtonsButton;
    private AE2Button favoritesButton;
    private AE2Button keyboardNavigationButton;
    private AE2Button mouseScrollButton;
    private AE2Button favoriteDragButton;
    private AE2Button applyOnClickButton;
    private AE2Button syncExternalSearchButton;

    public RecentSearchTerminalSettingsScreen(TerminalSettingsScreen<C> parent) {
        super(parent, "/screens/ae2_recent_search_settings.json");
        hideTerminalSlots();
        widgets.add("back", new TabButton(Icon.BACK,
                Component.translatable("gui.back"), ignored -> returnToParent()));
        enabledButton = widgets.addButton("enabled", enabledText(), this::toggleEnabled);
        deleteButtonsButton = widgets.addButton("deleteButtons", deleteButtonsText(), this::toggleDeleteButtons);
        favoritesButton = widgets.addButton("favorites", favoritesText(), this::toggleFavorites);
        keyboardNavigationButton = widgets.addButton("keyboardNavigation", keyboardNavigationText(),
                this::toggleKeyboardNavigation);
        mouseScrollButton = widgets.addButton("mouseScroll", mouseScrollText(), this::toggleMouseScroll);
        favoriteDragButton = widgets.addButton("favoriteDrag", favoriteDragText(), this::toggleFavoriteDrag);
        applyOnClickButton = widgets.addButton("applyOnClick", applyOnClickText(), this::toggleApplyOnClick);
        syncExternalSearchButton = widgets.addButton("syncExternalSearch", syncExternalSearchText(),
                this::toggleSyncExternalSearch);
        widgets.addButton("clear", Component.translatable("ae2_recent_search.button.clear"), SearchHistoryStore::clear);
    }

    private void hideTerminalSlots() {
        for (var semantic : List.of(SlotSemantics.CRAFTING_GRID, SlotSemantics.CRAFTING_RESULT,
                SlotSemantics.PROCESSING_INPUTS, SlotSemantics.PROCESSING_OUTPUTS,
                SlotSemantics.SMITHING_TABLE_TEMPLATE, SlotSemantics.SMITHING_TABLE_BASE,
                SlotSemantics.SMITHING_TABLE_ADDITION, SlotSemantics.SMITHING_TABLE_RESULT,
                SlotSemantics.STONECUTTING_INPUT, SlotSemantics.BLANK_PATTERN,
                SlotSemantics.ENCODED_PATTERN, SlotSemantics.PLAYER_INVENTORY,
                SlotSemantics.PLAYER_HOTBAR)) {
            setSlotsHidden(semantic, true);
        }
    }

    private void toggleEnabled() {
        SearchHistoryStore.setEnabled(!SearchHistoryStore.isEnabled());
        enabledButton.setMessage(enabledText());
    }

    private void toggleDeleteButtons() {
        SearchHistoryStore.setDeleteButtonsEnabled(!SearchHistoryStore.isDeleteButtonsEnabled());
        deleteButtonsButton.setMessage(deleteButtonsText());
    }

    private void toggleFavorites() {
        SearchHistoryStore.setFavoritesEnabled(!SearchHistoryStore.isFavoritesEnabled());
        favoritesButton.setMessage(favoritesText());
    }

    private void toggleKeyboardNavigation() {
        SearchHistoryStore.setKeyboardNavigationEnabled(!SearchHistoryStore.isKeyboardNavigationEnabled());
        keyboardNavigationButton.setMessage(keyboardNavigationText());
    }

    private void toggleMouseScroll() {
        SearchHistoryStore.setMouseScrollEnabled(!SearchHistoryStore.isMouseScrollEnabled());
        mouseScrollButton.setMessage(mouseScrollText());
    }

    private void toggleFavoriteDrag() {
        SearchHistoryStore.setFavoriteDragEnabled(!SearchHistoryStore.isFavoriteDragEnabled());
        favoriteDragButton.setMessage(favoriteDragText());
    }

    private void toggleApplyOnClick() {
        SearchHistoryStore.setApplyOnClick(!SearchHistoryStore.isApplyOnClick());
        applyOnClickButton.setMessage(applyOnClickText());
    }

    private void toggleSyncExternalSearch() {
        SearchHistoryStore.setSyncExternalSearch(!SearchHistoryStore.isSyncExternalSearch());
        syncExternalSearchButton.setMessage(syncExternalSearchText());
    }

    private Component enabledText() {
        return Component.translatable(SearchHistoryStore.isEnabled()
                ? "ae2_recent_search.button.enabled_on"
                : "ae2_recent_search.button.enabled_off");
    }

    private Component deleteButtonsText() {
        return Component.translatable(SearchHistoryStore.isDeleteButtonsEnabled()
                ? "ae2_recent_search.button.delete_buttons_on"
                : "ae2_recent_search.button.delete_buttons_off");
    }

    private Component favoritesText() {
        return Component.translatable(SearchHistoryStore.isFavoritesEnabled()
                ? "ae2_recent_search.button.favorites_on"
                : "ae2_recent_search.button.favorites_off");
    }

    private Component keyboardNavigationText() {
        return Component.translatable(SearchHistoryStore.isKeyboardNavigationEnabled()
                ? "ae2_recent_search.button.keyboard_navigation_on"
                : "ae2_recent_search.button.keyboard_navigation_off");
    }

    private Component mouseScrollText() {
        return Component.translatable(SearchHistoryStore.isMouseScrollEnabled()
                ? "ae2_recent_search.button.mouse_scroll_on"
                : "ae2_recent_search.button.mouse_scroll_off");
    }

    private Component favoriteDragText() {
        return Component.translatable(SearchHistoryStore.isFavoriteDragEnabled()
                ? "ae2_recent_search.button.favorite_drag_on"
                : "ae2_recent_search.button.favorite_drag_off");
    }

    private Component applyOnClickText() {
        return Component.translatable(SearchHistoryStore.isApplyOnClick()
                ? "ae2_recent_search.button.apply_on"
                : "ae2_recent_search.button.apply_off");
    }

    private Component syncExternalSearchText() {
        return Component.translatable(SearchHistoryStore.isSyncExternalSearch()
                ? "ae2_recent_search.button.sync_external_on"
                : "ae2_recent_search.button.sync_external_off");
    }

    @Override
    public void drawFG(GuiGraphics graphics, int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(graphics, offsetX, offsetY, mouseX, mouseY);
        graphics.drawString(font, Component.translatable("ae2_recent_search.settings.history"),
                10, 28, 0x404040, false);
        graphics.drawWordWrap(font,
                Component.translatable("ae2_recent_search.settings.history.hint"),
                10, 88, 180, 0x666666);
        graphics.drawString(font, Component.translatable("ae2_recent_search.settings.entry_actions"),
                10, 116, 0x404040, false);
        graphics.drawString(font, Component.translatable("ae2_recent_search.settings.click_behavior"),
                10, 202, 0x404040, false);
        graphics.drawWordWrap(font,
                Component.translatable("ae2_recent_search.settings.click_behavior.hint"),
                10, 240, 180, 0x666666);
        graphics.drawString(font, Component.translatable("ae2_recent_search.settings.external_search"),
                10, 264, 0x404040, false);
        graphics.drawWordWrap(font,
                Component.translatable("ae2_recent_search.settings.external_search.hint"),
                10, 300, 180, 0x666666);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            returnToParent();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

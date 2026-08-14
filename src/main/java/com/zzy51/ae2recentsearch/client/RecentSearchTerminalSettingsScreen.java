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
    private AE2Button applyOnClickButton;
    private AE2Button syncExternalSearchButton;

    public RecentSearchTerminalSettingsScreen(TerminalSettingsScreen<C> parent) {
        super(parent, "/screens/ae2_recent_search_settings.json");
        hideTerminalSlots();
        widgets.add("back", new TabButton(Icon.BACK,
                Component.translatable("gui.back"), ignored -> returnToParent()));
        enabledButton = widgets.addButton("enabled", enabledText(), this::toggleEnabled);
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
                10, 30, 0x404040, false);
        graphics.drawWordWrap(font,
                Component.translatable("ae2_recent_search.settings.history.hint"),
                10, 98, 180, 0x666666);
        graphics.drawString(font, Component.translatable("ae2_recent_search.settings.click_behavior"),
                10, 118, 0x404040, false);
        graphics.drawWordWrap(font,
                Component.translatable("ae2_recent_search.settings.click_behavior.hint"),
                10, 154, 180, 0x666666);
        graphics.drawString(font, Component.translatable("ae2_recent_search.settings.external_search"),
                10, 184, 0x404040, false);
        graphics.drawWordWrap(font,
                Component.translatable("ae2_recent_search.settings.external_search.hint"),
                10, 220, 180, 0x666666);
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

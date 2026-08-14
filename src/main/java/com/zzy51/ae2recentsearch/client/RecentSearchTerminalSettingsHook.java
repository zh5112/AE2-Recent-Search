package com.zzy51.ae2recentsearch.client;

import java.util.List;

import appeng.client.gui.Icon;
import appeng.client.gui.me.common.TerminalSettingsScreen;
import appeng.client.gui.widgets.IconButton;

import com.zzy51.ae2recentsearch.AE2RecentSearch;
import com.zzy51.ae2recentsearch.mixin.AEBaseScreenAccessor;
import com.zzy51.ae2recentsearch.mixin.VerticalButtonBarAccessor;

import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = AE2RecentSearch.MOD_ID, value = Dist.CLIENT)
public final class RecentSearchTerminalSettingsHook {
    private RecentSearchTerminalSettingsHook() {
    }

    @SubscribeEvent
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static void addRecentSearchSettingsTab(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof TerminalSettingsScreen settings)) {
            return;
        }

        var toolbar = ((AEBaseScreenAccessor) settings).ae2RecentSearch$getVerticalToolbar();
        var buttons = ((VerticalButtonBarAccessor) toolbar).ae2RecentSearch$getButtons();
        for (var existing : buttons) {
            if (existing instanceof RecentSearchSettingsButton) {
                return;
            }
        }

        var button = new RecentSearchSettingsButton(ignored ->
                settings.switchToScreen(new RecentSearchTerminalSettingsScreen(settings)));
        toolbar.add(button);
        event.addListener(button);
    }

    private static final class RecentSearchSettingsButton extends IconButton {
        private RecentSearchSettingsButton(OnPress onPress) {
            super(onPress);
        }

        @Override
        protected Icon getIcon() {
            return Icon.COG;
        }

        @Override
        public List<Component> getTooltipMessage() {
            return List.of(Component.translatable("ae2_recent_search.settings.title"));
        }
    }
}

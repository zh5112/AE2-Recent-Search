# AE2 Recent Search

[English Introduction](#english-introduction)

AE2 Recent Search 是一个适用于 Minecraft `1.21.1` NeoForge 的 [Applied Energistics 2](https://github.com/AppliedEnergistics/Applied-Energistics-2) 客户端附属模组。

它为 AE2 终端搜索框添加最近搜索历史、收藏、单条删除、键盘导航和终端内设置等功能，方便重复使用物品名称、模组筛选、标签筛选、Tooltip 搜索和物品 ID 搜索。

## 功能

- 在 AE2 终端搜索框下方显示最近搜索记录。
- 按 Minecraft 玩家账号分别保存搜索历史。
- 完全相同的搜索词只保留一条，再次使用时移动到顶部。
- 支持 AE2 特殊搜索语法，例如 `@mod`、`#tag`、`$tooltip` 和 `*id`。
- 支持收藏搜索词，收藏词条显示在普通历史记录上方。
- 支持删除单条历史记录。
- 支持鼠标滚轮滚动较长的历史记录列表。
- 支持长按拖动收藏词条调整顺序。
- 支持使用键盘选择候选词条，并按回车键确认；默认按键为上、下方向键，可在 Minecraft 控制设置中修改。
- 支持在 AE2 终端设置界面中配置相关功能。
- 支持选择点击历史词条后的行为：
  - 立即搜索
  - 仅填入搜索框
- 可选同步搜索到 JEI / REI / EMI。
- 使用贴近 AE2 原版风格的界面。

## 使用方法

打开 AE2 终端并点击搜索框。存在历史记录时，记录会显示在搜索框下方。

点击历史词条时，会按照终端设置执行：

- `点击：立即搜索`：填入词条、执行搜索并收回历史记录列表。
- `点击：仅填入`：只填入搜索框，方便继续编辑。

搜索框右侧的星标按钮可以收藏或取消收藏当前搜索词。历史记录右侧的删除按钮可以删除单条记录。

当收藏拖动功能开启时，长按收藏词条后可以拖动调整收藏顺序。历史记录较多时，可以在列表上使用鼠标滚轮滚动。

最近搜索设置可以从 AE2 终端的设置界面进入。

## 设置

以下功能可以在游戏内 AE2 终端设置界面中单独开关：

- 最近搜索
- 单条删除按钮
- 收藏功能
- 键盘选择
- 鼠标滚轮滚动
- 收藏词条拖动排序
- 点击历史词条后的行为
- JEI / REI / EMI 外部搜索同步

显示的历史记录数量由客户端配置控制：

```toml
maxVisibleEntries = 10
```

每个玩家的历史记录和游戏内设置会保存在当前 Minecraft 实例的配置目录中。

## 相关移植版本

社区贡献者 **卿暨** 基于本项目制作了 Minecraft `1.20.1` Forge 移植版，并保留了最近搜索历史、收藏、单条删除、键盘导航、终端设置页以及 JEI / REI / EMI 外部搜索同步等功能。

- [1.20.1 Forge 移植版仓库](https://github.com/An8362/AE2-Recent-Search-1.20.1-Forge)
- [1.20.1 Forge 移植版 v1.2.0](https://github.com/An8362/AE2-Recent-Search-1.20.1-Forge/releases/tag/v1.2.0)

移植版本的运行要求：

- Forge `47.1.3` 或更高版本
- Applied Energistics 2 `15.4.10` 或更高版本，包含 GuideME
- JDK `17`

移植版本沿用 MIT 许可证。感谢社区贡献者对旧版本平台的适配。

## 需求

- Minecraft `1.21.1`
- NeoForge
- Applied Energistics 2 `19.2.17` 或更高版本
- Java `21`

JEI、REI 或 EMI 是可选依赖。外部搜索同步只有在 AE2 自身启用外部搜索同步时才会生效。

## 说明

这是一个 AE2 终端客户端界面附属模组，不添加物品、方块、网络机制或存储行为。

## 许可证

MIT

## English Introduction

AE2 Recent Search is a client-side addon for [Applied Energistics 2](https://github.com/AppliedEnergistics/Applied-Energistics-2) on Minecraft `1.21.1` with NeoForge.

It adds recent search history, favorites, per-entry deletion, keyboard navigation, and in-terminal settings to AE2 terminals. This makes it easier to reuse item names, mod filters, tag filters, tooltip searches, and item ID searches.

### Features

- Shows recent searches below the AE2 terminal search box.
- Stores search history separately for each Minecraft account.
- Keeps exact duplicate searches as one entry and moves reused non-favorite entries to the top.
- Supports AE2 search syntax such as `@mod`, `#tag`, `$tooltip`, and `*id`.
- Supports favorite search entries above normal history.
- Supports deleting individual history entries.
- Supports mouse wheel scrolling when the history list is longer than the visible area.
- Supports long-press dragging to reorder favorite entries.
- Supports keyboard candidate selection and Enter to confirm. The default keys are Up and Down, and can be changed in Minecraft's controls screen.
- Provides an in-terminal settings screen for these features.
- Lets history entries either search immediately or only fill the search box.
- Can optionally synchronize searches with JEI, REI, or EMI.
- Uses an interface styled to fit AE2's original terminal UI.

### Usage

Open an AE2 terminal and click the search box. Existing search history appears directly below it.

History entries follow the configured click behavior:

- `Click: Search` fills the entry, applies the search, and closes the history list.
- `Click: Fill` only fills the search box so you can continue editing.

Use the star button on the right side of the search box to favorite or unfavorite the current search. Use the delete button on a history row to remove one entry.

When favorite dragging is enabled, hold a favorite entry briefly and drag it to change its order. Use the mouse wheel over the list to scroll through longer histories.

The recent search settings are available from the AE2 terminal settings screen.

### Settings

The following features can be toggled separately from the in-game AE2 terminal settings screen:

- Recent search history
- Per-entry delete buttons
- Favorites
- Keyboard selection
- Mouse wheel scrolling
- Favorite entry reordering
- History entry click behavior
- JEI / REI / EMI external search synchronization

The number of visible history entries is controlled by the client configuration:

```toml
maxVisibleEntries = 10
```

Each player's history and in-game settings are stored in the configuration directory of the current Minecraft instance.

### Related Port

Community contributor **卿暨** created a Minecraft `1.20.1` Forge port based on this project. The port keeps recent search history, favorites, per-entry deletion, keyboard navigation, the in-terminal settings screen, and JEI / REI / EMI external search synchronization.

- [Minecraft 1.20.1 Forge port repository](https://github.com/An8362/AE2-Recent-Search-1.20.1-Forge)
- [Minecraft 1.20.1 Forge port v1.2.0](https://github.com/An8362/AE2-Recent-Search-1.20.1-Forge/releases/tag/v1.2.0)

Port requirements:

- Forge `47.1.3` or newer
- Applied Energistics 2 `15.4.10` or newer, including GuideME
- JDK `17`

The port uses the MIT license. Thanks to the community contributor for adapting the project to an older platform.

### Requirements

- Minecraft `1.21.1`
- NeoForge
- Applied Energistics 2 `19.2.17` or newer
- Java `21`

JEI, REI, and EMI are optional dependencies. External search synchronization only works when AE2's own external search synchronization is enabled.

### License

MIT

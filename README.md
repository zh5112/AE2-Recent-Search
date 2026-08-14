# AE2 Recent Search / AE2 最近搜索

[English Introduction](#english-introduction)

AE2 Recent Search 是一个用于 Minecraft `1.21.1` NeoForge 的 [Applied Energistics 2 / 应用能源2](https://github.com/AppliedEnergistics/Applied-Energistics-2) 客户端附属模组。

它为 AE2 终端添加“最近搜索 / 搜索历史记录”功能，方便重复使用物品名、模组筛选、标签筛选、tooltip 搜索、物品 ID 搜索等 AE2 搜索表达式。

## 功能

- 在 AE2 终端搜索框下方显示最近搜索记录。
- 按 Minecraft 账号在本地保存历史记录。
- 完全相同的搜索词不会重复记录，再次使用时会移动到最上方。
- 支持 AE2 搜索语法，例如 `@mod`、`#tag`、`$tooltip`、`*id`。
- 点击历史记录可选择“立即搜索”或“仅填入搜索框”。
- 在 AE2 终端设置界面内提供开关、清空和点击行为设置。
- 可选同步点击的最近搜索到 JEI / REI 搜索框。
- UI 尽量贴近 AE2 原版终端风格。

## 需求

- Minecraft `1.21.1`
- NeoForge
- Applied Energistics 2 / 应用能源2 `19.2.17` 或更高版本

JEI / REI 是可选依赖。外部搜索同步只有在 AE2 自身开启外部搜索同步时才会生效。

## 使用方法

打开 AE2 终端并点击搜索框。如果已有最近搜索记录，它们会显示在搜索框下方。

点击一条历史记录时，会按照终端设置执行：

- `点击：立即搜索`：立刻应用搜索，并关闭最近搜索弹窗。
- `点击：仅填入`：只把文本填入搜索框，方便继续编辑。

最近搜索设置可以在 AE2 终端的设置界面中找到。

## 配置

显示的历史记录数量由客户端配置控制：

```toml
maxVisibleEntries = 10
```

每个玩家的历史记录和游戏内开关状态会保存在当前 Minecraft 实例的本地配置目录中。

## 说明

这是一个 AE2 终端客户端便利 UI 模组，不添加物品、方块、网络机制或存储行为。

## 许可证

MIT

## English Introduction

AE2 Recent Search is a client-side addon for [Applied Energistics 2](https://github.com/AppliedEnergistics/Applied-Energistics-2) on Minecraft `1.21.1` with NeoForge.

It adds recent search history to AE2 terminals, making it easier to reuse previous searches such as item names, mod filters, tag filters, tooltip searches, and item ID searches.

### Features

- Shows recent searches below the AE2 terminal search box.
- Stores history locally per Minecraft account.
- Keeps exact duplicate searches as one entry and moves reused searches to the top.
- Supports special AE2 search syntax such as `@mod`, `#tag`, `$tooltip`, and `*id`.
- Lets history entries either search immediately or only fill the search box.
- Adds an in-terminal settings page for enabling, clearing, and changing recent-search behavior.
- Can optionally sync clicked recent searches to JEI/REI through AE2's external search integration.
- Uses an AE2-style UI instead of a separate config-only workflow.

### Requirements

- Minecraft `1.21.1`
- NeoForge
- Applied Energistics 2 `19.2.17` or newer

JEI or REI is optional. External search sync only applies when AE2's own external search sync is enabled.

### Usage

Open an AE2 terminal and click the search box. If recent searches exist, they appear directly below the search field.

Clicking an entry applies it according to the terminal setting:

- `Click: Search` applies the search immediately and closes the recent-search popup.
- `Click: Fill` only fills the search box so you can edit the text before searching.

The recent-search settings are available from AE2's terminal settings screen.

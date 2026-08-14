# AE2 Recent Search

AE2 Recent Search is a client-side addon for [Applied Energistics 2](https://github.com/AppliedEnergistics/Applied-Energistics-2) on Minecraft 1.21.1 with NeoForge.

It adds recent search history to AE2 terminals, making it easier to reuse previous searches such as item names, mod filters, tag filters, and other AE2 search expressions.

## Features

- Shows recent searches below the AE2 terminal search box.
- Stores history locally per Minecraft account.
- Keeps exact duplicate searches as one entry and moves reused searches to the top.
- Supports special AE2 search syntax such as `@mod`, `#tag`, `$tooltip`, and `*id`.
- Lets history entries either search immediately or only fill the search box.
- Adds an in-terminal settings page for enabling, clearing, and changing recent-search behavior.
- Can optionally sync clicked recent searches to JEI/REI through AE2's external search integration.
- Uses an AE2-style UI instead of a separate config-only workflow.

## Requirements

- Minecraft `1.21.1`
- NeoForge
- Applied Energistics 2 `19.2.17` or newer

JEI or REI is optional. External search sync only applies when AE2's own external search sync is enabled.

## Usage

Open an AE2 terminal and click the search box. If recent searches exist, they appear directly below the search field.

Clicking an entry applies it according to the terminal setting:

- `Click: Search` applies the search immediately and closes the recent-search popup.
- `Click: Fill` only fills the search box so you can edit the text before searching.

The recent-search settings are available from AE2's terminal settings screen.

## Configuration

The visible history count is controlled by the client config:

```toml
maxVisibleEntries = 10
```

Per-player history and in-game toggle states are stored locally in the Minecraft instance config folder.

## Notes

This mod is client-side convenience UI for AE2 terminals. It does not add storage behavior, network mechanics, items, or blocks.

## License

MIT

# FreeTerraForged — 1.20.1 Forge Backport

A private backport of [FreeTerraForged](https://github.com/ETcodehome/FreeTerraForged) 1.0.0
(originally NeoForge, Minecraft 1.21.1) to **Forge, Minecraft 1.20.1**.

This exists purely to run FTF's terrain generation on an existing modded Forge 1.20.1 server
(a personal SMMP world) that can't move to 1.21.1 due to other mod dependencies. It is **not**
an official release and is not affiliated with, endorsed by, or supported by the FreeTerraForged
project or any of the projects it builds on. It's shared publicly as-is, for anyone in a similar
situation, but is maintained for one private server's own needs first — no support is provided,
and there's no guarantee of keeping pace with upstream beyond what that server needs.

**If you don't specifically need Forge 1.20.1, use the real
[FreeTerraForged](https://github.com/ETcodehome/FreeTerraForged) instead** — it's the actively
maintained project, on a current Minecraft version, with real support.

## Status

Terrain generation has been extensively validated against the real NeoForge 1.21.1 release,
same seed / same preset, including:

- Continents, mountains, rivers, coastlines — match
- Vanilla biome placement, climate, and ore/feature placement — match
- FTF's dynamic ore height-remapping system — match
- Strata rock-type banding (granite/andesite/diorite/stone) — match, after tracking down a
  Forge-vs-NeoForge tag-resolution-order difference (see commit history)
- Preset editor GUI, live preview, and preset import — working
- River flow-field sync to clients (boat currents) — reimplemented for 1.20.1's older
  networking API, since it doesn't exist yet in this Minecraft version
- [Biolith](https://modrinth.com/mod/biolith) compat (preset-preview integration) — working,
  targeting Biolith's only Forge 1.20.1 build (`1.0.1-beta.1`)
- TerraBlender compat — working
- Pulled fix from upstream PR #248 (Biome Replacer mixin-priority fix)

**Known gaps**, both deliberate:
- Biolith support beyond the preview GUI needs no extra code (Biolith and FTF each hook vanilla's
  biome system independently) — only the preview-accuracy piece needed porting
- Structures and full modpack integration testing are less thoroughly verified than terrain
  generation itself

## Credits

This wouldn't exist without the chain of projects it's built on:

- **[FreeTerraForged](https://github.com/ETcodehome/FreeTerraForged)** (ETcodehome, squinch,
  and contributors) — the current, actively-maintained fork this backport tracks
- **[ReTerraForged](https://github.com/racoonman2/ReTerraForged)** (racoonman2) — substantial
  post-1.19 continuation work FTF builds on
- **[NeoTerraForged](https://github.com/equalizer32/NeoTerraForged)** (equalizer32) — the
  NeoForge port groundwork
- **[TerraForged](https://github.com/TerraForged/TerraForged)** (dags, Won-Ton) — the original
  project

All original logic is theirs; this repository only adapts it to run on an older platform.

## License

MIT, inherited from upstream FreeTerraForged (see `LICENSE`).

## Building

Requires Forge `47.1.30` for Minecraft `1.20.1`.

```
./gradlew build
```

Output jar: `forge/build/libs/freeterraforged-<version>-forge-1.20.1.jar`

Biolith compat requires [Biolith Forge `1.0.1-beta.1`](https://modrinth.com/mod/biolith/versions?g=1.20.1&l=forge)
installed alongside — the only Forge build Biolith has released for 1.20.1.

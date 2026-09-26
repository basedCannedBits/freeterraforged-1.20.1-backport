# FreeTerraForged — 1.20.1 Forge Backport

Backport of [FreeTerraForged](https://github.com/ETcodehome/FreeTerraForged) 1.0.0 (normally NeoForge 1.21.1) to Forge 1.20.1.

Made this to run FTF on my own modded 1.20.1 server, since I can't move it to 1.21.1 yet. Not official, not affiliated with the real FTF project, just a personal backport I'm sharing in case it helps someone else in the same spot.

If you don't specifically need 1.20.1, just use [real FreeTerraForged](https://github.com/ETcodehome/FreeTerraForged) instead — it's actively maintained and you'll get actual support.

## Status

Mostly working and tested against the real 1.21.1 release, but this is still experimental. Terrain, biomes, ores, rivers, Biolith and TerraBlender compat all seem to work. There's probably still bugs I haven't hit yet — structures and full modpack testing haven't been stress-tested much.

Use at your own risk, no promises, no support.

## Credits

Built on top of a long chain of people's work:

- [FreeTerraForged](https://github.com/ETcodehome/FreeTerraForged) — ETcodehome, squinch, contributors
- [ReTerraForged](https://github.com/racoonman2/ReTerraForged) — racoonman2
- [NeoTerraForged](https://github.com/equalizer32/NeoTerraForged) — equalizer32
- [TerraForged](https://github.com/TerraForged/TerraForged) — dags, Won-Ton

All credit for the actual terrain gen goes to them. This repo just makes it run on an older platform.

## License

MIT, same as upstream.

## Building

Needs Forge 47.1.30 for 1.20.1.

```
./gradlew build
```

Jar shows up in `forge/build/libs/`.

Biolith compat needs [Biolith Forge 1.0.1-beta.1](https://modrinth.com/mod/biolith/versions?g=1.20.1&l=forge) installed too — only Forge build they've got for 1.20.1.

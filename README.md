# FreeTerraForged — 1.20.1 Forge Backport

Backport of [FreeTerraForged](https://github.com/ETcodehome/FreeTerraForged) 1.0.0 (NeoForge & Fabric 1.21.1) to Forge 1.20.1.

Made this to run FTF on my 1.20.1 server, since I can't move it to 1.21.1 yet. Not official, not affiliated with the real FTF project.

If you don't specifically need 1.20.1, just use [real FreeTerraForged](https://github.com/ETcodehome/FreeTerraForged) instead

## Status

Mostly working and tested against the real 1.21.1 release, but this is still experimental. Terrain, biomes, ores, rivers, Biolith and TerraBlender compat all seem to work. There's probably still bugs I haven't hit yet, feel free to test.

Use at your own risk, no promises everything will work.

## Credits

Built on top of a long chain of people's work:

- [FreeTerraForged](https://github.com/ETcodehome/FreeTerraForged) — ETcodehome, squinch, contributors
- [ReTerraForged](https://github.com/racoonman2/ReTerraForged) — racoonman2
- [NeoTerraForged](https://github.com/equalizer32/NeoTerraForged) — equalizer32
- [TerraForged](https://github.com/TerraForged/TerraForged) — dags, Won-Ton

All credits due

## License

MIT

## Building

Needs Forge 47.1.30+ for 1.20.1.

```
./gradlew build
```

Jar shows up in `forge/build/libs/`.

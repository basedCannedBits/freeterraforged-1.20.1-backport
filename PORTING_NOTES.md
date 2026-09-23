# FTF -> 1.20.1 Forge backport notes

Base: equalizer32/NeoTerraForged `1.20.1` @ 2c029ca (RTF 0.0.6, what Kanned actually built; Forge pinned to 47.1.30)
Upstream: ETcodehome/FreeTerraForged `1.21.1_v1.0.0` @ dfa4368
Common ancestor: cabeff6 (NTF 1.21.1 "Removed unused classes"). NTF's own 1.21->1.20.1 backport
(cabeff6 -> 2c029ca) is the recipe reused here.

Method: FTF renamed back to RTF identity (branch in history), then a real 3-way git merge into NTF 1.20.1.
Non-conflicting NTF backport changes merged automatically; conflicts resolved to FTF logic + 1.20.1 rules.

Applied 1.20.1 rules: MapCodec->Codec (dispatch types), BootstrapContext->BootstapContext,
ResourceLocation factories->constructor, ChunkStatus package, DataResult.getOrThrow, GUI (WidgetList ctor,
setLeftPos, renderBackground, mouseScrolled 3-arg, SystemToastIds), fillFromNoise Executor param,
Math.clamp->Mth.clamp, mixin compat level JAVA_17. fabric/ and neoforge/ modules dropped (Forge only).

Parked (backport-parked/, not compiled):
- biolith/: FTF Biolith compat targets Biolith 3.x internals. Stubbed as disabled.
- flowfield/: PlayerChunkSender mixin (class is 1.20.2+) + 1.21 payload networking for river flow sync.
  TODO: re-implement via ChunkMap#playerLoadedChunk + Forge SimpleChannel. Until then clients don't
  receive river flow data (boat currents / debug render client-side).

Known leftovers for the compiler to surface: remaining Java 21 API (List.getFirst/getLast/removeFirst),
vanilla mixin target signatures, forge/ module vs new common API.

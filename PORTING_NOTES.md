# FTF -> 1.20.1 Forge backport notes

used equalizer32/NeoTerraForged `1.20.1` @ 2c029ca as the base (FTF 0.0.6, what I actually built on, forge pinned to 47.1.30)
upstream is ETcodehome/FreeTerraForged `1.21.1_v1.0.0` @ dfa4368
common ancestor is cabeff6 (NTF 1.21.1 "Removed unused classes"), reused NTF's own 1.21 to 1.20.1 backport recipe (cabeff6 -> 2c029ca)

renamed FTF back to FTF identity then did a real 3 way git merge into NTF 1.20.1
non conflicting NTF backport changes merged automatically, conflicts resolved to FTF logic plus 1.20.1 rules

1.20.1 rules applied: MapCodec -> Codec (dispatch types), BootstrapContext -> BootstapContext, ResourceLocation factories -> constructor, ChunkStatus package, DataResult.getOrThrow, gui stuff (WidgetList ctor, setLeftPos, renderBackground, mouseScrolled 3 arg, SystemToastIds), fillFromNoise executor param, Math.clamp -> Mth.clamp, mixin compat level java 17, dropped fabric and neoforge modules since its forge only

biolith compat and river flow sync to clients are both done now, used to be parked/stubbed early on but not anymore

known leftovers for the compiler to catch if anything shifts: remaining java 21 api stuff, vanilla mixin target signatures, forge module vs new common api

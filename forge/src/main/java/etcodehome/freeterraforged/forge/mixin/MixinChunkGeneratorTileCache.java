package etcodehome.freeterraforged.forge.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import etcodehome.freeterraforged.world.worldgen.GeneratorContext;
import etcodehome.freeterraforged.world.worldgen.FTFRandomState;
import etcodehome.freeterraforged.world.worldgen.densityfunction.tile.TileCache;

/**
 * 1.20.1 Forge replacement for FTF's MixinChunkStatusTasks (1.21's ChunkStatusTasks doesn't exist here) and
 * NTF's MixinChunkStatus, which targeted Fabric lambda names that never match on Forge (require = 0 hid that).
 * Queues the terrain tile when a chunk starts structure generation and drops it after feature decoration,
 * hooking the real ChunkGenerator methods those ChunkStatus steps call.
 */
@Mixin(ChunkGenerator.class)
public class MixinChunkGeneratorTileCache {

	@Inject(method = "createStructures", at = @At("HEAD"))
	private void freeterraforged$queueTile(RegistryAccess registryAccess, ChunkGeneratorStructureState structureState, StructureManager structureManager, ChunkAccess chunk, StructureTemplateManager templateManager, CallbackInfo callback) {
		TileCache cache = freeterraforged$cache(structureState.randomState());
		if (cache != null) {
			ChunkPos chunkPos = chunk.getPos();
			cache.queueAtChunk(chunkPos.x, chunkPos.z);
		}
	}

	@Inject(method = "applyBiomeDecoration", at = @At("TAIL"))
	private void freeterraforged$dropTile(WorldGenLevel level, ChunkAccess chunk, StructureManager structureManager, CallbackInfo callback) {
		TileCache cache = freeterraforged$cache(level.getLevel().getChunkSource().randomState());
		if (cache != null) {
			ChunkPos chunkPos = chunk.getPos();
			cache.dropAtChunk(chunkPos.x, chunkPos.z);
		}
	}

	@Nullable
	private static TileCache freeterraforged$cache(RandomState randomState) {
		if ((Object) randomState instanceof FTFRandomState rtfRandomState) {
			GeneratorContext context = rtfRandomState.generatorContext();
			return context != null ? context.cache : null;
		}
		return null;
	}
}

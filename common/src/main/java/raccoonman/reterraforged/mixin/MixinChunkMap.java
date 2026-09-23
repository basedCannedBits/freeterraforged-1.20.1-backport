package raccoonman.reterraforged.mixin;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

import raccoonman.reterraforged.world.worldgen.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.RTFWorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.datafixers.DataFixer;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.entity.ChunkStatusUpdateListener;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;

@Mixin(ChunkMap.class)
public class MixinChunkMap {
	@Shadow
    private RandomState randomState;

	// 1.20.1 backport: FTF injects a static handler at the very start of the constructor, which needs a newer
	// Mixin than Forge 47.1.30 ships (0.8.5 rejects it: "'static' modifier of handler method does not match").
	// Setting the flag right before the RandomState is created is equivalent, since that's what reads it.
	@Inject(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/RandomState;create(Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/core/HolderGetter;J)Lnet/minecraft/world/level/levelgen/RandomState;"
		),
		method = "<init>"
	)
	private void beforeChunkMapInit(ServerLevel serverLevel, LevelStorageSource.LevelStorageAccess storageAccess, DataFixer dataFixer, StructureTemplateManager templateLoader, Executor executor, BlockableEventLoop<Runnable> eventLoop, LightChunkGetter lightChunkGetter, ChunkGenerator chunkGenerator, ChunkProgressListener chunkProgressListener, ChunkStatusUpdateListener chunkStatusListener, Supplier<DimensionDataStorage> dimensionStorage, int viewDistance, boolean syncChunkWrites, CallbackInfo callback) {
		RTFWorldGenContext.IS_VANILLA_OVERWORLD.set(serverLevel.dimension() == Level.OVERWORLD);
	}

	@Inject(
		at = @At("TAIL"),
		method = "<init>"
	)
	public void ChunkMap(ServerLevel serverLevel, LevelStorageSource.LevelStorageAccess storageAccess, DataFixer dataFixer, StructureTemplateManager templateLoader, Executor executor, BlockableEventLoop<Runnable> eventLoop, LightChunkGetter lightChunkGetter, ChunkGenerator chunkGenerator, ChunkProgressListener chunkProgressListener, ChunkStatusUpdateListener chunkStatusListener, Supplier<DimensionDataStorage> dimensionStorage, int viewDistance, boolean syncChunkWrites, CallbackInfo callback) {
		if((Object) this.randomState instanceof RTFRandomState ftfRandomState) {
			ftfRandomState.initialize(serverLevel.registryAccess());
		}
		RTFWorldGenContext.IS_VANILLA_OVERWORLD.remove();
	}
}

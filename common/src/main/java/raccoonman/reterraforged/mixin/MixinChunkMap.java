package raccoonman.reterraforged.mixin;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

import raccoonman.reterraforged.world.worldgen.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.RTFWorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;
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
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;

@Mixin(ChunkMap.class)
public class MixinChunkMap {
	@Shadow
    private RandomState randomState;

	@Shadow
	@Final
	ServerLevel level;

	// 1.20.1 backport: FTF sets this flag from a static handler at the very start of the constructor, which
	// needs a newer Mixin than Forge 47.1.30 ships (0.8.5 only allows @Inject at RETURN/TAIL in constructors).
	// A @Redirect around RandomState.create is allowed there and scopes the flag to exactly the call that reads it
	// (this.level is assigned earlier in the constructor).
	@Redirect(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/RandomState;create(Lnet/minecraft/world/level/levelgen/NoiseGeneratorSettings;Lnet/minecraft/core/HolderGetter;J)Lnet/minecraft/world/level/levelgen/RandomState;"
		)
	)
	private RandomState reterraforged$createRandomState(NoiseGeneratorSettings settings, HolderGetter<NormalNoise.NoiseParameters> noiseParameters, long seed) {
		RTFWorldGenContext.IS_VANILLA_OVERWORLD.set(this.level.dimension() == Level.OVERWORLD);
		try {
			return RandomState.create(settings, noiseParameters, seed);
		} finally {
			RTFWorldGenContext.IS_VANILLA_OVERWORLD.remove();
		}
	}

	@Inject(
		at = @At("TAIL"),
		method = "<init>"
	)
	public void ChunkMap(ServerLevel serverLevel, LevelStorageSource.LevelStorageAccess storageAccess, DataFixer dataFixer, StructureTemplateManager templateLoader, Executor executor, BlockableEventLoop<Runnable> eventLoop, LightChunkGetter lightChunkGetter, ChunkGenerator chunkGenerator, ChunkProgressListener chunkProgressListener, ChunkStatusUpdateListener chunkStatusListener, Supplier<DimensionDataStorage> dimensionStorage, int viewDistance, boolean syncChunkWrites, CallbackInfo callback) {
		if((Object) this.randomState instanceof RTFRandomState ftfRandomState) {
			ftfRandomState.initialize(serverLevel.registryAccess());
		}
	}
}

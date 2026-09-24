package etcodehome.freeterraforged.mixin;

import java.util.concurrent.Executor;
import java.util.function.Supplier;

import etcodehome.freeterraforged.world.worldgen.FTFRandomState;
import etcodehome.freeterraforged.world.worldgen.FTFWorldGenContext;
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
	private RandomState freeterraforged$createRandomState(NoiseGeneratorSettings settings, HolderGetter<NormalNoise.NoiseParameters> noiseParameters, long seed) {
		FTFWorldGenContext.IS_VANILLA_OVERWORLD.set(this.level.dimension() == Level.OVERWORLD);
		try {
			return RandomState.create(settings, noiseParameters, seed);
		} finally {
			FTFWorldGenContext.IS_VANILLA_OVERWORLD.remove();
		}
	}

	@Inject(
		at = @At("TAIL"),
		method = "<init>"
	)
	public void ChunkMap(ServerLevel serverLevel, LevelStorageSource.LevelStorageAccess storageAccess, DataFixer dataFixer, StructureTemplateManager templateLoader, Executor executor, BlockableEventLoop<Runnable> eventLoop, LightChunkGetter lightChunkGetter, ChunkGenerator chunkGenerator, ChunkProgressListener chunkProgressListener, ChunkStatusUpdateListener chunkStatusListener, Supplier<DimensionDataStorage> dimensionStorage, int viewDistance, boolean syncChunkWrites, CallbackInfo callback) {
		if((Object) this.randomState instanceof FTFRandomState ftfRandomState) {
			ftfRandomState.initialize(serverLevel.registryAccess());
		}
	}

	// 1.20.1 backport: real FTF (1.21.1+) hooks PlayerChunkSender.sendChunk, a class that doesn't exist in
	// 1.20.1. playerLoadedChunk is the equivalent point in this version's pipeline -- it fires once per
	// player per chunk actually sent to them, with the same information available (player, chunk).
	@Inject(
		at = @At("TAIL"),
		method = "playerLoadedChunk"
	)
	private void freeterraforged$onPlayerLoadedChunk(net.minecraft.server.level.ServerPlayer serverPlayer, org.apache.commons.lang3.mutable.MutableObject<net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket> packetHolder, net.minecraft.world.level.chunk.LevelChunk levelChunk, CallbackInfo ci) {
		if (levelChunk instanceof etcodehome.freeterraforged.world.worldgen.IFlowFieldHolder holder) {
			etcodehome.freeterraforged.world.worldgen.ChunkFlowField flowField = holder.freeterraforged$getFlowField();
			if (flowField != null && flowField.hasRivers()) {
				etcodehome.freeterraforged.platform.FTFNetworkUtil.sendFlowFieldSync(serverPlayer, levelChunk.getPos(), flowField.getRawGrid());
			}
		}
	}
}

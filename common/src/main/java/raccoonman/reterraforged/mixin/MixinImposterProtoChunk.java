package raccoonman.reterraforged.mixin;

import raccoonman.reterraforged.world.worldgen.RTFChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;

@Mixin(ImposterProtoChunk.class)
public abstract class MixinImposterProtoChunk implements RTFChunk {

	@Inject(
		method = "<init>",
		at = @At("TAIL")
	)
	public void init(LevelChunk levelChunk, boolean bl, CallbackInfo callback) {
		RTFChunk ftfChunk = (RTFChunk) levelChunk;
		ftfChunk.getMaxHeight().ifPresent(this::setMaxHeight);
	}
}

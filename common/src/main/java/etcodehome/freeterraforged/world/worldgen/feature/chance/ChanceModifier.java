package etcodehome.freeterraforged.world.worldgen.feature.chance;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import etcodehome.freeterraforged.registries.FTFBuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public interface ChanceModifier {
	public static final Codec<ChanceModifier> CODEC = FTFBuiltInRegistries.CHANCE_MODIFIER_TYPE.byNameCodec().dispatch(ChanceModifier::codec, Function.identity());
	
	float getChance(ChanceContext chanceCtx, FeaturePlaceContext<?> placeCtx);
	
	Codec<? extends ChanceModifier> codec();
}

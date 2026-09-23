package etcodehome.freeterraforged.world.worldgen.structure.rule;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import etcodehome.freeterraforged.registries.FTFBuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.RandomState;

public interface StructureRule {
    public static final Codec<StructureRule> DIRECT_CODEC = FTFBuiltInRegistries.STRUCTURE_RULE_TYPE.byNameCodec().dispatch(StructureRule::codec, Function.identity());

	boolean test(RandomState randomState, BlockPos pos);
	
	Codec<? extends StructureRule> codec();
}

package raccoonman.reterraforged.world.worldgen.structure.rule;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import com.mojang.serialization.MapCodec;
import raccoonman.reterraforged.registries.RTFBuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.RandomState;

public interface StructureRule {
    public static final Codec<StructureRule> DIRECT_CODEC = RTFBuiltInRegistries.STRUCTURE_RULE_TYPE.byNameCodec().dispatch(StructureRule::codec, Function.identity());

	boolean test(RandomState randomState, BlockPos pos);
	
	MapCodec<? extends StructureRule> codec();
}

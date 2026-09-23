package etcodehome.freeterraforged.world.worldgen.noise.function;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import etcodehome.freeterraforged.registries.FTFBuiltInRegistries;

public interface CurveFunction {
    public static final Codec<CurveFunction> CODEC = FTFBuiltInRegistries.CURVE_FUNCTION_TYPE.byNameCodec().dispatch(CurveFunction::codec, Function.identity());
	
	float apply(float f);
	
	Codec<? extends CurveFunction> codec();
}

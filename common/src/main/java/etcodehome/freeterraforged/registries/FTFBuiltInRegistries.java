package etcodehome.freeterraforged.registries;

import com.mojang.serialization.Codec;
import etcodehome.freeterraforged.world.worldgen.biome.modifier.BiomeModifier;
import etcodehome.freeterraforged.world.worldgen.noise.domain.Domain;
import etcodehome.freeterraforged.world.worldgen.noise.function.CurveFunction;
import net.minecraft.core.Registry;
import etcodehome.freeterraforged.platform.RegistryUtil;
import etcodehome.freeterraforged.world.worldgen.feature.chance.ChanceModifier;
import etcodehome.freeterraforged.world.worldgen.feature.template.decorator.TemplateDecorator;
import etcodehome.freeterraforged.world.worldgen.feature.template.placement.TemplatePlacement;
import etcodehome.freeterraforged.world.worldgen.noise.module.Noise;
import etcodehome.freeterraforged.world.worldgen.structure.rule.StructureRule;

public class FTFBuiltInRegistries {
	public static final Registry<Codec<? extends Noise>> NOISE_TYPE = RegistryUtil.createRegistry(FTFRegistries.NOISE_TYPE);
	public static final Registry<Codec<? extends Domain>> DOMAIN_TYPE = RegistryUtil.createRegistry(FTFRegistries.DOMAIN_TYPE);
	public static final Registry<Codec<? extends CurveFunction>> CURVE_FUNCTION_TYPE = RegistryUtil.createRegistry(FTFRegistries.CURVE_FUNCTION_TYPE);
	public static final Registry<Codec<? extends ChanceModifier>> CHANCE_MODIFIER_TYPE = RegistryUtil.createRegistry(FTFRegistries.CHANCE_MODIFIER_TYPE);
	public static final Registry<Codec<? extends TemplatePlacement<?>>> TEMPLATE_PLACEMENT_TYPE = RegistryUtil.createRegistry(FTFRegistries.TEMPLATE_PLACEMENT_TYPE);
	public static final Registry<Codec<? extends TemplateDecorator<?>>> TEMPLATE_DECORATOR_TYPE = RegistryUtil.createRegistry(FTFRegistries.TEMPLATE_DECORATOR_TYPE);
	public static final Registry<Codec<? extends BiomeModifier>> BIOME_MODIFIER_TYPE = RegistryUtil.createRegistry(FTFRegistries.BIOME_MODIFIER_TYPE);
	public static final Registry<Codec<? extends StructureRule>> STRUCTURE_RULE_TYPE = RegistryUtil.createRegistry(FTFRegistries.STRUCTURE_RULE_TYPE);

	public static void bootstrap() {
	}
}

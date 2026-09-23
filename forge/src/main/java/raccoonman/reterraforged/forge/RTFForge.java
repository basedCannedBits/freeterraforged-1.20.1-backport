package raccoonman.reterraforged.forge;

import com.mojang.serialization.Codec;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.client.data.RTFLanguageProvider;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.platform.forge.RegistryUtilImpl;
import raccoonman.reterraforged.server.RTFMinecraftServer;
import raccoonman.reterraforged.world.worldgen.biome.modifier.forge.AddModifier;
import raccoonman.reterraforged.world.worldgen.biome.modifier.forge.ReplaceModifier;

@Mod(RTFCommon.MOD_ID)
public class RTFForge {

	public RTFForge() {
		RTFCommon.bootstrap();

		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		// FTF keeps its own reterraforged:biome_modifier_serializers dispatch registry (for data-gen);
		// Forge also needs the codecs in forge:biome_modifier_serializers to decode forge/biome_modifier/*.json.
		DeferredRegister<Codec<? extends BiomeModifier>> biomeModifierSerializers = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, RTFCommon.MOD_ID);
		biomeModifierSerializers.register("add", () -> AddModifier.CODEC);
		biomeModifierSerializers.register("replace", () -> ReplaceModifier.CODEC);
		biomeModifierSerializers.register(modBus);

		if (FMLEnvironment.dist == Dist.CLIENT) {
			modBus.addListener(RTFForgeClient::registerPresetEditors);
		}
		modBus.addListener(RTFForge::gatherData);

		// 1.20.1 backport: reload feature templates on /reload via Forge's event instead of a
		// mixin into MinecraftServer's reloadResources lambda (lambda names differ on Forge).
		MinecraftForge.EVENT_BUS.addListener(RTFForge::addReloadListeners);

		RegistryUtilImpl.register(modBus);
	}

	private static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener((ResourceManagerReloadListener) (resourceManager) -> {
			MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
			if (server instanceof RTFMinecraftServer rtfServer && rtfServer.getFeatureTemplateManager() != null) {
				rtfServer.getFeatureTemplateManager().onReload(resourceManager);
			}
		});
	}

	private static void gatherData(GatherDataEvent event) {
		boolean includeClient = event.includeClient();
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();

		generator.addProvider(includeClient, new RTFLanguageProvider.EnglishUS(output));
		generator.addProvider(includeClient, PackMetadataGenerator.forFeaturePack(output, Component.translatable(RTFTranslationKeys.METADATA_DESCRIPTION)));
	}
}

package etcodehome.freeterraforged.forge;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import etcodehome.freeterraforged.FTFCommon;
import etcodehome.freeterraforged.client.data.FTFLanguageProvider;
import etcodehome.freeterraforged.client.data.FTFTranslationKeys;
import etcodehome.freeterraforged.data.worldgen.Datapacks;
import etcodehome.freeterraforged.data.worldgen.preset.settings.Preset;
import etcodehome.freeterraforged.data.worldgen.preset.settings.Presets;
import etcodehome.freeterraforged.platform.forge.RegistryUtilImpl;
import etcodehome.freeterraforged.server.FTFMinecraftServer;
import etcodehome.freeterraforged.world.worldgen.biome.modifier.forge.AddModifier;
import etcodehome.freeterraforged.world.worldgen.biome.modifier.forge.ReplaceModifier;

@Mod(FTFCommon.MOD_ID)
public class FTFForge {

	public FTFForge() {
		FTFCommon.bootstrap();

		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		// FTF keeps its own freeterraforged:biome_modifier_serializers dispatch registry (for data-gen);
		// Forge also needs the codecs in forge:biome_modifier_serializers to decode forge/biome_modifier/*.json.
		DeferredRegister<Codec<? extends BiomeModifier>> biomeModifierSerializers = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, FTFCommon.MOD_ID);
		biomeModifierSerializers.register("add", () -> AddModifier.CODEC);
		biomeModifierSerializers.register("replace", () -> ReplaceModifier.CODEC);
		biomeModifierSerializers.register(modBus);

		if (FMLEnvironment.dist == Dist.CLIENT) {
			modBus.addListener(FTFForgeClient::registerPresetEditors);
		}
		modBus.addListener(FTFForge::gatherData);

		// 1.20.1 backport: reload feature templates on /reload via Forge's event instead of a
		// mixin into MinecraftServer's reloadResources lambda (lambda names differ on Forge).
		MinecraftForge.EVENT_BUS.addListener(FTFForge::addReloadListeners);

		// Backport testing aid: -Dfreeterraforged.exportDefaultPreset=<dir> writes the default preset as a
		// datapack folder when a server starts, so FTF terrain can be tested on a headless server.
		if (System.getProperty(EXPORT_PRESET_PROPERTY) != null) {
			MinecraftForge.EVENT_BUS.addListener(FTFForge::exportDefaultPreset);
		}

		RegistryUtilImpl.register(modBus);

		etcodehome.freeterraforged.network.FTFForgeNetwork.register();
	}

	private static final String EXPORT_PRESET_PROPERTY = "freeterraforged.exportDefaultPreset";

	private static void exportDefaultPreset(ServerStartedEvent event) {
		Path outputPath = Paths.get(System.getProperty(EXPORT_PRESET_PROPERTY)).toAbsolutePath();
		try {
			Path datagenPath = Files.createTempDirectory("rtf-preset-export-");
			Preset preset = Presets.makeFTFDefault();
			String presetName = "FTF Default (backport test)";
			// Optional: -Dfreeterraforged.exportPresetJson=<file> exports a saved preset (e.g. an old RTF 0.0.6 one) instead
			String presetJson = System.getProperty("freeterraforged.exportPresetJson");
			if (presetJson != null) {
				Path presetPath = Paths.get(presetJson).toAbsolutePath();
				try (java.io.Reader reader = Files.newBufferedReader(presetPath)) {
					com.mojang.serialization.DataResult<Preset> result = Preset.DIRECT_CODEC.parse(com.mojang.serialization.JsonOps.INSTANCE, com.google.gson.JsonParser.parseReader(reader));
					result.error().ifPresent(error -> FTFCommon.LOGGER.error("Preset {} did not parse cleanly: {}", presetPath, error.message()));
					preset = result.result().orElseThrow(() -> new IllegalStateException("Preset " + presetPath + " could not be loaded"));
					presetName = presetPath.getFileName().toString();
					FTFCommon.LOGGER.info("Loaded preset {} for export", presetPath);
				}
			}
			DataGenerator generator = Datapacks.makePreset(preset, event.getServer().registryAccess(), datagenPath, outputPath, presetName);
			generator.run();
			FTFCommon.LOGGER.info("Exported default preset datapack to {}", outputPath);
		} catch (Exception e) {
			FTFCommon.LOGGER.error("Failed to export default preset datapack to {}", outputPath, e);
		}
	}

	private static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener((ResourceManagerReloadListener) (resourceManager) -> {
			MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
			if (server instanceof FTFMinecraftServer rtfServer && rtfServer.getFeatureTemplateManager() != null) {
				rtfServer.getFeatureTemplateManager().onReload(resourceManager);
			}
		});
	}

	private static void gatherData(GatherDataEvent event) {
		boolean includeClient = event.includeClient();
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();

		generator.addProvider(includeClient, new FTFLanguageProvider.EnglishUS(output));
		generator.addProvider(includeClient, PackMetadataGenerator.forFeaturePack(output, Component.translatable(FTFTranslationKeys.METADATA_DESCRIPTION)));
	}
}

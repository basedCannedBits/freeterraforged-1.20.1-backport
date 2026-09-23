package etcodehome.freeterraforged.platform.forge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DataPackRegistriesHooks;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryBuilder;
import etcodehome.freeterraforged.FTFCommon;

/**
 * Forge 1.20.1 implementation of FTF's RegistryUtil platform API
 * (ported from FTF's NeoForge RegistryUtilImpl, using NTF's DeferredRegistry wrapper for custom registries).
 */
public final class RegistryUtilImpl {
	private static final List<DataRegistry<?>> DATA_REGISTRIES = Collections.synchronizedList(new ArrayList<>());
	private static final Map<ResourceKey<?>, DeferredRegister<?>> REGISTERS = new ConcurrentHashMap<>();

	public static void register(IEventBus bus) {
		bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
			DATA_REGISTRIES.forEach((registry) -> registry.register(event));
		});
		REGISTERS.values().forEach((register) -> register.register(bus));
	}

	public static <T> void register(Registry<T> registry, String name, T value) {
		getRegister(registry.key()).register(name, () -> value);
	}

	public static <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {
		DeferredRegister<T> register = getRegister(key);
		register.makeRegistry(() -> new RegistryBuilder<T>().hasTags());
		return DeferredRegistry.memoize(key, () -> GameData.getWrapper(key, Lifecycle.stable()));
	}

	public static <T> void createDataRegistry(ResourceKey<Registry<T>> key, Codec<T> codec, boolean synced) {
		DATA_REGISTRIES.add(new DataRegistry<>(key, codec, synced));
	}

	public static <T extends GameRules.Value<T>> GameRules.Key<T> registerGameRule(String name, GameRules.Category category, GameRules.Type<T> type) {
		// Unused by FTF's common code at v1.0.0; vanilla GameRules.register is private on 1.20.1.
		throw new UnsupportedOperationException("registerGameRule is not implemented on Forge 1.20.1");
	}

	public static List<RegistryDataLoader.RegistryData<?>> getDynamicRegistries() {
		return DataPackRegistriesHooks.getDataPackRegistries();
	}

	@SuppressWarnings("unchecked")
	private static <T> DeferredRegister<T> getRegister(ResourceKey<? extends Registry<T>> key) {
		return (DeferredRegister<T>) REGISTERS.computeIfAbsent(key, (k) -> DeferredRegister.create(key, FTFCommon.MOD_ID));
	}

	private record DataRegistry<T>(ResourceKey<Registry<T>> key, Codec<T> codec, boolean synced) {

		public void register(DataPackRegistryEvent.NewRegistry event) {
			event.dataPackRegistry(this.key, this.codec, this.synced ? this.codec : null);
		}
	}
}

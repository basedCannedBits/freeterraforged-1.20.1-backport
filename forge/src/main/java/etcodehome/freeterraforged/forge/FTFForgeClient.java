package etcodehome.freeterraforged.forge;

import net.minecraft.world.level.levelgen.presets.WorldPresets;
import net.minecraftforge.client.event.RegisterPresetEditorsEvent;
import etcodehome.freeterraforged.client.gui.screen.presetconfig.PresetConfigScreen;

class FTFForgeClient {

	public static void registerPresetEditors(RegisterPresetEditorsEvent event) {
		// TODO we probably shouldn't register this for the default preset
		event.register(WorldPresets.NORMAL, (screen, ctx) -> new PresetConfigScreen(screen));
	}
}
package yuki.snowautoselect.client;

import yuki.snowautoselect.SnowAutoSelect;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

public class SnowAutoSelectClient implements ClientModInitializer {
	public static boolean enabled = true;
	private static KeyMapping toggleKey;

	@Override
	public void onInitializeClient() {
		toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
			"key.snowautoselect.toggle",
			GLFW.GLFW_KEY_H,
			KeyMapping.Category.MISC
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleKey.consumeClick()) {
				enabled = !enabled;
				if (client.player != null) {
					client.player.sendSystemMessage(Component.literal(
						"§b[SnowAutoSelect] §7Auto-accept: " + (enabled ? "§aON" : "§cOFF")
					));
				}
			}
		});
	}
}

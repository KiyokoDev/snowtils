package yuki.snowautoselect.client;

import yuki.snowautoselect.SnowAutoSelect;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SkyblockDialogueHandler {
	private static final Set<String> clickedCommands = new HashSet<>();
	private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	public static void handleDialogue(Component message) {
		if (!SnowAutoSelectClient.enabled) return;
		if (Minecraft.getInstance().screen != null) return;

		var player = Minecraft.getInstance().player;
		if (player == null) return;

		processComponent(message);
	}

	private static boolean processComponent(Component component) {
		var style = component.getStyle();
		var clickEvent = style.getClickEvent();

		if (clickEvent instanceof ClickEvent.RunCommand runCmd && isBracketed(component)) {
			String command = runCmd.command();
			String normalised = command.startsWith("/") ? command.substring(1) : command;

			if (clickedCommands.add(normalised)) {
				scheduler.schedule(() -> {
					Minecraft.getInstance().execute(() -> {
						var conn = Minecraft.getInstance().getConnection();
						if (conn != null) {
							SnowAutoSelect.LOGGER.info("[SnowAutoSelect] Auto-accepting dialogue: /{}", normalised);
							conn.sendCommand(normalised);
							var player = Minecraft.getInstance().player;
							if (player != null) {
								player.sendSystemMessage(Component.literal("§b[SnowAutoSelect] §7Clicked first dialogue option."));
							}
						}
					});
				}, 350, TimeUnit.MILLISECONDS);
			}
			return true;
		}

		for (Component child : component.getSiblings()) {
			if (processComponent(child)) return true;
		}
		return false;
	}

	private static boolean isBracketed(Component component) {
		String text = component.getString().replaceAll("§[0-9a-fk-or]", "").trim();
		return text.startsWith("[") && text.endsWith("]");
	}
}

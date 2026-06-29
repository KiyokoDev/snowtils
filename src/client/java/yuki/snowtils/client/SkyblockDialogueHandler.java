package yuki.snowtils.client;

import yuki.snowtils.Snowtils;
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
		if (!SnowtilsClient.enabled) return;
		if (Minecraft.getInstance().screen != null) return;

		var player = Minecraft.getInstance().player;
		if (player == null) return;

		processComponent(message);
	}

	private static boolean processComponent(Component component) {
		return processComponentWithContext(component, "");
	}

	private static boolean processComponentWithContext(Component component, String precedingText) {
		var style = component.getStyle();
		var clickEvent = style.getClickEvent();

		if (clickEvent instanceof ClickEvent.RunCommand runCmd && isBracketed(component)) {
			if (!containsSkippedPhrase(precedingText)) {
				String command = runCmd.command();
				String normalised = command.startsWith("/") ? command.substring(1) : command;

				if (clickedCommands.add(normalised)) {
					scheduler.schedule(() -> {
						Minecraft.getInstance().execute(() -> {
							var conn = Minecraft.getInstance().getConnection();
							if (conn != null) {
								Snowtils.LOGGER.info("[Snowtils] Auto-accepting dialogue: /{}", normalised);
								conn.sendCommand(normalised);
								var player = Minecraft.getInstance().player;
								if (player != null) {
									player.sendSystemMessage(Component.literal("§b[Snowtils] §7Clicked first dialogue option."));
								}
							}
						});
					}, 350, TimeUnit.MILLISECONDS);
				}
				return true;
			}
		}

		String text = component.getString().replaceAll("§[0-9a-fk-or]", "");
		StringBuilder acc = new StringBuilder(precedingText).append(text);
		for (Component child : component.getSiblings()) {
			if (processComponentWithContext(child, acc.toString())) return true;
			acc.append(child.getString().replaceAll("§[0-9a-fk-or]", ""));
		}
		return false;
	}

	private static final Set<String> RANKS = Set.of("VIP", "VIP+", "MVP", "MVP+", "MVP++", "YOUTUBE", "YOUTUBE+");
	private static final Set<String> SKIPPED_PREFIXES = Set.of("is holding ", "is wearing ", "is friends with a ");

	private static boolean isBracketed(Component component) {
		String text = component.getString().replaceAll("§[0-9a-fk-or]", "").trim();
		if (!text.startsWith("[") || !text.endsWith("]")) return false;
		String inner = text.substring(1, text.length() - 1).trim();
		return !RANKS.contains(inner);
	}

	private static boolean containsSkippedPhrase(String text) {
		for (String phrase : SKIPPED_PREFIXES) {
			if (text.contains(phrase)) return true;
		}
		return false;
	}
}

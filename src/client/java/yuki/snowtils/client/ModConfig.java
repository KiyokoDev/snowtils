package yuki.snowtils.client;

import yuki.snowtils.Snowtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH = Minecraft.getInstance().gameDirectory.toPath().resolve("config").resolve("snowtils.json");

	public boolean enabled = true;

	public static ModConfig load() {
		try {
			if (Files.exists(CONFIG_PATH)) {
				try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
					return GSON.fromJson(reader, ModConfig.class);
				}
			}
		} catch (IOException e) {
			Snowtils.LOGGER.warn("Failed to load config", e);
		}
		return new ModConfig();
	}

	public void save() {
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException e) {
			Snowtils.LOGGER.warn("Failed to save config", e);
		}
	}
}

package io.github.ultimateboomer.lowfire;

import com.mojang.blaze3d.platform.InputConstants;
import io.github.ultimateboomer.lowfire.config.LowFireConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;

public class LowFire implements ClientModInitializer {
	public static final String MOD_ID = "lowfire";
	public static final String MOD_NAME = "Low Fire";

	public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

	public static LowFire INSTANCE;

	public LowFireConfig config;
	public ConfigHolder<LowFireConfig> configHolder;

	private static final DecimalFormat df = new DecimalFormat("0.0");


	private static final KeyMapping.Category category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(LowFire.MOD_ID, "key.categories.lowfire"));
	private KeyMapping toggleKey;
	private KeyMapping toggleRenderKey;
	private KeyMapping nextFireOffsetKey;

	@Override
	public void onInitializeClient() {
		INSTANCE = this;

		configHolder = AutoConfig.register(LowFireConfig.class, GsonConfigSerializer::new);
		config = configHolder.getConfig();

		toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.lowfire.toggle",
				InputConstants.Type.KEYSYM,
				-1,
				category
		));

		toggleRenderKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.lowfire.toggleRender",
				InputConstants.Type.KEYSYM,
				-1,
				category
		));

		nextFireOffsetKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
				"key.lowfire.nextFireOffset",
				InputConstants.Type.KEYSYM,
				-1,
				category
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleKey.consumeClick()) {
				config.enabled ^= true;
				configHolder.save();

				if (client.player != null) {
					client.player.displayClientMessage(
							Component.translatable(config.enabled ? "lowfire.toggle.enabled" : "lowfire.toggle.disabled"),
							true
					);
				}
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleRenderKey.consumeClick()) {
				config.renderFire ^= true;
				configHolder.save();

				if (client.player != null) {
					client.player.displayClientMessage(
							Component.translatable(config.renderFire ? "lowfire.toggleRender.enabled" : "lowfire.toggleRender.disabled"),
							true
					);
				}
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (nextFireOffsetKey.consumeClick()) {
				if (config.fireOffset >= 0.5 || config.fireOffset < 0.0) {
					config.fireOffset = 0.0;
				} else {
					config.fireOffset += 0.1;
					config.fireOffset = Math.floor(config.fireOffset * 10) / 10;
				}

				configHolder.save();

				if (client.player != null) {
					client.player.displayClientMessage(
							Component.translatable("lowfire.nextFireOffset", df.format(config.fireOffset)
									.replaceAll("^-(?=0(\\.0*)?$)", "")),
							true
					);
				}
			}
		});
	}
}

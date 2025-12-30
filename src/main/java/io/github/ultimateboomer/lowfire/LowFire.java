package io.github.ultimateboomer.lowfire;

import io.github.ultimateboomer.lowfire.config.LowFireConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
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


	private static final KeyBinding.Category category = KeyBinding.Category.create(Identifier.of(LowFire.MOD_ID, "key.categories.lowfire"));
	private KeyBinding toggleKey;
	private KeyBinding toggleRenderKey;
	private KeyBinding nextFireOffsetKey;

	@Override
	public void onInitializeClient() {
		INSTANCE = this;

		configHolder = AutoConfig.register(LowFireConfig.class, GsonConfigSerializer::new);
		config = configHolder.getConfig();

		toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.lowfire.toggle",
				InputUtil.Type.KEYSYM,
				-1,
				category
		));

		toggleRenderKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.lowfire.toggleRender",
				InputUtil.Type.KEYSYM,
				-1,
				category
		));

		nextFireOffsetKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.lowfire.nextFireOffset",
				InputUtil.Type.KEYSYM,
				-1,
				category
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleKey.wasPressed()) {
				config.enabled ^= true;
				configHolder.save();

				if (client.player != null) {
					client.player.sendMessage(
							Text.translatable(config.enabled ? "lowfire.toggle.enabled" : "lowfire.toggle.disabled"),
							true
					);
				}
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (toggleRenderKey.wasPressed()) {
				config.renderFire ^= true;
				configHolder.save();

				if (client.player != null) {
					client.player.sendMessage(
							Text.translatable(config.renderFire ? "lowfire.toggleRender.enabled" : "lowfire.toggleRender.disabled"),
							true
					);
				}
			}
		});

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (nextFireOffsetKey.wasPressed()) {
				if (config.fireOffset >= 0.5 || config.fireOffset < 0.0) {
					config.fireOffset = 0.0;
				} else {
					config.fireOffset += 0.1;
					config.fireOffset = Math.floor(config.fireOffset * 10) / 10;
				}

				configHolder.save();

				if (client.player != null) {
					client.player.sendMessage(
							Text.translatable("lowfire.nextFireOffset", df.format(config.fireOffset)
									.replaceAll("^-(?=0(\\.0*)?$)", "")),
							true
					);
				}
			}
		});
	}
}

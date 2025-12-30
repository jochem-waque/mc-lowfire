package io.github.ultimateboomer.lowfire.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.ultimateboomer.lowfire.LowFire;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenEffectRenderer.class)
public class InGameOverlayRendererMixin {
	@Inject(method = "renderFire",
			at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
	private static void onRenderFireOverlay(PoseStack matrices, MultiBufferSource vertexConsumers, TextureAtlasSprite sprite, CallbackInfo ci) {
		if (!LowFire.INSTANCE.config.enabled)
			return;

		matrices.translate(0.0, -LowFire.INSTANCE.config.fireOffset, 0.0);
	}

	@Inject(method = "renderFire",
			at = @At("HEAD"),
			cancellable = true)
	private static void onRenderFireOverlay2(PoseStack matrices, MultiBufferSource vertexConsumers, TextureAtlasSprite sprite, CallbackInfo ci) {
		if (LowFire.INSTANCE.config.renderFire)
			return;

		ci.cancel();
	}
}

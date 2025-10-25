package me.kubbidev.fabriclab.mixins.render;

import me.kubbidev.fabriclab.render.debug.EntityDebugRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {

    @Inject(method = "initRenderers()V", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ((DebugRendererAccessor) this).fabriclab$getDebugRenderers().add(new EntityDebugRenderer(minecraftClient));
    }
}

package me.kubbidev.fabriclab.mixins.render;

import me.kubbidev.fabriclab.render.debug.EntityDebugRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {

    @Unique
    private EntityDebugRenderer entityDebugRenderer;

    @Inject(method = "<init>(Lnet/minecraft/client/MinecraftClient;)V", at = @At("RETURN"))
    private void onInit(MinecraftClient client, CallbackInfo ci) {
        this.entityDebugRenderer = new EntityDebugRenderer(client);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Frustum;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;DDD)V", at = @At("RETURN"))
    private void onRenderMain(MatrixStack matrices, net.minecraft.client.render.Frustum frustum,
                              VertexConsumerProvider.Immediate vertexConsumers,
                              double cameraX,
                              double cameraY,
                              double cameraZ,
                              CallbackInfo ci
    ) {
        if (this.entityDebugRenderer != null) {
            this.entityDebugRenderer.render(matrices, vertexConsumers, cameraX, cameraY, cameraZ);
        }
    }

    @Inject(method = "reset()V", at = @At("RETURN"))
    private void onReset(CallbackInfo ci) {
        if (this.entityDebugRenderer != null) {
            this.entityDebugRenderer.clear();
        }
    }
}

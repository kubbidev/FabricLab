package me.kubbidev.fabriclab.mixins;

import net.minecraft.client.render.entity.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
    private void alwaysVisible(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
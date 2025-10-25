package me.kubbidev.fabriclab.mixins.render;

import java.util.List;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.DebugRenderer.Renderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DebugRenderer.class)
public interface DebugRendererAccessor {

    @Accessor("debugRenderers")
    List<Renderer> fabriclab$getDebugRenderers();
}
package me.kubbidev.fabriclab.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.debug.DebugDataStore;

@Environment(EnvType.CLIENT)
public class EntityDebugRenderer implements DebugRenderer.Renderer {

    private final MinecraftClient client;

    public EntityDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ,
                       DebugDataStore store, Frustum frustum
    ) {
        draw(matrices, vertexConsumers);
    }

    private void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        Iterable<Entity> entities = this.client.world.getEntities();
        entities.forEach(entity -> {
            if (this.client.player.isInRange(entity, 30) && entity instanceof LivingEntity) {
                drawPlayer(matrices, vertexConsumers, (LivingEntity) entity);
            }
        });
    }

    private void drawPlayer(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity) {
        int line = 0;
        Team scoreboardTeam = entity.getScoreboardTeam();

        Vec3d entityPos = entity.getEntityPos();
        if (entity.isInvisible()) {
            drawString(matrices, vertexConsumers, entityPos, line++, "INVISIBLE", 0xff09ed20);
        }

        if (scoreboardTeam == null) {
            drawString(matrices, vertexConsumers, entityPos, line++, "No team", 0xfffe7f9c);
        } else {
            drawString(matrices, vertexConsumers, entityPos, line++, "Team: " + scoreboardTeam.getName());
            drawString(matrices, vertexConsumers, entityPos, line++, "Prefix: " + scoreboardTeam.getPrefix().getString());
            drawString(matrices, vertexConsumers, entityPos, line++, "Suffix: " + scoreboardTeam.getSuffix().getString());
            drawString(matrices, vertexConsumers, entityPos, line++, "Color: " + scoreboardTeam.getColor().name());
        }

        drawString(matrices, vertexConsumers, entityPos, line++, "Absorption: " + String.format("%.2f", entity.getAbsorptionAmount()));
        drawString(matrices, vertexConsumers, entityPos, line++, "Max Absorption: " + String.format("%.2f", entity.getMaxAbsorption()));
        drawString(matrices, vertexConsumers, entityPos, line++, "Health: " + String.format("%.2f", entity.getHealth()));
        drawString(matrices, vertexConsumers, entityPos, line++, "Max Health: " + String.format("%.2f", entity.getMaxHealth()));
        RegistryKey<World> world = entity.getEntityWorld().getRegistryKey();
        drawString(matrices, vertexConsumers, entityPos, line++, "World: " + world.getValue().toString());
        drawString(matrices, vertexConsumers, entityPos, line++,
            "Pos: "
                + String.format("%.2f", entityPos.getX()) + ","
                + String.format("%.2f", entityPos.getY()) + ","
                + String.format("%.2f", entityPos.getZ())
        );
        drawString(matrices, vertexConsumers, entityPos, line++, "Id: " + entity.getId());
        drawString(matrices, vertexConsumers, entityPos, line++, "Type: " + entity.getType().getName().getString());
    }

    private static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                   Position pos,
                                   int line,
                                   String string
    ) {
        drawString(matrices, vertexConsumers, pos, line, string, 0xffffff00);
    }

    private static void drawString(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                   Position pos,
                                   int line,
                                   String string,
                                   int color
    ) {
        BlockPos blockPos = BlockPos.ofFloored(pos);
        double f = blockPos.getX() + 0.5;
        double g = pos.getY() + 2.6 + line * 0.25;
        double h = blockPos.getZ() + 0.5;
        DebugRenderer.drawString(matrices, vertexConsumers, string, f, g, h, color, 0.02f, false, 0.5F, true);
    }
}

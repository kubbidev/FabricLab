package me.kubbidev.fabriclab.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;

@Environment(EnvType.CLIENT)
public class EntityDebugRenderer implements DebugRenderer.Renderer {

    private final MinecraftClient client;

    public EntityDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, double cameraX, double cameraY, double cameraZ) {
        render(matrices, vertexConsumers);
    }

    private void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        ClientWorld clientWorld = this.client.world;
        if (clientWorld == null) {
            return;
        }

        clientWorld.getEntities().forEach(entity -> {
            if (isInRange(entity) && entity instanceof LivingEntity) {
                drawPlayer(matrices, vertexConsumers, (LivingEntity) entity);
            }
        });
    }

    private void drawPlayer(MatrixStack matrices, VertexConsumerProvider vertexConsumers, LivingEntity entity) {
        int line = 0;
        Team scoreboardTeam = entity.getScoreboardTeam();
        if (scoreboardTeam == null) {
            drawString(matrices, vertexConsumers, entity.getPos(), line++, "No team", 0xfffe7f9c);
        } else {
            drawString(matrices, vertexConsumers, entity.getPos(), line++, "Team: " + scoreboardTeam.getName());
            drawString(matrices, vertexConsumers, entity.getPos(), line++, "Prefix: " + scoreboardTeam.getPrefix().getString());
            drawString(matrices, vertexConsumers, entity.getPos(), line++, "Suffix: " + scoreboardTeam.getSuffix().getString());
            drawString(matrices, vertexConsumers, entity.getPos(), line++, "Color: " + scoreboardTeam.getColor().name());
        }

        drawString(matrices, vertexConsumers, entity.getPos(), line++, "Health: " + String.format("%.2f", entity.getHealth()));
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

    private boolean isInRange(Entity target) {
        ClientPlayerEntity clientPlayer = this.client.player;
        if (clientPlayer == null) {
            return false;
        }

        BlockPos playerPos = BlockPos.ofFloored(clientPlayer.getX(), target.getPos().getY(), clientPlayer.getZ());
        BlockPos targetPos = BlockPos.ofFloored(target.getPos());
        return playerPos.isWithinDistance(targetPos, 30.0);
    }
}

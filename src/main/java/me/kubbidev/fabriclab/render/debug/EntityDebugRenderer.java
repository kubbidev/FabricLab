package me.kubbidev.fabriclab.render.debug;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

@Environment(EnvType.CLIENT)
public class EntityDebugRenderer implements DebugRenderer.Renderer {

    private final MinecraftClient client;

    public EntityDebugRenderer(MinecraftClient client) {
        this.client = client;
    }

    @Override
    public void render(double cameraX, double cameraY, double cameraZ, DebugDataStore store, Frustum frustum, float tickProgress) {
        draw();
    }

    private void draw() {
        ClientWorld world = this.client.world;
        if (world == null) {
            return;
        }

        ClientPlayerEntity player = this.client.player;
        if (player == null) {
            return;
        }

        Box box = player.getBoundingBox().expand(30);
        for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, box, _ -> true)) {
            drawPlayer(entity);
        }
    }

    private void drawPlayer(LivingEntity target) {
        int line = 0;
        Team scoreboardTeam = target.getScoreboardTeam();
        
        if (target.isInvisible()) {
            drawString(target, line++, "INVISIBLE", 0xff09ed20);
        }

        if (scoreboardTeam == null) {
            drawString(target, line++, "No team", 0xfffe7f9c);
        } else {
            drawString(target, line++, "Team: " + scoreboardTeam.getName());
            drawString(target, line++, "Prefix: " + scoreboardTeam.getPrefix().getString());
            drawString(target, line++, "Suffix: " + scoreboardTeam.getSuffix().getString());
            drawString(target, line++, "Color: " + scoreboardTeam.getColor().name());
        }

        drawString(target, line++, "Absorption: " + String.format("%.2f", target.getAbsorptionAmount()));
        drawString(target, line++, "Max Absorption: " + String.format("%.2f", target.getMaxAbsorption()));
        drawString(target, line++, "Health: " + String.format("%.2f", target.getHealth()));
        drawString(target, line++, "Max Health: " + String.format("%.2f", target.getMaxHealth()));
        RegistryKey<World> world = target.getEntityWorld().getRegistryKey();
        drawString(target, line++, "World: " + world.getValue().toString());

        Vec3d entityPos = target.getEntityPos();
        drawString(target, line++,
            "Pos: "
                + String.format("%.2f", entityPos.getX()) + ","
                + String.format("%.2f", entityPos.getY()) + ","
                + String.format("%.2f", entityPos.getZ())
        );
        drawString(target, line++, "Id: " + target.getId());
        drawString(target, line++, "Type: " + target.getType().getName().getString());
    }

    private static void drawString(Entity entity, int line, String text) {
        drawString(entity, line, text, 0xffffff00);
    }

    private static void drawString(Entity entity, int line, String text, int color) {
        GizmoDrawing.entityLabel(entity, line, text, color, 0.32f);
    }
}

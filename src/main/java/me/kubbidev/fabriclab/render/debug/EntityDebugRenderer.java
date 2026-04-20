package me.kubbidev.fabriclab.render.debug;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.Collection;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.debug.DebugDataStore;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import org.apache.commons.lang3.mutable.MutableInt;

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
            drawLivingEntity(entity);
        }
        for (ItemEntity entity : world.getEntitiesByClass(ItemEntity.class, box, _ -> true)) {
            drawItem(entity);
        }
        for (ItemFrameEntity entity : world.getEntitiesByClass(ItemFrameEntity.class, box, _ -> true)) {
            drawItemFrame(entity);
        }
    }

    private void drawItem(ItemEntity item) {
        MutableInt line = new MutableInt();
        drawEntity(item, line);
        drawItemStack(item, line, item.getStack());
    }

    private void drawItemFrame(ItemFrameEntity itemFrame) {
        MutableInt line = new MutableInt();
        drawEntity(itemFrame, line);
        drawItemStack(itemFrame, line, itemFrame.getHeldItemStack());
    }

    private void drawLivingEntity(LivingEntity livingEntity) {
        MutableInt line = new MutableInt();
        drawEntity(livingEntity, line);
        drawString(livingEntity, line.getAndIncrement(), "Absorption: " + String.format("%.2f", livingEntity.getAbsorptionAmount()));
        drawString(livingEntity, line.getAndIncrement(), "Max Absorption: " + String.format("%.2f", livingEntity.getMaxAbsorption()));
        drawString(livingEntity, line.getAndIncrement(), "Health: " + String.format("%.2f", livingEntity.getHealth()));
        drawString(livingEntity, line.getAndIncrement(), "Max Health: " + String.format("%.2f", livingEntity.getMaxHealth()));
        drawItemStack(livingEntity, line, livingEntity.getMainHandStack());
        drawStatusEffects(livingEntity, line);
    }

    private void drawEntity(Entity entity, MutableInt line) {
        Team scoreboardTeam = entity.getScoreboardTeam();

        if (entity.isInvisible()) {
            drawString(entity, line.getAndIncrement(), "INVISIBLE", 0xff09ed20);
        }

        if (scoreboardTeam == null) {
            drawString(entity, line.getAndIncrement(), "No team", 0xfffe7f9c);
        } else {
            drawString(entity, line.getAndIncrement(), "Team: " + scoreboardTeam.getName());
            drawString(entity, line.getAndIncrement(), "Prefix: " + scoreboardTeam.getPrefix().getString());
            drawString(entity, line.getAndIncrement(), "Suffix: " + scoreboardTeam.getSuffix().getString());
            drawString(entity, line.getAndIncrement(), "Color: " + scoreboardTeam.getColor().name());
        }

        RegistryKey<World> world = entity.getEntityWorld().getRegistryKey();
        drawString(entity, line.getAndIncrement(), "World: " + world.getValue().toString());

        Vec3d entityPos = entity.getEntityPos();
        drawString(entity, line.getAndIncrement(),
            "Pos: "
                + String.format("%.2f", entityPos.getX()) + ","
                + String.format("%.2f", entityPos.getY()) + ","
                + String.format("%.2f", entityPos.getZ())
        );
        drawString(entity, line.getAndIncrement(), "Id: " + entity.getId());
        drawString(entity, line.getAndIncrement(), "Type: " + entity.getType().getName().getString());
    }

    private static void drawItemStack(Entity entity, MutableInt line, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        drawString(entity, line.getAndIncrement(), "Item: " + stack.getName().getString() + " x" + stack.getCount());

        LoreComponent lore = stack.get(DataComponentTypes.LORE);
        if (lore == null) {
            drawString(entity, line.getAndIncrement(), "Lore: (null)");
        } else if (lore.lines().isEmpty()) {
            drawString(entity, line.getAndIncrement(), "Lore: (empty)");
        } else {
            drawString(entity, line.getAndIncrement(), "Lore:");
            for (Text loreLine : lore.lines()) {
                drawString(entity, line.getAndIncrement(), " - " + loreLine.getString());
            }
        }

        ItemEnchantmentsComponent enchantments = stack.getEnchantments();
        drawString(entity, line.getAndIncrement(), "Enchantments: (" + enchantments.getSize() + ")");
        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : enchantments.getEnchantmentEntries()) {
            drawString(entity, line.getAndIncrement(), " - " + Enchantment.getName(entry.getKey(), entry.getIntValue()).getString());
        }
    }

    private static void drawStatusEffects(LivingEntity livingEntity, MutableInt line) {

        Collection<StatusEffectInstance> statusEffects = livingEntity.getStatusEffects();
        drawString(livingEntity, line.getAndIncrement(), "Effects : (" + statusEffects.size() + ")");
        for (StatusEffectInstance statusEffectInstance : statusEffects) {
            drawString(livingEntity, line.getAndIncrement(),
                " - a=" + statusEffectInstance.getAmplifier() + ", d=" + statusEffectInstance.getDuration());
        }
    }

    private static void drawString(Entity entity, int line, String text) {
        drawString(entity, line, text, 0xffffff00);
    }

    private static void drawString(Entity entity, int line, String text, int color) {
        GizmoDrawing.entityLabel(entity, -line, text, color, 0.32f);
    }
}

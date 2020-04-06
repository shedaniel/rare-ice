package me.shedaniel.rareice.blocks.entities;

import me.shedaniel.rareice.ItemLocation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;

import java.util.List;

@Environment(EnvType.CLIENT)
public class RareIceBlockEntityRenderer extends BlockEntityRenderer<RareIceBlockEntity> {
    public RareIceBlockEntityRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }
    
    @Override
    public void render(RareIceBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity.isRemoved()) return;
        DefaultedList<ItemStack> contained = blockEntity.getItemsContained();
        List<ItemLocation> locations = blockEntity.getItemsLocations();
        for (int i = 0; i < contained.size(); i++) {
            ItemStack stack = contained.get(i);
            ItemLocation location = locations.get(i);
            if (!stack.isEmpty()) {
                matrices.push();
                matrices.translate(location.x, location.y, location.z);
                double yawDegrees = location.yaw * 180.0;
                if (yawDegrees < 0) yawDegrees += 360.0;
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float) yawDegrees));
                double pitchDegrees = location.pitch * 180.0 - 90.0;
                if (pitchDegrees < 0) pitchDegrees += 360.0;
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((float) pitchDegrees));
                matrices.scale(0.8f, 0.8f, 0.8f);
                MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light, overlay, matrices, vertexConsumers);
                matrices.pop();
            }
        }
    }
}

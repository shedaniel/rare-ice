package me.shedaniel.rareice;

import me.shedaniel.rareice.blocks.entities.RareIceBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;

@Environment(EnvType.CLIENT)
public class RareIceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.translucent(), RareIce.RARE_ICE_BLOCK);
        BlockEntityRendererRegistry.register(RareIce.RARE_ICE_BLOCK_ENTITY_TYPE, RareIceBlockEntityRenderer::new);
    }
}

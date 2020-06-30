package me.shedaniel.rareice;

import me.shedaniel.rareice.blocks.entities.RareIceBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

@Environment(EnvType.CLIENT)
public class RareIceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), RareIce.RARE_ICE_BLOCK);
        BlockEntityRendererRegistry.INSTANCE.register(RareIce.RARE_ICE_BLOCK_ENTITY_TYPE, RareIceBlockEntityRenderer::new);
    }
}

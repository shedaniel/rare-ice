package me.shedaniel.rareice;

import me.shedaniel.rareice.blocks.entities.RareIceBlockEntity;
import me.shedaniel.rareice.blocks.entities.RareIceBlockEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.BlockEntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class RareIceClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(RareIceBlockEntity.class, new RareIceBlockEntityRenderer());
    }
}

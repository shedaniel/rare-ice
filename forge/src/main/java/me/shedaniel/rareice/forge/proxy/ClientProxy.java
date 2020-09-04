package me.shedaniel.rareice.forge.proxy;

import me.shedaniel.rareice.forge.blocks.entities.RareIceTileEntity;
import me.shedaniel.rareice.forge.blocks.entities.RareIceTileEntityRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends CommonProxy {
    @Override
    public void preInit() {
        super.preInit();
        ClientRegistry.bindTileEntitySpecialRenderer(RareIceTileEntity.class, new RareIceTileEntityRenderer());
    }
}

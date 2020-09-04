package me.shedaniel.rareice.forge.world.gen.feature;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Random;

public class RareIceWorldGen {
    private static RareIceFeature feature = new RareIceFeature(RareIceConfig.DEFAULT);
    
    @SubscribeEvent
    public static void generateOre(OreGenEvent.Pre event) {
        World world = event.getWorld();
        Random random = event.getRand();
        BlockPos chunkPos = event.getPos();
        if (world.provider.getDimensionType() == DimensionType.OVERWORLD) {
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
            int i = random.nextInt(16);
            for (int j = 0; j < i; j++) {
                int x = random.nextInt(16) + chunkPos.getX();
                int z = random.nextInt(16) + chunkPos.getZ();
                int y = random.nextInt(256 - 128) + 32;
                pos.setPos(x + 8, y, z + 8);
                
                feature.generate(world, random, pos, chunkPos);
            }
        }
    }
}

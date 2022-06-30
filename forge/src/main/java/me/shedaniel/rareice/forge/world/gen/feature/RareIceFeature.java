package me.shedaniel.rareice.forge.world.gen.feature;

import com.mojang.serialization.Codec;
import me.shedaniel.rareice.forge.RareIce;
import me.shedaniel.rareice.forge.blocks.entities.RareIceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.BitSet;
import java.util.Random;

public class RareIceFeature extends Feature<RareIceConfig> {
    public RareIceFeature(Codec<RareIceConfig> codec) {
        super(codec);
    }
    
    @Override
    public boolean place(FeaturePlaceContext<RareIceConfig> ctx) {
        RandomSource random = ctx.random();
        WorldGenLevel level = ctx.level();
        RareIceConfig config = ctx.config();
        BlockPos pos = ctx.origin();
        float f = random.nextFloat() * 3.1415927F;
        float g = (float) config.size / 8.0F;
        int i = Mth.ceil(((float) config.size / 16.0F * 2.0F + 1.0F) / 2.0F);
        double d = (float) pos.getX() + Mth.sin(f) * g;
        double e = (float) pos.getX() - Mth.sin(f) * g;
        double h = (float) pos.getZ() + Mth.cos(f) * g;
        double j = (float) pos.getZ() - Mth.cos(f) * g;
        double l = pos.getY() + random.nextInt(3) - 2;
        double m = pos.getY() + random.nextInt(3) - 2;
        int n = pos.getX() - Mth.ceil(g) - i;
        int o = pos.getY() - 2 - i;
        int p = pos.getZ() - Mth.ceil(g) - i;
        int q = 2 * (Mth.ceil(g) + i);
        int r = 2 * (2 + i);
        
        return this.generateVeinPart(level, random, config, d, e, h, j, l, m, n, o, p, q, r);
    }
    
    protected boolean generateVeinPart(WorldGenLevel world, RandomSource random, RareIceConfig config, double startX, double endX, double startZ, double endZ, double startY, double endY, int x, int y, int z, int size, int i) {
        int j = 0;
        BitSet bitSet = new BitSet(size * i * size);
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        double[] ds = new double[config.size * 4];
        
        int m;
        double o;
        double p;
        double q;
        double r;
        for (m = 0; m < config.size; ++m) {
            float f = (float) m / (float) config.size;
            o = Mth.lerp(f, startX, endX);
            p = Mth.lerp(f, startY, endY);
            q = Mth.lerp(f, startZ, endZ);
            r = random.nextDouble() * (double) config.size / 16.0D;
            double l = ((double) (Mth.sin(3.1415927F * f) + 1.0F) * r + 1.0D) / 2.0D;
            ds[m * 4] = o;
            ds[m * 4 + 1] = p;
            ds[m * 4 + 2] = q;
            ds[m * 4 + 3] = l;
        }
        
        for (m = 0; m < config.size - 1; ++m) {
            if (ds[m * 4 + 3] > 0.0D) {
                for (int n = m + 1; n < config.size; ++n) {
                    if (ds[n * 4 + 3] > 0.0D) {
                        o = ds[m * 4] - ds[n * 4];
                        p = ds[m * 4 + 1] - ds[n * 4 + 1];
                        q = ds[m * 4 + 2] - ds[n * 4 + 2];
                        r = ds[m * 4 + 3] - ds[n * 4 + 3];
                        if (r * r > o * o + p * p + q * q) {
                            if (r > 0.0D) {
                                ds[n * 4 + 3] = -1.0D;
                            } else {
                                ds[m * 4 + 3] = -1.0D;
                            }
                        }
                    }
                }
            }
        }
        
        for (m = 0; m < config.size; ++m) {
            double t = ds[m * 4 + 3];
            if (t >= 0.0D) {
                double u = ds[m * 4];
                double v = ds[m * 4 + 1];
                double w = ds[m * 4 + 2];
                int aa = Math.max(Mth.floor(u - t), x);
                int ab = Math.max(Mth.floor(v - t), y);
                int ac = Math.max(Mth.floor(w - t), z);
                int ad = Math.max(Mth.floor(u + t), aa);
                int ae = Math.max(Mth.floor(v + t), ab);
                int af = Math.max(Mth.floor(w + t), ac);
                
                for (int ag = aa; ag <= ad; ++ag) {
                    double ah = ((double) ag + 0.5D - u) / t;
                    if (ah * ah < 1.0D) {
                        for (int ai = ab; ai <= ae; ++ai) {
                            double aj = ((double) ai + 0.5D - v) / t;
                            if (ah * ah + aj * aj < 1.0D) {
                                for (int ak = ac; ak <= af; ++ak) {
                                    double al = ((double) ak + 0.5D - w) / t;
                                    if (ah * ah + aj * aj + al * al < 1.0D) {
                                        int am = ag - x + (ai - y) * size + (ak - z) * size * i;
                                        if (!bitSet.get(am)) {
                                            bitSet.set(am);
                                            mutable.set(ag, ai, ak);
                                            if (config.predicate.test(world.getBlockState(mutable))) {
                                                world.setBlock(mutable, RareIce.RARE_ICE_BLOCK.get().defaultBlockState(), 2);
                                                BlockEntity entity = world.getBlockEntity(mutable);
                                                if (entity instanceof RareIceBlockEntity) {
                                                    ((RareIceBlockEntity) entity).addLootTable(world.getLevel());
                                                }
                                                ++j;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return j > 0;
    }
}

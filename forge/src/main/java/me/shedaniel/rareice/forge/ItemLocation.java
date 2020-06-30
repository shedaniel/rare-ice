package me.shedaniel.rareice.forge;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;

public class ItemLocation extends Vector3d {
    public final double yaw;
    public final double pitch;
    
    public ItemLocation(double x, double y, double z) {
        super(x, y, z);
        this.yaw = Math.random();
        this.pitch = Math.random();
    }
    
    public ItemLocation(double x, double y, double z, double yaw, double pitch) {
        super(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    public static ItemLocation fromTag(CompoundNBT compoundNBT) {
        double x = compoundNBT.getDouble("x");
        double y = compoundNBT.getDouble("y");
        double z = compoundNBT.getDouble("z");
        double yaw = compoundNBT.getDouble("yaw");
        double pitch = compoundNBT.getDouble("pitch");
        return new ItemLocation(x, y, z, yaw, pitch);
    }
    
    public void toTag(CompoundNBT compoundNBT) {
        compoundNBT.putDouble("x", x);
        compoundNBT.putDouble("y", y);
        compoundNBT.putDouble("z", z);
        compoundNBT.putDouble("yaw", yaw);
        compoundNBT.putDouble("pitch", pitch);
    }
}

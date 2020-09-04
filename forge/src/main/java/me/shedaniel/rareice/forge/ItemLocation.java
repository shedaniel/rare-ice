package me.shedaniel.rareice.forge;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;

public class ItemLocation extends Vec3d {
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
    
    public static ItemLocation fromTag(NBTTagCompound compoundNBT) {
        double x = compoundNBT.getDouble("x");
        double y = compoundNBT.getDouble("y");
        double z = compoundNBT.getDouble("z");
        double yaw = compoundNBT.getDouble("yaw");
        double pitch = compoundNBT.getDouble("pitch");
        return new ItemLocation(x, y, z, yaw, pitch);
    }
    
    public void toTag(NBTTagCompound compoundNBT) {
        compoundNBT.setDouble("x", x);
        compoundNBT.setDouble("y", y);
        compoundNBT.setDouble("z", z);
        compoundNBT.setDouble("yaw", yaw);
        compoundNBT.setDouble("pitch", pitch);
    }
}

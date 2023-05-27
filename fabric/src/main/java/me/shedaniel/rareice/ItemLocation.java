package me.shedaniel.rareice;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;

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
    
    public static ItemLocation fromTag(CompoundTag compoundTag) {
        double x = compoundTag.getDouble("x");
        double y = compoundTag.getDouble("y");
        double z = compoundTag.getDouble("z");
        double yaw = compoundTag.getDouble("yaw");
        double pitch = compoundTag.getDouble("pitch");
        return new ItemLocation(x, y, z, yaw, pitch);
    }
    
    public void toTag(CompoundTag compoundTag) {
        compoundTag.putDouble("x", x);
        compoundTag.putDouble("y", y);
        compoundTag.putDouble("z", z);
        compoundTag.putDouble("yaw", yaw);
        compoundTag.putDouble("pitch", pitch);
    }
}

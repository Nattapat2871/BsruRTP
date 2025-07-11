package com.bsru;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

public class Zone {

    private final String name;
    private final String worldName;
    private final int x, y, z;
    private final int radius;
    private final String shape;
    private final String targetWorld;
    private final int countdown;
    private final Material requiredBlock;

    // Constructor ที่ถูกต้อง ต้องรับพารามิเตอร์ 10 ตัว (รวม String ของ requiredBlock)
    public Zone(String name, String worldName, int x, int y, int z, int radius, String shape, String targetWorld, int countdown, String requiredBlockMaterial) {
        this.name = name;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.shape = shape;
        this.targetWorld = targetWorld;
        this.countdown = countdown;

        if (requiredBlockMaterial != null && !requiredBlockMaterial.isEmpty()) {
            this.requiredBlock = Material.getMaterial(requiredBlockMaterial.toUpperCase());
        } else {
            this.requiredBlock = null;
        }
    }

    // Getters
    public String getName() { return name; }
    public String getWorldName() { return worldName; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getZ() { return z; }
    public int getRadius() { return radius; }
    public String getShape() { return shape; }
    public String getTargetWorld() { return targetWorld; }
    public int getCountdown() { return countdown; }
    public Material getRequiredBlock() { return requiredBlock; }

    public Location getCenter() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        return new Location(world, x, y, z);
    }
}
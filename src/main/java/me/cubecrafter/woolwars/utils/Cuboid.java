package me.cubecrafter.woolwars.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class Cuboid {

    private final int xMin;
    private final int xMax;
    private final int yMin;
    private final int yMax;
    private final int zMin;
    private final int zMax;
    private final World world;

    public Cuboid(Location point1, Location point2) {
        this.xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        this.xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        this.yMin = Math.min(point1.getBlockY(), point2.getBlockY());
        this.yMax = Math.max(point1.getBlockY(), point2.getBlockY());
        this.zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        this.zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
        this.world = point1.getWorld();
    }

    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>(this.getTotalBlocks());
        for (int x = this.xMin; x <= this.xMax; ++x) {
            for (int y = this.yMin; y <= this.yMax; ++y) {
                for (int z = this.zMin; z <= this.zMax; ++z) {
                    Block b = this.world.getBlockAt(x, y, z);
                    blocks.add(b);
                }
            }
        }
        return blocks;
    }

    public void clear() {
        for (Block block : getBlocks()) {
            block.setType(Material.AIR);
        }
    }

    public boolean isInside(Location location) {
        return location.getWorld().equals(this.world)
                && location.getBlockX() >= this.xMin
                && location.getBlockX() <= this.xMax
                && location.getBlockY() >= this.yMin
                && location.getBlockY() <= this.yMax
                && location.getBlockZ() >= this.zMin
                && location.getBlockZ() <= this.zMax;
    }

    public int getHeight() {
        return this.yMax - this.yMin + 1;
    }

    public int getXWidth() {
        return this.xMax - this.xMin + 1;
    }

    public int getZWidth() {
        return this.zMax - this.zMin + 1;
    }

    public int getTotalBlocks() {
        return this.getHeight() * this.getXWidth() * this.getZWidth();
    }

}

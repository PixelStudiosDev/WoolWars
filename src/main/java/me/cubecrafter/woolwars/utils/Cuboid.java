/*
 * Wool Wars
 * Copyright (C) 2022 CubeCrafter Development
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.cubecrafter.woolwars.utils;

import lombok.Getter;
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
    private final double xMinCentered;
    private final double xMaxCentered;
    private final double yMinCentered;
    private final double yMaxCentered;
    private final double zMinCentered;
    private final double zMaxCentered;
    @Getter private final World world;

    public Cuboid(Location point1, Location point2) {
        this.xMin = Math.min(point1.getBlockX(), point2.getBlockX());
        this.xMax = Math.max(point1.getBlockX(), point2.getBlockX());
        this.yMin = Math.min(point1.getBlockY(), point2.getBlockY());
        this.yMax = Math.max(point1.getBlockY(), point2.getBlockY());
        this.zMin = Math.min(point1.getBlockZ(), point2.getBlockZ());
        this.zMax = Math.max(point1.getBlockZ(), point2.getBlockZ());
        this.xMinCentered = this.xMin + 0.5;
        this.xMaxCentered = this.xMax + 0.5;
        this.yMinCentered = this.yMin + 0.5;
        this.yMaxCentered = this.yMax + 0.5;
        this.zMinCentered = this.zMin + 0.5;
        this.zMaxCentered = this.zMax + 0.5;
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

    public void fill(Material material) {
        for (Block block : getBlocks()) {
            block.setType(material);
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

    public boolean isInsideWithMarge(Location location, double marge) {
        return location.getWorld().equals(this.world)
                && location.getX() >= this.xMinCentered - marge
                && location.getX() <= this.xMaxCentered + marge
                && location.getY() >= this.yMinCentered - marge
                && location.getY() <= this.yMaxCentered + marge
                && location.getZ() >= this.zMinCentered - marge
                && location.getZ() <= this.zMaxCentered + marge;
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

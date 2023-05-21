/*
 * Wool Wars
 * Copyright (C) 2023 CubeCrafter Development
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

import com.cryptomorin.xseries.XBlock;
import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class Cuboid {

    private final World world;
    private final int xMin, xMax, yMin, yMax, zMin, zMax;

    public Cuboid(Location first, Location second) {
        this.world = first.getWorld();
        this.xMin = Math.min(first.getBlockX(), second.getBlockX());
        this.xMax = Math.max(first.getBlockX(), second.getBlockX());
        this.yMin = Math.min(first.getBlockY(), second.getBlockY());
        this.yMax = Math.max(first.getBlockY(), second.getBlockY());
        this.zMin = Math.min(first.getBlockZ(), second.getBlockZ());
        this.zMax = Math.max(first.getBlockZ(), second.getBlockZ());
    }

    public List<Block> getBlocks() {
        List<Block> blocks = new ArrayList<>(getBlockCount());
        for (int x = xMin; x <= xMax; ++x) {
            for (int y = yMin; y <= yMax; ++y) {
                for (int z = zMin; z <= zMax; ++z) {
                    blocks.add(world.getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    public void fill(String material) {
        for (Block block : getBlocks()) {
            XBlock.setType(block, XMaterial.matchXMaterial(material).orElse(XMaterial.WHITE_WOOL));
        }
    }

    public void fill(List<String> materials) {
        for (Block block : getBlocks()) {
            String material = materials.get(ThreadLocalRandom.current().nextInt(materials.size()));
            XBlock.setType(block, XMaterial.matchXMaterial(material).orElse(XMaterial.WHITE_WOOL));
        }
    }

    public boolean isInside(Location location) {
        return location.getWorld().equals(world)
                && location.getBlockX() >= xMin
                && location.getBlockX() <= xMax
                && location.getBlockY() >= yMin
                && location.getBlockY() <= yMax
                && location.getBlockZ() >= zMin
                && location.getBlockZ() <= zMax;
    }

    public int getBlockCount() {
        return (xMax - xMin + 1) * (yMax - yMin + 1) * (zMax - zMin + 1);
    }

}

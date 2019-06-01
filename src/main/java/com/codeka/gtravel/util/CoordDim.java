package com.codeka.gtravel.util;

import com.google.common.base.Objects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nullable;

/** A 3D coordinate + dimension container. */
public class CoordDim {
    public int x;
    public int y;
    public int z;
    public int dim;

    public CoordDim(int x, int y, int z, int dim) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
    }

    public CoordDim(BlockPos pos, World world) {
        this(pos.getX(), pos.getY(), pos.getZ(), world.provider.getDimension());
    }

    @Nullable
    public static CoordDim fromNBT(NBTTagCompound nbt) {
        if (nbt.getSize() == 0) {
            return null;
        }

        return new CoordDim(
                nbt.getInteger("x"),
                nbt.getInteger("y"),
                nbt.getInteger("z"),
                nbt.getInteger("dim"));
    }

    public NBTTagCompound toNBT(NBTTagCompound nbt) {
        nbt.setInteger("x", x);
        nbt.setInteger("y", y);
        nbt.setInteger("z", z);
        nbt.setInteger("dim", dim);
        return nbt;
    }

    /** Get the {@link TileEntity} at this coord/dim, or null if there's nothing there. */
    @Nullable
    public TileEntity getTileEntity() {
        World world = DimensionManager.getWorld(dim);
        return world.getTileEntity(new BlockPos(x, y, z));
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CoordDim &&
                ((CoordDim) obj).x == x &&
                ((CoordDim) obj).y == y &&
                ((CoordDim) obj).z == z &&
                ((CoordDim) obj).dim == dim;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y, z, dim);
    }
}
package me.desht.pneumaticcraft.common.entity.living;

import me.desht.pneumaticcraft.client.render.RenderDroneHeldItem;
import me.desht.pneumaticcraft.client.render.RenderLaser;
import net.minecraft.entity.EntityCreature;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public abstract class EntityDroneBase extends EntityCreature {
    public float oldPropRotation;
    public float propRotation;
    public float laserExtension; // How far the laser comes out of the drone. 1F is fully extended
    public float oldLaserExtension;
    @SideOnly(Side.CLIENT)
    protected RenderLaser digLaser;
    @SideOnly(Side.CLIENT)
    RenderDroneHeldItem renderDroneHeldItem;

    public EntityDroneBase(World world) {
        super(world);
    }

    public void renderExtras(double x, double y, double z, float partialTicks) {
        BlockPos diggingPos = getDugBlock();
        if (diggingPos != null) {
            if (digLaser == null) {
                digLaser = new RenderLaser(0xFF000000 | getLaserColor());
            }
            digLaser.render(partialTicks, 0, getLaserOffsetY(), 0, diggingPos.getX() + 0.5 - posX, diggingPos.getY() + 0.45 - posY, diggingPos.getZ() + 0.5 - posZ);
        }
    }

    protected double getLaserOffsetY() {
        return 0.05;
    }

    public int getLaserColor() {
        return 0xFF0000;
    }

    public int getDroneColor() {
        return 0;
    }

    public boolean isAccelerating() {
        return true;
    }

    protected abstract BlockPos getDugBlock();

    @Nonnull
    public abstract ItemStack getDroneHeldItem();
}

/*
 * This file is part of pnc-repressurized.
 *
 *     pnc-repressurized is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     pnc-repressurized is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with pnc-repressurized.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.desht.pneumaticcraft.common.block.entity;

import me.desht.pneumaticcraft.common.core.ModBlockEntities;
import me.desht.pneumaticcraft.common.inventory.CreativeCompressedIronBlockMenu;
import me.desht.pneumaticcraft.common.network.GuiSynced;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class CreativeCompressedIronBlockBlockEntity extends CompressedIronBlockBlockEntity implements MenuProvider {
    @GuiSynced
    public int targetTemperature = -1;  // -1 = uninited

    public CreativeCompressedIronBlockBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_COMPRESSED_IRON_BLOCK.get(), pos, state);

        heatExchanger.setThermalCapacity(1_000_000);
    }

    @Override
    public void tickServer() {
        super.tickServer();

        if (targetTemperature < 0) {
            targetTemperature = (int) heatExchanger.getAmbientTemperature();
        }
        heatExchanger.setTemperature(targetTemperature);
    }

    @Override
    public boolean shouldShowGuiHeatTab() {
        return false;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("targetTemperature", targetTemperature);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        targetTemperature = tag.getInt("targetTemperature");
    }

    @Override
    public void handleGUIButtonPress(String tag, boolean shiftHeld, ServerPlayer player) {
        try {
            targetTemperature += Integer.parseInt(tag) * (shiftHeld ? 10 : 1);
            targetTemperature = Mth.clamp(targetTemperature, 0, 2273);
            setChanged();
        } catch (IllegalArgumentException ignored) {
        }
    }

    public void setTargetTemperature(int temp) {
        this.targetTemperature = temp;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        return new CreativeCompressedIronBlockMenu(windowId, playerInventory, getBlockPos());
    }
}

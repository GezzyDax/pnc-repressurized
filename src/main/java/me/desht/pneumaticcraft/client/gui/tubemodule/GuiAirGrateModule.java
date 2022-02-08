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

package me.desht.pneumaticcraft.client.gui.tubemodule;

import com.mojang.blaze3d.vertex.PoseStack;
import me.desht.pneumaticcraft.client.gui.widget.WidgetButtonExtended;
import me.desht.pneumaticcraft.client.gui.widget.WidgetLabel;
import me.desht.pneumaticcraft.client.gui.widget.WidgetTextField;
import me.desht.pneumaticcraft.client.util.ClientUtils;
import me.desht.pneumaticcraft.client.util.GuiUtils;
import me.desht.pneumaticcraft.common.block.tubes.ModuleAirGrate;
import me.desht.pneumaticcraft.common.core.ModItems;
import me.desht.pneumaticcraft.common.network.NetworkHandler;
import me.desht.pneumaticcraft.common.network.PacketUpdateAirGrateModule;
import me.desht.pneumaticcraft.common.util.EntityFilter;
import me.desht.pneumaticcraft.common.util.PneumaticCraftUtils;
import me.desht.pneumaticcraft.lib.Textures;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class GuiAirGrateModule extends GuiTubeModule<ModuleAirGrate> {
    private int sendTimer = 0;
    private WidgetButtonExtended warningButton;
    private WidgetButtonExtended rangeButton;
    private EditBox textfield;

    public GuiAirGrateModule(ModuleAirGrate module) {
        super(module);

        ySize = 57;
    }

    @Override
    public void init() {
        super.init();

        addLabel(this.title, this.guiLeft + this.xSize / 2, this.guiTop + 5, WidgetLabel.Alignment.CENTRE);

        WidgetLabel filterLabel = addLabel(PneumaticCraftUtils.xlate("pneumaticcraft.gui.entityFilter"), this.guiLeft + 10, this.guiTop + 21);
        filterLabel.visible = this.module.isUpgraded();

        WidgetLabel helpLabel = addLabel(PneumaticCraftUtils.xlate("pneumaticcraft.gui.holdF1forHelp"), this.guiLeft + this.xSize / 2, this.guiTop + this.ySize + 5, WidgetLabel.Alignment.CENTRE)
                .setColor(0xC0C0C0);
        helpLabel.visible = this.module.isUpgraded();

        WidgetButtonExtended advPCB = new WidgetButtonExtended(this.guiLeft + 10, this.guiTop + 21, 20, 20, TextComponent.EMPTY)
                .setRenderStacks(new ItemStack(ModItems.MODULE_EXPANSION_CARD.get()))
                .setTooltipKey("pneumaticcraft.gui.redstoneModule.addAdvancedPCB").setVisible(false);
        advPCB.visible = !module.isUpgraded();
        addRenderableWidget(advPCB);

        int tx = 12 + filterLabel.getWidth();
        textfield = new WidgetTextField(font, guiLeft + tx, guiTop + 20, xSize - tx - 10, 10);
        textfield.setValue(module.getEntityFilterString());
        textfield.setResponder(s -> sendTimer = 5);
        textfield.setFocus(true);
        textfield.setVisible(module.isUpgraded());
        setFocused(textfield);
        addRenderableWidget(textfield);

        warningButton = new WidgetButtonExtended(guiLeft + 152, guiTop + 20, 20, 20, TextComponent.EMPTY)
                .setVisible(false)
                .setRenderedIcon(Textures.GUI_PROBLEMS_TEXTURE);
        addRenderableWidget(warningButton);

        rangeButton = new WidgetButtonExtended(this.guiLeft + this.xSize - 20, this.guiTop + this.ySize - 20, 16, 16, getRangeButtonText(), b -> {
            module.setShowRange(!this.module.isShowRange());
            rangeButton.setMessage(getRangeButtonText());
        });
        addRenderableWidget(rangeButton);

        validateEntityFilter(textfield.getValue());
    }

    private Component getRangeButtonText() {
        return new TextComponent((this.module.isShowRange() ? ChatFormatting.AQUA : ChatFormatting.DARK_GRAY) + "R");
    }

    private void validateEntityFilter(String filter) {
        try {
            warningButton.visible = false;
            warningButton.setTooltipText(TextComponent.EMPTY);
            new EntityFilter(filter);  // syntax check
        } catch (IllegalArgumentException e) {
            warningButton.visible = true;
            warningButton.setTooltipText(new TextComponent(e.getMessage()).withStyle(ChatFormatting.GOLD));
        }
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        if (ClientUtils.isKeyDown(GLFW.GLFW_KEY_F1) && module.isUpgraded()) {
            GuiUtils.showPopupHelpScreen(matrixStack, this, font,
                    GuiUtils.xlateAndSplit("pneumaticcraft.gui.entityFilter.helpText"));
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!textfield.isFocused()) textfield.setValue(module.getEntityFilterString());

        if (sendTimer > 0 && --sendTimer == 0) {
            module.setEntityFilter(textfield.getValue());
            NetworkHandler.sendToServer(new PacketUpdateAirGrateModule(module, textfield.getValue()));
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return Textures.GUI_MODULE_SIMPLE;
    }

}

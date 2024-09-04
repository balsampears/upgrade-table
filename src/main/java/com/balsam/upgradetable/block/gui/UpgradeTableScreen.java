package com.balsam.upgradetable.block.gui;

import com.balsam.upgradetable.block.UpgradeTableContainer;
import com.balsam.upgradetable.block.UpgradeTableTileEntity;
import com.balsam.upgradetable.capability.itemAbility.BaseItemAbility;
import com.balsam.upgradetable.capability.itemAbility.IItemAbility;
import com.balsam.upgradetable.capability.pojo.ItemAttributePO;
import com.balsam.upgradetable.config.AttributeEnum;
import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.mod.ModCapability;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class UpgradeTableScreen extends ContainerScreen<UpgradeTableContainer> {
    private static final ResourceLocation BACKGROUND_RESOURCE = new ResourceLocation(Constants.MOD_ID, "textures/gui/upgradetable.png");
    private static final int TEXTURE_WIDTH = 190;
    private static final int TEXTURE_HEIGHT = 166;
    private List<Button> upgradeButtons;

    public UpgradeTableScreen(UpgradeTableContainer container, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(container, playerInventory, textComponent);
        this.imageWidth = TEXTURE_WIDTH;
        this.imageHeight = TEXTURE_HEIGHT;
        this.upgradeButtons = new ArrayList<>();
    }

    @Override
    protected void init() {
        super.init();
        //最多渲染4个按钮
        //渲染组件，坐标从上面方法的x,y开始算
        for (int i = 0; i < 4; i++) {
            Button button = new Button(this.leftPos + 165, this.topPos + 25 + i * 10, 20, 12,
                    new TranslationTextComponent(String.format("button.%s.upgrade", Constants.MOD_ID)), new UpgradeTableButton(this, i));
            this.buttons.add(button);
            this.upgradeButtons.add(button);
        }
    }

    //渲染背景
    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
        this.minecraft.getTextureManager().bind(BACKGROUND_RESOURCE);
//        int x = (this.width - this.imageWidth)/2;
//        int y = (this.height - this.imageHeight)/2;
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }

    //渲染提示框
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        Map<Integer, Boolean> buttonActiveMap = new HashMap<>();
        //渲染面板
        UpgradeTableTileEntity blockEntity = this.menu.blockEntity;
        ItemStack itemStack = blockEntity.getInventory().getItem(0);
        ItemStack materialItemStack = blockEntity.getInventory().getItem(1);
        if (itemStack != ItemStack.EMPTY) {
            LazyOptional<IItemAbility> capability = itemStack.getCapability(ModCapability.itemAbility);
            capability.ifPresent(o -> {
                BaseItemAbility base = (BaseItemAbility) o;

                float fontScale = 0.8f;
                matrixStack.scale(fontScale, fontScale, fontScale);

                for (int i = 0; i < base.getDisplayAttributes().size(); i++) {
                    ItemAttributePO attribute = base.getDisplayAttributes().get(i);
                    TranslationTextComponent textComponent = new TranslationTextComponent(attribute.getAttributeEnum().getName());
                    if (attribute.getAttributeEnum() == AttributeEnum.Total) {
                        textComponent.append(String.format(" %d / %d", attribute.getLevel(), attribute.getMaxLevel()));
                    } else {
                        textComponent.append(String.format(" +%.2f -> +%.2f", attribute.getValue(), attribute.getNextLevelValue()));
                        //渲染按钮
                        if (i - 1 >=0 && i-1 < upgradeButtons.size()) {
                            boolean active = attribute.canUpgrade() && base.getTotal().canUpgrade();
                            active = active && (Minecraft.getInstance().player.isCreative() || materialItemStack.getItem().equals(itemStack.getItem()));
                            if (attribute.getAttributeEnum()!=AttributeEnum.MAX_DURATION)
                                active = active && itemStack.getMaxDamage() - attribute.getPerLevelReduceDuration() > 0;
                            buttonActiveMap.put(i - 1, active);
                        }
                    }
                    //渲染文本
                    this.font.draw(matrixStack, textComponent, (this.leftPos + 38) / fontScale, (this.topPos + 17 + 10 * i) / fontScale, Color.BLACK.getRGB());

                }

                matrixStack.scale(1 / fontScale, 1 / fontScale, 1 / fontScale);
            });
        }
        //渲染按钮
        for (int i=0;i<upgradeButtons.size();i++) {
            Button upgradeButton = upgradeButtons.get(i);
            upgradeButton.active = buttonActiveMap.getOrDefault(i, false);
            upgradeButton.render(matrixStack, mouseX, mouseY, partialTicks);
        }
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX <= 0 || mouseY <= 0) return false;
        if (button == 0) {
            for (Button upgradeButton : this.upgradeButtons) {
                upgradeButton.mouseClicked(mouseX, mouseY, button);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


}

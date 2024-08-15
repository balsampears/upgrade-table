package com.balsam.upgradetable.block;

import com.balsam.upgradetable.config.Constants;
import com.balsam.upgradetable.network.Networking;
import com.balsam.upgradetable.network.pack.UpgradeButtonPack;
import com.balsam.upgradetable.util.Logger;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class UpgradeTableScreen extends ContainerScreen<UpgradeTableContainer> {
    private static final ResourceLocation BACKGROUND_RESOURCE = new ResourceLocation(Constants.MOD_ID, "textures/gui/upgradetable.png");
    private static final int TEXTURE_WIDTH = 176;
    private static final int TEXTURE_HEIGHT = 166;
    private Button upgradeButton;

    public UpgradeTableScreen(UpgradeTableContainer container, PlayerInventory playerInventory, ITextComponent textComponent) {
        super(container, playerInventory, textComponent);
        this.imageWidth = TEXTURE_WIDTH;
        this.imageHeight = TEXTURE_HEIGHT;

    }

    @Override
    protected void init() {
        super.init();
        //渲染组件，坐标从上面方法的x,y开始算
        this.upgradeButton = new Button(this.leftPos + 46, this.topPos + 36, 22, 14,
                new TranslationTextComponent(String.format("button.%s.upgrade", Constants.MOD_ID)), (button) -> {
            Networking.INSTANCE.sendToServer(new UpgradeButtonPack("你好，点击了按钮"));
        });
        this.buttons.add(upgradeButton);
    }

    //渲染背景
    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(matrixStack);
        this.minecraft.getTextureManager().bind(BACKGROUND_RESOURCE);
        int x = (this.width - this.imageWidth)/2;
        int y = (this.height - this.imageHeight)/2;
        blit(matrixStack, x,y,0,0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }

    //渲染提示框
    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.upgradeButton.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX<=0 || mouseY<=0) return false;
        if (button == 0){
            this.upgradeButton.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


}

package com.github.levoment.deathbagsrevived;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class DeathBagScreen extends HandledScreen<ScreenHandler> {

    private static final Identifier DEATH_BAG_TEXTURE = new Identifier("deathbagsrevived", "textures/gui/container/deathbag.png");

    public DeathBagScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, Text.of(title.getString()).getWithStyle(Style.EMPTY.withFormatting(Formatting.GOLD, Formatting.BLUE)).get(0));
        this.backgroundWidth = 195;
        this.backgroundHeight = 222;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, DEATH_BAG_TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        this.playerInventoryTitleY =  ((backgroundHeight - textRenderer.fontHeight) / 2) + 22;
    }
}

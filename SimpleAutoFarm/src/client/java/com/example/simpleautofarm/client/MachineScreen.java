package com.example.simpleautofarm.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import com.example.simpleautofarm.menu.MachineMenu;

public class MachineScreen<T extends MachineMenu>
        extends AbstractContainerScreen<T> {

    private static final Identifier SCREEN_TEXTURE =
        Identifier.withDefaultNamespace(
            "textures/gui/container/dispenser.png"
        );

    public MachineScreen(
        T menu,
        Inventory inventory,
        Component title
    ) {
        super(menu, inventory, title);

        this.titleLabelX =
            (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractBackground(
        GuiGraphicsExtractor graphics,
        int mouseX,
        int mouseY,
        float delta
    ) {
        super.extractBackground(graphics, mouseX, mouseY, delta);

        graphics.blit(
            RenderPipelines.GUI_TEXTURED,
            SCREEN_TEXTURE,
            this.leftPos,
            this.topPos,
            0.0F,
            0.0F,
            this.imageWidth,
            this.imageHeight,
            256,
            256
        );
    }
}

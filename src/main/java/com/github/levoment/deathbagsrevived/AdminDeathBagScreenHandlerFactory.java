package com.github.levoment.deathbagsrevived;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class AdminDeathBagScreenHandlerFactory implements NamedScreenHandlerFactory {

    private Text playerName;

    public AdminDeathBagScreenHandlerFactory(Text playerName) {
        this.playerName = playerName;
    }


    @Override
    public Text getDisplayName() {
        return this.playerName;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        AdminDeathBagScreenHandler adminDeathBagScreenHandler = new AdminDeathBagScreenHandler(syncId, player.getInventory(), this.playerName);
        return adminDeathBagScreenHandler;
    }
}

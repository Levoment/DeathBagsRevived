package com.github.levoment.deathbagsrevived.items;

import com.github.levoment.deathbagsrevived.AdminDeathBagScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.UUID;

public class AdminDeathBagItem extends GoldenDeathBagItem {

    private String playerName;
    private int lastAccessedInventory;

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    private UUID playerUUID;

    public AdminDeathBagItem(Settings settings) {
        super(settings);
        lastAccessedInventory = -1;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        AdminDeathBagScreenHandlerFactory adminDeathBagScreenHandlerFactory= new AdminDeathBagScreenHandlerFactory(Text.of(this.playerName));
        user.openHandledScreen(adminDeathBagScreenHandlerFactory);
        return TypedActionResult.success(this.asItem().getDefaultStack());
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getLastAccessedInventory() {
        return lastAccessedInventory;
    }

    public void setLastAccessedInventory(int lastAccessedInventory) {
        this.lastAccessedInventory = lastAccessedInventory;
    }
}

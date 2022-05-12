package com.github.levoment.deathbagsrevived.items;

import com.github.levoment.deathbagsrevived.DeathBagScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class DeathBagItem extends Item {

    private boolean playerIsSneaking;

    public DeathBagItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (user.isSneaking()) {
            this.playerIsSneaking = true;
        } else {
            this.playerIsSneaking = false;
        }
        user.openHandledScreen(new DeathBagScreenHandlerFactory(user.getName()));
        return TypedActionResult.success(this.asItem().getDefaultStack());
    }

    public boolean getPlayerIsSneaking() {
        return playerIsSneaking;
    }

    public void setPlayerIsSneaking(boolean playerIsSneaking) {
        this.playerIsSneaking = playerIsSneaking;
    }
}

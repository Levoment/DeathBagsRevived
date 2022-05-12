package com.github.levoment.deathbagsrevived.mixin;

import com.github.levoment.deathbagsrevived.DeathBagsRevivedMod;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public class LivingEntityDropMixin {

    @Redirect(method = "drop(Lnet/minecraft/entity/damage/DamageSource;)V",
    at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;dropInventory()V"))
    public void dropInventory(LivingEntity instance) {
        // If the entity is a ServerPlayerEntity
        if (instance instanceof ServerPlayerEntity serverPlayerEntity) {
            // Drop a death bag
            ItemStack itemStack = new ItemStack(DeathBagsRevivedMod.deathBagItem);
            serverPlayerEntity.dropItem(itemStack, true);
        }
    }
}

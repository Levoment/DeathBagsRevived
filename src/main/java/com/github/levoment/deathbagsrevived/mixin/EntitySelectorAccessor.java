package com.github.levoment.deathbagsrevived.mixin;

import net.minecraft.command.EntitySelector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntitySelector.class)
public interface EntitySelectorAccessor {
    @Accessor
    String getPlayerName();
}

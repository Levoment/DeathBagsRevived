package com.github.levoment.deathbagsrevived;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class DeathBagsRevivedModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register the client screens
        HandledScreens.register(DeathBagsRevivedMod.DEATH_BAG_SCREEN_HANDLER_TYPE, DeathBagScreen::new);
        HandledScreens.register(DeathBagsRevivedMod.ADMIN_DEATH_BAG_SCREEN_HANDLER_TYPE, AdminDeathBagScreen::new);
    }
}

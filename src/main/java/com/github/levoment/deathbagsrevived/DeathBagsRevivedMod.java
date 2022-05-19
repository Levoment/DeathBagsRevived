package com.github.levoment.deathbagsrevived;

import com.github.levoment.deathbagsrevived.items.AdminDeathBagItem;
import com.github.levoment.deathbagsrevived.items.DeathBagItem;
import com.github.levoment.deathbagsrevived.items.GoldenDeathBagItem;
import com.github.levoment.deathbagsrevived.mixin.EntitySelectorAccessor;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class DeathBagsRevivedMod implements ModInitializer {
    // Mod id and logger
    public static final String MOD_ID = "deathbagsrevived";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    // Items
    public static final DeathBagItem deathBagItem = new DeathBagItem(new FabricItemSettings().group(ItemGroup.MISC));
    public static final GoldenDeathBagItem goldenDeathBagItem = new GoldenDeathBagItem(new FabricItemSettings().group(ItemGroup.MISC));
    public static final AdminDeathBagItem adminDeathBagItem = new AdminDeathBagItem(new FabricItemSettings().group(ItemGroup.MISC));

    public static ScreenHandlerType<DeathBagScreenHandler> DEATH_BAG_SCREEN_HANDLER_TYPE;
    public static ScreenHandlerType<AdminDeathBagScreenHandler> ADMIN_DEATH_BAG_SCREEN_HANDLER_TYPE;

    @Override
    public void onInitialize() {
        // Register the death bag items
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "deathbag"), deathBagItem);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "goldendeathbag"), goldenDeathBagItem);
        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "admindeathbag"), adminDeathBagItem);
        // Create the screen handler types
        DEATH_BAG_SCREEN_HANDLER_TYPE = new ScreenHandlerType<>(DeathBagScreenHandler::new);
        ADMIN_DEATH_BAG_SCREEN_HANDLER_TYPE = new ScreenHandlerType<>(AdminDeathBagScreenHandler::new);
        // Register the screen handler types
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(MOD_ID, "deathbag"), DEATH_BAG_SCREEN_HANDLER_TYPE);
        Registry.register(Registry.SCREEN_HANDLER, new Identifier(MOD_ID, "admindeathbag"), ADMIN_DEATH_BAG_SCREEN_HANDLER_TYPE);

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(CommandManager.literal("giveDeathBagFrom")
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                    .requires(source -> source.hasPermissionLevel(4))
                    .executes(context -> {
                        // Get the EntitySelector
                        EntitySelector entitySelector = context.getArgument("targets", EntitySelector.class);
                        // Get the player name in the command
                        String playerName = ((EntitySelectorAccessor)entitySelector).getPlayerName();
                        // Get the server player entity
                        ServerPlayerEntity serverPlayerEntity = context.getSource().getPlayer();
                        // Get the current player inventory
                        PlayerInventory playerInventory = serverPlayerEntity.getInventory();


                        // Create the item
                        Item item = DeathBagsRevivedMod.adminDeathBagItem.getDefaultStack().getItem();
                        ((AdminDeathBagItem)item).setPlayerName(playerName);
                        ((AdminDeathBagItem)item).setPlayerUUID(serverPlayerEntity.getServer().getUserCache().findByName(playerName).get().getId());
                        // Get stack
                        ItemStack itemStack = item.getDefaultStack();
                        // If the main hand has no items on it
                        if (serverPlayerEntity.getMainHandStack().isEmpty()) {
                             serverPlayerEntity.setStackInHand(Hand.MAIN_HAND, item.getDefaultStack());
                        } else {
                            // If their inventory has an empty slot
                            int emptySlot = playerInventory.getEmptySlot();
                            if (emptySlot != -1) {
                                playerInventory.setStack(emptySlot, itemStack);
                            } else {
                                serverPlayerEntity.dropItem(itemStack, true);
                            }
                        }
                        return 1;
                    })));
        });
    }
}

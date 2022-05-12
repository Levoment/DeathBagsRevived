package com.github.levoment.deathbagsrevived.mixin;

import com.github.levoment.deathbagsrevived.DeathBagsRevivedMod;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;

@Mixin(ServerPlayerEntity.class)
public abstract class OnPlayerDeathMixin {
    @Shadow
    @Final
    public MinecraftServer server;

    @Shadow
    public abstract void playerTick();

    @Inject(at = @At("HEAD"), method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V", cancellable = true)
    public void onDeathCallback(DamageSource source, CallbackInfo onDeathCallbackInfoReturnable) {
        // Get the player inventory
        PlayerInventory playerInventory = ((ServerPlayerEntity) (Object) this).getInventory();

        // Get the player UUID as string
        String playerUUID = ((ServerPlayerEntity) (Object) this).getUuidAsString();

        // Save the player inventory to a nbt list
        NbtList list = new NbtList();
        playerInventory.writeNbt(list);

        // Get the save path as string of the world folder
        String savePath = server.getSavePath(WorldSavePath.ROOT).toString();
        if (savePath.lastIndexOf('.') == savePath.length() - 1) {
            savePath = savePath.substring(0, savePath.length() - 1);
        }
        // Try to check if folder exists for DeathBagsRevived data. If it doesn't exist, create it
        File deathBagsRevivedFolder = new File(savePath + "deathbagsrevived");
        if (!deathBagsRevivedFolder.exists()) {
            try {
                // Create the file
                Files.createDirectory(deathBagsRevivedFolder.toPath());
            } catch (IOException ioException) {
                DeathBagsRevivedMod.LOGGER.error("There was a problem creating the folder that should contain the DeathBagsRevived data as: " + deathBagsRevivedFolder.getPath().toString());
            }
        }

        // Check again if the file exists and if it can be written to because if an exception was caught, the game would not crash and continue
        if (deathBagsRevivedFolder.exists() && deathBagsRevivedFolder.canWrite()) {
            // Get the File for the file that will contain the player data
            File allPlayersInventoriesFile = new File(deathBagsRevivedFolder.getPath() + FileSystems.getDefault().getSeparator() + "all_players_dbr_inventories.dat");

            // Check if the file exists
            if (allPlayersInventoriesFile.exists()) {
                try {
                    // Get the previous data
                    NbtCompound previousData = NbtIo.read(allPlayersInventoriesFile);
                    // Get the player previous compound
                    NbtList playerInventories = previousData.getList(playerUUID, NbtElement.LIST_TYPE);
                    // Add this inventory to the list of inventories at the end of the list
                    playerInventories.add(playerInventories.size(), list);
                    // Put the new list of inventories
                    previousData.put(playerUUID, playerInventories);
                    // Write to file
                    NbtIo.write(previousData, allPlayersInventoriesFile);
                } catch (IOException fileWriteIOException) {
                    DeathBagsRevivedMod.LOGGER.error("There was a problem trying to write to file: " + allPlayersInventoriesFile.getPath().toString());
                }
            } else {
                // Create a nbt compound
                NbtCompound nbtCompound = new NbtCompound();
                // Create a list to serve as root
                NbtList rootList = new NbtList();
                // Add the current player inventory to this list
                rootList.add(rootList.size(), list);
                // Put the player data in the nbt compound
                nbtCompound.put(playerUUID, rootList);
                try {
                    // Write the nbt compound to disk
                    NbtIo.write(nbtCompound, allPlayersInventoriesFile);
                } catch (IOException ioException) {
                    DeathBagsRevivedMod.LOGGER.error("There was a problem trying to write to file: " + allPlayersInventoriesFile.getPath().toString() + " when the file didn't exist and it was attempted to be created.");
                }
            }
        } else {
            DeathBagsRevivedMod.LOGGER.error("The folder that should contain the DeathBagsRevived data as: " + deathBagsRevivedFolder.getPath().toString() + " does not exist or the program does not have write permissions to write in it");
        }
    }
}

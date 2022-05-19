package com.github.levoment.deathbagsrevived;

import com.github.levoment.deathbagsrevived.items.DeathBagItem;
import com.github.levoment.deathbagsrevived.items.GoldenDeathBagItem;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.screen.PlayerScreenHandler.*;

public class DeathBagScreenHandler extends ScreenHandler {

    // Variable for the player inventory
    private PlayerInventory playerInventory;
    private DeathBagInventory deathBagInventory;
    private List<Integer> freeSlots;
    private List<Integer> freeArmorSlots;

    static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{EMPTY_BOOTS_SLOT_TEXTURE, EMPTY_LEGGINGS_SLOT_TEXTURE, EMPTY_CHESTPLATE_SLOT_TEXTURE, EMPTY_HELMET_SLOT_TEXTURE, EMPTY_OFFHAND_ARMOR_SLOT};
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD, EquipmentSlot.OFFHAND};

    public DeathBagScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new PlayerInventory(playerInventory.player));
    }

    public DeathBagScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(DeathBagsRevivedMod.DEATH_BAG_SCREEN_HANDLER_TYPE, syncId);
        this.playerInventory = playerInventory;
        this.deathBagInventory = new DeathBagInventory();
        this.freeSlots = new ArrayList<>();
        this.freeArmorSlots = new ArrayList<>();

        for (int i = 0; i < this.playerInventory.size(); i++) {
            switch (i) {
                case 36: {
                    if (this.playerInventory.getStack(i).isEmpty() && !freeArmorSlots.contains(Integer.valueOf(i))) {
                        freeArmorSlots.add(i);
                    }
                }
                case 37: {
                    if (this.playerInventory.getStack(i).isEmpty() && !freeArmorSlots.contains(Integer.valueOf(i))) {
                        freeArmorSlots.add(i);
                    }
                }
                case 38: {
                    if (this.playerInventory.getStack(i).isEmpty() && !freeArmorSlots.contains(Integer.valueOf(i))) {
                        freeArmorSlots.add(i);
                    }
                }
                case 39: {
                    if (this.playerInventory.getStack(i).isEmpty() && !freeArmorSlots.contains(Integer.valueOf(i))) {
                        freeArmorSlots.add(i);
                    }
                }
                case 40: {
                    if (this.playerInventory.getStack(i).isEmpty() && !freeArmorSlots.contains(Integer.valueOf(i))) {
                        freeArmorSlots.add(i);
                    }
                }
                default: {
                    if (this.playerInventory.getStack(i).isEmpty() && !freeSlots.contains(Integer.valueOf(i))) {
                        freeSlots.add(i);
                    }
                }
            }
        }

        fillDeathBagInventory();

        int playerHotbarCurrentSlot;
        //The player Hotbar
        for (playerHotbarCurrentSlot = 0; playerHotbarCurrentSlot < 9; ++playerHotbarCurrentSlot) {
            this.addSlot(new Slot(playerInventory, playerHotbarCurrentSlot, 8 + playerHotbarCurrentSlot * 18, 198));
        }

        int currentSlotCount = 0;
        int currentRowYPosition = 0;
        int playerCurrentInventorySlot;

        // The player inventory slots
        for (playerCurrentInventorySlot = 9; playerCurrentInventorySlot < 36; ++playerCurrentInventorySlot) {
            this.addSlot(new Slot(playerInventory, playerCurrentInventorySlot, 8 + currentSlotCount * 18, currentRowYPosition + 140));
            currentSlotCount++;
            if (currentSlotCount % 9 == 0) {
                currentSlotCount = 0;
                currentRowYPosition += 18;
            }
        }

        // The player armor slots
        currentRowYPosition = 0;
        currentSlotCount = 0;
        int currentPlayerArmorSlot;
        for (currentPlayerArmorSlot = 36; currentPlayerArmorSlot < 41; currentPlayerArmorSlot++, currentSlotCount++) {
            int finalCurrentSlotCount = currentSlotCount;
            final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[currentSlotCount];
            this.addSlot(new Slot(playerInventory, currentPlayerArmorSlot, 172, 198 - currentRowYPosition) {
                @Override
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[finalCurrentSlotCount]);
                }

                @Override
                public boolean canInsert(ItemStack stack) {
                    return equipmentSlot == MobEntity.getPreferredEquipmentSlot(stack);
                }
            });
            currentRowYPosition += 18;
        }

        int currentInventorySlot;
        currentRowYPosition = 0;
        currentSlotCount = 0;
        int deathBagSlotCount = 0;

        // The death bag inventory slots
        for (currentInventorySlot = 40; currentInventorySlot < 94; ++currentInventorySlot, deathBagSlotCount++) {
            this.addSlot(new Slot(deathBagInventory, deathBagSlotCount, 8 + currentSlotCount * 18, currentRowYPosition + 18) {
                @Override
                public boolean canInsert(ItemStack stack) {
                    return false;
                }
            });
            currentSlotCount++;
            if (currentSlotCount % 9 == 0) {
                currentSlotCount = 0;
                currentRowYPosition += 18;
            }
        }

        // The armor of the player in the death bag
        currentRowYPosition = 0;
        int deathArmorSlot;
        deathBagSlotCount = 54;
        int deathBagArmorCurrentSlot = 0;
        for (deathArmorSlot = 94; deathArmorSlot < 99; deathArmorSlot++, deathBagSlotCount++) {
            int finalDeathArmorSlot = deathArmorSlot;
            this.addSlot(new Slot(deathBagInventory, deathBagSlotCount, 172, 90 - currentRowYPosition) {
                @Override
                public Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[finalDeathArmorSlot - 94]);
                }

                @Override
                public boolean canInsert(ItemStack stack) {
                    return false;
                }
            });
            currentRowYPosition += 18;
        }

        if (playerInventory.player.getMainHandStack().getItem() instanceof DeathBagItem deathBagItem) {
            if (deathBagItem.getPlayerIsSneaking()) {
                for (int i = 0; i < deathBagInventory.size(); i++) {
                    if (!this.deathBagInventory.getStack(i).isEmpty()) {
                        transferSlot(playerInventory.player, i + 41);
                    }
                }
                deathBagItem.setPlayerIsSneaking(false);
            }
        }

        if (playerInventory.player.getOffHandStack().getItem() instanceof DeathBagItem deathBagItem) {
            if (deathBagItem.getPlayerIsSneaking()) {
                for (int i = 0; i < deathBagInventory.size(); i++) {
                    if (!this.deathBagInventory.getStack(i).isEmpty()) {
                        transferSlot(playerInventory.player, i + 41);
                    }
                }
                deathBagItem.setPlayerIsSneaking(false);
            }
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }


    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
        if (player instanceof ServerPlayerEntity) {
            if (slotIndex < 0) {
                if (slotIndex == -999 && actionType == SlotActionType.PICKUP) {
                    // Get the item stack
                    ItemStack cursorStack = this.getCursorStack().copy();
                    // Set the cursor stack to empty
                    this.setCursorStack(ItemStack.EMPTY);
                    // Drop the stack that was on the cursor
                    player.dropItem(cursorStack, true);
                }
                return;
            }

            if (actionType == SlotActionType.QUICK_CRAFT)
                this.onSlotClick(slotIndex, button, SlotActionType.PICKUP, player);
            if (actionType == SlotActionType.QUICK_MOVE) this.transferSlot(player, slotIndex);
            if (actionType == SlotActionType.PICKUP && button == 0) {
                Slot currentSlot = this.getSlot(slotIndex);
                if (this.getCursorStack() != null && !this.getCursorStack().isEmpty() && slotIndex > 40) {
                    // Do nothing
                } else if (this.getCursorStack() != null && !this.getCursorStack().isEmpty() && slotIndex < 41) {
                    if (this.getSlot(slotIndex).getStack() == ItemStack.EMPTY) {

                        ItemStack stack = this.getCursorStack().copy();
                        if (currentSlot.canInsert(stack)) {
                            currentSlot.insertStack(stack);
                            this.setCursorStack(ItemStack.EMPTY);
                        }
                    } else {
                        ItemStack currentSlotStack = currentSlot.getStack().copy();
                        ItemStack cursorSlotStack = this.getCursorStack().copy();
                        int maxStackCount = currentSlotStack.getMaxCount();
                        int totalCombination = currentSlotStack.getCount() + cursorSlotStack.getCount();
                        if (currentSlotStack.isItemEqual(cursorSlotStack) && currentSlotStack.isStackable() && totalCombination <= maxStackCount) {
                            currentSlot.getStack().increment(cursorSlotStack.getCount());
                            this.setCursorStack(ItemStack.EMPTY);
                        } else if (currentSlotStack.isItemEqual(cursorSlotStack) && currentSlotStack.isStackable() && totalCombination > maxStackCount) {
                            // Add to max
                            int totalToAdd = maxStackCount - currentSlotStack.getCount();
                            currentSlot.getStack().increment(totalToAdd);
                            this.getCursorStack().decrement(totalToAdd);
                        } else {
                            currentSlot.setStack(ItemStack.EMPTY);
                            currentSlot.insertStack(cursorSlotStack);
                            this.setCursorStack(ItemStack.EMPTY);
                            this.setCursorStack(currentSlotStack);
                        }
                    }
                } else {
                    ItemStack stack = currentSlot.getStack();
                    this.setCursorStack(stack);
                    currentSlot.setStack(ItemStack.EMPTY);
                }
            } else if (actionType == SlotActionType.PICKUP && button == 1) {
                Slot currentSlot = this.getSlot(slotIndex);
                ItemStack currentSlotStack = currentSlot.getStack().copy();
                if (this.getCursorStack().isEmpty()) {
                    // Get half the items
                    int itemCount = (int) Math.ceil(currentSlotStack.getCount() / 2);
                    ItemStack newStack = currentSlotStack;
                    newStack.setCount(itemCount);
                    this.setCursorStack(newStack);
                    currentSlot.getStack().decrement(itemCount);
                } else {
                    this.onSlotClick(slotIndex, 0, actionType, player);
                }
            }
        }
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        // If the index is on the player inventory do nothing
        if (index < 41) return ItemStack.EMPTY;
        else {
            // Get the slot
            Slot currentSlot = slots.get(index);
            // If the slot is not null and has a stack
            if (currentSlot != null && currentSlot.hasStack()) {
                /************************************************************************************
                 * Credit to gudenau for this if statement logic code:
                 * GitHub: https://github.com/gudenau
                 ************************************************************************************/
                if(index >= 95 && index <= 99) {
                    tryToPutArmorOnSlot(index, index - 59, currentSlot);
                } else {
                    int mappedPlayerIndex = index - 41;
                    // Get the stack to transfer
                    ItemStack originalStack = currentSlot.getStack();
                    ItemStack newStack = originalStack.copy();
                    // Put the item in an empty slot
                    // Get if the mapped index is free
                    if (this.freeSlots.contains(mappedPlayerIndex)) {
                        slots.get(mappedPlayerIndex).setStack(newStack);
                        // Remove the slot from the list of available slots
                        this.freeSlots.remove(Integer.valueOf(mappedPlayerIndex));
                        slots.get(index).setStack(ItemStack.EMPTY);
                        return ItemStack.EMPTY;
                    } else if (this.freeSlots.size() > 0) {
                        // Get the first free slot
                        Integer freeSlot = this.freeSlots.get(0);
                        // Set the stack
                        slots.get(freeSlot).setStack(newStack);
                        // Remove the slot from the list of available slots
                        this.freeSlots.remove(0);
                        slots.get(index).setStack(ItemStack.EMPTY);
                        return ItemStack.EMPTY;
                    } else {
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                // Return an empty stack because the slot is either null or has no stack
                return ItemStack.EMPTY;
            }
        }
        return ItemStack.EMPTY;
    }

    public ItemStack tryToPutArmorOnSlot(int index, int armorSlot, Slot currentSlot) {
        ItemStack originalStack = currentSlot.getStack();
        ItemStack newStack = originalStack.copy();
        if (this.freeArmorSlots.contains(armorSlot)) {
            // Put the boots on the player's boot slot
            slots.get(armorSlot).setStack(newStack);
            // Remove the slot from the list of available slots
            this.freeArmorSlots.remove(Integer.valueOf(armorSlot));
            // Set the item stack to be empty
            slots.get(index).setStack(ItemStack.EMPTY);
            return ItemStack.EMPTY;
        } else {
            // Put the item in an empty slot
            if (this.freeArmorSlots.size() > 0) {
                // Get the first free slot
                Integer freeSlot = this.freeSlots.get(0);
                // Set the stack
                slots.get(freeSlot).setStack(newStack);
                // Remove the slot from the list of available slots
                this.freeSlots.remove(0);
                slots.get(index).setStack(ItemStack.EMPTY);
                return ItemStack.EMPTY;
            } else {
                return ItemStack.EMPTY;
            }
        }
    }

    @Override
    public void close(PlayerEntity player) {
        if (player instanceof ServerPlayerEntity serverPlayerEntity) {
            // Get the player UUID as string
            String playerUUID = serverPlayerEntity.getUuidAsString();
            // Get the save path as string of the world folder
            String savePath = this.playerInventory.player.getServer().getSavePath(WorldSavePath.ROOT).toString();
            if (savePath.lastIndexOf('.') == savePath.length() - 1) {
                savePath = savePath.substring(0, savePath.length() - 1);
            }

            // If the deathbag is empty
            if (deathBagInventory.isEmpty()) {
                // Try to check if folder exists for DeathBagsRevived data. If it doesn't exist, create it
                File deathBagsRevivedFolder = new File(savePath + "deathbagsrevived");
                if (!deathBagsRevivedFolder.exists()) {
                    try {
                        // Create the file
                        Files.createDirectory(deathBagsRevivedFolder.toPath());
                    } catch (IOException ioException) {
                        DeathBagsRevivedMod.LOGGER.error("There was a problem creating the folder that should contain the DeathBagsRevived data as: " + deathBagsRevivedFolder.getPath().toString());
                        return;
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
                            // Remove the last list if the size of inventory is more than 0
                            if (playerInventories.size() > 0) {
                                playerInventories.remove(playerInventories.size() - 1);
                            }
                            previousData.put(playerUUID, playerInventories);
                            // Write to file
                            NbtIo.write(previousData, allPlayersInventoriesFile);
                            // Get rid of the item because it is empty
                            for (int i = 0; i < this.playerInventory.size(); i++) {
                                if (this.playerInventory.getStack(i) != null) {
                                    Item currentItem = this.playerInventory.getStack(i).getItem();
                                    if (currentItem instanceof DeathBagItem && !(currentItem instanceof GoldenDeathBagItem)) {
                                        this.playerInventory.setStack(i, ItemStack.EMPTY);
                                        break;
                                    }
                                }
                            }
                        } catch (IOException fileWriteIOException) {
                            DeathBagsRevivedMod.LOGGER.error("There was a problem trying to write to file: " + allPlayersInventoriesFile.getPath().toString());
                        }
                    }
                } else {
                    DeathBagsRevivedMod.LOGGER.error("The folder that should contain the DeathBagsRevived data as: " + deathBagsRevivedFolder.getPath().toString() + " does not exist or the program does not have write permissions to write in it");
                }
            }

            if (!deathBagInventory.isEmpty() && player instanceof ServerPlayerEntity serverPlayer) {
                // Create a container for the remaining inventory
                PlayerInventory temporaryInventory = new PlayerInventory(null);

                // Get the main inventory and put it on the deathbag inventory
                for (int i = 0; i < 36; i++) {
                    temporaryInventory.setStack(i, this.deathBagInventory.getStack(i));
                }

                // Get the armor inventory
                int currentArmorSlot = 54;
                for (int i = 36; i < 41; i++) {
                    // ItemStack currentArmorStack = temporaryInventory.getStack(i);
                    temporaryInventory.setStack(i, this.deathBagInventory.getStack(currentArmorSlot));
                    currentArmorSlot++;
                }

//                for (int i = 0; i < deathBagInventory.size(); i++) {
//                    temporaryInventory.setStack(i, deathBagInventory.getStack(i));
//                }
                // Save the player inventory to a nbt list
                NbtList list = new NbtList();
                temporaryInventory.writeNbt(list);

                // Try to check if folder exists for DeathBagsRevived data. If it doesn't exist, create it
                File deathBagsRevivedFolder = new File(savePath + "deathbagsrevived");
                if (!deathBagsRevivedFolder.exists()) {
                    try {
                        // Create the file
                        Files.createDirectory(deathBagsRevivedFolder.toPath());
                    } catch (IOException ioException) {
                        DeathBagsRevivedMod.LOGGER.error("There was a problem creating the folder that should contain the DeathBagsRevived data as: " + deathBagsRevivedFolder.getPath().toString());
                        return;
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
                            // Remove the last list
                            playerInventories.remove(playerInventories.size() - 1);
                            previousData.put(playerUUID, playerInventories);
                            // Write to file
                            NbtIo.write(previousData, allPlayersInventoriesFile);
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

        super.close(player);
    }

    public void fillDeathBagInventory() {
        if (this.playerInventory.player.getServer() != null) {
            // Get Player UUID as string
            String playerUUID = this.playerInventory.player.getUuidAsString();
            // Get the save path as string of the world folder
            String savePath = this.playerInventory.player.getServer().getSavePath(WorldSavePath.ROOT).toString();
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
                    return;
                }
            }

            // Get the File for the file that will contain the player data
            File allPlayersInventoriesFile = new File(deathBagsRevivedFolder.getPath() + FileSystems.getDefault().getSeparator() + "all_players_dbr_inventories.dat");
            // Check if the file exists
            if (allPlayersInventoriesFile.exists()) {
                try {
                    // Get the previous data
                    NbtCompound previousData = NbtIo.read(allPlayersInventoriesFile);
                    // Get the player previous compound
                    NbtList playerInventories = previousData.getList(playerUUID, NbtElement.LIST_TYPE);
                    // Get the last player inventory
                    NbtList lastList = playerInventories.getList(playerInventories.size() - 1);

                    PlayerInventory temporaryInventory = new PlayerInventory(null);
                    temporaryInventory.readNbt(lastList);

                    // Get the main inventory and put it on the deathbag inventory
                    for (int i = 0; i < 36; i++) {
                        this.deathBagInventory.setStack(i, temporaryInventory.getStack(i));
                    }

                    // Get the armor inventory
                    int currentArmorSlot = 54;
                    for (int i = 36; i < 41; i++) {
                        ItemStack currentArmorStack = temporaryInventory.getStack(i);
                        this.deathBagInventory.setStack(currentArmorSlot, currentArmorStack);
                        currentArmorSlot++;
                    }

                } catch (IOException fileWriteIOException) {
                    DeathBagsRevivedMod.LOGGER.error("There was a problem trying to write to file: " + allPlayersInventoriesFile.getPath().toString());
                }
            } else {
                return;
            }
        }
    }
}

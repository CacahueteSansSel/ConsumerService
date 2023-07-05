package dev.cacahuete.consume.ui;

import dev.cacahuete.consume.blocks.ItemStackHandlerWrapper;
import dev.cacahuete.consume.marketing.MarketingUtilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import dev.cacahuete.consume.ConsumerContainers;

// This is probably copy-pasted (stolen!) code from somewhere
// I don't have the source anymore but any help is accepted
// - Cacahuète
public class CashMachineContainer extends Container {

    public static CashMachineContainer newServer(int windowID, PlayerInventory playerInventory, ItemStackHandler inv, BlockPos pos) {
        return new CashMachineContainer(windowID, playerInventory, inv).withTileEntityPos(pos);
    }

    public static CashMachineContainer newClient(int windowID, PlayerInventory playerInventory, net.minecraft.network.PacketBuffer extraData) {
        //  don't need extraData for this example; if you want you can use it to provide extra information from the server, that you can use
        //  when creating the client container
        //  eg String detailedDescription = extraData.readString(128);
        int x = extraData.readInt();
        int y = extraData.readInt();
        int z = extraData.readInt();
        ItemStackHandler handler = new ItemStackHandler(TileEntitySlotCount);

        // on the client side there is no parent TileEntity to communicate with, so we:
        // 1) use a dummy inventory
        // 2) use "do nothing" lambda functions for canPlayerAccessInventory and markDirty
        return new CashMachineContainer(windowID, playerInventory, handler).withTileEntityPos(new BlockPos(x, y, z));
    }

    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)

    private static final int HotbarSlotCount = 9;
    private static final int PlayerInventoryRowCount = 3;
    private static final int PlayerInventoryColumnCount = 9;
    private static final int PlayerInventorySlotCount = PlayerInventoryColumnCount * PlayerInventoryRowCount;
    private static final int VanillaSlotCount = HotbarSlotCount + PlayerInventorySlotCount;

    private static final int VanillaFirstSlotIndex = 0;
    private static final int TileEntityInventoryFirstSlotIndex = VanillaFirstSlotIndex + VanillaSlotCount;
    private static final int TileEntitySlotCount = 13;  // must match TileEntityInventoryBasic.NUMBER_OF_SLOTS

    public static final int TileEntityInventoryX = 24;  // the ContainerScreenBasic needs to know these so it can tell where to draw the Titles
    public static final int TileEntityInventoryY = 16;  // the ContainerScreenBasic needs to know these so it can tell where to draw the Titles
    public static final int PlayerInventoryX = 7;
    public static final int PlayerInventoryY = 83;
    public static final int MoneyInventoryX = 115;
    public static final int MoneyInventoryY = 34;

    public PlayerInventory player;
    public BlockPos tileEntityPos;

    public CashMachineContainer withTileEntityPos(BlockPos tileEntityPos) {
        this.tileEntityPos = tileEntityPos;
        return this;
    }

    /**
     * Creates a container suitable for server side or client side
     * @param windowID ID of the container
     * @param playerInventory the inventory of the player
     * @param inv the inventory stored in the chest
     */
    private CashMachineContainer(int windowID, PlayerInventory playerInventory, ItemStackHandler inv) {
        super(ConsumerContainers.ContainerCashMachine, windowID);

        player = playerInventory;
        PlayerInvWrapper playerInventoryForge = new PlayerInvWrapper(playerInventory);  // wrap the IInventory in a Forge IItemHandler.
        // Not actually necessary - can use Slot(playerInventory) instead of SlotItemHandler(playerInventoryForge)
        this.inv = inv;

        int hotbarX = 8;
        int hotbarY = 142;
        // Add the players hotbar to the gui - the [xpos, ypos] location of each item
        for (int x = 0; x < HotbarSlotCount; x++) {
            int slotNumber = x;
            addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, hotbarX + x * 18, hotbarY));
        }

        // Add the rest of the player's inventory to the gui
        for (int y = 0; y < PlayerInventoryRowCount; y++) {
            for (int x = 0; x < PlayerInventoryColumnCount; x++) {
                int slotNumber = HotbarSlotCount + y * PlayerInventoryColumnCount + x;
                int xpos = PlayerInventoryX + 1 + x * 18;
                int ypos = PlayerInventoryY + 1 + y * 18;
                addSlot(new SlotItemHandler(playerInventoryForge, slotNumber,  xpos, ypos));
            }
        }

        ItemStackHandlerWrapper wrap = new ItemStackHandlerWrapper(inv, null);
        // The items inventory (where we put all items to buy)
        int slotIdx = 0;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(new Slot(wrap, slotIdx, TileEntityInventoryX + 1 + 18 * x, TileEntityInventoryY + 1 + 18 * y));
                slotIdx++;
            }
        }
        // The currency inventory (where we put currency)
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                addSlot(new CashMachineMoneySlot(wrap, slotIdx, MoneyInventoryX + 1 + 18 * x, MoneyInventoryY + 1 + 18 * y));
                slotIdx++;
            }
        }
    }

    public int getTotalPriceInEmeralds() {
        int priceTotal = 0;

        for (int i = 0; i < inv.getSlots() - 4; i++) {
            ItemStack item = inv.getStackInSlot(i);
            if (MarketingUtilities.isItemBought(item)) continue;

            priceTotal += MarketingUtilities.getItemPrice(item) * item.getCount();
        }

        return priceTotal;
    }

    // Vanilla calls this method every tick to make sure the player is still able to access the inventory, and if not closes the gui
    // Called on the SERVER side only
    @Override
    public boolean canInteractWith(PlayerEntity playerEntity)
    {
        // This is typically a check that the player is within 8 blocks of the container.
        //  Some containers perform it using just the block placement:
        //  return isWithinUsableDistance(this.iWorldPosCallable, playerIn, Blocks.MYBLOCK); eg see BeaconContainer
        //  where iWorldPosCallable is a lambda that retrieves the blockstate at a particular world blockpos
        // for other containers, it defers to the IInventory provided to the Container (i.e. the TileEntity) which does the same
        //  calculation
        // return this.furnaceInventory.isUsableByPlayer(playerEntity);
        // Sometimes it perform an additional check (eg for EnderChests - the player owns the chest)

        return true;
    }


    // This is where you specify what happens when a player shift clicks a slot in the gui
    //  (when you shift click a slot in the TileEntity Inventory, it moves it to the first available position in the hotbar and/or
    //    player inventory.  When you you shift-click a hotbar or player inventory item, it moves it to the first available
    //    position in the TileEntity inventory)
    // At the very least you must override this and return ItemStack.EMPTY or the game will crash when the player shift clicks a slot
    // returns ItemStack.EMPTY if the source slot is empty, or if none of the the source slot item could be moved
    //   otherwise, returns a copy of the source stack
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerEntity, int sourceSlotIndex)
    {
        Slot sourceSlot = inventorySlots.get(sourceSlotIndex);
        if (sourceSlot == null || !sourceSlot.getHasStack()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getStack();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (sourceSlotIndex >= VanillaFirstSlotIndex && sourceSlotIndex < VanillaFirstSlotIndex + VanillaSlotCount) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!mergeItemStack(sourceStack, TileEntityInventoryFirstSlotIndex, TileEntityInventoryFirstSlotIndex + TileEntitySlotCount, false)){
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (sourceSlotIndex >= TileEntityInventoryFirstSlotIndex && sourceSlotIndex < TileEntityInventoryFirstSlotIndex + TileEntitySlotCount) {
            // This is a TE slot so merge the stack into the players inventory
            if (!mergeItemStack(sourceStack, VanillaFirstSlotIndex, VanillaFirstSlotIndex + VanillaSlotCount, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.putStack(ItemStack.EMPTY);
        } else {
            sourceSlot.onSlotChanged();
        }

        sourceSlot.onTake(playerEntity, sourceStack);
        return copyOfSourceStack;
    }

    // pass the close container message to the parent inventory (not strictly needed for this example)
    //  see ContainerChest and TileEntityChest - used to animate the lid when no players are accessing the chest any more
    @Override
    public void onContainerClosed(PlayerEntity playerIn)
    {
        super.onContainerClosed(playerIn);
    }

    private ItemStackHandler inv;

    public class CashMachineMoneySlot extends Slot {
        public CashMachineMoneySlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack.getItem() == Items.EMERALD;
        }
    }
}

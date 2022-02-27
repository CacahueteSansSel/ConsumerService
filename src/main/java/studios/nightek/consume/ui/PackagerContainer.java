package studios.nightek.consume.ui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import studios.nightek.consume.ConsumerContainers;
import studios.nightek.consume.ConsumerItems;
import studios.nightek.consume.blocks.ItemStackHandlerWrapper;
import studios.nightek.consume.items.WrapperItem;

public class PackagerContainer extends Container {

    public static PackagerContainer newServer(int windowID, PlayerInventory playerInventory, ItemStackHandler inv, BlockPos pos) {
        return new PackagerContainer(windowID, playerInventory, inv).withTileEntityPos(pos);
    }

    public static PackagerContainer newClient(int windowID, PlayerInventory playerInventory, net.minecraft.network.PacketBuffer extraData) {
        //  don't need extraData for this example; if you want you can use it to provide extra information from the server, that you can use
        //  when creating the client container
        //  eg String detailedDescription = extraData.readString(128);
        ItemStackHandler handler = new ItemStackHandler(TE_INVENTORY_SLOT_COUNT);
        int x = extraData.readInt();
        int y = extraData.readInt();
        int z = extraData.readInt();

        // on the client side there is no parent TileEntity to communicate with, so we:
        // 1) use a dummy inventory
        // 2) use "do nothing" lambda functions for canPlayerAccessInventory and markDirty
        return new PackagerContainer(windowID, playerInventory, handler).withTileEntityPos(new BlockPos(x, y, z));
    }

    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)

    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;

    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;
    private static final int TE_INVENTORY_SLOT_COUNT = 3;  // must match TileEntityInventoryBasic.NUMBER_OF_SLOTS

    public static final int TILE_INVENTORY_YPOS = 16;  // the ContainerScreenBasic needs to know these so it can tell where to draw the Titles
    public static final int PLAYER_INVENTORY_YPOS = 83;

    public BlockPos tileEntityPos;

    public PackagerContainer withTileEntityPos(BlockPos tileEntityPos) {
        this.tileEntityPos = tileEntityPos;
        return this;
    }

    /**
     * Creates a container suitable for server side or client side
     * @param windowID ID of the container
     * @param playerInventory the inventory of the player
     * @param inv the inventory stored in the chest
     */
    private PackagerContainer(int windowID, PlayerInventory playerInventory, ItemStackHandler inv) {
        super(ConsumerContainers.ContainerPackager, windowID);

        PlayerInvWrapper playerInventoryForge = new PlayerInvWrapper(playerInventory);  // wrap the IInventory in a Forge IItemHandler.
        // Not actually necessary - can use Slot(playerInventory) instead of SlotItemHandler(playerInventoryForge)
        this.inv = inv;

        int hotbarX = 8;
        int hotbarY = 142;
        // Add the players hotbar to the gui - the [xpos, ypos] location of each item
        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) {
            int slotNumber = x;
            addSlot(new SlotItemHandler(playerInventoryForge, slotNumber, hotbarX + x * 18, hotbarY));
        }

        final int PLAYER_INVENTORY_XPOS = 7;
        // Add the rest of the player's inventory to the gui
        for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
                int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
                int xpos = PLAYER_INVENTORY_XPOS + 1 + x * 18;
                int ypos = PLAYER_INVENTORY_YPOS + 1 + y * 18;
                addSlot(new SlotItemHandler(playerInventoryForge, slotNumber,  xpos, ypos));
            }
        }

        final int TILE_INVENTORY_XPOS = 60;
        ItemStackHandlerWrapper wrap = new ItemStackHandlerWrapper(inv, null);

        // Add the different slots
        addSlot(new WrappingPackagerSlot(wrap, 0, 50, 14)); // The wrapping slot
        addSlot(new InputPackagerSlot(wrap, 1, 80, 14)); // The slot where you put the item to wrap
        addSlot(new Slot(wrap, 2, 80, 48)); // The output slot
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
        if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!mergeItemStack(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT, false)){
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (sourceSlotIndex >= TE_INVENTORY_FIRST_SLOT_INDEX && sourceSlotIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!mergeItemStack(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
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

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        super.onCraftMatrixChanged(inventoryIn);
    }

    private ItemStackHandler inv;

    public class WrappingPackagerSlot extends Slot {
        public WrappingPackagerSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return stack.getItem() == ConsumerItems.WRAPPER && !WrapperItem.hasContainedItem(stack);
        }
    }

    public class InputPackagerSlot extends Slot {
        public InputPackagerSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            return WrapperItem.canBeWrapped(stack.getItem());
        }
    }
}

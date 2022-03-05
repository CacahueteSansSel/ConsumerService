package studios.nightek.consume;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.TallBlockItem;
import net.minecraftforge.event.RegistryEvent;
import studios.nightek.consume.blocks.*;
import studios.nightek.consume.items.CommerceBlockItem;

import java.util.HashMap;

public class ConsumerBlocks {

    private static HashMap<Block, BlockItem> blockItems = new HashMap<>();
    public static final ProtectiveBlockBase PROTECTIVE_WALL = new ProtectiveBlockBase("protective_block");
    public static final ProtectiveBlockBase PROTECTIVE_TILE = new ProtectiveBlockBase("protective_tile");
    public static final ProtectiveLight PROTECTIVE_LAMP = new ProtectiveLight("protective_lamp");
    public static final ProtectiveBlockBase PROTECTIVE_CEILING_RED = new ProtectiveBlockBase("protective_ceiling_red");
    public static final ProtectiveBlockBase PROTECTIVE_CEILING_GREEN = new ProtectiveBlockBase("protective_ceiling_green");
    public static final ProtectiveBlockBase PROTECTIVE_CEILING_BLUE = new ProtectiveBlockBase("protective_ceiling_blue");
    public static final ProtectiveBlockBase PROTECTIVE_CEILING_GRAY = new ProtectiveBlockBase("protective_ceiling_gray");
    public static final ProtectiveBlockBase PROTECTIVE_CEILING_YELLOW = new ProtectiveBlockBase("protective_ceiling_yellow");
    public static final ProtectiveBlockBase PROTECTIVE_CEILING_BLACK = new ProtectiveBlockBase("protective_ceiling_black");
    public static final ProtectiveBlockTransparentBase PROTECTIVE_GLASS = new ProtectiveBlockTransparentBase("protective_glass");
    public static final ProtectiveModelBlock PROTECTIVE_DESK = new ProtectiveModelBlock("protective_desk")
            .withShape(0, 13, 0, 16, 16, 16)
            .withCollShape(0, 0, 0, 16, 24, 16);
    public static final ProtectiveShelfBlock PROTECTIVE_SHOP_SHELF_START = new ProtectiveShelfBlock("shop_shelf_start");
    public static final ProtectiveShelfBlock PROTECTIVE_SHOP_SHELF_MIDDLE = new ProtectiveShelfBlock("shop_shelf_middle");
    public static final ProtectiveShelfBlock PROTECTIVE_SHOP_SHELF_END = new ProtectiveShelfBlock("shop_shelf_end");
    public static final ProtectiveGlassShowcase PROTECTIVE_SHOWCASE = new ProtectiveGlassShowcase();
    public static final DetectorPressurePlate PROTECTIVE_DETECTOR_PRESSURE_PLATE = new DetectorPressurePlate(AbstractBlock.Properties.create(Material.IRON));
    public static final AlarmBlock PROTECTIVE_ALARM_BLOCK = new AlarmBlock();
    public static final ProtectiveCashMachineBlock PROTECTIVE_CASH_MACHINE_BLOCK = new ProtectiveCashMachineBlock();
    public static final ProtectiveEntranceDoorBlock PROTECTIVE_ENTRANCE_DOOR_BLOCK = new ProtectiveEntranceDoorBlock();
    public static final ShelfSignBlock PROTECTIVE_SHELF_SIGN_FOOD = new ShelfSignBlock("food");
    public static final ShelfSignBlock PROTECTIVE_SHELF_SIGN_ALCOHOLS = new ShelfSignBlock("alcohols");
    public static final ShelfSignBlock PROTECTIVE_SHELF_SIGN_CHECKOUT = new ShelfSignBlock("checkout");
    public static final ShelfSignBlock PROTECTIVE_SHELF_SIGN_CULTURE = new ShelfSignBlock("culture");
    public static final BankAccountControllerBlock BANK_ACCOUNT_CONTROLLER_BLOCK = new BankAccountControllerBlock();
    public static final ATMBlock PROTECTIVE_ATM_BLOCK = new ATMBlock();
    public static final RadioBlock RADIO = new RadioBlock();
    public static final PlateBlock PLATE = new PlateBlock();
    public static final TVBlock TV = new TVBlock();
    public static final CardboardBlock EMPTY_CARDBOARD = new CardboardBlock().withPrice(1).withChance(0.75f);
    public static final CardboardBlock TV_CARDBOARD = new CardboardBlock("tv", () -> TV.getDefaultState()).withPrice(150).withChance(0.25f);
    public static final CardboardBlock RADIO_CARDBOARD = new CardboardBlock("radio", () -> RADIO.getDefaultState()).withPrice(40).withChance(0.35f);
    public static final JukeboxSpeakerBlock JUKEBOX_SPEAKER = new JukeboxSpeakerBlock();
    public static final ConsumerOreBlock PETROLEUM_CRYSTAL_ORE = new ConsumerOreBlock("petroleum_crystal_ore", ConsumerItems.PETROLEUM_CRYSTAL, 1, 3);
    public static final ProtectiveBlockBase PROTECTIVE_WARNING_SIGN_BLOCK = new ProtectiveBlockBase("warning_sign_block");
    public static final CoffeeMachineBlock COFFEE_MACHINE = new CoffeeMachineBlock();
    public static final CardboardBlock COFFEE_MACHINE_CARDBOARD = new CardboardBlock("coffee_machine", () -> COFFEE_MACHINE.getDefaultState()).withPrice(29).withChance(0.35f);
    public static final DeliveryBikeBlock DELIVERY_BIKE_BLOCK = new DeliveryBikeBlock();
    public static final PackagerBlock PACKAGER = new PackagerBlock();

    public static Block[] all() {
        return new Block[] {
                PROTECTIVE_WALL,
                PROTECTIVE_TILE,
                PROTECTIVE_CEILING_RED,
                PROTECTIVE_CEILING_BLUE,
                PROTECTIVE_CEILING_GREEN,
                PROTECTIVE_GLASS,
                PROTECTIVE_LAMP,
                PROTECTIVE_DESK,
                PROTECTIVE_SHOP_SHELF_START,
                PROTECTIVE_SHOP_SHELF_MIDDLE,
                PROTECTIVE_SHOP_SHELF_END,
                PROTECTIVE_SHOWCASE,
                PROTECTIVE_DETECTOR_PRESSURE_PLATE,
                PROTECTIVE_ALARM_BLOCK,
                PROTECTIVE_CASH_MACHINE_BLOCK,
                PROTECTIVE_ATM_BLOCK,
                PROTECTIVE_ENTRANCE_DOOR_BLOCK,
                PROTECTIVE_CEILING_YELLOW,
                PROTECTIVE_CEILING_GRAY,
                PROTECTIVE_CEILING_BLACK,
                PROTECTIVE_SHELF_SIGN_FOOD,
                PROTECTIVE_SHELF_SIGN_ALCOHOLS,
                PROTECTIVE_SHELF_SIGN_CHECKOUT,
                PROTECTIVE_SHELF_SIGN_CULTURE,
                BANK_ACCOUNT_CONTROLLER_BLOCK,
                RADIO,
                PLATE,
                TV,
                EMPTY_CARDBOARD,
                TV_CARDBOARD,
                RADIO_CARDBOARD,
                JUKEBOX_SPEAKER,
                PETROLEUM_CRYSTAL_ORE,
                PROTECTIVE_WARNING_SIGN_BLOCK,
                COFFEE_MACHINE,
                COFFEE_MACHINE_CARDBOARD,
                DELIVERY_BIKE_BLOCK,
                PACKAGER
        };
    }

    public static void register(RegistryEvent.Register<Block> evt) {
        for (Block b : all()) {
            evt.getRegistry().register(b);
        }
    }

    public static BlockItem[] getBlockItems() {
        Block[] blocks = all();
        BlockItem[] items = new BlockItem[all().length];

        for (int idx = 0; idx < items.length; idx++) {
            if (blocks[idx] instanceof DoorBlock) {
                items[idx] = (BlockItem) new TallBlockItem(blocks[idx], new Item.Properties().group(ConsumerGroups.MAIN))
                        .setRegistryName(blocks[idx].getRegistryName().getPath());
            } else if (blocks[idx] instanceof CardboardBlock) {
                items[idx] = (BlockItem) new CommerceBlockItem(blocks[idx], new Item.Properties().group(ConsumerGroups.MAIN))
                        .withPrice(((CardboardBlock) blocks[idx]).price)
                        .setRegistryName(blocks[idx].getRegistryName().getPath());
            } else {
                items[idx] = (BlockItem) new BlockItem(blocks[idx], new Item.Properties().group(ConsumerGroups.MAIN))
                        .setRegistryName(blocks[idx].getRegistryName().getPath());
            }

            blockItems.put(blocks[idx], items[idx]);
        }

        return items;
    }

    public static BlockItem getItemForBlock(Block block)
    {
        return blockItems.get(block);
    }
}

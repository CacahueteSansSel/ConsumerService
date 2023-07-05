package dev.cacahuete.consume;

import dev.cacahuete.consume.items.*;
import net.minecraft.item.*;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.RegistryEvent;

public class ConsumerItems {

    public static final Item MOD_ICON_CART = new Item(new Item.Properties()).setRegistryName("cart");
    public static final FrozenFoodItemCouple FOOD_MINCED_BEEF = new FrozenFoodItemCouple("minced_beef", 6, 3, 1);
    public static final FrozenFoodItemCouple FOOD_ESCALOPE = new FrozenFoodItemCouple("escalope", 10, 5, 2);
    public static final FrozenFoodItemCouple FOOD_BAGUETTE = new FrozenFoodItemCouple("baguette", 2, 1, 1);
    public static final ContainerFoodItemCouple FOOD_CHIPS = new ContainerFoodItemCouple("chips", 2, 0, 16, 2);
    public static final ContainerFoodItemCouple FOOD_CANDY = new ContainerFoodItemCouple("candy", 1, 0, 16, 2);
    public static final ContainerFoodItemCouple FOOD_PIZZA = new ContainerFoodItemCouple("pizza", 4, 2, 4, 2);
    public static final Item FOOD_SPAGHETTI = new CommerceItem(new Item.Properties().group(ConsumerGroups.FOOD).food(new Food.Builder().hunger(1).saturation(0).fastToEat().build())).withPrice(3).setRegistryName("food_spaghett");
    public static final Item FOOD_SPAGHETTI_BOWL = new Item(new Item.Properties().group(ConsumerGroups.FOOD).food(new Food.Builder().hunger(10).saturation(7).fastToEat().build())).setRegistryName("bowl_spaghett");
    public static final DrinkableItem WATER_BOWL = new DrinkableItem("bowl_water", 0, 0);
    public static final DrinkableItem SODA_BOTTLE = new DrinkableItem("soda_bottle", 2, 2);
    public static final Item LUMINESCENT_SPIRIT_BOTTLE = new AlcoholDrinkableItem("luminescent_spirit_bottle", AlcoholDrinkableItem.AlcoholStrength.Medium).withPrice(6);
    public static final DrinkableItem COFFEE_CUP_FILLED = new DrinkableItem("coffee_cup_filled", 16, 4);
    public static final Item ALCOHOL_BOTTLE = new AlcoholDrinkableItem("alcohol_bottle", AlcoholDrinkableItem.AlcoholStrength.High).withPrice(5);
    public static final Item WATER_BOTTLE = new DrinkableItem("water_bottle", 10, 4).withPrice(1);
    public static final Item ORANGE_JUICE_BOTTLE = new DrinkableItem("orange_juice_bottle", 0, 8).withPrice(3);
    public static final Item CREEPER_ENERGY_DRINK = new DrinkableItem("creeper_energy_drink", 2, 9).withPrice(5);
    public static final ContainerFoodItemCouple FOOD_CHOCOLATE_BAR = new ContainerFoodItemCouple("chocolate_bar", 5, 1, 5, 2);
    public static final ContainerFoodItemCouple FOOD_PURPLODOCUS = new ContainerFoodItemCouple("purplodocus", 7, 0, 5, 4);
    public static final ContainerFoodItemCouple FOOD_STRAWBERRY_YOGURT = new ContainerFoodItemCouple("strawberry_yogurt", 3, 1, 20, 2);
    public static final ContainerFoodItemCouple FOOD_PEACH_YOGURT = new ContainerFoodItemCouple("peach_yogurt", 3, 1, 20, 2);
    public static final ContainerFoodItemCouple FOOD_HAM = new ContainerFoodItemCouple("ham", 5, 0, 1, 3);
    public static final ContainerFoodItemCouple FOOD_ILLUMINACHIPS = new ContainerFoodItemCouple("illuminachips", 3, 1, 24, 3);
    public static final ContainerFoodItemCouple FOOD_CEREALS = new ContainerFoodItemCouple("cereals", 6, 5, 2, 4);
    public static final ContainerFoodItemCouple FOOD_SLICED_BREAD = new ContainerFoodItemCouple("sliced_bread", 4, 2, 6, 2);
    public static final Item ALCOHOL_90 = new AlcoholDrinkableItem("alcohol_90", AlcoholDrinkableItem.AlcoholStrength.Lethal).withPrice(6);
    public static final Item ALCOHOL_CATERINA = new AlcoholDrinkableItem("alcohol_caterina", AlcoholDrinkableItem.AlcoholStrength.Light).withPrice(4);
    public static final Item HUNGER_GUM = new CommerceItem(new Item.Properties().group(ConsumerGroups.FOOD).food(new Food.Builder().effect(() -> new EffectInstance(Effects.HUNGER, 50, 255), 1f).setAlwaysEdible().build())).setRegistryName("hunger_gum");
    public static final Item COFFEE_CUP = new CommerceItem(new Item.Properties().group(ConsumerGroups.FOOD)).setRegistryName("coffee_cup_empty");
    public static final Item EMPTY_POT = new CommerceItem(new Item.Properties().group(ConsumerGroups.FOOD)).setRegistryName("empty_pot");
    public static final Item COFFEE_PACKET = new CommerceItem(new Item.Properties().group(ConsumerGroups.FOOD)).withPrice(2).setRegistryName("coffee");
    public static final Item HONEY_POT = new CommerceHoneyBottleItem(new Item.Properties().group(ConsumerGroups.FOOD).food(new Food.Builder().hunger(8).saturation(0.3f).build())).withPrice(3).setRegistryName("honey_pot");
    public static final Item DISC_SLEEVE_EMPTY = new DiscSleeveItem().withPrice(1);
    public static final Item DISC_SLEEVE_13 = new DiscSleeveItem((MusicDiscItem) Items.MUSIC_DISC_13, "13").withPrice(2);
    public static final Item DISC_SLEEVE_BLOCKS = new DiscSleeveItem((MusicDiscItem) Items.MUSIC_DISC_BLOCKS, "blocks").withPrice(3);
    public static final Item DISC_SLEEVE_CAT = new DiscSleeveItem((MusicDiscItem) Items.MUSIC_DISC_CAT, "cat", () -> Items.COD).withPrice(5);
    public static final Item DISC_SLEEVE_CHIRP = new DiscSleeveItem((MusicDiscItem) Items.MUSIC_DISC_CHIRP, "chirp").withPrice(2);
    public static final Item DISC_SLEEVE_FAR = new DiscSleeveItem((MusicDiscItem) Items.MUSIC_DISC_FAR, "far", () -> Items.COMPASS).withPrice(5);
    public static final Item DISC_SLEEVE_MALL = new DiscSleeveItem((MusicDiscItem) Items.MUSIC_DISC_MALL, "mall").withPrice(2);
    public static final Item DISC_SLEEVE_MELLOHI = new DiscSleeveItem((MusicDiscItem) Items.MUSIC_DISC_MELLOHI, "mellohi", () -> ConsumerItems.STICKER_MANIGANCE).withPrice(4);
    public static final Item DISC_SLEEVE_PIGSTEP = new DiscSleeveItem((MusicDiscItem) Items.MUSIC_DISC_PIGSTEP, "pigstep").withPrice(4);
    public static final Item DISC_SLEEVE_STAL = new DiscSleeveItem((MusicDiscItem) Items.MUSIC_DISC_STAL, "stal").withPrice(2);
    public static final Item DISC_SLEEVE_STRAD = new DiscSleeveItem((MusicDiscItem) Items.MUSIC_DISC_STRAD, "strad", () -> ConsumerItems.STICKER_HOLIDAY).withPrice(3);
    public static final Item DISC_SLEEVE_WAIT = new DiscSleeveItem((MusicDiscItem) Items.MUSIC_DISC_WAIT, "wait").withPrice(3);
    public static final Item DISC_SLEEVE_WARD = new DiscSleeveItem((MusicDiscItem) Items.MUSIC_DISC_WARD, "ward").withPrice(3);
    public static final Item STICKER_HOLIDAY = new Item(new Item.Properties().maxStackSize(1).group(ConsumerGroups.MAIN)).setRegistryName("sticker_holiday");
    public static final Item STICKER_MANIGANCE = new Item(new Item.Properties().maxStackSize(1).group(ConsumerGroups.MAIN)).setRegistryName("sticker_manigance");
    public static final Item CREDIT_CARD = new CreditCardItem();
    public static final Item SWISS_KNIFE = new CommerceItem(new Item.Properties().maxStackSize(1).group(ConsumerGroups.MAIN)).withPrice(2).setRegistryName("swiss_knife");
    public static final Item COMMERCE_AXE = new CommerceAxeItem("commerce_axe", ItemTier.IRON, 3.5f, -3f);
    public static final Item COMMERCE_PICKAXE = new CommercePickaxeItem("commerce_pickaxe", ItemTier.IRON, 1, -2f);
    public static final Item COMMERCE_SHOVEL = new CommerceShovelItem("commerce_shovel", ItemTier.IRON, 1.35f, -3f);
    public static final Item RECEIPT = new ReceiptItem();
    public static final Item ROOTKIT = new RootkitItem();
    public static final Item PETROLEUM_CRYSTAL = new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName("petroleum_crystal");
    public static final Item MOLTEN_PETROLEUM = new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName("molten_petroleum");
    public static final Item PLASTIC = new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName("plastic");
    public static final Item PLASTIC_PLATE = new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName("plastic_plate");
    public static final Item PROTECTIVE_COAT_SPRAY = new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName("protective_coat_spray");
    public static final Item CREDIT_CHIP = new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName("credit_chip");
    public static final Item SCREEN = new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName("screen");
    public static final Item ANTENNA = new Item(new Item.Properties().group(ItemGroup.MISC)).setRegistryName("antenna");
    public static final Item WRAPPER = new WrapperItem(new Item.Properties().group(ConsumerGroups.MAIN).maxStackSize(1));

    public static Item[] all() {
        return new Item[] {
                WATER_BOWL,
                MOD_ICON_CART,
                FOOD_SPAGHETTI,
                FOOD_SPAGHETTI_BOWL,
                ALCOHOL_BOTTLE,
                ALCOHOL_90,
                ALCOHOL_CATERINA,
                LUMINESCENT_SPIRIT_BOTTLE,
                WATER_BOTTLE,
                SODA_BOTTLE,
                CREEPER_ENERGY_DRINK,
                HUNGER_GUM,
                EMPTY_POT,
                HONEY_POT,
                COFFEE_CUP,
                COFFEE_CUP_FILLED,
                COFFEE_PACKET,
                DISC_SLEEVE_EMPTY,
                DISC_SLEEVE_13,
                DISC_SLEEVE_BLOCKS,
                DISC_SLEEVE_CAT,
                DISC_SLEEVE_CHIRP,
                DISC_SLEEVE_FAR,
                DISC_SLEEVE_MALL,
                DISC_SLEEVE_MELLOHI,
                DISC_SLEEVE_PIGSTEP,
                DISC_SLEEVE_STAL,
                DISC_SLEEVE_STRAD,
                DISC_SLEEVE_WAIT,
                DISC_SLEEVE_WARD,
                STICKER_HOLIDAY,
                STICKER_MANIGANCE,
                CREDIT_CARD,
                ORANGE_JUICE_BOTTLE,
                SWISS_KNIFE,
                COMMERCE_AXE,
                COMMERCE_PICKAXE,
                COMMERCE_SHOVEL,
                RECEIPT,
                ROOTKIT,
                PETROLEUM_CRYSTAL,
                MOLTEN_PETROLEUM,
                PLASTIC,
                PLASTIC_PLATE,
                PROTECTIVE_COAT_SPRAY,
                CREDIT_CHIP,
                SCREEN,
                ANTENNA,
                WRAPPER
        };
    }

    public static IItemCouple[] allCouples() {
        return new IItemCouple[] {
                FOOD_MINCED_BEEF,
                FOOD_CHIPS,
                FOOD_ILLUMINACHIPS,
                FOOD_CANDY,
                FOOD_PIZZA,
                FOOD_ESCALOPE,
                FOOD_PURPLODOCUS,
                FOOD_CHOCOLATE_BAR,
                FOOD_STRAWBERRY_YOGURT,
                FOOD_PEACH_YOGURT,
                FOOD_BAGUETTE,
                FOOD_HAM,
                FOOD_CEREALS,
                FOOD_SLICED_BREAD
        };
    }

    public static void register(RegistryEvent.Register<Item> evt) {
        for (Item b : all()) {
            evt.getRegistry().register(b);
        }
        for (IItemCouple b : allCouples()) {
            evt.getRegistry().registerAll(b.all());
        }
        for (Item blkItem : ConsumerBlocks.getBlockItems()) {
            evt.getRegistry().register(blkItem);
        }
    }

    public static boolean isItemFromMod(Item item) {
        for (Item modItem : all()) {
            if (modItem.getRegistryName().toString().equals(item.getRegistryName().toString())) return true;
        }

        for (IItemCouple modItem : allCouples()) {
            for (Item coupleItem : modItem.all()) {
                if (coupleItem.getRegistryName().toString().equals(item.getRegistryName().toString())) return true;
            }
        }
        return false;
    }
}

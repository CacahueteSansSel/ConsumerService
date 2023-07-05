package dev.cacahuete.consume.items;

import dev.cacahuete.consume.marketing.MarketingUtilities;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import dev.cacahuete.consume.ConsumerGroups;

import javax.annotation.Nullable;
import java.util.List;

public class ContainerFoodItemCouple implements IItemCouple {

    public int givenHunger;
    public int givenSaturation;
    public int givenCount;
    public int price;
    public String name;

    public ContainerItem container;
    public FoodItem food;

    public ContainerFoodItemCouple(String name, int hunger, int saturation, int count) {
        givenHunger = hunger;
        givenSaturation = saturation;
        givenCount = count;
        this.price = 1;
        this.name = name;

        food = new FoodItem(this);
        container = (ContainerItem)new ContainerItem(this).withPrice(price);
    }

    public ContainerFoodItemCouple(String name, int hunger, int saturation, int count, int price) {
        givenHunger = hunger;
        givenSaturation = saturation;
        givenCount = count;
        this.price = price;
        this.name = name;

        food = new FoodItem(this);
        container = (ContainerItem)new ContainerItem(this).withPrice(price);
    }

    @Override
    public Item[] all() {
        return new Item[] {
                container,
                food
        };
    }

    public class ContainerItem extends CommerceItem {
        Item foodItem;
        int foodItemCount = 1;
        public ContainerItem(ContainerFoodItemCouple couple) {
            super(new Properties().maxStackSize(1).group(ConsumerGroups.FOOD));
            foodItem = couple.food;
            foodItemCount = couple.givenCount;
            setRegistryName("container_" + couple.name);
        }

        @Override
        public ActionResultType onItemUse(ItemUseContext context) {
            if (MarketingUtilities.isCommerceItem(context.getItem())
                    && !MarketingUtilities.isItemBought(context.getItem())
                    && !context.getPlayer().isCreative())
                return ActionResultType.FAIL;

            BlockPos pos = context.getPos();
            World worldIn = context.getWorld();
            PlayerEntity ply = context.getPlayer();
            CompoundNBT nbt = context.getItem().getTag();
            ItemStack finalItem = new ItemStack(foodItem, foodItemCount);
            if (nbt != null) finalItem.setTag(nbt);
            ply.inventory.removeStackFromSlot(ply.inventory.getSlotFor(context.getItem())); // The Container Item must be alone in the slot !
            ply.inventory.addItemStackToInventory(finalItem);
            worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_COMPOSTER_EMPTY, SoundCategory.PLAYERS, 1f, 1f, false);
            return ActionResultType.SUCCESS;
        }

        @Override
        public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
            super.addInformation(stack, worldIn, tooltip, flagIn);
            if (foodItem == null) {
                tooltip.add(new StringTextComponent("Contains a NullPointerException ?!"));
                return;
            }
            if (foodItemCount <= 1) return;
            tooltip.add(new StringTextComponent(TextFormatting.YELLOW + "Contains " + foodItemCount + " " + foodItem.getName().getString()));
        }
    }

    public class FoodItem extends Item {
        public FoodItem(ContainerFoodItemCouple couple) {
            super(new Properties().food(new Food.Builder().meat().saturation(couple.givenSaturation).hunger(couple.givenHunger).build())
                .group(ConsumerGroups.FOOD));
            setRegistryName("food_" + couple.name);
        }
    }
}

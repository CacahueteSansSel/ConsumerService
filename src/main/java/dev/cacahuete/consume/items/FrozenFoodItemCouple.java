package dev.cacahuete.consume.items;

import net.minecraft.item.Food;
import net.minecraft.item.Item;
import dev.cacahuete.consume.ConsumerGroups;

public class FrozenFoodItemCouple implements IItemCouple {

    public int givenHunger;
    public int givenSaturation;
    public int price;
    public String name;

    public FrozenItem frozen;
    public BakedItem baked;

    public FrozenFoodItemCouple(String name, int hunger, int saturation, int price) {
        givenHunger = hunger;
        givenSaturation = saturation;
        this.name = name;
        this.price = price;

        frozen = (FrozenItem)new FrozenItem(this).withPrice(price);
        baked = (BakedItem)new BakedItem(this).withPrice(price);
    }

    @Override
    public Item[] all() {
        return new Item[] {
                frozen,
                baked
        };
    }

    public class FrozenItem extends CommerceItem {
        public FrozenItem(FrozenFoodItemCouple couple) {
            super(new Properties().food(new Food.Builder().fastToEat().saturation(0).hunger(1).build())
                .group(ConsumerGroups.FOOD));
            setRegistryName("frozen_" + couple.name);
        }
    }

    public class BakedItem extends CommerceItem {
        public BakedItem(FrozenFoodItemCouple couple) {
            super(new Properties().food(new Food.Builder().meat().saturation(couple.givenSaturation).hunger(couple.givenHunger).build())
                .group(ConsumerGroups.FOOD));
            setRegistryName("baked_" + couple.name);
        }
    }
}

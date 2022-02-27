package studios.nightek.consume.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Food;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import studios.nightek.consume.ConsumerGroups;

import javax.annotation.Nullable;
import java.util.List;

public class AlcoholDrinkableItem extends CommerceItem {
    AlcoholStrength strength;

    public AlcoholDrinkableItem(String name, AlcoholStrength strength) {
        super(new Properties().group(ConsumerGroups.FOOD).food(getFoodFromStrength(strength)));
        this.strength = strength;
        setRegistryName(name);
    }

    public static Food getFoodFromStrength(AlcoholStrength strength) {
        switch (strength) {
            case Light:
                return new Food.Builder()
                        .saturation(0).hunger(1)
                        .effect(() -> new EffectInstance(Effects.HUNGER, 20), 0.45f)
                        .effect(() -> new EffectInstance(Effects.POISON, 50), 0.05f)
                        .effect(() -> new EffectInstance(Effects.INSTANT_HEALTH, 1), 1f)
                        .build();
            case Medium:
                return new Food.Builder()
                        .saturation(0).hunger(1)
                        .effect(() -> new EffectInstance(Effects.HUNGER, 50), 0.5f)
                        .effect(() -> new EffectInstance(Effects.POISON, 70), 0.15f)
                        .effect(() -> new EffectInstance(Effects.INSTANT_HEALTH, 1), 0.7f)
                        .build();
            case High:
                return new Food.Builder()
                        .saturation(0).hunger(0)
                        .effect(() -> new EffectInstance(Effects.HUNGER, 70), 0.65f)
                        .effect(() -> new EffectInstance(Effects.POISON, 90), 0.2f)
                        .effect(() -> new EffectInstance(Effects.INSTANT_HEALTH, 1), 0.5f)
                        .build();
            case Lethal:
                return new Food.Builder().saturation(0).hunger(0)
                        .effect(() -> new EffectInstance(Effects.INSTANT_DAMAGE, 1, 65535), 1f)
                        .build();
        }
        return null;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new StringTextComponent(((strength == AlcoholStrength.High || strength == AlcoholStrength.Lethal) ? TextFormatting.DARK_RED.toString() : TextFormatting.RED.toString()) + super.getDisplayName(stack).getString());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent("item.consume.alcohol"));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    public enum AlcoholStrength {
        Light,
        Medium,
        High,
        Lethal
    }
}

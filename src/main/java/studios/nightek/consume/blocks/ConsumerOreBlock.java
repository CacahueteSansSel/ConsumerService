package studios.nightek.consume.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootContext;
import net.minecraftforge.common.ToolType;

import java.util.ArrayList;
import java.util.List;

public class ConsumerOreBlock extends Block {
    Item droppedItem;
    int dropCountMin;
    int dropCountMax;

    public ConsumerOreBlock(String name, Item itemDrop, int itemDropCountMin, int itemDropCountMax) {
        super(Properties.create(Material.ROCK).setRequiresTool().hardnessAndResistance(3.0F, 3.0F)
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(2));
        // ^^ Same as the Iron Ore

        droppedItem = itemDrop;
        dropCountMin = itemDropCountMin;
        dropCountMax = itemDropCountMax;

        setRegistryName(name);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        ArrayList<ItemStack> items = new ArrayList<>();

        int count = dropCountMin + builder.getWorld().rand.nextInt(dropCountMax - dropCountMin + 1);
        items.add(new ItemStack(droppedItem, count));

        return items;
    }
}

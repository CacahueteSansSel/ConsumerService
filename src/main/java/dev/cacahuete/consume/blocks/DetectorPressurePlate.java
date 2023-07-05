package dev.cacahuete.consume.blocks;

import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import dev.cacahuete.consume.marketing.MarketingUtilities;

import java.util.List;

public class DetectorPressurePlate extends AbstractPressurePlateBlock {
    //Based on PressurePlateBlock's code, but with additions

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public DetectorPressurePlate(AbstractBlock.Properties propertiesIn) {
        super(propertiesIn.hardnessAndResistance(-1f, 3600000.0f)
                .sound(SoundType.METAL));
        setRegistryName("detector_pressure_plate");
        this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, Boolean.valueOf(false)));
    }

    protected int getRedstoneStrength(BlockState state) {
        return state.get(POWERED) ? 15 : 0;
    }

    protected BlockState setRedstoneStrength(BlockState state, int strength) {
        return state.with(POWERED, Boolean.valueOf(strength > 0));
    }

    protected void playClickOnSound(IWorld worldIn, BlockPos pos) {
        worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
    }

    protected void playClickOffSound(IWorld worldIn, BlockPos pos) {
        worldIn.playSound(null, pos, SoundEvents.BLOCK_STONE_PRESSURE_PLATE_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
    }

    protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
        AxisAlignedBB axisalignedbb = PRESSURE_AABB.offset(pos);
        List<? extends Entity> list;
        list = worldIn.getEntitiesWithinAABB(PlayerEntity.class, axisalignedbb);

        if (!list.isEmpty()) {
            for (Entity entity : list) {
                if (entity.doesEntityNotTriggerPressurePlate()) continue;

                PlayerEntity ply = (PlayerEntity) entity;
                for (ItemStack stack : ply.inventory.mainInventory) {
                    if (MarketingUtilities.isCommerceItem(stack) && !MarketingUtilities.isItemBought(stack)) {
                        return 15;
                    }
                }
            }
        }

        return 0;
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }
}

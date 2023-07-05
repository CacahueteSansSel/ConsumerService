package dev.cacahuete.consume.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import dev.cacahuete.consume.ui.ATMScreen;
import dev.cacahuete.consume.ui.BankPortalLoginScreen;
import dev.cacahuete.consume.ui.BankPortalResponseScreen;

import javax.annotation.Nullable;

public class ATMBlock extends ProtectiveBlockBase {
    public static final DirectionProperty Facing = HorizontalBlock.HORIZONTAL_FACING;

    public ATMBlock() {
        super("atm");
        setDefaultState(this.getDefaultState().with(Facing, Direction.NORTH));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        builder.add(Facing);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(Facing, context.getPlacementHorizontalFacing().getOpposite());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote) return ActionResultType.SUCCESS; // Client side only
        Minecraft mc = Minecraft.getInstance();
        mc.displayGuiScreen(new BankPortalLoginScreen((scr, packet) -> {
            if (packet.isError) {
                mc.displayGuiScreen(new BankPortalResponseScreen(packet));
            } else {
                mc.displayGuiScreen(new ATMScreen(packet));
            }
        }));
        return ActionResultType.PASS;
    }
}

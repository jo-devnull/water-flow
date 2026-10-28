package github.jodevnull.waterflow.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.TierSortingRegistry;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("deprecation")
public class WaterChannelBlock extends HorizontalDirectionalBlock implements IWaterChannel
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

    public WaterChannelBlock(Block parent) {
        super(BlockBehaviour.Properties.copy(parent));
        this.registerDefaultState(this.getStateDefinition().any()
            .setValue(FACING, Direction.NORTH)
        );
    }

    public static boolean handleInteraction(Level level, Player player, ItemStack itemStack, BlockHitResult hit) {
        final var blockState = level.getBlockState(hit.getBlockPos());

        if (itemStack.getItem() instanceof PickaxeItem item) {
            final Direction dir = blockState.getValue(FACING);

            if (TierSortingRegistry.isCorrectTierForDrops(item.getTier(), blockState)) {
                final BlockState rotated = blockState.setValue(FACING, player.isShiftKeyDown()
                    ? player.getDirection()
                    : dir.getClockWise()
                );

                return level.setBlockAndUpdate(hit.getBlockPos(), rotated);
            }
        }

        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockPos pos = pContext.getClickedPos();
        Direction facing = pContext.getHorizontalDirection();
        BlockState neighborState;

        if (pContext.getPlayer() != null && !pContext.getPlayer().isSteppingCarefully()) {
            for (final var dir : Direction.Plane.HORIZONTAL) {
                neighborState = pContext.getLevel().getBlockState(pos.relative(dir));

                if (neighborState.getBlock() instanceof WaterChannelBlock) {
                    facing = neighborState.getValue(FACING);
                    break;
                }
            }
        }

        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
}

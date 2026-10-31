package github.jodevnull.waterflow.block;

import github.jodevnull.waterflow.ModBlocks;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("deprecation")
public class ChannelBlock extends HorizontalDirectionalBlock implements IChannelBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

    private final Block parent;

    public ChannelBlock(Block parent) {
        super(BlockBehaviour.Properties.copy(parent));
        this.parent = parent;
        this.registerDefaultState(this.getStateDefinition().any()
            .setValue(FACING, Direction.NORTH)
        );
    }

    public Block getParent() {
        return this.parent;
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

                if (neighborState.getBlock() instanceof ChannelBlock) {
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

    public static final class BlockMap
    {
        private static final Map<Block, Supplier<ChannelBlock>> blockMap = new HashMap<>();

        public static void init() {
            blockMap.put(Blocks.SAND,       ModBlocks.SAND_CHANNEL);
            blockMap.put(Blocks.DIRT,       ModBlocks.DIRT_CHANNEL);
            blockMap.put(Blocks.DIRT_PATH,  ModBlocks.DIRT_CHANNEL);
        }

        @Nullable
        public static BlockState get(BlockState input) {
            Supplier<ChannelBlock> output;

            if (input.getBlock() instanceof ChannelBlock block) {
                output = blockMap.get(block.getParent());
            } else {
                output = blockMap.get(input.getBlock());
            }

            return output == null ? null : output.get().defaultBlockState();
        }
    }
}

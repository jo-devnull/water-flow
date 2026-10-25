package github.jodevnull.waterflow.content.block;

import github.jodevnull.waterflow.core.api.IWaterFlow;
import github.jodevnull.waterflow.core.flow.FlowHandler;
import github.jodevnull.waterflow.core.fluid.FluidUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class WaterChannelBlock extends HorizontalDirectionalBlock implements IWaterFlow
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty FLOWING = BooleanProperty.create("flowing");

    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

    private final Block parent;

    public WaterChannelBlock(Block parent) {
        super(BlockBehaviour.Properties.copy(parent));
        this.parent = parent;
        this.registerDefaultState(this.getStateDefinition().any()
            .setValue(FACING, Direction.NORTH)
            .setValue(FLOWING, false)
        );
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return FluidUtils.getWater(pState.getValue(FLOWING) ? 1 : 0);
    }

    @Override
    public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        if (!canSurvive(pState, pLevel, pPos)) {
            turnToParent((null), pState, pLevel, pPos);
        }
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (!pState.canSurvive(pLevel, pCurrentPos) || pFacing == Direction.UP) {
            pLevel.scheduleTick(pCurrentPos, this, 1);
        }

        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, FLOWING);
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

    private void turnToParent(@Nullable Entity pEntity, BlockState pState, Level pLevel, BlockPos pPos) {
        BlockState blockstate = pushEntitiesUp(pState, parent.defaultBlockState(), pLevel, pPos);
        pLevel.setBlockAndUpdate(pPos, blockstate);
        pLevel.gameEvent(GameEvent.BLOCK_CHANGE, pPos, GameEvent.Context.of(pEntity, blockstate));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
        final var blockState = pLevel.getBlockState(pPos.above());
        return blockState.isAir() || blockState.is(Blocks.WATER);
    }

    @Override
    public int getVolume(BlockState state) {
        return 7;
    }

    @Override
    public byte blockFlowIn(BlockState state) {
        return FlowHandler.all();
    }

    @Override
    public byte blockFlowOut(BlockState state) {
        return FlowHandler.get(Direction.DOWN);
    }

    @Override
    public float fluidOffset(BlockState state) {
        return .88888888888888f;
    }
}

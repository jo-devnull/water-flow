package github.jodevnull.waterflow.content.block;

import github.jodevnull.waterflow.ModBlocks;
import github.jodevnull.waterflow.core.WaterManager;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PickaxeItem;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.TierSortingRegistry;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("deprecation")
public class WaterChannelBlock extends HorizontalDirectionalBlock
{
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

    public WaterChannelBlock(Block parent) {
        super(BlockBehaviour.Properties.copy(parent).lootFrom(() -> parent));
        this.registerDefaultState(this.getStateDefinition().any()
            .setValue(FACING, Direction.NORTH)
        );
    }

    public static void flowWaterAbove(WaterManager manager, BlockState state, BlockPos pos, int level) {
        final var dir = state.getValue(FACING);
        final int nextLevel = manager.getWaterLevel(pos.relative(dir));

        if (level <= 0)
            return;

        if (level > 1 && level >= (nextLevel + 1) && (nextLevel >= 0 && nextLevel < 8)) {
            manager.setWater(pos, level - 1);
            manager.setWater(pos.relative(dir), nextLevel + 1);
        }
    }

    public static boolean isChannel(BlockGetter getter, BlockPos pos) {
        final BlockState state = getter.getBlockState(pos);
        return (state.getBlock() instanceof WaterChannelBlock);// && state.getValue(TIME) == 0;
    }

    //  TODO: reimplement this
    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return true;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {

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

    public static boolean handleInteraction(Level level, Player player, InteractionHand hand, ItemStack itemStack, BlockHitResult hit) {
        final var blockState = level.getBlockState(hit.getBlockPos());

        if (hit.getDirection() != Direction.UP) {
            return false;
        }

        if (itemStack.getItem() instanceof PickaxeItem item) {
            final var output = (WaterChannelBlock) BlockMap.get(blockState.getBlock());

            if (output != null) {
                if (TierSortingRegistry.isCorrectTierForDrops(item.getTier(), blockState)) {
                    final BlockPlaceContext context = new BlockPlaceContext(player, hand, itemStack, hit);
                    level.setBlockAndUpdate(hit.getBlockPos(), output.getStateForPlacement(context));
                    return true;
                }
            }
        }

        return false;
    }

    public static final class BlockMap {
        private static final Map<Block, Block> map = new HashMap<>();

        public static void init() {
            // dirt
            add(ModBlocks.DIRT_CHANNEL,
                Blocks.DIRT,
                Blocks.GRASS_BLOCK
            );

            // stone
            add(ModBlocks.STONE_CHANNEL, Blocks.STONE);
            add(ModBlocks.ANDESITE_CHANNEL, Blocks.ANDESITE);

            add(ModBlocks.SAND_CHANNEL, Blocks.SAND);
            // TODO: implement other variants
        }

        private static void add(Supplier<Block> channel, Block... inputs) {
            final Block output = channel.get();
            map.put(output, output);

            for (final Block block : inputs) {
                map.put(block, output);
            }
        }

        @Nullable
        public static Block get(Block block) {
            return map.get(block);
        }
    }
}

package shagejack.lostgrace.contents.block.bloodAltar;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import shagejack.lostgrace.foundation.block.BaseTileEntityBlock;
import shagejack.lostgrace.foundation.utility.VoxelShapeUtils;
import shagejack.lostgrace.registries.tile.AllTileEntities;


public class BloodAltarBlock extends BaseTileEntityBlock<BloodAltarTileEntity> {

    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;

    private static final VoxelShape SHAPE = VoxelShapeUtils.joinAllShape(BooleanOp.OR,
            Shapes.box(0.3125, 0.4375, 0.3125, 0.6875, 0.5, 0.6875),
            Shapes.box(0.4375, 0.0625, 0.4375, 0.5625, 0.4375, 0.5625),
            Shapes.box(0.3125, 0.5, 0.25, 0.6875, 0.5625, 0.3125),
            Shapes.box(0.25, 0.5, 0.3125, 0.3125, 0.5625, 0.6875),
            Shapes.box(0.6875, 0.5, 0.3125, 0.75, 0.5625, 0.6875),
            Shapes.box(0.3125, 0.5, 0.6875, 0.6875, 0.5625, 0.75),
            Shapes.box(0.75, 0.5625, 0.3125, 0.8125, 0.75, 0.6875),
            Shapes.box(0.1875, 0.5625, 0.3125, 0.25, 0.75, 0.6875),
            Shapes.box(0.3125, 0.5625, 0.1875, 0.6875, 0.75, 0.25),
            Shapes.box(0.6875, 0.5625, 0.6875, 0.75, 0.75, 0.75),
            Shapes.box(0.25, 0.5625, 0.6875, 0.3125, 0.75, 0.75),
            Shapes.box(0.6875, 0.5625, 0.25, 0.75, 0.75, 0.3125),
            Shapes.box(0.25, 0.5625, 0.25, 0.3125, 0.75, 0.3125),
            Shapes.box(0.3125, 0.5625, 0.75, 0.6875, 0.75, 0.8125),
            Shapes.box(0.375, 0, 0.375, 0.625, 0.0625, 0.625),
            Shapes.box(0.3125, 0, 0.375, 0.375, 0.0625, 0.625),
            Shapes.box(0.625, 0, 0.375, 0.6875, 0.0625, 0.625),
            Shapes.box(0.375, 0, 0.3125, 0.625, 0.0625, 0.375),
            Shapes.box(0.375, 0, 0.625, 0.625, 0.0625, 0.6875)
    );

    public BloodAltarBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(LOCKED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LOCKED);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) {
            withTileEntityDo(level, pos, BloodAltarTileEntity::tryStart);
            return InteractionResult.CONSUME;
        } else {
            LazyOptional<IFluidHandlerItem> handler = FluidUtil.getFluidHandler(stack);

            if (handler.isPresent()) {
                if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, result.getDirection()))
                    return InteractionResult.CONSUME_PARTIAL;

                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return state.getValue(LOCKED) ? Shapes.empty() : SHAPE;
    }

    @Override
    public Class<BloodAltarTileEntity> getTileEntityClass() {
        return BloodAltarTileEntity.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlockEntityType<? extends BloodAltarTileEntity> getTileEntityType() {
        return (BlockEntityType<? extends BloodAltarTileEntity>) AllTileEntities.bloodAltar.get();
    }
}

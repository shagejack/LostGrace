package shagejack.lostgrace.contents.block.dreamPool;

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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import shagejack.lostgrace.foundation.block.BaseTileEntityBlock;
import shagejack.lostgrace.foundation.utility.TileEntityUtils;
import shagejack.lostgrace.registries.item.AllItems;
import shagejack.lostgrace.registries.tile.AllTileEntities;

public class DreamPoolBlock extends BaseTileEntityBlock<DreamPoolTileEntity> {

    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D);

    public DreamPoolBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {

        ItemStack stack = player.getItemInHand(hand);

        if (stack.isEmpty())
            return InteractionResult.FAIL;

        if (stack.is(AllItems.trinaCrystalBallFull.get())) {
            TileEntityUtils.get(DreamPoolTileEntity.class, level, pos).ifPresent(te -> {
                if (!player.isCreative()) {
                    player.addItem(stack.getContainerItem());
                    stack.shrink(1);
                }
                te.fillDream();
            });
            return InteractionResult.SUCCESS;
        }

        LazyOptional<IFluidHandlerItem> handler = FluidUtil.getFluidHandler(stack);

        if (handler.isPresent()) {
            if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, result.getDirection()))
                return InteractionResult.CONSUME_PARTIAL;

            return InteractionResult.FAIL;
        }

        return InteractionResult.FAIL;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public Class<DreamPoolTileEntity> getTileEntityClass() {
        return DreamPoolTileEntity.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlockEntityType<? extends DreamPoolTileEntity> getTileEntityType() {
        return (BlockEntityType<? extends DreamPoolTileEntity>) AllTileEntities.dreamPool.get();
    }
}

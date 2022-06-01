package shagejack.lostgrace.contents.block.spell.unkown;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import shagejack.lostgrace.contents.entity.blackKnifeAssassin.BlackKnifeAssassin;
import shagejack.lostgrace.foundation.utility.ColorUtils;

public class ChalkSpellBlock extends Block {

    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public ChalkSpellBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {

        if (player.getItemInHand(hand).isEmpty()) {
            switch (getSpellType(level, pos)) {
                case SUMMON_BLACK_KNIFE_ASSASSIN -> {
                    summonBlackKnife(level, pos.offset(0, 8, 0));
                    return InteractionResult.PASS;
                }
                case NULL -> {}
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isFaceSturdy(level, pos.below(), Direction.UP);
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        return pFacing == Direction.DOWN && !this.canSurvive(pState, pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public ChalkSpellType getSpellType(Level level, BlockPos pos) {
        for(ChalkSpellType type : ChalkSpellType.values()) {
            if (type.check(level, pos))
                return type;
        }

        return ChalkSpellType.NULL;
    }

    public void summonBlackKnife(Level level, BlockPos pos) {
        BlackKnifeAssassin blackKnifeAssassin = new BlackKnifeAssassin(level, -4.5, 3 + level.getRandom().nextDouble() * 5, true);
        blackKnifeAssassin.setPos(Vec3.atCenterOf(pos));
        blackKnifeAssassin.setColor(ColorUtils.getRandomColor(level.getRandom()));
        level.addFreshEntity(blackKnifeAssassin);
    }
}

package shagejack.lostgrace.contents.block.grace;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import shagejack.lostgrace.contents.grace.GlobalGraceSet;
import shagejack.lostgrace.contents.grace.Grace;
import shagejack.lostgrace.contents.grace.GraceProvider;
import shagejack.lostgrace.contents.grace.IGraceHandler;
import shagejack.lostgrace.foundation.block.BaseTileEntityBlock;
import shagejack.lostgrace.foundation.network.AllPackets;
import shagejack.lostgrace.foundation.network.packet.DiscoverGracePacket;
import shagejack.lostgrace.foundation.utility.DropUtils;
import shagejack.lostgrace.foundation.utility.TileEntityUtils;
import shagejack.lostgrace.registries.block.AllBlocks;
import shagejack.lostgrace.registries.item.AllItems;
import shagejack.lostgrace.registries.tile.AllTileEntities;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GraceBlock extends BaseTileEntityBlock<GraceTileEntity> {

    public static final BooleanProperty COOLDOWN = BlockStateProperties.ATTACHED;

    private static final VoxelShape SHAPE = Block.box(4.0D, 8.0D, 4.0D, 12.0D, 16.0D, 12.0D);
    private static final VoxelShape TABLE_SHAPE = Block.box(0.0D, 8.0D, 0.0D, 16.0D, 24.0D, 16.0D);

    public GraceBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(COOLDOWN, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(COOLDOWN);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        GraceTileEntity te = getTileEntity(worldIn, pos);
        return (te != null && te.isTableGrace()) ? TABLE_SHAPE : state.getValue(COOLDOWN) ? Shapes.empty() : SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        if (level.isClientSide())
            return InteractionResult.CONSUME;

        LazyOptional<IGraceHandler> handler = player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY);
        AtomicBoolean interacted = new AtomicBoolean(false);
        withTileEntityDo(level, pos, te -> {
            // trivial sync
            te.syncToClient();
            if (!te.isLocked()) {
                Grace grace = te.getGrace();
                handler.ifPresent(graceData -> {
                    if (GlobalGraceSet.getGraceSet().contains(grace)) {
                        if (graceData.visitGrace(grace)) {
                            firstVisit(level, pos, player);
                            interacted.set(true);
                        } else {
                            commonVisit(level, pos, player);
                            interacted.set(true);
                        }

                        if (!level.isClientSide()) {
                            graceData.syncToClient((ServerPlayer) player);
                        }
                    }
                });
            }
        });

        if (interacted.get())
            return InteractionResult.CONSUME;

        return InteractionResult.FAIL;
    }

    public void firstVisit(Level level, BlockPos pos, Player player) {
        withTileEntityDo(level, pos, te -> {
            if (te.isTableGrace()) {
                level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(COOLDOWN, true));
            }
        });

        if (player instanceof ServerPlayer serverPlayer) {
            DiscoverGracePacket discoverGracePacket = new DiscoverGracePacket();
            AllPackets.sendToPlayer(serverPlayer, discoverGracePacket);
            MutableComponent component = new TextComponent("LOST GRACE DISCOVERED").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.YELLOW);

            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(serverPlayer, ParticleTypes.FLAME, true, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 500, 0.0D, 0.0D, 0.0D, 0.3D);
            }

            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(component));
            serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(10, 40, 10));
            serverPlayer.connection.send(new ClientboundSoundPacket(SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), 1.0f, 1.0f));
        }
    }

    public void commonVisit(Level level, BlockPos pos, Player player) {
        withTileEntityDo(level, pos, te -> {
            if (te.isTableGrace()) {
            level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(COOLDOWN, true));
            te.setLocked(true);
            }
        });
    }

    @Override
    public Class<GraceTileEntity> getTileEntityClass() {
        return GraceTileEntity.class;
    }

    @SuppressWarnings("unchecked")
    @Override
    public BlockEntityType<? extends GraceTileEntity> getTileEntityType() {
        return (BlockEntityType<? extends GraceTileEntity>) AllTileEntities.grace.get();
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        double x = (double) pos.getX() + 0.5D;
        double y = (double) pos.getY() + 0.75D;
        double z = (double) pos.getZ() + 0.5D;
        if (random.nextDouble() < 0.1D) {
            level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }

        level.addParticle(ParticleTypes.END_ROD, x + random.nextDouble() - 0.5D, y + random.nextDouble() - 0.5D, z + random.nextDouble() - 0.5D, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (oldState.hasBlockEntity() && oldState.getBlock() != newState.getBlock()) {
            ItemStack dropSeed = new ItemStack(AllItems.goldenSeed.get());

            withTileEntityDo(level, pos, te -> {
                te.onRemoved();
                if (te.hasGraceName())
                    dropSeed.setHoverName(new TextComponent(te.getGraceName()));
            });

            DropUtils.dropItemStack(level, pos, dropSeed);
            level.removeBlockEntity(pos);
        }
    }
}

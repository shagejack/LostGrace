package shagejack.lostgrace.contents.block.grace;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import shagejack.lostgrace.contents.grace.Grace;
import shagejack.lostgrace.contents.grace.GraceProvider;
import shagejack.lostgrace.contents.grace.IGraceHandler;
import shagejack.lostgrace.foundation.block.BaseTileEntityBlock;
import shagejack.lostgrace.foundation.block.ITE;
import shagejack.lostgrace.foundation.network.AllPackets;
import shagejack.lostgrace.foundation.network.packet.DiscoverGracePacket;
import shagejack.lostgrace.registries.tile.AllTileEntities;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class GraceBlock extends BaseTileEntityBlock<GraceTileEntity> {

    public static final BooleanProperty COOLDOWN = BlockStateProperties.ATTACHED;

    private static final VoxelShape SHAPE = Block.box(4.0D, 4.0D, 4.0D, 12.0D, 12.0D, 12.0D);

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
        return state.getValue(COOLDOWN) ? Shapes.empty() : SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        LazyOptional<IGraceHandler> handler = player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY);
        AtomicBoolean interacted = new AtomicBoolean(false);
        withTileEntityDo(level, pos, te -> {
            if (!te.isLocked()) {
                Grace grace = te.getGrace();
                handler.ifPresent(graceData -> {
                    if (graceData.visitGrace(grace)) {
                        firstVisit(level, pos, player);
                        interacted.set(true);
                    } else {
                        commonVisit(level, pos, player);
                        interacted.set(true);
                    }

                    if (!level.isClientSide())
                        graceData.syncToClient((ServerPlayer) player);
                });
            }
        });

        if (interacted.get())
            return InteractionResult.CONSUME;

        return InteractionResult.FAIL;
    }

    public void firstVisit(Level level, BlockPos pos, Player player) {
        level.setBlock(pos, level.getBlockState(pos).setValue(COOLDOWN, true), 3);
        if (player instanceof ServerPlayer serverPlayer) {
            DiscoverGracePacket discoverGracePacket = new DiscoverGracePacket();
            AllPackets.sendToPlayer(serverPlayer, discoverGracePacket);
            MutableComponent component = new TextComponent("LOST GRACE DISCOVERED").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.YELLOW);
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(component));
            serverPlayer.connection.send(new ClientboundSetTitlesAnimationPacket(10, 40, 10));
            serverPlayer.connection.send(new ClientboundSoundPacket(SoundEvents.END_PORTAL_SPAWN, SoundSource.BLOCKS, pos.getX(), pos.getY(), pos.getZ(), 1.0f, 1.0f));
        }
    }

    public void commonVisit(Level level, BlockPos pos, Player player) {
        level.setBlock(pos, level.getBlockState(pos).setValue(COOLDOWN, true), 3);
        withTileEntityDo(level, pos, te -> te.setLocked(true));
    }

    @Override
    public Class<GraceTileEntity> getTileEntityClass() {
        return GraceTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends GraceTileEntity> getTileEntityType() {
        return (BlockEntityType<? extends GraceTileEntity>) AllTileEntities.grace.get();
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
        double x = (double) pos.getX() + 0.5D;
        double y = (double) pos.getY() + 0.5D;
        double z = (double) pos.getZ() + 0.5D;
        if (random.nextDouble() < 0.1D) {
            level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }


        level.addParticle(ParticleTypes.FLAME, x + random.nextDouble() - 0.5D, y + random.nextDouble() - 0.5D, z + random.nextDouble() - 0.5D, 0.0D, 0.0D, 0.0D);
    }
}

package shagejack.lostgrace.contents.block.grace;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.world.ForgeChunkManager;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.contents.grace.GlobalGraceSet;
import shagejack.lostgrace.contents.grace.Grace;
import shagejack.lostgrace.contents.grace.GraceProvider;
import shagejack.lostgrace.contents.grace.IGraceHandler;
import shagejack.lostgrace.foundation.network.AllPackets;
import shagejack.lostgrace.foundation.network.packet.GraceTileEntityUpdatePacket;
import shagejack.lostgrace.foundation.tile.BaseTileEntity;
import shagejack.lostgrace.foundation.utility.Constants;
import shagejack.lostgrace.foundation.utility.Vector3;
import shagejack.lostgrace.registries.tile.AllTileEntities;

import java.util.ArrayList;
import java.util.List;

public class GraceTileEntity extends BaseTileEntity {

    private Grace grace = null;
    private boolean loaded;
    protected Player interactedPlayer = null;

    protected int cooldown;
    protected int summoned;
    protected boolean locked;

    protected int syncCounter = 1200;

    public String graceName;
    public boolean renderFog;

    public GraceTileEntity(BlockPos pos, BlockState state) {
        super(AllTileEntities.grace.get(), pos, state);
        this.cooldown = 0;
        this.summoned = 2400;
        this.locked = false;
        this.graceName = "";
    }

    @Override
    public void tick() {
        if (level == null)
            return;

        if (this.grace == null || grace.equals(Grace.NULL) || grace.getLevel() == null || !grace.getRawName().equals(graceName))
            this.grace = getGrace(true);

        if (locked) {
            List<Player> players = new ArrayList<>();
            for(Player player : level.players()) {
                if (Vector3.of(getBlockPos()).add(0.5, Constants.GRACE_DISTANCE_Y_OFFSET, 0.5).distance(Vector3.of(player.position())) < Constants.GRACE_FORCE_FIRST_PERSON_DISTANCE) {
                    players.add(player);
                }
            }

            if (players.size() == 0 || players.stream().noneMatch(GraceTileEntity::activatedGrace)) {
                this.locked = false;
            } else if (level.isClientSide()) {
                // try to create fog for client player
                createFog();
            }
        }

        if (summoned > 0)
            summoned--;

        if (getBlockState().getValue(GraceBlock.COOLDOWN)) {
            if (cooldown < 60) {
                cooldown++;
            } else {
                level.setBlock(getBlockPos(), getBlockState().setValue(GraceBlock.COOLDOWN, false), 3);
                cooldown = 0;
            }
        }

        if (!level.isClientSide()) {
            if (syncCounter > 0) {
                syncCounter--;
            } else {
                syncCounter = 1200;
                syncToClient();
            }
        }
    }

    public static boolean activatedGrace(Player player) {
        LazyOptional<IGraceHandler> graceHandler = player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY);
        if (!graceHandler.isPresent())
            return false;

        return graceHandler.resolve().isPresent() && graceHandler.resolve().get().isGraceActivated();
    }

    public boolean shouldRenderFog() {
        return renderFog;
    }

    @OnlyIn(Dist.CLIENT)
    public void createFog() {
        if (Minecraft.getInstance().player == null || !this.isLocked()) {
            renderFog = false;
            return;
        }

        LazyOptional<IGraceHandler> graceHandler = Minecraft.getInstance().player.getCapability(GraceProvider.GRACE_HANDLER_CAPABILITY);

        if (!graceHandler.isPresent()) {
            renderFog = false;
            return;
        }

        graceHandler.ifPresent(handler -> {
            if (handler.isGraceActivated() && handler.getLastGrace().equals(this.getGrace())) {
                Vector3 center = Vector3.of(getBlockPos()).add(0.5, Constants.GRACE_DISTANCE_Y_OFFSET, 0.5);
                double distance = Vector3.of(Minecraft.getInstance().player.position()).distance(center);

                this.renderFog = true;

                if (distance < Constants.GRACE_FORCE_FIRST_PERSON_DISTANCE)
                    Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);

                if (distance < Constants.GRACE_MAX_DISTANCE)
                    GraceUIHandler.getInstance().getOrCreateUI(getLevel(), getBlockPos(), handler);
            }
        });
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("Summoned", this.summoned);
        tag.putBoolean("Locked", this.locked);
        tag.putString("GraceName", this.graceName);
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        if (tag.contains("Summoned", Tag.TAG_INT)) {
            this.summoned = tag.getInt("Summoned");
        } else {
            this.summoned = 2400;
        }
        this.locked = tag.getBoolean("Locked");
        this.graceName = tag.getString("GraceName");
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Grace getGrace() {
        return this.getGrace(false);
    }

    public Grace getGrace(boolean forceCreate) {
        if (grace == null) {
            this.grace = new Grace(graceName, level, getBlockPos());
            GlobalGraceSet.addGrace(this.grace);

            if (level != null && !level.isClientSide())
                syncToClient();

        } else if (forceCreate) {
            GlobalGraceSet.removeGrace(this.grace);
            this.grace = new Grace(graceName, level, getBlockPos());
            GlobalGraceSet.addGrace(this.grace);

            if (level != null && !level.isClientSide())
                syncToClient();
        }
        return this.grace;
    }

    public int getSummonRemainingTicks() {
        return this.summoned;
    }

    public void setGraceName(String name) {
        this.graceName = name;
        GlobalGraceSet.removeGrace(this.grace);
        this.grace = new Grace(graceName, level, getBlockPos());
        GlobalGraceSet.addGrace(this.grace);

        if (level != null && !level.isClientSide())
            syncToClient();
    }

    public String getGraceName() {
        return this.graceName;
    }

    public boolean hasGraceName() {
        return this.graceName != null && !this.graceName.isEmpty();
    }

    public void clearGraceName() {
        setGraceName("");
    }

    public void syncToClient() {
        if (level != null && !level.isClientSide())
            AllPackets.sendToSameDimension(level, new GraceTileEntityUpdatePacket(getBlockPos(), this.graceName));
    }

    @Override
    public void onRemoved() {
        GlobalGraceSet.removeGrace(getGrace());

        super.onRemoved();
    }
}

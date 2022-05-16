package shagejack.lostgrace.contents.block.grace;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import shagejack.lostgrace.contents.grace.GlobalGraceSet;
import shagejack.lostgrace.contents.grace.Grace;
import shagejack.lostgrace.contents.grace.GraceProvider;
import shagejack.lostgrace.contents.grace.IGraceHandler;
import shagejack.lostgrace.foundation.tileEntity.BaseTileEntity;
import shagejack.lostgrace.foundation.utility.Vector3;
import shagejack.lostgrace.registries.tileEntities.AllTileEntities;

import java.util.ArrayList;
import java.util.List;

public class GraceTileEntity extends BaseTileEntity {

    private Grace grace = null;
    protected Player interactedPlayer = null;

    protected int cooldown;
    protected int summoned;
    protected boolean locked;

    public String graceName = "";
    public boolean renderFog;

    public GraceTileEntity(BlockPos pos, BlockState state) {
        super(AllTileEntities.grace.get(), pos, state);
        this.cooldown = 0;
        this.summoned = 2400;
        this.locked = false;
    }

    @Override
    public void tick() {
        if (level == null)
            return;

        if (locked) {
            List<Player> players = new ArrayList<>();
            for(Player player : level.players()) {
                if (Vector3.of(getBlockPos()).add(0.5, 1.6, 0.5).distance(Vector3.of(player.position())) < 5.5D) {
                    players.add(player);
                }
            }

            if (players.size() == 0 || players.stream().noneMatch(this::activatedGrace)) {
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
    }

    private boolean activatedGrace(Player player) {
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

        if (graceHandler.resolve().isPresent() && graceHandler.resolve().get().isGraceActivated()) {
            Vector3 center = Vector3.of(getBlockPos()).add(0.5, 1.6, 0.5);
            double distance = Vector3.of(Minecraft.getInstance().player.position()).distance(center);

            renderFog = true;

            if (distance < 5.5)
                Minecraft.getInstance().options.setCameraType(CameraType.FIRST_PERSON);

            if (distance < 2.5)
                GraceUIRenderHandler.getInstance().getOrCreateUI(getLevel(), getBlockPos(), graceHandler.resolve().get());


            return;
        }

        renderFog = false;
    }



    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("Summoned", this.summoned);
        tag.putBoolean("Locked", this.locked);
        tag.putString("GraceName", graceName);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("Summoned", Tag.TAG_INT)) {
            this.summoned = tag.getInt("Summoned");
        } else {
            this.summoned = 2400;
        }
        this.locked = tag.getBoolean("Locked");
        this.graceName = tag.getString("GraceName");
        super.load(tag);
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public Grace getGrace() {
        if (grace == null) {
            this.grace = new Grace(graceName, level, getBlockPos());
            GlobalGraceSet.addGrace(this.grace);
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
    }

    public void clearGraceName() {
        setGraceName("");
    }

    @Override
    public void onRemoved() {
        GlobalGraceSet.removeGrace(getGrace());
        super.onRemoved();
    }
}

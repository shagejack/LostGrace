package shagejack.lostgrace.contents.block.grace;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import shagejack.lostgrace.contents.grace.GlobalGraceSet;
import shagejack.lostgrace.contents.grace.Grace;
import shagejack.lostgrace.foundation.tileEntity.BaseTileEntity;
import shagejack.lostgrace.registries.tileEntities.AllTileEntities;

public class GraceTileEntity extends BaseTileEntity {

    private Grace grace = null;

    protected int cooldown;
    protected int summoned;

    public GraceTileEntity(BlockPos pos, BlockState state) {
        super(AllTileEntities.grace.get(), pos, state);
        this.cooldown = 0;
        this.summoned = 2400;
    }

    @Override
    public void tick() {
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

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("Summoned", this.summoned);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        if (tag.contains("Summoned", Tag.TAG_INT)) {
            this.summoned = tag.getInt("Summoned");
        } else {
            this.summoned = 2400;
        }
        super.load(tag);
    }

    public Grace getGrace() {
        if (grace == null) {
            this.grace = new Grace(level, getBlockPos());
            GlobalGraceSet.addGrace(this.grace);
        }
        return this.grace;
    }

    public int getSummonRemainingTicks() {
        return this.summoned;
    }

    @Override
    public void onRemoved() {
        GlobalGraceSet.removeGrace(getGrace());
        super.onRemoved();
    }
}

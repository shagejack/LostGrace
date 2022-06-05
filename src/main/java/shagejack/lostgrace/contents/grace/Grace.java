package shagejack.lostgrace.contents.grace;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Objects;

public class Grace {
    public static final Grace NULL = new Grace();
    private final String name;
    private final ResourceKey<Level> dimension;
    private final BlockPos pos;
    private final boolean isNull;
    private final boolean isClientSide;

    public Grace() {
        this.name = "";
        this.dimension = null;
        this.pos = null;
        this.isNull = true;
        this.isClientSide = false;
    }

    public Grace(Level level, BlockPos pos) {
        this.name = "";
        this.dimension = level.dimension();
        this.pos = pos;
        this.isNull = false;
        this.isClientSide = level.isClientSide();
    }

    public Grace(ResourceKey<Level> dimension, BlockPos pos, boolean isClientSide) {
        this.name = "";
        this.dimension = dimension;
        this.pos = pos;
        this.isNull = false;
        this.isClientSide = isClientSide;
    }

    public Grace(String name, Level level, BlockPos pos) {
        this.name = name;
        this.dimension = level.dimension();
        this.pos = pos;
        this.isNull = false;
        this.isClientSide = level.isClientSide();
    }

    public Grace(String name, ResourceKey<Level> dimension, BlockPos pos, boolean isClientSide) {
        this.name = name;
        this.dimension = dimension;
        this.pos = pos;
        this.isNull = false;
        this.isClientSide = isClientSide;
    }

    public Grace(CompoundTag nbt) {
        if (nbt.contains("IsNull", Tag.TAG_BYTE) && !nbt.getBoolean("IsNull")) {
            this.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("Dimension")));
            this.pos = new BlockPos(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
            this.isNull = false;
            this.name = nbt.getString("Name");
            this.isClientSide = nbt.getBoolean("IsClientSide");
        } else {
            this.dimension = null;
            this.name = "";
            this.pos = null;
            this.isNull = true;
            this.isClientSide = false;
        }
    }

    public BlockPos getPos() {
        return pos;
    }

    // server-side only
    public Level getLevel() {
        if (isClientSide)
            return null;

        return Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer()).getLevel(this.dimension);
    }

    public boolean isClientSide() {
        return isClientSide;
    }

    public ResourceKey<Level> getDimension() {
        return dimension;
    }

    public boolean hasName() {
        return this.name != null && !this.name.isEmpty();
    }

    public String getName() {
        return hasName() ? name : "Empty";
    }

    public Component getHoverName() {
        return hasName() ? new TextComponent(name) : new TextComponent("Empty");
    }

    public String getRawName() {
        return hasName() ? name : "";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof Grace grace))
            return false;

        if (grace.isNull)
            return false;

        return grace.getPos().equals(this.getPos()) && grace.getDimension().location().equals(this.getDimension().location()) && grace.getRawName().equals(this.getRawName());
    }

    @Override
    public int hashCode() {
        return isNull || pos == null || dimension == null ? 0 : getRawName().hashCode() * 31 * 31 + dimension.location().hashCode() * 31 + pos.hashCode();
    }

    @Override
    public String toString() {
        if (isNull) {
            return "[Grace] NULL";
        } else {
            return "[Grace] Name: " + (hasName() ? name : "Empty") + ", Dimension: " + dimension.location() + ", Pos: " + pos.toString();
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (isNull || pos == null) {
            tag.putString("Dimension", "");
            tag.putString("Name", "");
            tag.putDouble("X", 0);
            tag.putDouble("Y", 0);
            tag.putDouble("Z", 0);
            tag.putBoolean("IsNull", true);
            tag.putBoolean("IsClientSide", false);
        } else {
            if (hasName()) {
                tag.putString("Name", name);
            } else {
                tag.putString("Name", "");
            }
            tag.putString("Dimension", dimension.location().toString());
            tag.putDouble("X", pos.getX());
            tag.putDouble("Y", pos.getY());
            tag.putDouble("Z", pos.getZ());
            tag.putBoolean("IsNull", false);
            tag.putBoolean("IsClientSide", isClientSide);
        }
        return tag;
    }
}

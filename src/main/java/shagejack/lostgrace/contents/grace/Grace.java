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

public class Grace {
    public static final Grace NULL = new Grace();
    private final String name;
    private final Level level;
    private final BlockPos pos;
    private final boolean isNull;

    public Grace() {
        this.name = "";
        this.level = null;
        this.pos = null;
        this.isNull = true;
    }

    public Grace(Level level, BlockPos pos) {
        this.name = "";
        this.level = level;
        this.pos = pos;
        this.isNull = false;
    }

    public Grace(String name, Level level, BlockPos pos) {
        this.name = name;
        this.level = level;
        this.pos = pos;
        this.isNull = false;
    }

    public Grace(CompoundTag nbt) {
        if (nbt.contains("IsNull", Tag.TAG_BYTE) && !nbt.getBoolean("IsNull")) {
            this.level = ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("Dimension"))));
            this.pos = new BlockPos(nbt.getDouble("X"), nbt.getDouble("Y"), nbt.getDouble("Z"));
            this.isNull = false;
            this.name = nbt.getString("Name");
        } else {
            this.level = null;
            this.name = "";
            this.pos = null;
            this.isNull = true;
        }
    }

    public BlockPos getPos() {
        return pos;
    }

    public Level getLevel() {
        return level;
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

        return grace.getPos().equals(this.getPos()) && grace.getLevel().dimension().location().equals(this.getLevel().dimension().location()) && grace.getRawName().equals(this.getRawName());
    }

    @Override
    public int hashCode() {
        return isNull || pos == null || level == null ? 0 : getRawName().hashCode() * 31 * 31 + level.dimension().location().hashCode() * 31 + pos.hashCode();
    }

    @Override
    public String toString() {
        if (isNull) {
            return "[Grace] NULL";
        } else {
            return "[Grace] Name: " + (hasName() ? name : "Empty") + ", Level: " + level.dimension().location() + ", Pos: " + pos.toString();
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
        } else {
            if (hasName()) {
                tag.putString("Name", name);
            } else {
                tag.putString("Name", "");
            }
            tag.putString("Dimension", level.dimension().location().toString());
            tag.putDouble("X", pos.getX());
            tag.putDouble("Y", pos.getY());
            tag.putDouble("Z", pos.getZ());
            tag.putBoolean("IsNull", false);
        }
        return tag;
    }
}

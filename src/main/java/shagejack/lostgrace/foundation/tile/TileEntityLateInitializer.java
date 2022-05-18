package shagejack.lostgrace.foundation.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import shagejack.lostgrace.foundation.utility.TileEntityUtils;

import java.util.Optional;
import java.util.function.Consumer;

public interface TileEntityLateInitializer<T extends BlockEntity> {

    default boolean tick() {
        if (validate())
            return true;

        decreaseLife();

        return tryInit();
    }

    default boolean validate() {
        return initialized() || getLife() <= 0;
    }

    default boolean tryInit() {
        Optional<T> te = TileEntityUtils.get(getTileClass(), getLevel(), getTilePos(), true);
        if (te.isPresent()) {
            getAction().accept(te.get());
            setInitialized();
            return true;
        }
        return false;
    }

    int getLife();

    void decreaseLife();

    boolean initialized();

    void setInitialized();

    Class<T> getTileClass();
    Level getLevel();
    BlockPos getTilePos();
    Consumer<T> getAction();

}

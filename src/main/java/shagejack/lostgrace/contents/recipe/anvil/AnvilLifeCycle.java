package shagejack.lostgrace.contents.recipe.anvil;

import com.google.common.collect.Sets;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import java.util.Set;

public class AnvilLifeCycle {

    public static Set<FallingBlockEntity> anvilWatchList = Sets.newConcurrentHashSet();

    public static void onAnvilFall(final EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof FallingBlockEntity entity && entity.getBlockState().is(Blocks.ANVIL)) {
            anvilWatchList.add(entity);
        }
    }

    public static void serverTick(final TickEvent.ServerTickEvent event) {
        anvilWatchList.removeIf(anvil -> {
            boolean removed = anvil.isRemoved();
            if (removed)
                AnvilRecipeProcessor.onAnvilHitGround(anvil);

            return removed;
        });
    }
}

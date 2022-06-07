package shagejack.lostgrace.contents.block.spell;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import shagejack.lostgrace.contents.entity.blackKnifeAssassin.BlackKnifeAssassin;
import shagejack.lostgrace.foundation.utility.ColorUtils;

import java.util.Optional;
import java.util.function.BiConsumer;

public enum SpellType {
    NULL(SpellShape.NULL, ($0, $1) -> {}),
    SUMMON_BLACK_KNIFE_ASSASSIN(
            new SpellShape.Builder()
                    .spell(RuneType.UNKNOWN)
                    .append(0, 0)
                    .append(-2, -2)
                    .append(2, -2)
                    .append(-2, 2)
                    .append(2, 2)
                    .append(3, -1)
                    .append(3, 0)
                    .append(3, 1)
                    .append(-3, -1)
                    .append(-3, 0)
                    .append(-3, 1)
                    .append(-1, 3)
                    .append(0, 3)
                    .append(1, 3)
                    .append(-1, -3)
                    .append(0, -3)
                    .append(1, -3)

                    .block(Blocks.CRIMSON_ROOTS)
                    .append(2, 0)
                    .append(-2, 0)
                    .append(0, 2)
                    .append(0, -2)

                    .block(Blocks.WARPED_ROOTS)
                    .append(2, 1)
                    .append(2, -1)
                    .append(-2, 1)
                    .append(-2, -1)
                    .append(1, 2)
                    .append(-1, 2)
                    .append(1, -2)
                    .append(-1, -2)

                    .state(Blocks.WHITE_CANDLE, state -> state.getValue(BlockStateProperties.LIT))
                    .append(1, 1)
                    .append(1, -1)
                    .append(-1 , 1)
                    .append(-1, -1)

                    .block(Blocks.AMETHYST_BLOCK)
                    .append(0, -1, 0)

                    .block(Blocks.CRIMSON_NYLIUM)
                    .append(1, -1, 0)
                    .append(2, -1, 0)
                    .append(-1, -1, 0)
                    .append(-2, -1, 0)
                    .append(0, -1, 1)
                    .append(0, -1, 2)
                    .append(0, -1, -1)
                    .append(0, -1, -2)

                    .block(Blocks.WARPED_NYLIUM)
                    .append(1, -1, 1)
                    .append(-1, -1, 1)
                    .append(1, -1, -1)
                    .append(-1, -1, -1)
                    .append(2, -1, -1)
                    .append(2, -1, 1)
                    .append(-2, -1, -1)
                    .append(-2, -1, 1)
                    .append(-1, -1, 2)
                    .append(1, -1, 2)
                    .append(-1, -1, -2)
                    .append(1, -1, -2)
                    
                    .block(Blocks.PURPUR_BLOCK)
                    .append(-2, -1, -2)
                    .append(2, -1, -2)
                    .append(-2, -1,2)
                    .append(2, -1,2)
                    .append(3, -1,-1)
                    .append(3, -1,0)
                    .append(3, -1,1)
                    .append(-3, -1,-1)
                    .append(-3, -1,0)
                    .append(-3, -1,1)
                    .append(-1, -1, 3)
                    .append(0, -1,3)
                    .append(1, -1,3)
                    .append(-1, -1,-3)
                    .append(0, -1,-3)
                    .append(1, -1,-3)

                    .fullSquareWithAir(3)
                    .levelPredicate(Level::isNight)
                    .build(),
            (level, pos) -> {
                if (level.isClientSide())
                    return;

                BlockPos summonPos = pos.offset(0, 8, 0);
                level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 16, Explosion.BlockInteraction.DESTROY);
                double radius = 3 + level.getRandom().nextDouble() * 5;
                BlackKnifeAssassin blackKnifeAssassin = new BlackKnifeAssassin(level, radius, radius, true);
                blackKnifeAssassin.setPos(Vec3.atCenterOf(summonPos));
                blackKnifeAssassin.setColor(ColorUtils.getRandomColor(level.getRandom()));
                level.addFreshEntity(blackKnifeAssassin);
            }
    )
    ;

    private final SpellShape spellShape;
    private final BiConsumer<Level, BlockPos> function;

    SpellType(SpellShape spellShape, BiConsumer<Level, BlockPos> function) {
        this.spellShape = spellShape;
        this.function = function;
    }

    public SpellShape getSpell() {
        return this.spellShape;
    }

    public void run(Level level, BlockPos pos) {
        this.function.accept(level, pos);
    }

    public boolean check(Level level, BlockPos pos) {
        if (spellShape.isEmpty())
            return false;
        return this.spellShape.check(level, pos);
    }

    public static Optional<SpellType> getSpellType(Level level, BlockPos pos) {
        for(SpellType type : SpellType.values()) {
            if (type.check(level, pos))
                return Optional.of(type);
        }

        return Optional.empty();
    }

    public static boolean tryRun(Level level, BlockPos pos) {
        Optional<SpellType> spellType = getSpellType(level, pos);
        if (spellType.isEmpty())
            return false;

        spellType.get().run(level, pos);
        return true;
    }
}

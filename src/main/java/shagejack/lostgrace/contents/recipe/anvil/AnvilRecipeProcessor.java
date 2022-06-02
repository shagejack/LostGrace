package shagejack.lostgrace.contents.recipe.anvil;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import shagejack.lostgrace.foundation.utility.recipe.RecipeConditions;
import shagejack.lostgrace.foundation.utility.recipe.RecipeFinder;
import shagejack.lostgrace.registries.recipe.AllRecipeTypes;

import java.util.List;
import java.util.function.Predicate;

public class AnvilRecipeProcessor {

    public static Object anvilRecipeCacheKey = null;

    public static void onAnvilHitGround(FallingBlockEntity entity) {
        if(entity.getBlockState().is(Blocks.ANVIL)) {
            List<ItemEntity> items = entity.getLevel().getEntitiesOfClass(ItemEntity.class, entity.getBoundingBox().inflate(0.1));
            items.forEach(item -> {
                ItemStack input = item.getItem().copy();
                input.setCount(1);
                ItemStack result = crushItem(input, item.getLevel());
                if (result != ItemStack.EMPTY) {
                    int count = item.getItem().getCount() * result.getCount();
                    item.setItem(ItemStack.EMPTY);
                    while(count > 0) {
                        if (count >= result.getMaxStackSize()) {
                            item.spawnAtLocation(new ItemStack(result.getItem(), result.getMaxStackSize()));
                            count -= result.getMaxStackSize();
                        } else {
                            item.spawnAtLocation(new ItemStack(result.getItem(), count));
                            count = 0;
                        }
                    }
                    item.discard();
                }
            });
        }
    }

    public static ItemStack crushItem(ItemStack stack, Level level) {

        Predicate<Recipe<?>> types;

        types = RecipeConditions.isOfType(AllRecipeTypes.ANVIL.getType());

        List<Recipe<?>> recipes = RecipeFinder.get(anvilRecipeCacheKey, level, types)
                .stream()
                .filter(RecipeConditions.firstIngredientMatches(stack))
                .toList();

        if (!recipes.isEmpty() && recipes.get(0) instanceof AnvilRecipe recipe) {
            ItemStack result = recipe.getResultItem().copy();
            result.grow(level.getRandom().nextInt(0, recipe.getExtraCount() + 1));
            return result;
        }

        return ItemStack.EMPTY;
    }

}

package shagejack.lostgrace.registries.recipe.vanilla;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import shagejack.lostgrace.registries.block.AllBlocks;
import shagejack.lostgrace.registries.effect.AllPotions;

public class AllBrewingRecipes {

    public static void init() {
        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WEAKNESS)),
                Ingredient.of(AllBlocks.trinaLily.item().get()),
                PotionUtils.setPotion(new ItemStack(Items.POTION), AllPotions.SLEEP)
        );

        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), AllPotions.SLEEP)),
                Ingredient.of(Items.REDSTONE),
                PotionUtils.setPotion(new ItemStack(Items.POTION), AllPotions.LONG_SLEEP)
        );

        BrewingRecipeRegistry.addRecipe(
                Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION), AllPotions.SLEEP)),
                Ingredient.of(Items.GLOWSTONE_DUST),
                PotionUtils.setPotion(new ItemStack(Items.POTION), AllPotions.STRONG_SLEEP)
        );
    }
}

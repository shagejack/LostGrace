package shagejack.lostgrace.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import shagejack.lostgrace.LostGrace;
import shagejack.lostgrace.compat.jei.anvil.AnvilRecipeCategory;
import shagejack.lostgrace.contents.recipe.anvil.AnvilRecipe;
import shagejack.lostgrace.foundation.config.LostGraceConfig;
import shagejack.lostgrace.registries.recipe.AllRecipeTypes;

import java.util.Objects;

@JeiPlugin
public class LostGraceJEIPlugin implements IModPlugin {

    private static final ResourceLocation UID = LostGrace.asResource("plugin_" + LostGrace.MOD_ID);

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        if (LostGraceConfig.ENABLE_JEI_PLUGIN.get()) {
            registration.addRecipeCategories(new AnvilRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        }
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (LostGraceConfig.ENABLE_JEI_PLUGIN.get()) {
            registration.addRecipeCatalyst(new ItemStack(Items.ANVIL), new RecipeType<>(AllRecipeTypes.ANVIL.getId(), AnvilRecipe.class));
        }
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Objects.requireNonNull(Minecraft.getInstance().level);

        if (LostGraceConfig.ENABLE_JEI_PLUGIN.get()) {
            registration.addRecipes(new RecipeType<>(AllRecipeTypes.ANVIL.getId(), AnvilRecipe.class), AllRecipeTypes.ANVIL.getAllRecipes(level));
        }
    }
}

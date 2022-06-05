package shagejack.lostgrace.compat.jei.anvil;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import shagejack.lostgrace.contents.recipe.anvil.AnvilRecipe;
import shagejack.lostgrace.registries.AllTextures;
import shagejack.lostgrace.registries.recipe.AllRecipeTypes;

public class AnvilRecipeCategory implements IRecipeCategory<AnvilRecipe> {

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable anvil;

    public AnvilRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(AllTextures.ANVIL_RECIPE_CATEGORY_GUI_BACKGROUND, 12, 12, 128, 64);
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.ANVIL));
        this.anvil = new AnimatedAnvil(guiHelper);
    }

    @Override
    public Component getTitle() {
        return new TranslatableComponent("lostgrace.jei.category.anvil");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void draw(AnvilRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        anvil.draw(stack, 3, 0);
    }

    @Override
    public RecipeType<AnvilRecipe> getRecipeType() {
        return new RecipeType<>(AllRecipeTypes.ANVIL.getId(), AnvilRecipe.class);
    }

    @Deprecated(forRemoval = true)
    @Override
    public ResourceLocation getUid() {
        return AllRecipeTypes.ANVIL.getId();
    }

    @Deprecated(forRemoval = true)
    @Override
    public Class<? extends AnvilRecipe> getRecipeClass() {
        return AnvilRecipe.class;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AnvilRecipe recipe, IFocusGroup focuses) {
        builder.createFocusLink(builder.addSlot(RecipeIngredientRole.INPUT, 10, 42).addIngredients(recipe.getIngredients().get(0)));
        builder.createFocusLink(
                builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 20)
                        .addItemStack(recipe.getResultItem())
                        .addTooltipCallback((recipeSlotView, toolTip) -> toolTip.add(1, new TranslatableComponent("lostgrace.jei.info.base_output").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY)))
        );
        builder.createFocusLink(
                builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 20)
                        .addItemStack(recipe.getExtraResultItem())
                        .addTooltipCallback((recipeSlotView, toolTip) -> toolTip.add(1, new TranslatableComponent("lostgrace.jei.info.extra_output").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY)))
        );
    }
}

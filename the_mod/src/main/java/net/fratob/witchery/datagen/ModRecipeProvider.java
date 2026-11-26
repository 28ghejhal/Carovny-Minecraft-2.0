package net.fratob.witchery.datagen;

import java.util.function.Consumer;

import net.fratob.witchery.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

// Autogenerate recipes like smelting etc.
public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder{
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MANA_CRYSTAL.get())
                .pattern("CPC")
                .pattern("PDP")
                .pattern("CPC")
                .define('C', ModItems.CRYSTAL.get())
                .define('P', ModItems.MANA_POWDER.get())
                .define('D', Items.DIAMOND)
                .unlockedBy(getHasName(ModItems.CRYSTAL.get()), has(ModItems.CRYSTAL.get()))
                .unlockedBy(getHasName(ModItems.MANA_POWDER.get()), has(ModItems.MANA_POWDER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WAND_OF_LIGHT.get())
                .pattern("  M")
                .pattern(" S ")
                .pattern("S  ")
                .define('M', ModItems.MANA_CRYSTAL.get())
                .define('S', Items.STICK)
                .unlockedBy(getHasName(ModItems.MANA_CRYSTAL.get()), has(ModItems.MANA_CRYSTAL.get()))
                .save(pWriter);
    }

    // Sadly minecraft RecipeProvider does not support different MODIDs, so I have to copy all of them once more

}

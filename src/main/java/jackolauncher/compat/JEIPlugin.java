package jackolauncher.compat;


import jackolauncher.JackOLauncher;
import jackolauncher.item.JackOAmmoRecipe;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@JeiPlugin
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class JEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(JackOLauncher.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<IRecipe> recipes = new ArrayList<>();
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_GUNPOWDER, Items.GUNPOWDER));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_FIRE_CHARGE, Items.FIRE_CHARGE));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_SLIMEBALLS, Items.SLIME_BALL));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_NUGGETS_IRON, Items.IRON_NUGGET));
        recipes.add(new DummyRecipe(Arrays.asList(JackOAmmoRecipe.INGREDIENT_GUNPOWDER, JackOAmmoRecipe.INGREDIENT_WOOL), Items.GUNPOWDER, Blocks.WHITE_WOOL.asItem()));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_BONE_BLOCK, Blocks.BONE_BLOCK.asItem()));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_ENDER_PEARLS, Items.ENDER_PEARL));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_FIREWORK_ROCKET, Items.FIREWORK_ROCKET));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_POTION, Items.SPLASH_POTION));
        recipes.add(new DummyRecipe(Ingredient.fromTag(Tags.Items.ARROWS), Items.ARROW));
        registration.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private static class DummyRecipe extends SpecialRecipe {

        private static final Ingredient INGREDIENT_PUMPKIN = Ingredient.fromItems(Blocks.PUMPKIN, Blocks.CARVED_PUMPKIN, Blocks.JACK_O_LANTERN);
        private final NonNullList<Ingredient> ingredients;
        private final ItemStack output;

        private DummyRecipe(Ingredient ingredient, Item... ingredientsForOutput) {
            this(Collections.singletonList(ingredient), ingredientsForOutput);
        }

        private DummyRecipe(List<Ingredient> ingredients, Item... ingredientsForOutput) {
            super(new ResourceLocation(""));
            this.ingredients = NonNullList.create();
            this.ingredients.addAll(ingredients);
            this.ingredients.add(0, INGREDIENT_PUMPKIN);
            ArrayList<Item> ingredientsForOutputList = new ArrayList<>(Arrays.asList(ingredientsForOutput));
            ingredientsForOutputList.add(Blocks.PUMPKIN.asItem());
            this.output = JackOAmmoRecipe.getCraftingResult(ingredientsForOutputList.stream().map(ItemStack::new).toArray(ItemStack[]::new));
        }

        @Override
        public boolean matches(CraftingInventory inv, World worldIn) {
            return true;
        }

        @Override
        public ItemStack getCraftingResult(CraftingInventory inv) {
            return output;
        }

        @Override
        public boolean canFit(int width, int height) {
            return true;
        }

        @Override
        public ItemStack getRecipeOutput() {
            return output;
        }

        @Override
        public ResourceLocation getId() {
            return new ResourceLocation(JackOLauncher.MODID, "");
        }

        @Override
        public IRecipeSerializer<?> getSerializer() {
            // noinspection ConstantConditions
            return null;
        }

        @Override
        public IRecipeType<?> getType() {
            return IRecipeType.CRAFTING;
        }

        @Override
        public NonNullList<Ingredient> getIngredients() {
            return ingredients;
        }
    }
}


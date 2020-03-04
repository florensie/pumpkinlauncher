package jackolauncher.compat;
/*


import jackolauncher.JackOLauncher;
import jackolauncher.item.JackOAmmoRecipe;
import mcp.MethodsReturnNonnullByDefault;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
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
    public Identifier getPluginUid() {
        return new Identifier(JackOLauncher.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<Recipe> recipes = new ArrayList<>();
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_GUNPOWDER, Items.field_8054));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_FIRE_CHARGE, Items.field_8814));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_SLIMEBALLS, Items.field_8777));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_NUGGETS_IRON, Items.field_8675));
        recipes.add(new DummyRecipe(Arrays.asList(JackOAmmoRecipe.INGREDIENT_GUNPOWDER, JackOAmmoRecipe.INGREDIENT_WOOL), Items.field_8054, Blocks.field_10446.asItem()));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_BONE_BLOCK, Blocks.field_10166.asItem()));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_ENDER_PEARLS, Items.field_8634));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_FIREWORK_ROCKET, Items.field_8639));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_POTION, Items.field_8436));
        recipes.add(new DummyRecipe(Ingredient.fromTag(Tags.Items.ARROWS), Items.field_8107));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_NUGGETS_GOLD, Items.field_8397));
        recipes.add(new DummyRecipe(JackOAmmoRecipe.INGREDIENT_FEATHERS, Items.field_8153));
        registration.addRecipes(recipes, VanillaRecipeCategoryUid.CRAFTING);
    }

    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    private static class DummyRecipe extends JackOAmmoRecipe {

        private static final Ingredient INGREDIENT_PUMPKIN = Ingredient.ofItems(Blocks.field_10261, Blocks.field_10147, Blocks.field_10009);
        private final DefaultedList<Ingredient> ingredients;
        private final ItemStack output;

        private DummyRecipe(Ingredient ingredient, Item... ingredientsForOutput) {
            this(Collections.singletonList(ingredient), ingredientsForOutput);
        }

        private DummyRecipe(List<Ingredient> ingredientsForDisplay, Item... ingredientsForOutput) {
            super(new Identifier(JackOLauncher.MODID, "crafting_special_jack_o_ammo"));
            ingredients = DefaultedList.of();
            ingredients.addAll(ingredientsForDisplay);
            ingredients.add(0, INGREDIENT_PUMPKIN);
            ArrayList<Item> ingredientsForOutputList = new ArrayList<>(Arrays.asList(ingredientsForOutput));
            ingredientsForOutputList.add(Blocks.field_10261.asItem());
            output = craft(ingredientsForOutputList.stream().map(ItemStack::new).toArray(ItemStack[]::new));
        }

        @Override
        public ItemStack getOutput() {
            return output;
        }

        @Override
        public DefaultedList<Ingredient> getPreviewInputs() {
            return ingredients;
        }
    }
}

*/

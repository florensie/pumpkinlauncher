package jackolauncher.item;

import jackolauncher.JackOLauncher;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.block.GourdBlock;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class JackOAmmoRecipe extends SpecialCraftingRecipe {

    public static final Ingredient INGREDIENT_WOOL = Ingredient.fromTag(ItemTags.WOOL);

    public static final Ingredient INGREDIENT_NUGGETS_IRON = Ingredient.ofItems(Items.IRON_NUGGET);
    public static final Ingredient INGREDIENT_GUNPOWDER = Ingredient.ofItems(Items.GUNPOWDER);
    public static final Ingredient INGREDIENT_ENDER_PEARLS = Ingredient.ofItems(Items.ENDER_PEARL);
    public static final Ingredient INGREDIENT_SLIMEBALLS = Ingredient.ofItems(Items.SLIME_BALL);
    public static final Ingredient INGREDIENT_NUGGETS_GOLD = Ingredient.ofItems(Items.GOLD_NUGGET);
    public static final Ingredient INGREDIENT_FEATHERS = Ingredient.ofItems(Items.FEATHER);

    public static final Ingredient INGREDIENT_BONE_BLOCK = Ingredient.ofItems(Blocks.BONE_BLOCK);
    public static final Ingredient INGREDIENT_FIRE_CHARGE = Ingredient.ofItems(Items.FIRE_CHARGE);
    public static final Ingredient INGREDIENT_FIREWORK_ROCKET = Ingredient.ofItems(Items.FIREWORK_ROCKET);
    public static final Ingredient INGREDIENT_POTION = Ingredient.ofItems(Items.SPLASH_POTION, Items.LINGERING_POTION);


    public JackOAmmoRecipe(Identifier resourceLocation) {
        super(resourceLocation);
    }

    public static ItemStack craft(ItemStack... inputs) {
        ItemStack result = new ItemStack(JackOLauncher.JACK_O_AMMO, 3);

        int explosionPower = 0;
        int bounceAmount = 0;
        int extraDamage = 0;
        int fortuneLevel = 0;

        ItemStack arrowsStack = ItemStack.EMPTY;

        for (ItemStack inputStack : inputs) {
            if (!inputStack.isEmpty()) {
                if (Block.getBlockFromItem(inputStack.getItem()) instanceof GourdBlock || Block.getBlockFromItem(inputStack.getItem()) instanceof CarvedPumpkinBlock) {
                    JackOAmmoHelper.setBlockState(result, Block.getBlockFromItem(inputStack.getItem()).getDefaultState());
                } else if (INGREDIENT_BONE_BLOCK.test(inputStack)) {
                    JackOAmmoHelper.setBoneMeal(result);
                } else if (INGREDIENT_ENDER_PEARLS.test(inputStack)) {
                    JackOAmmoHelper.setEnderPearl(result);
                } else if (INGREDIENT_FIRE_CHARGE.test(inputStack)) {
                    JackOAmmoHelper.setFlaming(result);
                } else if (INGREDIENT_WOOL.test(inputStack)) {
                    JackOAmmoHelper.setShouldDamageTerrain(result, false);
                } else if (INGREDIENT_FEATHERS.test(inputStack)) {
                    JackOAmmoHelper.setSilkTouch(result);
                } else if (INGREDIENT_GUNPOWDER.test(inputStack)) {
                    ++explosionPower;
                } else if (INGREDIENT_SLIMEBALLS.test(inputStack)) {
                    ++bounceAmount;
                } else if (INGREDIENT_NUGGETS_IRON.test(inputStack)) {
                    ++extraDamage;
                } else if (INGREDIENT_NUGGETS_GOLD.test(inputStack)) {
                    ++fortuneLevel;
                } else if (INGREDIENT_POTION.test(inputStack)) {
                    JackOAmmoHelper.setPotion(result, inputStack);
                } else if (INGREDIENT_FIREWORK_ROCKET.test(inputStack)) {
                    JackOAmmoHelper.setFireworks(result, inputStack);
                } else if (inputStack.getItem() instanceof ArrowItem) {
                    if (arrowsStack.isEmpty()) {
                        arrowsStack = inputStack.copy();
                        arrowsStack.setCount(1);
                    } else {
                        arrowsStack.increment(1);
                    }
                }
            }
        }

        JackOAmmoHelper.setArrows(result, arrowsStack);
        JackOAmmoHelper.setExplosionPower(result, explosionPower);
        JackOAmmoHelper.setBouncesAmount(result, bounceAmount);
        JackOAmmoHelper.setExtraDamage(result, extraDamage);
        JackOAmmoHelper.setFortuneLevel(result, fortuneLevel);

        return result;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean matches(CraftingInventory inventory, World world) {
        if (!(inventory instanceof CraftingInventory)) {
            return false;
        }

        boolean boneBlockFlag = false;
        boolean enderPearlFlag = false;
        boolean fireChargeFlag = false;
        boolean fireworkRocketFlag = false;
        boolean potionFlag = false;
        boolean pumpkinFlag = false;
        boolean featherFlag = false;
        boolean woolFlag = false;

        int gunpowderAmount = 0;
        int slimeBallAmount = 0;
        int ironNuggetAmount = 0;
        int goldNuggetAmount = 0;

        ItemStack arrowsStack = ItemStack.EMPTY;

        for (int slotId = 0; slotId < inventory.getInvSize(); ++slotId) {
            ItemStack stackInSlot = inventory.getInvStack(slotId);

            if (Block.getBlockFromItem(stackInSlot.getItem()) instanceof GourdBlock || Block.getBlockFromItem(stackInSlot.getItem()) instanceof CarvedPumpkinBlock) {
                if (pumpkinFlag) {
                    return false;
                }
                pumpkinFlag = true;
            } else if (INGREDIENT_POTION.test(stackInSlot)) {
                if (potionFlag || !stackInSlot.hasTag()) {
                    return false;
                }
                potionFlag = true;
            } else if (INGREDIENT_BONE_BLOCK.test(stackInSlot)) {
                if (boneBlockFlag) {
                    return false;
                }
                boneBlockFlag = true;
            } else if (INGREDIENT_FIRE_CHARGE.test(stackInSlot)) {
                if (fireChargeFlag) {
                    return false;
                }
                fireChargeFlag = true;
            } else if (INGREDIENT_ENDER_PEARLS.test(stackInSlot)) {
                if (enderPearlFlag) {
                    return false;
                }
                enderPearlFlag = true;
            } else if (INGREDIENT_FIREWORK_ROCKET.test(stackInSlot)) {
                if (fireworkRocketFlag) {
                    return false;
                }
                fireworkRocketFlag = true;
            } else if (INGREDIENT_WOOL.test(stackInSlot)) {
                if (woolFlag) {
                    return false;
                }
                woolFlag = true;
            } else if (INGREDIENT_FEATHERS.test(stackInSlot)) {
                if (featherFlag) {
                    return false;
                }
                featherFlag = true;
            } else if (INGREDIENT_GUNPOWDER.test(stackInSlot)) {
                if (++gunpowderAmount > 16) {
                    return false;
                }
            } else if (INGREDIENT_NUGGETS_IRON.test(stackInSlot)) {
                if (++ironNuggetAmount > 4) {
                    return false;
                }
            } else if (INGREDIENT_NUGGETS_GOLD.test(stackInSlot)) {
                if (++goldNuggetAmount > 3) {
                    return false;
                }
            } else if (stackInSlot.getItem() instanceof ArrowItem) {
                if (arrowsStack.isEmpty() || arrowsStack.getCount() >= 16) {
                    arrowsStack = stackInSlot.copy();
                    arrowsStack.setCount(1);
                } else {
                    ItemStack stackInSlotCopy = stackInSlot.copy();
                    stackInSlotCopy.setCount(arrowsStack.getCount());
                    if (!ItemStack.areEqualIgnoreDamage(arrowsStack, stackInSlotCopy)) {
                        return false;
                    }
                    arrowsStack.increment(1);
                }
            } else if (INGREDIENT_SLIMEBALLS.test(stackInSlot)) {
                if (++slimeBallAmount > 1) {
                    return false;
                }
            } else if (!stackInSlot.isEmpty()) {
                return false;
            }
        }

        if (!(potionFlag || boneBlockFlag || enderPearlFlag || fireChargeFlag || fireworkRocketFlag || gunpowderAmount > 0 || ironNuggetAmount > 0 || slimeBallAmount > 0 || !arrowsStack.isEmpty())) {
            return false;
        }
        if ((featherFlag || goldNuggetAmount > 0 || woolFlag) && gunpowderAmount == 0) {
            return false;
        }
        if ((woolFlag && featherFlag) || (woolFlag && goldNuggetAmount > 0) || (featherFlag && goldNuggetAmount > 0)) {
            return false;
        }

        return pumpkinFlag;
    }

    @Override
    public ItemStack craft(CraftingInventory inventory) {
        ItemStack[] inputs = new ItemStack[inventory.getInvSize()];
        for (int slotId = 0; slotId < inventory.getInvSize(); ++slotId) {
            inputs[slotId] = inventory.getInvStack(slotId);
        }
        return craft(inputs);
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height > 2;
    }

    @Override
    public ItemStack getOutput() {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return JackOLauncher.JACK_O_AMMO_RECIPE_SERIALIZER;
    }
}

package jackolauncher.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;

public class JackOAmmoHelper {

    public static CompoundTag getAmmoProperties(ItemStack stack) {
        return stack.getOrCreateSubTag("AmmoProperties");
    }

    public static void setBlockState(ItemStack stack, BlockState blockState) {
        getAmmoProperties(stack).put("BlockState", NbtHelper.fromBlockState(blockState));
    }

    public static BlockState getBlockState(ItemStack stack) {
        CompoundTag compound = getAmmoProperties(stack);
        if (compound.contains("BlockState")) {
            return NbtHelper.toBlockState(compound.getCompound("BlockState"));
        }
        return Blocks.JACK_O_LANTERN.getDefaultState();
    }

    public static void setExplosionPower(ItemStack stack, int explosionPower) {
        getAmmoProperties(stack).putByte("ExplosionPower", (byte) explosionPower);
    }

    public static int getExplosionPower(ItemStack stack) {
        CompoundTag compound = getAmmoProperties(stack);
        if (compound.contains("ExplosionPower")) {
            return getAmmoProperties(stack).getByte("ExplosionPower");
        }
        return 3;
    }

    public static void setShouldDamageTerrain(ItemStack stack, boolean shouldDamageTerrain) {
        getAmmoProperties(stack).putBoolean("ShouldDamageTerrain", shouldDamageTerrain);
    }

    public static boolean getShouldDamageTerrain(ItemStack stack) {
        CompoundTag compound = getAmmoProperties(stack);
        return !compound.contains("ShouldDamageTerrain") || compound.getBoolean("ShouldDamageTerrain");
    }

    public static void setFlaming(ItemStack stack) {
        getAmmoProperties(stack).putBoolean("IsFlaming", true);
    }

    public static boolean isFlaming(ItemStack stack) {
        return getAmmoProperties(stack).getBoolean("IsFlaming");
    }

    public static void setSilkTouch(ItemStack stack) {
        getAmmoProperties(stack).putBoolean("HasSilkTouch", true);
    }

    public static boolean hasSilkTouch(ItemStack stack) {
        return getAmmoProperties(stack).getBoolean("HasSilkTouch");
    }

    public static void setEnderPearl(ItemStack stack) {
        getAmmoProperties(stack).putBoolean("IsEnderPearl", true);
    }

    public static boolean isEnderPearl(ItemStack stack) {
        return getAmmoProperties(stack).getBoolean("IsEnderPearl");
    }

    public static void setBoneMeal(ItemStack stack) {
        getAmmoProperties(stack).putBoolean("IsBoneMeal", true);
    }

    public static boolean isBoneMeal(ItemStack stack) {
        return getAmmoProperties(stack).getBoolean("IsBoneMeal");
    }

    public static void setBouncesAmount(ItemStack stack, int bounces) {
        getAmmoProperties(stack).putByte("BouncesAmount", (byte) bounces);
    }

    public static int getBouncesAmount(ItemStack stack) {
        return getAmmoProperties(stack).getByte("BouncesAmount");
    }

    public static void setFortuneLevel(ItemStack stack, int fortuneLevel) {
        getAmmoProperties(stack).putByte("FortuneLevel", (byte) fortuneLevel);
    }

    public static int getFortuneLevel(ItemStack stack) {
        return getAmmoProperties(stack).getByte("FortuneLevel");
    }

    public static void setExtraDamage(ItemStack stack, int extraDamage) {
        getAmmoProperties(stack).putByte("ExtraDamage", (byte) extraDamage);
    }

    public static int getExtraDamage(ItemStack stack) {
        return getAmmoProperties(stack).getByte("ExtraDamage");
    }

    public static void setArrows(ItemStack stack, ItemStack arrows) {
        getAmmoProperties(stack).put("Arrows", arrows.toTag(new CompoundTag()));
    }

    public static ItemStack getArrows(ItemStack stack) {
        return ItemStack.fromTag(getAmmoProperties(stack).getCompound("Arrows"));
    }

    public static void setPotion(ItemStack stack, ItemStack potion) {
        getAmmoProperties(stack).put("Potion", potion.toTag(new CompoundTag()));
    }

    public static ItemStack getPotion(ItemStack stack) {
        return ItemStack.fromTag(getAmmoProperties(stack).getCompound("Potion"));
    }

    public static void setFireworks(ItemStack stack, ItemStack fireworksStack) {
        if (!fireworksStack.hasTag()) {
            CompoundTag fireworksCompound = new CompoundTag();
            fireworksCompound.putByte("Flight", (byte) 2);
            getAmmoProperties(stack).put("Fireworks", fireworksCompound);
        } else {
            getAmmoProperties(stack).put("Fireworks", fireworksStack.getOrCreateSubTag("Fireworks"));
        }
    }

    public static int getFlight(ItemStack stack) {
        return getAmmoProperties(stack).getCompound("Fireworks").getByte("Flight");
    }

    public static ListTag getFireworkExplosions(ItemStack stack) {
        return getAmmoProperties(stack).getCompound("Fireworks").getList("Explosions", 10);
    }
}

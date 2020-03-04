package jackolauncher.enchantment;

import jackolauncher.item.JackOLauncherItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class ModEnchantment extends Enchantment {

    protected final int maxLevel;

    protected ModEnchantment(Weight rarity, int maxLevel) {
        super(rarity, EnchantmentTarget.BOW, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        this.maxLevel = maxLevel;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof JackOLauncherItem && super.isAcceptableItem(stack);
    }

    // TODO: patch?
    /*@Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof JackOLauncherItem && super.canApplyAtEnchantingTable(stack);
    }*/

    @Override
    public int getMaximumLevel() {
        return maxLevel;
    }
}

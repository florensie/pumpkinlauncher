package jackolauncher.enchantment;

public class BlastShieldEnchantment extends ModEnchantment {

    public BlastShieldEnchantment() {
        super(Weight.UNCOMMON, 1);
    }

    public int getMinimumPower(int enchantmentLevel) {
        return 20;
    }

    public int getMaximumPower(int enchantmentLevel) {
        return 50;
    }
}

package jackolauncher.enchantment;

public class LaunchingEnchantment extends ModEnchantment {

    public LaunchingEnchantment() {
        super(Weight.COMMON, 5);
    }

    public int getMinimumPower(int enchantmentLevel) {
        return 1 + (enchantmentLevel - 1) * 10;
    }

    public int getMaximumPower(int enchantmentLevel) {
        return this.getMinimumPower(enchantmentLevel) + 15;
    }
}

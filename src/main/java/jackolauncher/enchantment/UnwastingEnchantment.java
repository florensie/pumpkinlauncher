package jackolauncher.enchantment;

public class UnwastingEnchantment extends ModEnchantment {

    public UnwastingEnchantment() {
        super(Weight.UNCOMMON, 3);
    }

    @Override
    public int getMinimumPower(int enchantmentLevel) {
        return enchantmentLevel * 10;
    }

    @Override
    public int getMaximumPower(int enchantmentLevel) {
        return getMinimumPower(enchantmentLevel) + 20;
    }
}

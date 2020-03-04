package jackolauncher.enchantment;

public class ReloadingEnchantment extends ModEnchantment {

    public ReloadingEnchantment() {
        super(Weight.UNCOMMON, 4);
    }

    @Override
    public int getMinimumPower(int enchantmentLevel) {
        return 1 + (enchantmentLevel - 1) * 10;
    }

    @Override
    public int getMaximumPower(int enchantmentLevel) {
        return this.getMinimumPower(enchantmentLevel) + 15;
    }
}

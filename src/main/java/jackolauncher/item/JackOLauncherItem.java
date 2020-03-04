package jackolauncher.item;

import jackolauncher.JackOLauncher;
import jackolauncher.entity.JackOProjectileEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public class JackOLauncherItem extends Item {

    public static final List<Enchantment> VALID_ENCHANTMENTS = Arrays.asList(Enchantments.MENDING, Enchantments.UNBREAKING, Enchantments.LOOTING, JackOLauncher.UNWASTING, JackOLauncher.BLAST_SHIELD, JackOLauncher.LAUNCHING, JackOLauncher.RELOADING);

    public JackOLauncherItem() {
        super(new Settings().maxDamageIfAbsent(95).group(ItemGroup.COMBAT));
    }

    private ItemStack findAmmo(PlayerEntity player) {
        if (player.getStackInHand(Hand.OFF_HAND).getItem() == JackOLauncher.JACK_O_AMMO) {
            return player.getStackInHand(Hand.OFF_HAND);
        } else if (player.getStackInHand(Hand.MAIN_HAND).getItem() == JackOLauncher.JACK_O_AMMO) {
            return player.getStackInHand(Hand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.inventory.getInvSize(); ++i) {
                ItemStack stack = player.inventory.getInvStack(i);
                if (stack.getItem() == JackOLauncher.JACK_O_AMMO) {
                    return stack;
                }
            }
            if (player.abilities.creativeMode) {
                return new ItemStack(JackOLauncher.JACK_O_AMMO);
            }
            return ItemStack.EMPTY;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = findAmmo(player);

        if (!stack.isEmpty()) {
            if (!world.isClient) {
                int reloadingLevel = EnchantmentHelper.getLevel(JackOLauncher.RELOADING, player.getStackInHand(hand));
                int blastShieldLevel = EnchantmentHelper.getLevel(JackOLauncher.BLAST_SHIELD, player.getStackInHand(hand));
                int launchingLevel = EnchantmentHelper.getLevel(JackOLauncher.LAUNCHING, player.getStackInHand(hand));
                int unwastingLevel = EnchantmentHelper.getLevel(JackOLauncher.UNWASTING, player.getStackInHand(hand));

                player.getItemCooldownManager().set(this, 40 - 6 * reloadingLevel);

                JackOProjectileEntity projectile = new JackOProjectileEntity(world, player, JackOAmmoHelper.getAmmoProperties(stack).copy(), blastShieldLevel > 0);
                projectile.setVelocity(player, player.pitch, player.yaw, 1.3F + 0.13F * launchingLevel, 3 - 2.5F * launchingLevel / JackOLauncher.LAUNCHING.getMaximumLevel());
                world.spawnEntity(projectile);

                if (!player.abilities.creativeMode && RANDOM.nextInt(unwastingLevel + 1) == 0) {
                    stack.decrement(1);
                }
            }
            world.playSound(player, player.x, player.y, player.z, SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.NEUTRAL, 1.0F, 0.6F);
            player.getStackInHand(hand).damage(1, player, (entity) -> entity.sendToolBreakStatus(hand));
            return new TypedActionResult<>(ActionResult.SUCCESS, player.getStackInHand(hand));
        } else {
            if (!world.isClient) {
                world.playSound(null, player.x, player.y, player.z, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.NEUTRAL, 1.0F, 1.2F);
            }
            return new TypedActionResult<>(ActionResult.FAIL, player.getStackInHand(hand));
        }
    }

    @Override
    public int getEnchantability() {
        return 4;
    }

/*    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment) {
        return VALID_ENCHANTMENTS.contains(enchantment);
    }*/
}

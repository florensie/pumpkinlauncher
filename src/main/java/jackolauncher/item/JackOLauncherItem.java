package jackolauncher.item;

import jackolauncher.JackOLauncher;
//import jackolauncher.entity.JackOProjectileEntity;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JackOLauncherItem extends Item {

    public static final List<Enchantment> VALID_ENCHANTMENTS = Arrays.asList(Enchantments.MENDING, Enchantments.UNBREAKING, Enchantments.LOOTING, JackOLauncher.UNWASTING, JackOLauncher.BLAST_SHIELD, JackOLauncher.LAUNCHING, JackOLauncher.RELOADING);

    public JackOLauncherItem() {
        super(new Properties().defaultMaxDamage(95).group(ItemGroup.COMBAT));
        setRegistryName(JackOLauncher.MODID, "jack_o_launcher");
    }

    private ItemStack findAmmo(PlayerEntity player) {
        if (player.getHeldItem(Hand.OFF_HAND).getItem() == JackOLauncher.JACK_O_AMMO) {
            return player.getHeldItem(Hand.OFF_HAND);
        } else if (player.getHeldItem(Hand.MAIN_HAND).getItem() == JackOLauncher.JACK_O_AMMO) {
            return player.getHeldItem(Hand.MAIN_HAND);
        } else {
            for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack.getItem() == JackOLauncher.JACK_O_AMMO) {
                    return stack;
                }
            }
            if (player.abilities.isCreativeMode) {
                return new ItemStack(JackOLauncher.JACK_O_AMMO);
            }
            return ItemStack.EMPTY;
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = findAmmo(player);

        if (!stack.isEmpty()) {
            if (!world.isRemote) {
                int reloadingLevel = EnchantmentHelper.getEnchantmentLevel(JackOLauncher.RELOADING, player.getHeldItem(hand));
                int blastShieldLevel = EnchantmentHelper.getEnchantmentLevel(JackOLauncher.BLAST_SHIELD, player.getHeldItem(hand));
                int launchingLevel = EnchantmentHelper.getEnchantmentLevel(JackOLauncher.LAUNCHING, player.getHeldItem(hand));
                int unwastingLevel = EnchantmentHelper.getEnchantmentLevel(JackOLauncher.UNWASTING, player.getHeldItem(hand));

                player.getCooldownTracker().setCooldown(this, 40 - 6 * reloadingLevel);

                //JackOProjectileEntity projectile = new JackOProjectileEntity(world, player, JackOAmmoHelper.getAmmoProperties(stack).copy(), blastShieldLevel > 0);
                //projectile.shoot(player, player.rotationPitch, player.rotationYaw, 1.3F + 0.13F * launchingLevel, 3 - 2.5F * launchingLevel / JackOLauncher.LAUNCHING.getMaxLevel());
                //world.addEntity(projectile);

                if (!player.abilities.isCreativeMode && random.nextInt(unwastingLevel + 1) == 0) {
                    stack.shrink(1);
                }
            }
            world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.NEUTRAL, 1.0F, 0.6F);
            player.getHeldItem(hand).damageItem(1, player, (entity) -> entity.sendBreakAnimation(hand));
            return new ActionResult<>(ActionResultType.SUCCESS, player.getHeldItem(hand));
        } else {
            if (!world.isRemote) {
                world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.BLOCK_DISPENSER_FAIL, SoundCategory.NEUTRAL, 1.0F, 1.2F);
            }
            return new ActionResult<>(ActionResultType.FAIL, player.getHeldItem(hand));
        }
    }

    @Override
    public int getItemEnchantability() {
        return 4;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, net.minecraft.enchantment.Enchantment enchantment) {
        return VALID_ENCHANTMENTS.contains(enchantment);
    }
}

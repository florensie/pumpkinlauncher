package jackolauncher.item;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.potion.PotionUtil;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.world.World;

import java.util.List;

public class JackOAmmoItem extends Item {

    public JackOAmmoItem() {
        super(new Settings().group(ItemGroup.COMBAT));
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void appendTooltip(ItemStack stack, World worldIn, List<Text> tooltip, TooltipContext flag) {
        super.appendTooltip(stack, worldIn, tooltip, flag);

        tooltip.add(JackOAmmoHelper.getBlockState(stack).getBlock().getName().formatted(Formatting.GRAY));

        if (JackOAmmoHelper.hasSilkTouch(stack)) {
            tooltip.add(Enchantments.SILK_TOUCH.getName(1).formatted(Formatting.GRAY));
        }

        int fortuneLevel = JackOAmmoHelper.getFortuneLevel(stack);
        if (fortuneLevel > 0) {
            tooltip.add(Enchantments.FORTUNE.getName(fortuneLevel).formatted(Formatting.GRAY));
        }

        ItemStack arrowStack = JackOAmmoHelper.getArrows(stack);
        if (!arrowStack.isEmpty()) {
            if (arrowStack.getCount() > 0) {
                tooltip.add(new TranslatableText("item.jack_o_launcher.jack_o_ammo.arrows").append(" " + arrowStack.getCount()).formatted(Formatting.GRAY));

                if (arrowStack.getItem() instanceof TippedArrowItem) {
                    List<StatusEffectInstance> effects = PotionUtil.getPotionEffects(arrowStack);
                    if (effects.isEmpty()) {
                        tooltip.add(new LiteralText("  ").append(new TranslatableText("effect.none")).formatted(Formatting.GRAY));
                    } else {
                        for (StatusEffectInstance effect : effects) {
                            Text potionEffectTextComponent = new TranslatableText(effect.getTranslationKey());

                            if (effect.getAmplifier() > 0) {
                                potionEffectTextComponent.append(" ").append(new TranslatableText("potion.potency." + effect.getAmplifier()));
                            }

                            if (effect.getDuration() > 20) {
                                potionEffectTextComponent.append(" (" + StatusEffectUtil.durationToString(effect, 0.125F) + ")");
                            }

                            if (effect.getEffectType().method_5573()) {
                                tooltip.add(new LiteralText("  ").append(potionEffectTextComponent).formatted(Formatting.BLUE));
                            } else {
                                tooltip.add(new LiteralText("  ").append(potionEffectTextComponent).formatted(Formatting.RED));
                            }
                        }
                    }
                } else if (arrowStack.getItem() instanceof SpectralArrowItem) {
                    tooltip.add(new LiteralText("  ").append(new TranslatableText("item.jack_o_launcher.jack_o_ammo.spectral")).formatted(Formatting.AQUA));
                } else if (arrowStack.getItem() != Items.ARROW) {
                    tooltip.add(new LiteralText("  ").append(arrowStack.toHoverableText()).formatted(Formatting.DARK_GREEN));
                }
            }
        }

        int explosionPower = JackOAmmoHelper.getExplosionPower(stack);
        addTranslationTextComponent(tooltip, explosionPower > 0, "explosion_power", " " + explosionPower);
        int bouncesAmount = JackOAmmoHelper.getBouncesAmount(stack);
        addTranslationTextComponent(tooltip, bouncesAmount > 0, "bounce");
        int extraDamage = JackOAmmoHelper.getExtraDamage(stack);
        addTranslationTextComponent(tooltip, extraDamage > 0, "extra_damage", " " + extraDamage);

        addTranslationTextComponent(tooltip, JackOAmmoHelper.isFlaming(stack), "flaming");
        addTranslationTextComponent(tooltip, !JackOAmmoHelper.getShouldDamageTerrain(stack), "cannot_destroy_blocks");
        addTranslationTextComponent(tooltip, JackOAmmoHelper.isBoneMeal(stack), "bone_meal");
        addTranslationTextComponent(tooltip, JackOAmmoHelper.isEnderPearl(stack), "ender_pearl");

        int flight = JackOAmmoHelper.getFlight(stack);
        if (flight > 0) {
            tooltip.add(new TranslatableText("item.minecraft.firework_rocket.flight").append(" " + flight).formatted(Formatting.GRAY));
        }


        ListTag fireworkExplosionsNBTList = JackOAmmoHelper.getFireworkExplosions(stack);
        for (int i = 0; i < fireworkExplosionsNBTList.size(); ++i) {
            CompoundTag fireworkExplosionNBT = fireworkExplosionsNBTList.getCompound(i);
            List<Text> fireworkExplosionTextComponents = Lists.newArrayList();
            FireworkChargeItem.appendFireworkTooltip(fireworkExplosionNBT, fireworkExplosionTextComponents);

            if (!fireworkExplosionTextComponents.isEmpty()) {
                for (int j = 1; j < fireworkExplosionTextComponents.size(); ++j) {
                    fireworkExplosionTextComponents.set(j, new LiteralText("  ").append(fireworkExplosionTextComponents.get(j)));
                }
                tooltip.addAll(fireworkExplosionTextComponents);
            }
        }

        ItemStack potionStack = JackOAmmoHelper.getPotion(stack);
        if (!potionStack.isEmpty()) {
            if (potionStack.getItem() == Items.LINGERING_POTION) {
                tooltip.add(new TranslatableText("item.jack_o_launcher.jack_o_ammo.lingering").formatted(Formatting.DARK_PURPLE));
            }
            PotionUtil.buildTooltip(potionStack, tooltip, potionStack.getItem() == Items.LINGERING_POTION ? 0.25F : 1);
        }
    }

    private void addTranslationTextComponent(List<Text> tooltip, boolean condition, String translationKey) {
        addTranslationTextComponent(tooltip, condition, translationKey, null);
    }

    private void addTranslationTextComponent(List<Text> tooltip, boolean condition, String translationKey, String suffix) {
        if (!condition) {
            return;
        }
        BaseText result = new TranslatableText("item.jack_o_launcher.jack_o_ammo." + translationKey);
        if (suffix != null) {
            result.append(suffix);
        }
        result.formatted(Formatting.GRAY);
        tooltip.add(result);
    }
}

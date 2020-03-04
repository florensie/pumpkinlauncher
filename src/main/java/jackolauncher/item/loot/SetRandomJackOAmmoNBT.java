package jackolauncher.item.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import jackolauncher.JackOLauncher;
import jackolauncher.item.JackOAmmoHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.FireworkItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.loot.condition.LootCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SetRandomJackOAmmoNBT extends ConditionalLootFunction {

    public static final Block[] PUMPKIN_BLOCKS = {
            Blocks.PUMPKIN,
            Blocks.CARVED_PUMPKIN,
            Blocks.JACK_O_LANTERN
    };

    public static final Block[] SPECIAL_BLOCKS = {
            Blocks.ANVIL,
            Blocks.CAKE,
            Blocks.MELON,
            Blocks.TNT,
    };

    protected SetRandomJackOAmmoNBT(LootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    private static void addRandomFireworks(ItemStack stack, Random random) {
        ItemStack fireworks = new ItemStack(Items.FIREWORK_ROCKET);
        CompoundTag compound = fireworks.getOrCreateSubTag("Fireworks");
        compound.putByte("Flight", (byte) (random.nextInt(3) + 1));

        CompoundTag explosionCompound = new CompoundTag();
        explosionCompound.putBoolean("Flicker", random.nextBoolean());
        explosionCompound.putBoolean("Trail", random.nextBoolean());

        List<Integer> colors = new ArrayList<>();
        int dyeAmount = 1 + random.nextInt(3);
        for (int i = 0; i < dyeAmount; i++) {
            colors.add(DyeColor.values()[random.nextInt(DyeColor.values().length)].getFireworkColor());
        }
        explosionCompound.putIntArray("Colors", colors);

        colors = new ArrayList<>();
        dyeAmount = 1 + random.nextInt(3);
        for (int i = 0; i < dyeAmount; i++) {
            colors.add(DyeColor.values()[random.nextInt(DyeColor.values().length)].getFireworkColor());
        }
        explosionCompound.putIntArray("FadeColors", colors);

        explosionCompound.putByte("Type", (byte) FireworkItem.Type.values()[random.nextInt(FireworkItem.Type.values().length)].getId());

        ListTag explosions = new ListTag();
        explosions.add(explosionCompound);

        compound.put("Explosions", explosions);

        JackOAmmoHelper.setFireworks(stack, fireworks);
    }

    private static void addRandomPotion(ItemStack stack, Random random) {
        List<Potion> potionTypes = new ArrayList<>(ForgeRegistries.POTION_TYPES.getValues());
        Registry.POTION.
        potionTypes.remove(Potions.EMPTY);
        ItemStack potion = new ItemStack(random.nextInt(4) == 0 ? Items.LINGERING_POTION : Items.SPLASH_POTION);
        PotionUtil.setPotion(potion, potionTypes.get(random.nextInt(potionTypes.size())));
        JackOAmmoHelper.setPotion(stack, potion);
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        Random random = context.getRandom();

        if (random.nextInt(12) == 0) {
            JackOAmmoHelper.setBlockState(stack, SPECIAL_BLOCKS[random.nextInt(SPECIAL_BLOCKS.length)].getDefaultState());
        } else {
            JackOAmmoHelper.setBlockState(stack, PUMPKIN_BLOCKS[random.nextInt(PUMPKIN_BLOCKS.length)].getDefaultState());
        }

        if (random.nextInt(3) == 0) {
            JackOAmmoHelper.setBouncesAmount(stack, 1);
        }

        if (random.nextInt(4) == 0) {
            JackOAmmoHelper.setBoneMeal(stack);
        }

        if (random.nextInt(4) == 0) {
            JackOAmmoHelper.setExtraDamage(stack, random.nextInt(5) + 2);
        }

        if (random.nextInt(3) == 0) {
            addRandomFireworks(stack, random);
        }

        if (random.nextBoolean()) {
            JackOAmmoHelper.setExplosionPower(stack, random.nextInt(9) + 4);

            switch (random.nextInt(6)) {
                case 0:
                    JackOAmmoHelper.setFortuneLevel(stack, random.nextInt(4) + 1);
                    break;
                case 1:
                    JackOAmmoHelper.setSilkTouch(stack);
                    break;
                case 2:
                    JackOAmmoHelper.setShouldDamageTerrain(stack, false);
            }

            if (random.nextInt(3) == 0) {
                JackOAmmoHelper.setFlaming(stack);
            }
        } else {
            JackOAmmoHelper.setExplosionPower(stack, 0);
            if (random.nextInt(3) == 0) {
                JackOAmmoHelper.setEnderPearl(stack);
            } else if (random.nextBoolean()) {
                JackOAmmoHelper.setArrows(stack, new ItemStack(Items.ARROW, 3 + random.nextInt(16)));
            }

            if (random.nextBoolean()) {
                addRandomPotion(stack, random);
            }
        }
        return stack;
    }

    public static ConditionalLootFunction.Builder<?> builder() {
        return builder(SetRandomJackOAmmoNBT::new);
    }

    public static class Factory extends ConditionalLootFunction.Factory<SetRandomJackOAmmoNBT> {

        public Factory() {
            super(new Identifier(JackOLauncher.MODID, "set_random_jack_o_ammo_nbt"), SetRandomJackOAmmoNBT.class);
        }

        @Override
        public void toJson(JsonObject object, SetRandomJackOAmmoNBT function, JsonSerializationContext serializationContext) {

        }

        @Override
        public SetRandomJackOAmmoNBT fromJson(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditions) {
            return new SetRandomJackOAmmoNBT(conditions);
        }
    }

}

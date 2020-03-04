package jackolauncher;

import jackolauncher.enchantment.BlastShieldEnchantment;
import jackolauncher.enchantment.LaunchingEnchantment;
import jackolauncher.enchantment.ReloadingEnchantment;
import jackolauncher.enchantment.UnwastingEnchantment;
import jackolauncher.entity.JackOProjectileEntity;
import jackolauncher.item.JackOAmmoDispenseBehavior;
import jackolauncher.item.JackOAmmoItem;
import jackolauncher.item.JackOAmmoRecipe;
import jackolauncher.item.JackOLauncherItem;
import jackolauncher.item.loot.SetRandomJackOAmmoNBT;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.minecraft.block.DispenserBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityCategory;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@SuppressWarnings("unused")
public class JackOLauncher implements ModInitializer {

    public static final String MODID = "jack_o_launcher";

    public static final Item JACK_O_LAUNCHER = Registry.register(Registry.ITEM, new Identifier(MODID, "jack_o_launcher"), new JackOLauncherItem());
    public static final Item JACK_O_AMMO = Registry.register(Registry.ITEM, new Identifier(MODID, "jack_o_ammo"), new JackOAmmoItem());

    public static final EntityType<JackOProjectileEntity> JACK_O_PROJECTILE_ENTITY_TYPE = Registry.register(
            Registry.ENTITY_TYPE,
            new Identifier(MODID, "jack_o_projectile"),
            FabricEntityTypeBuilder.create(EntityCategory.MISC, JackOProjectileEntity::new).size(EntityDimensions.fixed(0.8F, 0.8F)).build()
    );

    public static final SpecialRecipeSerializer<JackOAmmoRecipe> JACK_O_AMMO_RECIPE_SERIALIZER = Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "crafting_special_jack_o_ammo"), new SpecialRecipeSerializer<>(JackOAmmoRecipe::new));

    public static final Enchantment UNWASTING = Registry.register(Registry.ENCHANTMENT, new Identifier(MODID, "unwasting"), new UnwastingEnchantment());
    public static final Enchantment RELOADING = Registry.register(Registry.ENCHANTMENT, new Identifier(MODID, "reloading"), new ReloadingEnchantment());
    public static final Enchantment BLAST_SHIELD = Registry.register(Registry.ENCHANTMENT, new Identifier(MODID, "blast_shield"), new BlastShieldEnchantment());
    public static final Enchantment LAUNCHING = Registry.register(Registry.ENCHANTMENT, new Identifier(MODID, "launching"), new LaunchingEnchantment());

    @Override
    public void onInitialize() {
        DispenserBlock.registerBehavior(JackOLauncher.JACK_O_AMMO, new JackOAmmoDispenseBehavior());
        LootFunctions.register(new SetRandomJackOAmmoNBT.Factory());

        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, supplier, setter) -> {
            if ((new Identifier("chests/simple_dungeon")).equals(id)
                || (new Identifier("chests/buried_treasure")).equals(id)
                || (new Identifier("chests/jungle_temple_dispenser")).equals(id)) {

                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .withRolls(ConstantLootTableRange.create(1))
                        .withEntry(ItemEntry.builder(JACK_O_AMMO).setWeight(4)
                                .withFunction(SetCountLootFunction.builder(UniformLootTableRange.between(3, 24)))
                                .withFunction(SetRandomJackOAmmoNBT.builder()))
                        .withEntry(EmptyEntry.Serializer().setWeight(6));

                supplier.withPool(poolBuilder);
            } else if((new Identifier("chests/village/village_weaponsmith")).equals(id)) {
                FabricLootPoolBuilder poolBuilder = FabricLootPoolBuilder.builder()
                        .withRolls(ConstantLootTableRange.create(1))
                        .withEntry(ItemEntry.builder(JACK_O_LAUNCHER).setWeight(7))
                        .withEntry(EmptyEntry.Serializer().setWeight(3));
            }
        });
    }
}

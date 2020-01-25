package jackolauncher;

import jackolauncher.enchantment.BlastShieldEnchantment;
import jackolauncher.enchantment.LaunchingEnchantment;
import jackolauncher.enchantment.ReloadingEnchantment;
import jackolauncher.enchantment.UnwastingEnchantment;
//import jackolauncher.entity.JackOProjectileEntity;
//import jackolauncher.entity.JackOProjectileRenderer;
//import jackolauncher.item.JackOAmmoDispenseBehavior;
import jackolauncher.item.JackOAmmoItem;
import jackolauncher.item.JackOAmmoRecipe;
import jackolauncher.item.JackOLauncherItem;
import jackolauncher.item.loot.SetRandomJackOAmmoNBT;
import net.minecraft.block.DispenserBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static jackolauncher.JackOLauncher.MODID;

@Mod(MODID)
@SuppressWarnings("unused")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class JackOLauncher {

    public static final String MODID = "jack_o_launcher";

    public static final Item JACK_O_LAUNCHER = new JackOLauncherItem();
    public static final Item JACK_O_AMMO = new JackOAmmoItem();

    //public static final EntityType<JackOProjectileEntity> JACK_O_PROJECTILE_ENTITY_TYPE = createEntity();

    public static final SpecialRecipeSerializer<JackOAmmoRecipe> JACK_O_AMMO_RECIPE_SERIALIZER = new SpecialRecipeSerializer<>(JackOAmmoRecipe::new);

    /*
    private static EntityType<JackOProjectileEntity> createEntity() {
        EntityType.Builder<JackOProjectileEntity> builder = EntityType.Builder.create(JackOProjectileEntity::new, EntityClassification.MISC);
        builder.size(0.8F, 0.8F);
        builder.setTrackingRange(128);
        builder.setCustomClientFactory((spawnEntity, world) -> new JackOProjectileEntity(world));
        EntityType<JackOProjectileEntity> entityType = builder.build(MODID + "jack_o_projectile");
        entityType.setRegistryName(new ResourceLocation(MODID, "jack_o_projectile"));
        return entityType;
    }
     */

    public static final Enchantment UNWASTING = new UnwastingEnchantment();
    public static final Enchantment RELOADING = new ReloadingEnchantment();
    public static final Enchantment BLAST_SHIELD = new BlastShieldEnchantment();
    public static final Enchantment LAUNCHING = new LaunchingEnchantment();

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        //DispenserBlock.registerDispenseBehavior(JackOLauncher.JACK_O_AMMO, new JackOAmmoDispenseBehavior());
        LootFunctionManager.registerFunction(new SetRandomJackOAmmoNBT.Serializer());
    }

    /*
    @SubscribeEvent
    public static void registerEntity(RegistryEvent.Register<EntityType<?>> event) {
        event.getRegistry().register(JACK_O_PROJECTILE_ENTITY_TYPE);
    }
     */

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(JACK_O_LAUNCHER, JACK_O_AMMO);
    }

    @SubscribeEvent
    public static void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
        event.getRegistry().registerAll(UNWASTING, RELOADING, BLAST_SHIELD, LAUNCHING);
    }

    @SubscribeEvent
    public static void registerRecipeSerializer(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        JACK_O_AMMO_RECIPE_SERIALIZER.setRegistryName("jack_o_launcher:crafting_special_jack_o_ammo");
        event.getRegistry().register(JACK_O_AMMO_RECIPE_SERIALIZER);
    }

    /*
    @SubscribeEvent
    public static void registerEntityRenderer(ModelRegistryEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(JackOProjectileEntity.class, JackOProjectileRenderer::new);
    }
     */
}

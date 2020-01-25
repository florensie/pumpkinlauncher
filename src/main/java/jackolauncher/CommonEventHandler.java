package jackolauncher;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = JackOLauncher.MODID)
public class CommonEventHandler {

    /*
    @SubscribeEvent
    public static void addItemsToLootTable(LootTableLoadEvent event) {
        if (event.getName().equals(new ResourceLocation("minecraft:chests/simple_dungeon"))
                || event.getName().equals(new ResourceLocation("minecraft:chests/buried_treasure"))
                || event.getName().equals(new ResourceLocation("minecraft:chests/jungle_temple_dispenser"))) {
            LootEntry.Builder entry = TableLootEntry.builder(new ResourceLocation(JackOLauncher.MODID, "inject/jack_o_ammo")).weight(1);
            LootPool pool = LootPool.builder().addEntry(entry).name("jack_o_ammo_injection").build();
            event.getTable().addPool(pool);
        } else if (event.getName().equals(new ResourceLocation("minecraft:chests/village/village_weaponsmith"))) {
            LootEntry.Builder entry = TableLootEntry.builder(new ResourceLocation(JackOLauncher.MODID, "inject/jack_o_launcher")).weight(1);
            LootPool pool = LootPool.builder().addEntry(entry).name("jack_o_launcher_injection").build();
            event.getTable().addPool(pool);
        }
    }
     */
}

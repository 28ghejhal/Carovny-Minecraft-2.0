package net.fratob.witchery.item;

import net.fratob.witchery.Witchery;
import net.fratob.witchery.block.ModBlocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    // Create ITEMS (From regular Minecraft) ready for register new items
    public static final DeferredRegister<Item> ITEMS = 
            DeferredRegister.create(ForgeRegistries.ITEMS, Witchery.MODID);

    // Register white_berry item with properties
    public static final RegistryObject<Item> WHITE_BERRIES = ITEMS.register("white_berries", 
            () -> new ItemNameBlockItem(ModBlocks.WHITE_BERRY_BUSH.get(), new Item.Properties().food(ModFoods.WHITE_BERRIES)));
    public static final RegistryObject<Item> MANA_BERRIES = ITEMS.register("mana_berries", 
            () -> new ItemNameBlockItem(ModBlocks.MANA_BERRY_BUSH.get(), new Item.Properties()));


    public static final RegistryObject<Item> WAND_OF_LIGHT = ITEMS.register("wand_of_light", 
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> CRYSTAL = ITEMS.register("crystal", 
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MANA_CRYSTAL = ITEMS.register("mana_crystal", 
            () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MANA_POWDER = ITEMS.register("mana_powder", 
            () -> new Item(new Item.Properties()));
      


    // Register ModItems to Minecraft through Witchery.java
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}

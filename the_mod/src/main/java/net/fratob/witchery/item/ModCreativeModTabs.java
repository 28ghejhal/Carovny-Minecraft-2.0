package net.fratob.witchery.item;

import net.fratob.witchery.Witchery;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

// Same process in ModItems.java
public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MOD_TABS = 
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Witchery.MODID);

    // Add Witchery tab with icon texture of White Berry
    public static final RegistryObject<CreativeModeTab> WITCHERY_TAB = CREATIVE_MOD_TABS.register("witchery_tab", 
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.WHITE_BERRIES.get()))
                    .title(Component.translatable("creativetab.witchery_tab"))
                    // Define items that you want in a tab
                    .displayItems((pParameters, pOutput) -> {
                        // Custom Item
                        pOutput.accept(ModItems.WHITE_BERRIES.get());

                        // Regulat Item
                        pOutput.accept(Items.GLOW_BERRIES);

                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MOD_TABS.register(eventBus);
    }

}

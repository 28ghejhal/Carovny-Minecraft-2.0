package net.fratob.witchery.datagen;

import net.fratob.witchery.Witchery;
import net.fratob.witchery.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

// Autogenerate item model .json files
public class ModItemModelProvider extends  ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Witchery.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleTtem(ModItems.CRYSTAL);
        simpleTtem(ModItems.MANA_CRYSTAL);
        simpleTtem(ModItems.MANA_POWDER);
        simpleTtem(ModItems.WHITE_BERRIES);
        simpleTtem(ModItems.MANA_BERRIES);
        withExistingParent(ModItems.WAND_OF_LIGHT.getId().getPath(), new ResourceLocation("item/handheld")).texture("layer0", new ResourceLocation(Witchery.MODID, "item/" + ModItems.WAND_OF_LIGHT.getId().getPath()));
    }


    // Helper method that generate item model generated with texture layer0 (Like most of items)
    private ItemModelBuilder simpleTtem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
            new ResourceLocation("item/generated")).texture("layer0",
            new ResourceLocation(Witchery.MODID,"item/" + item.getId().getPath()));
    }

}

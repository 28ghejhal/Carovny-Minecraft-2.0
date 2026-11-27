package net.fratob.witchery.datagen;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.fratob.witchery.Witchery;
import net.fratob.witchery.worldgen.ModBiomeModifiers;
import net.fratob.witchery.worldgen.ModConfiguredFeatures;
import net.fratob.witchery.worldgen.ModPlacedFeatures;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

// Uses java files from worldgen to generate .json files to generate ores etc.
public class ModWorldGenProvider extends DatapackBuiltinEntriesProvider{
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap);


    public ModWorldGenProvider(PackOutput output, CompletableFuture<Provider> registries) {
        super(output, registries, BUILDER, Set.of(Witchery.MODID));
    }
}

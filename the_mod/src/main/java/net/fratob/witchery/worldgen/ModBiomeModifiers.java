package net.fratob.witchery.worldgen;

import net.fratob.witchery.Witchery;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ForgeBiomeModifiers;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBiomeModifiers {
    public static final ResourceKey<BiomeModifier> ADD_CRYSTAL_ORE = registerKey("add_crystal_ore");

    // Finally add biome modifiers
    public static void bootstrap(BootstapContext<BiomeModifier> context) {
        var PlacedFeatures = context.lookup(Registries.PLACED_FEATURE);
        var biomes = context.lookup(Registries.BIOME);

        // Crystal ore generates in mountain biomes
        context.register(ADD_CRYSTAL_ORE, new ForgeBiomeModifiers.AddFeaturesBiomeModifier(
            biomes.getOrThrow(BiomeTags.IS_MOUNTAIN),
            HolderSet.direct(PlacedFeatures.getOrThrow(ModPlacedFeatures.CRYSTAL_ORE_PLACED_KEY)),
            GenerationStep.Decoration.UNDERGROUND_ORES));

    }



    // Helper method
    public static ResourceKey<BiomeModifier> registerKey(String name) {
        return ResourceKey.create(ForgeRegistries.Keys.BIOME_MODIFIERS, new ResourceLocation(Witchery.MODID, name));
    }
}

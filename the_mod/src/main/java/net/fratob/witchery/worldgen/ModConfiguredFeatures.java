package net.fratob.witchery.worldgen;

import java.util.List;

import net.fratob.witchery.Witchery;
import net.fratob.witchery.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

public class ModConfiguredFeatures {
    // Add register key to generated ores
    public static final ResourceKey<ConfiguredFeature<?, ?>> CRYSTAL_ORE_KEY = registerKey("crystal_ore");

    // Defines rule tests what blocks can ore replace
    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context) {
        RuleTest stoneRepleacebles = new TagMatchTest(BlockTags.STONE_ORE_REPLACEABLES);
        RuleTest deepslateRepleacebles = new TagMatchTest(BlockTags.DEEPSLATE_ORE_REPLACEABLES);

        // In overworld are 2 types of crystal ore - deepslate replace deepslate - stone replace stone
        List<OreConfiguration.TargetBlockState> crystalOres = List.of(OreConfiguration.target(stoneRepleacebles, ModBlocks.CRYSTAL_ORE.get().defaultBlockState()),
                                                                                OreConfiguration.target(deepslateRepleacebles, ModBlocks.DEEPSLATE_CRYSTAL_ORE.get().defaultBlockState()));
        
        // Finally register the ores with size of their vein
        // Crystal Ore made like emerald
        register(context, CRYSTAL_ORE_KEY, Feature.ORE, new OreConfiguration(crystalOres, 3));
    }




    // Helper method
    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(Witchery.MODID, name));
    }

    // Helper method
    private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}

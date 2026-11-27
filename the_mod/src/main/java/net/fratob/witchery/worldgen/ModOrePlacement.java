package net.fratob.witchery.worldgen;

import java.util.List;

import net.minecraft.world.level.levelgen.placement.*;

// Methods copied from vanilla OrePlacement, there are private
public class ModOrePlacement {
        public static List<PlacementModifier> orePlacement(PlacementModifier pCountPlacement, PlacementModifier pHeightRange) {
            return List.of(pCountPlacement, InSquarePlacement.spread(), pHeightRange, BiomeFilter.biome());
        }

        public static List<PlacementModifier> commonOrePlacement(int pCount, PlacementModifier pHeightRange) {
            return orePlacement(CountPlacement.of(pCount), pHeightRange);
        }

        public static List<PlacementModifier> rareOrePlacement(int pChance, PlacementModifier pHeightRange) {
            return orePlacement(RarityFilter.onAverageOnceEvery(pChance), pHeightRange);
        }

}

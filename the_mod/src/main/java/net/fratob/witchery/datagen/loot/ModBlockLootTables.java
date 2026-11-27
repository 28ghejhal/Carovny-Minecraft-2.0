package net.fratob.witchery.datagen.loot;

import java.util.Set;

import net.fratob.witchery.block.ModBlocks;
import net.fratob.witchery.item.ModItems;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

// Autogenerate loot tables .json files for blocks
public class ModBlockLootTables extends BlockLootSubProvider{
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.add(ModBlocks.CRYSTAL_ORE.get(),
            block -> createCopperLikeOreDrops(ModBlocks.CRYSTAL_ORE.get(), 1.0F, 3.0F, ModItems.CRYSTAL.get()));
        this.add(ModBlocks.DEEPSLATE_CRYSTAL_ORE.get(),
            block -> createCopperLikeOreDrops(ModBlocks.DEEPSLATE_CRYSTAL_ORE.get(), 1.0F, 3.0F, ModItems.CRYSTAL.get()));
    }


    // Myself method for ores, that drop min-max number of "drop"
    protected LootTable.Builder createCopperLikeOreDrops(Block pBlock, float min, float max, Item drop) {
      return createSilkTouchDispatchTable(pBlock, 
        this.applyExplosionDecay(pBlock, 
            LootItem.lootTableItem(drop)
            .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)))
            .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
   }

    // Get all Blocks from ModBlocks without property noLootTable, to add them loot table, Forge needs it
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }

}

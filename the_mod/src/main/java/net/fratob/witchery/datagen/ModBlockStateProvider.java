package net.fratob.witchery.datagen;

import net.fratob.witchery.Witchery;
import net.fratob.witchery.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

// Autogenerator of blockstates .json file for blocks
public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, Witchery.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.CRYSTAL_ORE);
        blockWithItem(ModBlocks.DEEPSLATE_CRYSTAL_ORE);

    }
    



    // Helper method to autogenerate block and item together (Like most of items)
    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}

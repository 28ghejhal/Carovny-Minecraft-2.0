package net.fratob.witchery.datagen;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import net.fratob.witchery.Witchery;
import net.fratob.witchery.block.ModBlocks;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

// Autogenerate tags .json files, like mineable etc.
public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Witchery.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider pProvider) {
        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocks.CRYSTAL_ORE.get());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
            .add(ModBlocks.CRYSTAL_ORE.get());

        this.tag(Tags.Blocks.ORES)
            .add(ModBlocks.CRYSTAL_ORE.get());
    }

}

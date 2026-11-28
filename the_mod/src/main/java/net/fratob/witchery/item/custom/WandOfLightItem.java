package net.fratob.witchery.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class WandOfLightItem extends Item{
    public WandOfLightItem(Item.Properties pProperties) {
        super(pProperties);
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        BlockState state = pPlayer.level().getBlockState(pPlayer.blockPosition());
        if(state.is(BlockTags.REPLACEABLE)) {
        pLevel.playSound((Player)null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.NEUTRAL, 1F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
        pLevel.setBlock(pPlayer.blockPosition(), Blocks.LIGHT.defaultBlockState(), 54);
        }

        return InteractionResultHolder.sidedSuccess(pPlayer.getItemInHand(pUsedHand), pLevel.isClientSide());
    }

}

package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

class AllOfPredicate extends CombiningPredicate {
   public static final Codec<AllOfPredicate> CODEC = codec(AllOfPredicate::new);

   public AllOfPredicate(List<BlockPredicate> p_190373_) {
      super(p_190373_);
   }

   public boolean test(WorldGenLevel p_190376_, BlockPos p_190377_) {
      for(BlockPredicate $$2 : this.predicates) {
         if (!$$2.test(p_190376_, p_190377_)) {
            return false;
         }
      }

      return true;
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.ALL_OF;
   }

   // $FF: synthetic method
   public boolean test(Object var1, Object var2) {
      return this.test((WorldGenLevel)var1, (BlockPos)var2);
   }
}

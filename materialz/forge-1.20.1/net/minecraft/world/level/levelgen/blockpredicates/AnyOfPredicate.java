package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;

class AnyOfPredicate extends CombiningPredicate {
   public static final Codec<AnyOfPredicate> CODEC = codec(AnyOfPredicate::new);

   public AnyOfPredicate(List<BlockPredicate> p_190384_) {
      super(p_190384_);
   }

   public boolean test(WorldGenLevel p_190387_, BlockPos p_190388_) {
      for(BlockPredicate $$2 : this.predicates) {
         if ($$2.test(p_190387_, p_190388_)) {
            return true;
         }
      }

      return false;
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.ANY_OF;
   }

   // $FF: synthetic method
   public boolean test(Object var1, Object var2) {
      return this.test((WorldGenLevel)var1, (BlockPos)var2);
   }
}

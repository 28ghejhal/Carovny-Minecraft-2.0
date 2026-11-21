package net.minecraft.world.entity.ai.util;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.phys.Vec3;

public class AirRandomPos {
   @Nullable
   public static Vec3 getPosTowards(PathfinderMob p_148388_, int p_148389_, int p_148390_, int p_148391_, Vec3 p_148392_, double p_148393_) {
      Vec3 $$6 = p_148392_.subtract(p_148388_.getX(), p_148388_.getY(), p_148388_.getZ());
      boolean $$7 = GoalUtils.mobRestricted(p_148388_, p_148389_);
      return RandomPos.generateRandomPos(p_148388_, (Supplier)(() -> {
         BlockPos $$7 = AirAndWaterRandomPos.generateRandomPos(p_148388_, p_148389_, p_148390_, p_148391_, $$6.x, $$6.z, p_148393_, $$7);
         return $$7 != null && !GoalUtils.isWater(p_148388_, $$7) ? $$7 : null;
      }));
   }
}

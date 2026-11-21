package net.minecraft.world.entity.ai.goal;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;

public class SwellGoal extends Goal {
   private final Creeper creeper;
   @Nullable
   private LivingEntity target;

   public SwellGoal(Creeper p_25919_) {
      this.creeper = p_25919_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
   }

   public boolean canUse() {
      LivingEntity $$0 = this.creeper.getTarget();
      return this.creeper.getSwellDir() > 0 || $$0 != null && this.creeper.distanceToSqr($$0) < (double)9.0F;
   }

   public void start() {
      this.creeper.getNavigation().stop();
      this.target = this.creeper.getTarget();
   }

   public void stop() {
      this.target = null;
   }

   public boolean requiresUpdateEveryTick() {
      return true;
   }

   public void tick() {
      if (this.target == null) {
         this.creeper.setSwellDir(-1);
      } else if (this.creeper.distanceToSqr(this.target) > (double)49.0F) {
         this.creeper.setSwellDir(-1);
      } else if (!this.creeper.getSensing().hasLineOfSight(this.target)) {
         this.creeper.setSwellDir(-1);
      } else {
         this.creeper.setSwellDir(1);
      }
   }
}

package net.minecraft.world.entity.ai.goal;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;

public class FollowParentGoal extends Goal {
   public static final int HORIZONTAL_SCAN_RANGE = 8;
   public static final int VERTICAL_SCAN_RANGE = 4;
   public static final int DONT_FOLLOW_IF_CLOSER_THAN = 3;
   private final Animal animal;
   @Nullable
   private Animal parent;
   private final double speedModifier;
   private int timeToRecalcPath;

   public FollowParentGoal(Animal p_25319_, double p_25320_) {
      this.animal = p_25319_;
      this.speedModifier = p_25320_;
   }

   public boolean canUse() {
      if (this.animal.getAge() >= 0) {
         return false;
      } else {
         List<? extends Animal> $$0 = this.animal.level().getEntitiesOfClass(this.animal.getClass(), this.animal.getBoundingBox().inflate((double)8.0F, (double)4.0F, (double)8.0F));
         Animal $$1 = null;
         double $$2 = Double.MAX_VALUE;

         for(Animal $$3 : $$0) {
            if ($$3.getAge() >= 0) {
               double $$4 = this.animal.distanceToSqr($$3);
               if (!($$4 > $$2)) {
                  $$2 = $$4;
                  $$1 = $$3;
               }
            }
         }

         if ($$1 == null) {
            return false;
         } else if ($$2 < (double)9.0F) {
            return false;
         } else {
            this.parent = $$1;
            return true;
         }
      }
   }

   public boolean canContinueToUse() {
      if (this.animal.getAge() >= 0) {
         return false;
      } else if (!this.parent.isAlive()) {
         return false;
      } else {
         double $$0 = this.animal.distanceToSqr(this.parent);
         return !($$0 < (double)9.0F) && !($$0 > (double)256.0F);
      }
   }

   public void start() {
      this.timeToRecalcPath = 0;
   }

   public void stop() {
      this.parent = null;
   }

   public void tick() {
      if (--this.timeToRecalcPath <= 0) {
         this.timeToRecalcPath = this.adjustedTickDelay(10);
         this.animal.getNavigation().moveTo((Entity)this.parent, this.speedModifier);
      }
   }
}

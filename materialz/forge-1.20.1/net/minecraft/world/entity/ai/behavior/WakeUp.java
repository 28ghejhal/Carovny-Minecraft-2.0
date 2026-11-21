package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;
import net.minecraft.world.entity.schedule.Activity;

public class WakeUp {
   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((Function)((p_259813_) -> p_259813_.point((Trigger)(p_259555_, p_259657_, p_259316_) -> {
            if (!p_259657_.getBrain().isActive(Activity.REST) && p_259657_.isSleeping()) {
               p_259657_.stopSleeping();
               return true;
            } else {
               return false;
            }
         })));
   }
}

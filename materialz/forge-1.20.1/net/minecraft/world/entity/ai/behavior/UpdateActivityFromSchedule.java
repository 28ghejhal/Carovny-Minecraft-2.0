package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.Trigger;

public class UpdateActivityFromSchedule {
   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((Function)((p_259429_) -> p_259429_.point((Trigger)(p_288849_, p_288850_, p_288851_) -> {
            p_288850_.getBrain().updateActivityFromSchedule(p_288849_.getDayTime(), p_288849_.getGameTime());
            return true;
         })));
   }
}

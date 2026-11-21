package net.minecraft.world.entity.ai.behavior;

import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;

public class ReactToBell {
   public static BehaviorControl<LivingEntity> create() {
      return BehaviorBuilder.create((Function)((p_259349_) -> p_259349_.group(p_259349_.present(MemoryModuleType.HEARD_BELL_TIME)).apply(p_259349_, (p_259472_) -> (p_289371_, p_289372_, p_289373_) -> {
               Raid $$3 = p_289371_.getRaidAt(p_289372_.blockPosition());
               if ($$3 == null) {
                  p_289372_.getBrain().setActiveActivityIfPossible(Activity.HIDE);
               }

               return true;
            })));
   }
}

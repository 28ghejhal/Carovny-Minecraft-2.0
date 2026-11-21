package net.minecraft.world.entity.monster.piglin;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.item.ItemEntity;

public class StopAdmiringIfItemTooFarAway<E extends Piglin> {
   public static BehaviorControl<LivingEntity> create(int p_259415_) {
      return BehaviorBuilder.create((Function)((p_259152_) -> p_259152_.group(p_259152_.present(MemoryModuleType.ADMIRING_ITEM), p_259152_.registered(MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM)).apply(p_259152_, (p_260178_, p_259241_) -> (p_259613_, p_259304_, p_259748_) -> {
               if (!p_259304_.getOffhandItem().isEmpty()) {
                  return false;
               } else {
                  Optional<ItemEntity> $$7 = p_259152_.<ItemEntity>tryGet(p_259241_);
                  if ($$7.isPresent() && ((ItemEntity)$$7.get()).closerThan(p_259304_, (double)p_259415_)) {
                     return false;
                  } else {
                     p_260178_.erase();
                     return true;
                  }
               }
            })));
   }
}

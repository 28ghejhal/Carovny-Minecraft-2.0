package net.minecraft.advancements.triggers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.world.level.storage.loot.ValidationContextSource;

public class ImpossibleTrigger implements CriterionTrigger<TriggerInstance> {
   public Codec<TriggerInstance> codec() {
      return ImpossibleTrigger.TriggerInstance.CODEC;
   }

   public static record TriggerInstance() implements CriterionTriggerInstance {
      public static final Codec<TriggerInstance> CODEC = MapCodec.unitCodec(new TriggerInstance());

      public void validate(final ValidationContextSource validator) {
      }
   }
}

package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ShotCrossbowTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("shot_crossbow");

   public ResourceLocation getId() {
      return ID;
   }

   public TriggerInstance createInstance(JsonObject p_286679_, ContextAwarePredicate p_286410_, DeserializationContext p_286233_) {
      ItemPredicate $$3 = ItemPredicate.fromJson(p_286679_.get("item"));
      return new TriggerInstance(p_286410_, $$3);
   }

   public void trigger(ServerPlayer p_65463_, ItemStack p_65464_) {
      this.trigger(p_65463_, (p_65467_) -> p_65467_.matches(p_65464_));
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;

      public TriggerInstance(ContextAwarePredicate p_286262_, ItemPredicate p_286755_) {
         super(ShotCrossbowTrigger.ID, p_286262_);
         this.item = p_286755_;
      }

      public static TriggerInstance shotCrossbow(ItemPredicate p_159432_) {
         return new TriggerInstance(ContextAwarePredicate.ANY, p_159432_);
      }

      public static TriggerInstance shotCrossbow(ItemLike p_65484_) {
         return new TriggerInstance(ContextAwarePredicate.ANY, ItemPredicate.Builder.item().of(p_65484_).build());
      }

      public boolean matches(ItemStack p_65482_) {
         return this.item.matches(p_65482_);
      }

      public JsonObject serializeToJson(SerializationContext p_65486_) {
         JsonObject $$1 = super.serializeToJson(p_65486_);
         $$1.add("item", this.item.serializeToJson());
         return $$1;
      }
   }
}

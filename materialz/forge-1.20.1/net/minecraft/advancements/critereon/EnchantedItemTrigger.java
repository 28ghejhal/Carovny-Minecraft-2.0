package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class EnchantedItemTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   static final ResourceLocation ID = new ResourceLocation("enchanted_item");

   public ResourceLocation getId() {
      return ID;
   }

   public TriggerInstance createInstance(JsonObject p_286526_, ContextAwarePredicate p_286279_, DeserializationContext p_286881_) {
      ItemPredicate $$3 = ItemPredicate.fromJson(p_286526_.get("item"));
      MinMaxBounds.Ints $$4 = MinMaxBounds.Ints.fromJson(p_286526_.get("levels"));
      return new TriggerInstance(p_286279_, $$3, $$4);
   }

   public void trigger(ServerPlayer p_27669_, ItemStack p_27670_, int p_27671_) {
      this.trigger(p_27669_, (p_27675_) -> p_27675_.matches(p_27670_, p_27671_));
   }

   // $FF: synthetic method
   public AbstractCriterionTriggerInstance createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3) {
      return this.createInstance(var1, var2, var3);
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.Ints levels;

      public TriggerInstance(ContextAwarePredicate p_286871_, ItemPredicate p_286640_, MinMaxBounds.Ints p_286367_) {
         super(EnchantedItemTrigger.ID, p_286871_);
         this.item = p_286640_;
         this.levels = p_286367_;
      }

      public static TriggerInstance enchantedItem() {
         return new TriggerInstance(ContextAwarePredicate.ANY, ItemPredicate.ANY, MinMaxBounds.Ints.ANY);
      }

      public boolean matches(ItemStack p_27692_, int p_27693_) {
         if (!this.item.matches(p_27692_)) {
            return false;
         } else {
            return this.levels.matches(p_27693_);
         }
      }

      public JsonObject serializeToJson(SerializationContext p_27695_) {
         JsonObject $$1 = super.serializeToJson(p_27695_);
         $$1.add("item", this.item.serializeToJson());
         $$1.add("levels", this.levels.serializeToJson());
         return $$1;
      }
   }
}

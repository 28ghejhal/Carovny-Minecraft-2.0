package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SuspiciousStewItem;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SetStewEffectFunction extends LootItemConditionalFunction {
   final Map<MobEffect, NumberProvider> effectDurationMap;

   SetStewEffectFunction(LootItemCondition[] p_81216_, Map<MobEffect, NumberProvider> p_81217_) {
      super(p_81216_);
      this.effectDurationMap = ImmutableMap.copyOf(p_81217_);
   }

   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_STEW_EFFECT;
   }

   public Set<LootContextParam<?>> getReferencedContextParams() {
      return (Set)this.effectDurationMap.values().stream().flatMap((p_279082_) -> p_279082_.getReferencedContextParams().stream()).collect(ImmutableSet.toImmutableSet());
   }

   public ItemStack run(ItemStack p_81223_, LootContext p_81224_) {
      if (p_81223_.is(Items.SUSPICIOUS_STEW) && !this.effectDurationMap.isEmpty()) {
         RandomSource $$2 = p_81224_.getRandom();
         int $$3 = $$2.nextInt(this.effectDurationMap.size());
         Map.Entry<MobEffect, NumberProvider> $$4 = (Map.Entry)Iterables.get(this.effectDurationMap.entrySet(), $$3);
         MobEffect $$5 = (MobEffect)$$4.getKey();
         int $$6 = ((NumberProvider)$$4.getValue()).getInt(p_81224_);
         if (!$$5.isInstantenous()) {
            $$6 *= 20;
         }

         SuspiciousStewItem.saveMobEffect(p_81223_, $$5, $$6);
         return p_81223_;
      } else {
         return p_81223_;
      }
   }

   public static Builder stewEffect() {
      return new Builder();
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final Map<MobEffect, NumberProvider> effectDurationMap = Maps.newLinkedHashMap();

      protected Builder getThis() {
         return this;
      }

      public Builder withEffect(MobEffect p_165473_, NumberProvider p_165474_) {
         this.effectDurationMap.put(p_165473_, p_165474_);
         return this;
      }

      public LootItemFunction build() {
         return new SetStewEffectFunction(this.getConditions(), this.effectDurationMap);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer<SetStewEffectFunction> {
      public void serialize(JsonObject p_81247_, SetStewEffectFunction p_81248_, JsonSerializationContext p_81249_) {
         super.serialize(p_81247_, p_81248_, p_81249_);
         if (!p_81248_.effectDurationMap.isEmpty()) {
            JsonArray $$3 = new JsonArray();

            for(MobEffect $$4 : p_81248_.effectDurationMap.keySet()) {
               JsonObject $$5 = new JsonObject();
               ResourceLocation $$6 = BuiltInRegistries.MOB_EFFECT.getKey($$4);
               if ($$6 == null) {
                  throw new IllegalArgumentException("Don't know how to serialize mob effect " + $$4);
               }

               $$5.add("type", new JsonPrimitive($$6.toString()));
               $$5.add("duration", p_81249_.serialize(p_81248_.effectDurationMap.get($$4)));
               $$3.add($$5);
            }

            p_81247_.add("effects", $$3);
         }

      }

      public SetStewEffectFunction deserialize(JsonObject p_81239_, JsonDeserializationContext p_81240_, LootItemCondition[] p_81241_) {
         Map<MobEffect, NumberProvider> $$3 = Maps.newLinkedHashMap();
         if (p_81239_.has("effects")) {
            for(JsonElement $$5 : GsonHelper.getAsJsonArray(p_81239_, "effects")) {
               String $$6 = GsonHelper.getAsString($$5.getAsJsonObject(), "type");
               MobEffect $$7 = (MobEffect)BuiltInRegistries.MOB_EFFECT.getOptional(new ResourceLocation($$6)).orElseThrow(() -> new JsonSyntaxException("Unknown mob effect '" + $$6 + "'"));
               NumberProvider $$8 = (NumberProvider)GsonHelper.getAsObject($$5.getAsJsonObject(), "duration", p_81240_, NumberProvider.class);
               $$3.put($$7, $$8);
            }
         }

         return new SetStewEffectFunction(p_81241_, $$3);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}

package net.minecraft.advancements.critereon;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class MobEffectsPredicate {
   public static final MobEffectsPredicate ANY = new MobEffectsPredicate(Collections.emptyMap());
   private final Map<MobEffect, MobEffectInstancePredicate> effects;

   public MobEffectsPredicate(Map<MobEffect, MobEffectInstancePredicate> p_56551_) {
      this.effects = p_56551_;
   }

   public static MobEffectsPredicate effects() {
      return new MobEffectsPredicate(Maps.newLinkedHashMap());
   }

   public MobEffectsPredicate and(MobEffect p_56554_) {
      this.effects.put(p_56554_, new MobEffectInstancePredicate());
      return this;
   }

   public MobEffectsPredicate and(MobEffect p_154978_, MobEffectInstancePredicate p_154979_) {
      this.effects.put(p_154978_, p_154979_);
      return this;
   }

   public boolean matches(Entity p_56556_) {
      if (this == ANY) {
         return true;
      } else {
         return p_56556_ instanceof LivingEntity ? this.matches(((LivingEntity)p_56556_).getActiveEffectsMap()) : false;
      }
   }

   public boolean matches(LivingEntity p_56558_) {
      return this == ANY ? true : this.matches(p_56558_.getActiveEffectsMap());
   }

   public boolean matches(Map<MobEffect, MobEffectInstance> p_56562_) {
      if (this == ANY) {
         return true;
      } else {
         for(Map.Entry<MobEffect, MobEffectInstancePredicate> $$1 : this.effects.entrySet()) {
            MobEffectInstance $$2 = (MobEffectInstance)p_56562_.get($$1.getKey());
            if (!((MobEffectInstancePredicate)$$1.getValue()).matches($$2)) {
               return false;
            }
         }

         return true;
      }
   }

   public static MobEffectsPredicate fromJson(@Nullable JsonElement p_56560_) {
      if (p_56560_ != null && !p_56560_.isJsonNull()) {
         JsonObject $$1 = GsonHelper.convertToJsonObject(p_56560_, "effects");
         Map<MobEffect, MobEffectInstancePredicate> $$2 = Maps.newLinkedHashMap();

         for(Map.Entry<String, JsonElement> $$3 : $$1.entrySet()) {
            ResourceLocation $$4 = new ResourceLocation((String)$$3.getKey());
            MobEffect $$5 = (MobEffect)BuiltInRegistries.MOB_EFFECT.getOptional($$4).orElseThrow(() -> new JsonSyntaxException("Unknown effect '" + $$4 + "'"));
            MobEffectInstancePredicate $$6 = MobEffectsPredicate.MobEffectInstancePredicate.fromJson(GsonHelper.convertToJsonObject((JsonElement)$$3.getValue(), (String)$$3.getKey()));
            $$2.put($$5, $$6);
         }

         return new MobEffectsPredicate($$2);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject $$0 = new JsonObject();

         for(Map.Entry<MobEffect, MobEffectInstancePredicate> $$1 : this.effects.entrySet()) {
            $$0.add(BuiltInRegistries.MOB_EFFECT.getKey((MobEffect)$$1.getKey()).toString(), ((MobEffectInstancePredicate)$$1.getValue()).serializeToJson());
         }

         return $$0;
      }
   }

   public static class MobEffectInstancePredicate {
      private final MinMaxBounds.Ints amplifier;
      private final MinMaxBounds.Ints duration;
      @Nullable
      private final Boolean ambient;
      @Nullable
      private final Boolean visible;

      public MobEffectInstancePredicate(MinMaxBounds.Ints p_56572_, MinMaxBounds.Ints p_56573_, @Nullable Boolean p_56574_, @Nullable Boolean p_56575_) {
         this.amplifier = p_56572_;
         this.duration = p_56573_;
         this.ambient = p_56574_;
         this.visible = p_56575_;
      }

      public MobEffectInstancePredicate() {
         this(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, (Boolean)null, (Boolean)null);
      }

      public boolean matches(@Nullable MobEffectInstance p_56578_) {
         if (p_56578_ == null) {
            return false;
         } else if (!this.amplifier.matches(p_56578_.getAmplifier())) {
            return false;
         } else if (!this.duration.matches(p_56578_.getDuration())) {
            return false;
         } else if (this.ambient != null && this.ambient != p_56578_.isAmbient()) {
            return false;
         } else {
            return this.visible == null || this.visible == p_56578_.isVisible();
         }
      }

      public JsonElement serializeToJson() {
         JsonObject $$0 = new JsonObject();
         $$0.add("amplifier", this.amplifier.serializeToJson());
         $$0.add("duration", this.duration.serializeToJson());
         $$0.addProperty("ambient", this.ambient);
         $$0.addProperty("visible", this.visible);
         return $$0;
      }

      public static MobEffectInstancePredicate fromJson(JsonObject p_56580_) {
         MinMaxBounds.Ints $$1 = MinMaxBounds.Ints.fromJson(p_56580_.get("amplifier"));
         MinMaxBounds.Ints $$2 = MinMaxBounds.Ints.fromJson(p_56580_.get("duration"));
         Boolean $$3 = p_56580_.has("ambient") ? GsonHelper.getAsBoolean(p_56580_, "ambient") : null;
         Boolean $$4 = p_56580_.has("visible") ? GsonHelper.getAsBoolean(p_56580_, "visible") : null;
         return new MobEffectInstancePredicate($$1, $$2, $$3, $$4);
      }
   }
}

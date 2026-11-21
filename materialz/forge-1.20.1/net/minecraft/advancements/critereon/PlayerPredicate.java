package net.minecraft.advancements.critereon;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.RecipeBook;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatsCounter;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class PlayerPredicate implements EntitySubPredicate {
   public static final int LOOKING_AT_RANGE = 100;
   private final MinMaxBounds.Ints level;
   @Nullable
   private final GameType gameType;
   private final Map<Stat<?>, MinMaxBounds.Ints> stats;
   private final Object2BooleanMap<ResourceLocation> recipes;
   private final Map<ResourceLocation, AdvancementPredicate> advancements;
   private final EntityPredicate lookingAt;

   private static AdvancementPredicate advancementPredicateFromJson(JsonElement p_62290_) {
      if (p_62290_.isJsonPrimitive()) {
         boolean $$1 = p_62290_.getAsBoolean();
         return new AdvancementDonePredicate($$1);
      } else {
         Object2BooleanMap<String> $$2 = new Object2BooleanOpenHashMap();
         JsonObject $$3 = GsonHelper.convertToJsonObject(p_62290_, "criterion data");
         $$3.entrySet().forEach((p_62288_) -> {
            boolean $$2 = GsonHelper.convertToBoolean((JsonElement)p_62288_.getValue(), "criterion test");
            $$2.put((String)p_62288_.getKey(), $$2);
         });
         return new AdvancementCriterionsPredicate($$2);
      }
   }

   PlayerPredicate(MinMaxBounds.Ints p_156746_, @Nullable GameType p_156747_, Map<Stat<?>, MinMaxBounds.Ints> p_156748_, Object2BooleanMap<ResourceLocation> p_156749_, Map<ResourceLocation, AdvancementPredicate> p_156750_, EntityPredicate p_156751_) {
      this.level = p_156746_;
      this.gameType = p_156747_;
      this.stats = p_156748_;
      this.recipes = p_156749_;
      this.advancements = p_156750_;
      this.lookingAt = p_156751_;
   }

   public boolean matches(Entity p_222484_, ServerLevel p_222485_, @Nullable Vec3 p_222486_) {
      if (!(p_222484_ instanceof ServerPlayer $$3)) {
         return false;
      } else if (!this.level.matches($$3.experienceLevel)) {
         return false;
      } else if (this.gameType != null && this.gameType != $$3.gameMode.getGameModeForPlayer()) {
         return false;
      } else {
         StatsCounter $$4 = $$3.getStats();

         for(Map.Entry<Stat<?>, MinMaxBounds.Ints> $$5 : this.stats.entrySet()) {
            int $$6 = $$4.getValue((Stat)$$5.getKey());
            if (!((MinMaxBounds.Ints)$$5.getValue()).matches($$6)) {
               return false;
            }
         }

         RecipeBook $$7 = $$3.getRecipeBook();
         ObjectIterator $$9 = this.recipes.object2BooleanEntrySet().iterator();

         while($$9.hasNext()) {
            Object2BooleanMap.Entry<ResourceLocation> $$8 = (Object2BooleanMap.Entry)$$9.next();
            if ($$7.contains((ResourceLocation)$$8.getKey()) != $$8.getBooleanValue()) {
               return false;
            }
         }

         if (!this.advancements.isEmpty()) {
            PlayerAdvancements $$9 = $$3.getAdvancements();
            ServerAdvancementManager $$10 = $$3.getServer().getAdvancements();

            for(Map.Entry<ResourceLocation, AdvancementPredicate> $$11 : this.advancements.entrySet()) {
               Advancement $$12 = $$10.getAdvancement((ResourceLocation)$$11.getKey());
               if ($$12 == null || !((AdvancementPredicate)$$11.getValue()).test($$9.getOrStartProgress($$12))) {
                  return false;
               }
            }
         }

         if (this.lookingAt != EntityPredicate.ANY) {
            Vec3 $$13 = $$3.getEyePosition();
            Vec3 $$14 = $$3.getViewVector(1.0F);
            Vec3 $$15 = $$13.add($$14.x * (double)100.0F, $$14.y * (double)100.0F, $$14.z * (double)100.0F);
            EntityHitResult $$16 = ProjectileUtil.getEntityHitResult($$3.level(), $$3, $$13, $$15, (new AABB($$13, $$15)).inflate((double)1.0F), (p_156765_) -> !p_156765_.isSpectator(), 0.0F);
            if ($$16 == null || $$16.getType() != HitResult.Type.ENTITY) {
               return false;
            }

            Entity $$17 = $$16.getEntity();
            if (!this.lookingAt.matches($$3, $$17) || !$$3.hasLineOfSight($$17)) {
               return false;
            }
         }

         return true;
      }
   }

   public static PlayerPredicate fromJson(JsonObject p_222492_) {
      MinMaxBounds.Ints $$1 = MinMaxBounds.Ints.fromJson(p_222492_.get("level"));
      String $$2 = GsonHelper.getAsString(p_222492_, "gamemode", "");
      GameType $$3 = GameType.byName($$2, (GameType)null);
      Map<Stat<?>, MinMaxBounds.Ints> $$4 = Maps.newHashMap();
      JsonArray $$5 = GsonHelper.getAsJsonArray(p_222492_, "stats", (JsonArray)null);
      if ($$5 != null) {
         for(JsonElement $$6 : $$5) {
            JsonObject $$7 = GsonHelper.convertToJsonObject($$6, "stats entry");
            ResourceLocation $$8 = new ResourceLocation(GsonHelper.getAsString($$7, "type"));
            StatType<?> $$9 = (StatType)BuiltInRegistries.STAT_TYPE.get($$8);
            if ($$9 == null) {
               throw new JsonParseException("Invalid stat type: " + $$8);
            }

            ResourceLocation $$10 = new ResourceLocation(GsonHelper.getAsString($$7, "stat"));
            Stat<?> $$11 = getStat($$9, $$10);
            MinMaxBounds.Ints $$12 = MinMaxBounds.Ints.fromJson($$7.get("value"));
            $$4.put($$11, $$12);
         }
      }

      Object2BooleanMap<ResourceLocation> $$13 = new Object2BooleanOpenHashMap();
      JsonObject $$14 = GsonHelper.getAsJsonObject(p_222492_, "recipes", new JsonObject());

      for(Map.Entry<String, JsonElement> $$15 : $$14.entrySet()) {
         ResourceLocation $$16 = new ResourceLocation((String)$$15.getKey());
         boolean $$17 = GsonHelper.convertToBoolean((JsonElement)$$15.getValue(), "recipe present");
         $$13.put($$16, $$17);
      }

      Map<ResourceLocation, AdvancementPredicate> $$18 = Maps.newHashMap();
      JsonObject $$19 = GsonHelper.getAsJsonObject(p_222492_, "advancements", new JsonObject());

      for(Map.Entry<String, JsonElement> $$20 : $$19.entrySet()) {
         ResourceLocation $$21 = new ResourceLocation((String)$$20.getKey());
         AdvancementPredicate $$22 = advancementPredicateFromJson((JsonElement)$$20.getValue());
         $$18.put($$21, $$22);
      }

      EntityPredicate $$23 = EntityPredicate.fromJson(p_222492_.get("looking_at"));
      return new PlayerPredicate($$1, $$3, $$4, $$13, $$18, $$23);
   }

   private static <T> Stat<T> getStat(StatType<T> p_62268_, ResourceLocation p_62269_) {
      Registry<T> $$2 = p_62268_.getRegistry();
      T $$3 = (T)$$2.get(p_62269_);
      if ($$3 == null) {
         throw new JsonParseException("Unknown object " + p_62269_ + " for stat type " + BuiltInRegistries.STAT_TYPE.getKey(p_62268_));
      } else {
         return p_62268_.get($$3);
      }
   }

   private static <T> ResourceLocation getStatValueId(Stat<T> p_62266_) {
      return p_62266_.getType().getRegistry().getKey(p_62266_.getValue());
   }

   public JsonObject serializeCustomData() {
      JsonObject $$0 = new JsonObject();
      $$0.add("level", this.level.serializeToJson());
      if (this.gameType != null) {
         $$0.addProperty("gamemode", this.gameType.getName());
      }

      if (!this.stats.isEmpty()) {
         JsonArray $$1 = new JsonArray();
         this.stats.forEach((p_222489_, p_222490_) -> {
            JsonObject $$3 = new JsonObject();
            $$3.addProperty("type", BuiltInRegistries.STAT_TYPE.getKey(p_222489_.getType()).toString());
            $$3.addProperty("stat", getStatValueId(p_222489_).toString());
            $$3.add("value", p_222490_.serializeToJson());
            $$1.add($$3);
         });
         $$0.add("stats", $$1);
      }

      if (!this.recipes.isEmpty()) {
         JsonObject $$2 = new JsonObject();
         this.recipes.forEach((p_222499_, p_222500_) -> $$2.addProperty(p_222499_.toString(), p_222500_));
         $$0.add("recipes", $$2);
      }

      if (!this.advancements.isEmpty()) {
         JsonObject $$3 = new JsonObject();
         this.advancements.forEach((p_222495_, p_222496_) -> $$3.add(p_222495_.toString(), p_222496_.toJson()));
         $$0.add("advancements", $$3);
      }

      $$0.add("looking_at", this.lookingAt.serializeToJson());
      return $$0;
   }

   public EntitySubPredicate.Type type() {
      return EntitySubPredicate.Types.PLAYER;
   }

   static class AdvancementDonePredicate implements AdvancementPredicate {
      private final boolean state;

      public AdvancementDonePredicate(boolean p_62301_) {
         this.state = p_62301_;
      }

      public JsonElement toJson() {
         return new JsonPrimitive(this.state);
      }

      public boolean test(AdvancementProgress p_62304_) {
         return p_62304_.isDone() == this.state;
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((AdvancementProgress)var1);
      }
   }

   static class AdvancementCriterionsPredicate implements AdvancementPredicate {
      private final Object2BooleanMap<String> criterions;

      public AdvancementCriterionsPredicate(Object2BooleanMap<String> p_62293_) {
         this.criterions = p_62293_;
      }

      public JsonElement toJson() {
         JsonObject $$0 = new JsonObject();
         Object2BooleanMap var10000 = this.criterions;
         Objects.requireNonNull($$0);
         var10000.forEach($$0::addProperty);
         return $$0;
      }

      public boolean test(AdvancementProgress p_62296_) {
         ObjectIterator var2 = this.criterions.object2BooleanEntrySet().iterator();

         while(var2.hasNext()) {
            Object2BooleanMap.Entry<String> $$1 = (Object2BooleanMap.Entry)var2.next();
            CriterionProgress $$2 = p_62296_.getCriterion((String)$$1.getKey());
            if ($$2 == null || $$2.isDone() != $$1.getBooleanValue()) {
               return false;
            }
         }

         return true;
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((AdvancementProgress)var1);
      }
   }

   public static class Builder {
      private MinMaxBounds.Ints level;
      @Nullable
      private GameType gameType;
      private final Map<Stat<?>, MinMaxBounds.Ints> stats;
      private final Object2BooleanMap<ResourceLocation> recipes;
      private final Map<ResourceLocation, AdvancementPredicate> advancements;
      private EntityPredicate lookingAt;

      public Builder() {
         this.level = MinMaxBounds.Ints.ANY;
         this.stats = Maps.newHashMap();
         this.recipes = new Object2BooleanOpenHashMap();
         this.advancements = Maps.newHashMap();
         this.lookingAt = EntityPredicate.ANY;
      }

      public static Builder player() {
         return new Builder();
      }

      public Builder setLevel(MinMaxBounds.Ints p_156776_) {
         this.level = p_156776_;
         return this;
      }

      public Builder addStat(Stat<?> p_156769_, MinMaxBounds.Ints p_156770_) {
         this.stats.put(p_156769_, p_156770_);
         return this;
      }

      public Builder addRecipe(ResourceLocation p_156781_, boolean p_156782_) {
         this.recipes.put(p_156781_, p_156782_);
         return this;
      }

      public Builder setGameType(GameType p_156774_) {
         this.gameType = p_156774_;
         return this;
      }

      public Builder setLookingAt(EntityPredicate p_156772_) {
         this.lookingAt = p_156772_;
         return this;
      }

      public Builder checkAdvancementDone(ResourceLocation p_156784_, boolean p_156785_) {
         this.advancements.put(p_156784_, new AdvancementDonePredicate(p_156785_));
         return this;
      }

      public Builder checkAdvancementCriterions(ResourceLocation p_156778_, Map<String, Boolean> p_156779_) {
         this.advancements.put(p_156778_, new AdvancementCriterionsPredicate(new Object2BooleanOpenHashMap(p_156779_)));
         return this;
      }

      public PlayerPredicate build() {
         return new PlayerPredicate(this.level, this.gameType, this.stats, this.recipes, this.advancements, this.lookingAt);
      }
   }

   interface AdvancementPredicate extends Predicate<AdvancementProgress> {
      JsonElement toJson();
   }
}

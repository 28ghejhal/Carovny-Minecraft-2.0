package net.minecraft.world.level.storage.loot;

import com.google.common.collect.Sets;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class LootContext {
   private final LootParams params;
   private final RandomSource random;
   private final LootDataResolver lootDataResolver;
   private final Set<VisitedEntry<?>> visitedElements;
   private ResourceLocation queriedLootTableId;

   LootContext(LootParams p_287722_, RandomSource p_287702_, LootDataResolver p_287619_) {
      this.visitedElements = Sets.newLinkedHashSet();
      this.params = p_287722_;
      this.random = p_287702_;
      this.lootDataResolver = p_287619_;
   }

   public boolean hasParam(LootContextParam<?> p_78937_) {
      return this.params.hasParam(p_78937_);
   }

   public <T> T getParam(LootContextParam<T> p_165125_) {
      return (T)this.params.getParameter(p_165125_);
   }

   public void addDynamicDrops(ResourceLocation p_78943_, Consumer<ItemStack> p_78944_) {
      this.params.addDynamicDrops(p_78943_, p_78944_);
   }

   @Nullable
   public <T> T getParamOrNull(LootContextParam<T> p_78954_) {
      return (T)this.params.getParamOrNull(p_78954_);
   }

   public boolean hasVisitedElement(VisitedEntry<?> p_279182_) {
      return this.visitedElements.contains(p_279182_);
   }

   public boolean pushVisitedElement(VisitedEntry<?> p_279152_) {
      return this.visitedElements.add(p_279152_);
   }

   public void popVisitedElement(VisitedEntry<?> p_279198_) {
      this.visitedElements.remove(p_279198_);
   }

   public LootDataResolver getResolver() {
      return this.lootDataResolver;
   }

   public RandomSource getRandom() {
      return this.random;
   }

   public float getLuck() {
      return this.params.getLuck();
   }

   public ServerLevel getLevel() {
      return this.params.getLevel();
   }

   public static VisitedEntry<LootTable> createVisitedEntry(LootTable p_279327_) {
      return new VisitedEntry<LootTable>(LootDataType.TABLE, p_279327_);
   }

   public static VisitedEntry<LootItemCondition> createVisitedEntry(LootItemCondition p_279250_) {
      return new VisitedEntry<LootItemCondition>(LootDataType.PREDICATE, p_279250_);
   }

   public static VisitedEntry<LootItemFunction> createVisitedEntry(LootItemFunction p_279163_) {
      return new VisitedEntry<LootItemFunction>(LootDataType.MODIFIER, p_279163_);
   }

   public int getLootingModifier() {
      return ForgeHooks.getLootingLevel((Entity)this.getParamOrNull(LootContextParams.THIS_ENTITY), (Entity)this.getParamOrNull(LootContextParams.KILLER_ENTITY), (DamageSource)this.getParamOrNull(LootContextParams.DAMAGE_SOURCE));
   }

   private LootContext(LootParams p_287722_, RandomSource p_287702_, LootDataResolver p_287619_, ResourceLocation queriedLootTableId) {
      this(p_287722_, p_287702_, p_287619_);
      this.queriedLootTableId = queriedLootTableId;
   }

   public void setQueriedLootTableId(ResourceLocation queriedLootTableId) {
      if (this.queriedLootTableId == null && queriedLootTableId != null) {
         this.queriedLootTableId = queriedLootTableId;
      }

   }

   public ResourceLocation getQueriedLootTableId() {
      return this.queriedLootTableId == null ? LootTableIdCondition.UNKNOWN_LOOT_TABLE : this.queriedLootTableId;
   }

   public static class Builder {
      private final LootParams params;
      @Nullable
      private RandomSource random;
      private ResourceLocation queriedLootTableId;

      public Builder(LootParams p_287628_) {
         this.params = p_287628_;
      }

      public Builder(LootContext context) {
         this.params = context.params;
         this.random = context.random;
         this.queriedLootTableId = context.queriedLootTableId;
      }

      public Builder withOptionalRandomSeed(long p_78966_) {
         if (p_78966_ != 0L) {
            this.random = RandomSource.create(p_78966_);
         }

         return this;
      }

      public Builder withQueriedLootTableId(ResourceLocation queriedLootTableId) {
         this.queriedLootTableId = queriedLootTableId;
         return this;
      }

      public ServerLevel getLevel() {
         return this.params.getLevel();
      }

      public LootContext create(@Nullable ResourceLocation p_287626_) {
         ServerLevel serverlevel = this.getLevel();
         MinecraftServer minecraftserver = serverlevel.getServer();
         RandomSource randomsource;
         if (this.random != null) {
            randomsource = this.random;
         } else if (p_287626_ != null) {
            randomsource = serverlevel.getRandomSequence(p_287626_);
         } else {
            randomsource = serverlevel.getRandom();
         }

         return new LootContext(this.params, randomsource, minecraftserver.getLootData(), this.queriedLootTableId);
      }
   }

   public static enum EntityTarget {
      THIS("this", LootContextParams.THIS_ENTITY),
      KILLER("killer", LootContextParams.KILLER_ENTITY),
      DIRECT_KILLER("direct_killer", LootContextParams.DIRECT_KILLER_ENTITY),
      KILLER_PLAYER("killer_player", LootContextParams.LAST_DAMAGE_PLAYER);

      final String name;
      private final LootContextParam<? extends Entity> param;

      private EntityTarget(String p_79001_, LootContextParam<? extends Entity> p_79002_) {
         this.name = p_79001_;
         this.param = p_79002_;
      }

      public LootContextParam<? extends Entity> getParam() {
         return this.param;
      }

      public String getName() {
         return this.name;
      }

      public static EntityTarget getByName(String p_79007_) {
         for(EntityTarget lootcontext$entitytarget : values()) {
            if (lootcontext$entitytarget.name.equals(p_79007_)) {
               return lootcontext$entitytarget;
            }
         }

         throw new IllegalArgumentException("Invalid entity target " + p_79007_);
      }

      // $FF: synthetic method
      private static EntityTarget[] $values() {
         return new EntityTarget[]{THIS, KILLER, DIRECT_KILLER, KILLER_PLAYER};
      }

      public static class Serializer extends TypeAdapter<EntityTarget> {
         public void write(JsonWriter p_79015_, EntityTarget p_79016_) throws IOException {
            p_79015_.value(p_79016_.name);
         }

         public EntityTarget read(JsonReader p_79013_) throws IOException {
            return LootContext.EntityTarget.getByName(p_79013_.nextString());
         }
      }
   }

   public static record VisitedEntry<T>(LootDataType<T> type, T value) {
   }
}

package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;

public abstract class SimpleCriterionTrigger<T extends AbstractCriterionTriggerInstance> implements CriterionTrigger<T> {
   private final Map<PlayerAdvancements, Set<CriterionTrigger.Listener<T>>> players = Maps.newIdentityHashMap();

   public final void addPlayerListener(PlayerAdvancements p_66243_, CriterionTrigger.Listener<T> p_66244_) {
      ((Set)this.players.computeIfAbsent(p_66243_, (p_66252_) -> Sets.newHashSet())).add(p_66244_);
   }

   public final void removePlayerListener(PlayerAdvancements p_66254_, CriterionTrigger.Listener<T> p_66255_) {
      Set<CriterionTrigger.Listener<T>> $$2 = (Set)this.players.get(p_66254_);
      if ($$2 != null) {
         $$2.remove(p_66255_);
         if ($$2.isEmpty()) {
            this.players.remove(p_66254_);
         }
      }

   }

   public final void removePlayerListeners(PlayerAdvancements p_66241_) {
      this.players.remove(p_66241_);
   }

   protected abstract T createInstance(JsonObject var1, ContextAwarePredicate var2, DeserializationContext var3);

   public final T createInstance(JsonObject p_66246_, DeserializationContext p_66247_) {
      ContextAwarePredicate $$2 = EntityPredicate.fromJson(p_66246_, "player", p_66247_);
      return (T)this.createInstance(p_66246_, $$2, p_66247_);
   }

   protected void trigger(ServerPlayer p_66235_, Predicate<T> p_66236_) {
      PlayerAdvancements $$2 = p_66235_.getAdvancements();
      Set<CriterionTrigger.Listener<T>> $$3 = (Set)this.players.get($$2);
      if ($$3 != null && !$$3.isEmpty()) {
         LootContext $$4 = EntityPredicate.createContext(p_66235_, p_66235_);
         List<CriterionTrigger.Listener<T>> $$5 = null;

         for(CriterionTrigger.Listener<T> $$6 : $$3) {
            T $$7 = $$6.getTriggerInstance();
            if (p_66236_.test($$7) && $$7.getPlayerPredicate().matches($$4)) {
               if ($$5 == null) {
                  $$5 = Lists.newArrayList();
               }

               $$5.add($$6);
            }
         }

         if ($$5 != null) {
            for(CriterionTrigger.Listener<T> $$8 : $$5) {
               $$8.run($$2);
            }
         }

      }
   }

   // $FF: synthetic method
   public CriterionTriggerInstance createInstance(JsonObject var1, DeserializationContext var2) {
      return this.createInstance(var1, var2);
   }
}

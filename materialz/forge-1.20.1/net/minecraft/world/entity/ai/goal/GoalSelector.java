package net.minecraft.world.entity.ai.goal;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class GoalSelector {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final WrappedGoal NO_GOAL = new WrappedGoal(Integer.MAX_VALUE, new Goal() {
      public boolean canUse() {
         return false;
      }
   }) {
      public boolean isRunning() {
         return false;
      }
   };
   private final Map<Goal.Flag, WrappedGoal> lockedFlags = new EnumMap(Goal.Flag.class);
   private final Set<WrappedGoal> availableGoals = Sets.newLinkedHashSet();
   private final Supplier<ProfilerFiller> profiler;
   private final EnumSet<Goal.Flag> disabledFlags = EnumSet.noneOf(Goal.Flag.class);
   private int tickCount;
   private int newGoalRate = 3;

   public GoalSelector(Supplier<ProfilerFiller> p_25351_) {
      this.profiler = p_25351_;
   }

   public void addGoal(int p_25353_, Goal p_25354_) {
      this.availableGoals.add(new WrappedGoal(p_25353_, p_25354_));
   }

   @VisibleForTesting
   public void removeAllGoals(Predicate<Goal> p_262575_) {
      this.availableGoals.removeIf((p_262564_) -> p_262575_.test(p_262564_.getGoal()));
   }

   public void removeGoal(Goal p_25364_) {
      this.availableGoals.stream().filter((p_25378_) -> p_25378_.getGoal() == p_25364_).filter(WrappedGoal::isRunning).forEach(WrappedGoal::stop);
      this.availableGoals.removeIf((p_25367_) -> p_25367_.getGoal() == p_25364_);
   }

   private static boolean goalContainsAnyFlags(WrappedGoal p_186076_, EnumSet<Goal.Flag> p_186077_) {
      for(Goal.Flag $$2 : p_186076_.getFlags()) {
         if (p_186077_.contains($$2)) {
            return true;
         }
      }

      return false;
   }

   private static boolean goalCanBeReplacedForAllFlags(WrappedGoal p_186079_, Map<Goal.Flag, WrappedGoal> p_186080_) {
      for(Goal.Flag $$2 : p_186079_.getFlags()) {
         if (!((WrappedGoal)p_186080_.getOrDefault($$2, NO_GOAL)).canBeReplacedBy(p_186079_)) {
            return false;
         }
      }

      return true;
   }

   public void tick() {
      ProfilerFiller $$0 = (ProfilerFiller)this.profiler.get();
      $$0.push("goalCleanup");

      for(WrappedGoal $$1 : this.availableGoals) {
         if ($$1.isRunning() && (goalContainsAnyFlags($$1, this.disabledFlags) || !$$1.canContinueToUse())) {
            $$1.stop();
         }
      }

      Iterator<Map.Entry<Goal.Flag, WrappedGoal>> $$2 = this.lockedFlags.entrySet().iterator();

      while($$2.hasNext()) {
         Map.Entry<Goal.Flag, WrappedGoal> $$3 = (Map.Entry)$$2.next();
         if (!((WrappedGoal)$$3.getValue()).isRunning()) {
            $$2.remove();
         }
      }

      $$0.pop();
      $$0.push("goalUpdate");

      for(WrappedGoal $$4 : this.availableGoals) {
         if (!$$4.isRunning() && !goalContainsAnyFlags($$4, this.disabledFlags) && goalCanBeReplacedForAllFlags($$4, this.lockedFlags) && $$4.canUse()) {
            for(Goal.Flag $$5 : $$4.getFlags()) {
               WrappedGoal $$6 = (WrappedGoal)this.lockedFlags.getOrDefault($$5, NO_GOAL);
               $$6.stop();
               this.lockedFlags.put($$5, $$4);
            }

            $$4.start();
         }
      }

      $$0.pop();
      this.tickRunningGoals(true);
   }

   public void tickRunningGoals(boolean p_186082_) {
      ProfilerFiller $$1 = (ProfilerFiller)this.profiler.get();
      $$1.push("goalTick");

      for(WrappedGoal $$2 : this.availableGoals) {
         if ($$2.isRunning() && (p_186082_ || $$2.requiresUpdateEveryTick())) {
            $$2.tick();
         }
      }

      $$1.pop();
   }

   public Set<WrappedGoal> getAvailableGoals() {
      return this.availableGoals;
   }

   public Stream<WrappedGoal> getRunningGoals() {
      return this.availableGoals.stream().filter(WrappedGoal::isRunning);
   }

   public void setNewGoalRate(int p_148098_) {
      this.newGoalRate = p_148098_;
   }

   public void disableControlFlag(Goal.Flag p_25356_) {
      this.disabledFlags.add(p_25356_);
   }

   public void enableControlFlag(Goal.Flag p_25375_) {
      this.disabledFlags.remove(p_25375_);
   }

   public void setControlFlag(Goal.Flag p_25361_, boolean p_25362_) {
      if (p_25362_) {
         this.enableControlFlag(p_25361_);
      } else {
         this.disableControlFlag(p_25361_);
      }

   }
}

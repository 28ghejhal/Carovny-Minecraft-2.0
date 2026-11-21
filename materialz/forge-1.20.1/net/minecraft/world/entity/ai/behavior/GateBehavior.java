package net.minecraft.world.entity.ai.behavior;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class GateBehavior<E extends LivingEntity> implements BehaviorControl<E> {
   private final Map<MemoryModuleType<?>, MemoryStatus> entryCondition;
   private final Set<MemoryModuleType<?>> exitErasedMemories;
   private final OrderPolicy orderPolicy;
   private final RunningPolicy runningPolicy;
   private final ShufflingList<BehaviorControl<? super E>> behaviors = new ShufflingList<BehaviorControl<? super E>>();
   private Behavior.Status status;

   public GateBehavior(Map<MemoryModuleType<?>, MemoryStatus> p_22873_, Set<MemoryModuleType<?>> p_22874_, OrderPolicy p_22875_, RunningPolicy p_22876_, List<Pair<? extends BehaviorControl<? super E>, Integer>> p_22877_) {
      this.status = Behavior.Status.STOPPED;
      this.entryCondition = p_22873_;
      this.exitErasedMemories = p_22874_;
      this.orderPolicy = p_22875_;
      this.runningPolicy = p_22876_;
      p_22877_.forEach((p_258332_) -> this.behaviors.add((BehaviorControl)p_258332_.getFirst(), (Integer)p_258332_.getSecond()));
   }

   public Behavior.Status getStatus() {
      return this.status;
   }

   private boolean hasRequiredMemories(E p_259419_) {
      for(Map.Entry<MemoryModuleType<?>, MemoryStatus> $$1 : this.entryCondition.entrySet()) {
         MemoryModuleType<?> $$2 = (MemoryModuleType)$$1.getKey();
         MemoryStatus $$3 = (MemoryStatus)$$1.getValue();
         if (!p_259419_.getBrain().checkMemory($$2, $$3)) {
            return false;
         }
      }

      return true;
   }

   public final boolean tryStart(ServerLevel p_259362_, E p_259746_, long p_259560_) {
      if (this.hasRequiredMemories(p_259746_)) {
         this.status = Behavior.Status.RUNNING;
         this.orderPolicy.apply(this.behaviors);
         this.runningPolicy.apply(this.behaviors.stream(), p_259362_, p_259746_, p_259560_);
         return true;
      } else {
         return false;
      }
   }

   public final void tickOrStop(ServerLevel p_259934_, E p_259790_, long p_260259_) {
      this.behaviors.stream().filter((p_258342_) -> p_258342_.getStatus() == Behavior.Status.RUNNING).forEach((p_258336_) -> p_258336_.tickOrStop(p_259934_, p_259790_, p_260259_));
      if (this.behaviors.stream().noneMatch((p_258344_) -> p_258344_.getStatus() == Behavior.Status.RUNNING)) {
         this.doStop(p_259934_, p_259790_, p_260259_);
      }

   }

   public final void doStop(ServerLevel p_259962_, E p_260250_, long p_259847_) {
      this.status = Behavior.Status.STOPPED;
      this.behaviors.stream().filter((p_258337_) -> p_258337_.getStatus() == Behavior.Status.RUNNING).forEach((p_258341_) -> p_258341_.doStop(p_259962_, p_260250_, p_259847_));
      Set var10000 = this.exitErasedMemories;
      Brain var10001 = ((LivingEntity)p_260250_).getBrain();
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::eraseMemory);
   }

   public String debugString() {
      return this.getClass().getSimpleName();
   }

   public String toString() {
      Set<? extends BehaviorControl<? super E>> $$0 = (Set)this.behaviors.stream().filter((p_258343_) -> p_258343_.getStatus() == Behavior.Status.RUNNING).collect(Collectors.toSet());
      String var10000 = this.getClass().getSimpleName();
      return "(" + var10000 + "): " + $$0;
   }

   public static enum OrderPolicy {
      ORDERED((p_147530_) -> {
      }),
      SHUFFLED(ShufflingList::shuffle);

      private final Consumer<ShufflingList<?>> consumer;

      private OrderPolicy(Consumer<ShufflingList<?>> p_22930_) {
         this.consumer = p_22930_;
      }

      public void apply(ShufflingList<?> p_147528_) {
         this.consumer.accept(p_147528_);
      }

      // $FF: synthetic method
      private static OrderPolicy[] $values() {
         return new OrderPolicy[]{ORDERED, SHUFFLED};
      }
   }

   public static enum RunningPolicy permits null, null {
      RUN_ONE {
         public <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> p_147537_, ServerLevel p_147538_, E p_147539_, long p_147540_) {
            p_147537_.filter((p_258349_) -> p_258349_.getStatus() == Behavior.Status.STOPPED).filter((p_258348_) -> p_258348_.tryStart(p_258345_, p_258346_, p_258347_)).findFirst();
         }

         // $FF: synthetic method
         private static boolean lambda$apply$1(ServerLevel p_258345_, LivingEntity p_258346_, long p_258347_, BehaviorControl p_258348_) {
            return p_258348_.tryStart(p_258345_, p_258346_, p_258347_);
         }

         // $FF: synthetic method
         private static boolean lambda$apply$0(BehaviorControl p_258349_) {
            return p_258349_.getStatus() == Behavior.Status.STOPPED;
         }
      },
      TRY_ALL {
         public <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> p_147542_, ServerLevel p_147543_, E p_147544_, long p_147545_) {
            p_147542_.filter((p_258350_) -> p_258350_.getStatus() == Behavior.Status.STOPPED).forEach((p_258354_) -> p_258354_.tryStart(p_258351_, p_258352_, p_258353_));
         }

         // $FF: synthetic method
         private static void lambda$apply$1(ServerLevel p_258351_, LivingEntity p_258352_, long p_258353_, BehaviorControl p_258354_) {
            p_258354_.tryStart(p_258351_, p_258352_, p_258353_);
         }

         // $FF: synthetic method
         private static boolean lambda$apply$0(BehaviorControl p_258350_) {
            return p_258350_.getStatus() == Behavior.Status.STOPPED;
         }
      };

      public abstract <E extends LivingEntity> void apply(Stream<BehaviorControl<? super E>> var1, ServerLevel var2, E var3, long var4);

      // $FF: synthetic method
      private static RunningPolicy[] $values() {
         return new RunningPolicy[]{RUN_ONE, TRY_ALL};
      }
   }
}

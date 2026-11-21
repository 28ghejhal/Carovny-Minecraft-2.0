package net.minecraft.world.entity.ai.behavior.declarative;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.datafixers.kinds.Const;
import com.mojang.datafixers.kinds.IdF;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.kinds.OptionalBox;
import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.datafixers.util.Unit;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class BehaviorBuilder<E extends LivingEntity, M> implements App<Mu<E>, M> {
   private final TriggerWithResult<E, M> trigger;

   public static <E extends LivingEntity, M> BehaviorBuilder<E, M> unbox(App<Mu<E>, M> p_259593_) {
      return (BehaviorBuilder)p_259593_;
   }

   public static <E extends LivingEntity> Instance<E> instance() {
      return new Instance<E>();
   }

   public static <E extends LivingEntity> OneShot<E> create(Function<Instance<E>, ? extends App<Mu<E>, Trigger<E>>> p_259386_) {
      final TriggerWithResult<E, Trigger<E>> $$1 = get((App)p_259386_.apply(instance()));
      return new OneShot<E>() {
         public boolean trigger(ServerLevel p_259385_, E p_260003_, long p_259194_) {
            Trigger<E> $$3 = $$1.tryTrigger(p_259385_, p_260003_, p_259194_);
            return $$3 == null ? false : $$3.trigger(p_259385_, p_260003_, p_259194_);
         }

         public String debugString() {
            return "OneShot[" + $$1.debugString() + "]";
         }

         public String toString() {
            return this.debugString();
         }
      };
   }

   public static <E extends LivingEntity> OneShot<E> sequence(Trigger<? super E> p_260174_, Trigger<? super E> p_259134_) {
      return create((Function)((p_259495_) -> p_259495_.group(p_259495_.ifTriggered(p_260174_)).apply(p_259495_, (p_260322_) -> {
            Objects.requireNonNull(p_259134_);
            return p_259134_::trigger;
         })));
   }

   public static <E extends LivingEntity> OneShot<E> triggerIf(Predicate<E> p_260059_, OneShot<? super E> p_259640_) {
      return sequence(triggerIf(p_260059_), p_259640_);
   }

   public static <E extends LivingEntity> OneShot<E> triggerIf(Predicate<E> p_260112_) {
      return create((Function)((p_260353_) -> p_260353_.point((Trigger)(p_259280_, p_259428_, p_259845_) -> p_260112_.test(p_259428_))));
   }

   public static <E extends LivingEntity> OneShot<E> triggerIf(BiPredicate<ServerLevel, E> p_259227_) {
      return create((Function)((p_260191_) -> p_260191_.point((Trigger)(p_259079_, p_259093_, p_260140_) -> p_259227_.test(p_259079_, p_259093_))));
   }

   static <E extends LivingEntity, M> TriggerWithResult<E, M> get(App<Mu<E>, M> p_259615_) {
      return unbox(p_259615_).trigger;
   }

   BehaviorBuilder(TriggerWithResult<E, M> p_260164_) {
      this.trigger = p_260164_;
   }

   static <E extends LivingEntity, M> BehaviorBuilder<E, M> create(TriggerWithResult<E, M> p_259575_) {
      return new BehaviorBuilder<E, M>(p_259575_);
   }

   public static final class Mu<E extends LivingEntity> implements K1 {
   }

   static final class PureMemory<E extends LivingEntity, F extends K1, Value> extends BehaviorBuilder<E, MemoryAccessor<F, Value>> {
      PureMemory(MemoryCondition<F, Value> p_259776_) {
         super(new TriggerWithResult<E, MemoryAccessor<F, Value>>() {
            // $FF: synthetic field
            final MemoryCondition val$condition;

            {
               this.val$condition = var1;
            }

            public MemoryAccessor<F, Value> tryTrigger(ServerLevel p_259899_, E p_259558_, long p_259793_) {
               Brain<?> $$3 = ((LivingEntity)p_259558_).getBrain();
               Optional<Value> $$4 = $$3.<Value>getMemoryInternal(this.val$condition.memory());
               return $$4 == null ? null : this.val$condition.createAccessor($$3, $$4);
            }

            public String debugString() {
               return "M[" + this.val$condition + "]";
            }

            public String toString() {
               return this.debugString();
            }

            // $FF: synthetic method
            public Object tryTrigger(ServerLevel var1, LivingEntity var2, long var3) {
               return this.tryTrigger(var1, var2, var3);
            }
         });
      }
   }

   static final class Constant<E extends LivingEntity, A> extends BehaviorBuilder<E, A> {
      Constant(A p_259906_) {
         this(p_259906_, () -> "C[" + p_259906_ + "]");
      }

      Constant(A p_259514_, Supplier<String> p_259950_) {
         super(new TriggerWithResult<E, A>(p_259950_) {
            // $FF: synthetic field
            final Object val$a;
            // $FF: synthetic field
            final Supplier val$debugString;

            {
               this.val$a = var1;
               this.val$debugString = var2;
            }

            public A tryTrigger(ServerLevel p_259561_, E p_259467_, long p_259297_) {
               return (A)this.val$a;
            }

            public String debugString() {
               return (String)this.val$debugString.get();
            }

            public String toString() {
               return this.debugString();
            }
         });
      }
   }

   static final class TriggerWrapper<E extends LivingEntity> extends BehaviorBuilder<E, Unit> {
      TriggerWrapper(Trigger<? super E> p_259310_) {
         super(new TriggerWithResult<E, Unit>() {
            // $FF: synthetic field
            final Trigger val$dependentTrigger;

            {
               this.val$dependentTrigger = var1;
            }

            @Nullable
            public Unit tryTrigger(ServerLevel p_259397_, E p_260169_, long p_259155_) {
               return this.val$dependentTrigger.trigger(p_259397_, p_260169_, p_259155_) ? Unit.INSTANCE : null;
            }

            public String debugString() {
               return "T[" + this.val$dependentTrigger + "]";
            }

            // $FF: synthetic method
            @Nullable
            public Object tryTrigger(ServerLevel var1, LivingEntity var2, long var3) {
               return this.tryTrigger(var1, var2, var3);
            }
         });
      }
   }

   public static final class Instance<E extends LivingEntity> implements Applicative<Mu<E>, Mu<E>> {
      public <Value> Optional<Value> tryGet(MemoryAccessor<OptionalBox.Mu, Value> p_259352_) {
         return OptionalBox.unbox(p_259352_.value());
      }

      public <Value> Value get(MemoryAccessor<IdF.Mu, Value> p_259206_) {
         return (Value)IdF.get(p_259206_.value());
      }

      public <Value> BehaviorBuilder<E, MemoryAccessor<OptionalBox.Mu, Value>> registered(MemoryModuleType<Value> p_259477_) {
         return new PureMemory(new MemoryCondition.Registered(p_259477_));
      }

      public <Value> BehaviorBuilder<E, MemoryAccessor<IdF.Mu, Value>> present(MemoryModuleType<Value> p_259673_) {
         return new PureMemory(new MemoryCondition.Present(p_259673_));
      }

      public <Value> BehaviorBuilder<E, MemoryAccessor<Const.Mu<Unit>, Value>> absent(MemoryModuleType<Value> p_260198_) {
         return new PureMemory(new MemoryCondition.Absent(p_260198_));
      }

      public BehaviorBuilder<E, Unit> ifTriggered(Trigger<? super E> p_260247_) {
         return new TriggerWrapper(p_260247_);
      }

      public <A> BehaviorBuilder<E, A> point(A p_259634_) {
         return new Constant<E, A>(p_259634_);
      }

      public <A> BehaviorBuilder<E, A> point(Supplier<String> p_260070_, A p_260295_) {
         return new Constant<E, A>(p_260295_, p_260070_);
      }

      public <A, R> Function<App<Mu<E>, A>, App<Mu<E>, R>> lift1(App<Mu<E>, Function<A, R>> p_259294_) {
         return (p_259751_) -> {
            TriggerWithResult<E, A> $$2 = BehaviorBuilder.<E, A>get(p_259751_);
            TriggerWithResult<E, Function<A, R>> $$3 = BehaviorBuilder.<E, Function<A, R>>get(p_259294_);
            return BehaviorBuilder.create(new TriggerWithResult<E, R>($$2, $$3) {
               // $FF: synthetic field
               final BehaviorBuilder.TriggerWithResult val$aTrigger;
               // $FF: synthetic field
               final BehaviorBuilder.TriggerWithResult val$fTrigger;
               // $FF: synthetic field
               final BehaviorBuilder.Instance this$0;

               {
                  this.this$0 = p_259693_;
                  this.val$aTrigger = var2;
                  this.val$fTrigger = var3;
               }

               public R tryTrigger(ServerLevel p_259603_, E p_260233_, long p_259654_) {
                  A $$3 = (A)this.val$aTrigger.tryTrigger(p_259603_, p_260233_, p_259654_);
                  if ($$3 == null) {
                     return null;
                  } else {
                     Function<A, R> $$4 = (Function)this.val$fTrigger.tryTrigger(p_259603_, p_260233_, p_259654_);
                     return (R)($$4 == null ? null : $$4.apply($$3));
                  }
               }

               public String debugString() {
                  String var10000 = this.val$fTrigger.debugString();
                  return var10000 + " * " + this.val$aTrigger.debugString();
               }

               public String toString() {
                  return this.debugString();
               }
            });
         };
      }

      public <T, R> BehaviorBuilder<E, R> map(Function<? super T, ? extends R> p_259963_, App<Mu<E>, T> p_260355_) {
         TriggerWithResult<E, T> $$2 = BehaviorBuilder.<E, T>get(p_260355_);
         return BehaviorBuilder.create(new TriggerWithResult<E, R>($$2, p_259963_) {
            // $FF: synthetic field
            final BehaviorBuilder.TriggerWithResult val$tTrigger;
            // $FF: synthetic field
            final Function val$func;
            // $FF: synthetic field
            final BehaviorBuilder.Instance this$0;

            {
               this.this$0 = p_259389_;
               this.val$tTrigger = var2;
               this.val$func = var3;
            }

            public R tryTrigger(ServerLevel p_259755_, E p_259656_, long p_259300_) {
               T $$3 = (T)this.val$tTrigger.tryTrigger(p_259755_, p_259656_, p_259300_);
               return (R)($$3 == null ? null : this.val$func.apply($$3));
            }

            public String debugString() {
               String var10000 = this.val$tTrigger.debugString();
               return var10000 + ".map[" + this.val$func + "]";
            }

            public String toString() {
               return this.debugString();
            }
         });
      }

      public <A, B, R> BehaviorBuilder<E, R> ap2(App<Mu<E>, BiFunction<A, B, R>> p_259535_, App<Mu<E>, A> p_259162_, App<Mu<E>, B> p_259733_) {
         TriggerWithResult<E, A> $$3 = BehaviorBuilder.<E, A>get(p_259162_);
         TriggerWithResult<E, B> $$4 = BehaviorBuilder.<E, B>get(p_259733_);
         TriggerWithResult<E, BiFunction<A, B, R>> $$5 = BehaviorBuilder.<E, BiFunction<A, B, R>>get(p_259535_);
         return BehaviorBuilder.create(new TriggerWithResult<E, R>($$3, $$4, $$5) {
            // $FF: synthetic field
            final BehaviorBuilder.TriggerWithResult val$aTrigger;
            // $FF: synthetic field
            final BehaviorBuilder.TriggerWithResult val$bTrigger;
            // $FF: synthetic field
            final BehaviorBuilder.TriggerWithResult val$fTrigger;
            // $FF: synthetic field
            final BehaviorBuilder.Instance this$0;

            {
               this.this$0 = p_259170_;
               this.val$aTrigger = var2;
               this.val$bTrigger = var3;
               this.val$fTrigger = var4;
            }

            public R tryTrigger(ServerLevel p_259274_, E p_259817_, long p_259820_) {
               A $$3 = (A)this.val$aTrigger.tryTrigger(p_259274_, p_259817_, p_259820_);
               if ($$3 == null) {
                  return null;
               } else {
                  B $$4 = (B)this.val$bTrigger.tryTrigger(p_259274_, p_259817_, p_259820_);
                  if ($$4 == null) {
                     return null;
                  } else {
                     BiFunction<A, B, R> $$5 = (BiFunction)this.val$fTrigger.tryTrigger(p_259274_, p_259817_, p_259820_);
                     return (R)($$5 == null ? null : $$5.apply($$3, $$4));
                  }
               }
            }

            public String debugString() {
               String var10000 = this.val$fTrigger.debugString();
               return var10000 + " * " + this.val$aTrigger.debugString() + " * " + this.val$bTrigger.debugString();
            }

            public String toString() {
               return this.debugString();
            }
         });
      }

      public <T1, T2, T3, R> BehaviorBuilder<E, R> ap3(App<Mu<E>, Function3<T1, T2, T3, R>> p_260239_, App<Mu<E>, T1> p_259239_, App<Mu<E>, T2> p_259638_, App<Mu<E>, T3> p_259969_) {
         TriggerWithResult<E, T1> $$4 = BehaviorBuilder.<E, T1>get(p_259239_);
         TriggerWithResult<E, T2> $$5 = BehaviorBuilder.<E, T2>get(p_259638_);
         TriggerWithResult<E, T3> $$6 = BehaviorBuilder.<E, T3>get(p_259969_);
         TriggerWithResult<E, Function3<T1, T2, T3, R>> $$7 = BehaviorBuilder.<E, Function3<T1, T2, T3, R>>get(p_260239_);
         return BehaviorBuilder.create(new TriggerWithResult<E, R>($$4, $$5, $$6, $$7) {
            // $FF: synthetic field
            final BehaviorBuilder.TriggerWithResult val$t1Trigger;
            // $FF: synthetic field
            final BehaviorBuilder.TriggerWithResult val$t2Trigger;
            // $FF: synthetic field
            final BehaviorBuilder.TriggerWithResult val$t3Trigger;
            // $FF: synthetic field
            final BehaviorBuilder.TriggerWithResult val$fTrigger;
            // $FF: synthetic field
            final BehaviorBuilder.Instance this$0;

            {
               this.this$0 = p_259682_;
               this.val$t1Trigger = var2;
               this.val$t2Trigger = var3;
               this.val$t3Trigger = var4;
               this.val$fTrigger = var5;
            }

            public R tryTrigger(ServerLevel p_259096_, E p_260221_, long p_259035_) {
               T1 $$3 = (T1)this.val$t1Trigger.tryTrigger(p_259096_, p_260221_, p_259035_);
               if ($$3 == null) {
                  return null;
               } else {
                  T2 $$4 = (T2)this.val$t2Trigger.tryTrigger(p_259096_, p_260221_, p_259035_);
                  if ($$4 == null) {
                     return null;
                  } else {
                     T3 $$5 = (T3)this.val$t3Trigger.tryTrigger(p_259096_, p_260221_, p_259035_);
                     if ($$5 == null) {
                        return null;
                     } else {
                        Function3<T1, T2, T3, R> $$6 = (Function3)this.val$fTrigger.tryTrigger(p_259096_, p_260221_, p_259035_);
                        return (R)($$6 == null ? null : $$6.apply($$3, $$4, $$5));
                     }
                  }
               }
            }

            public String debugString() {
               String var10000 = this.val$fTrigger.debugString();
               return var10000 + " * " + this.val$t1Trigger.debugString() + " * " + this.val$t2Trigger.debugString() + " * " + this.val$t3Trigger.debugString();
            }

            public String toString() {
               return this.debugString();
            }
         });
      }

      public <T1, T2, T3, T4, R> BehaviorBuilder<E, R> ap4(App<Mu<E>, Function4<T1, T2, T3, T4, R>> p_259519_, App<Mu<E>, T1> p_259829_, App<Mu<E>, T2> p_259314_, App<Mu<E>, T3> p_260089_, App<Mu<E>, T4> p_259136_) {
         TriggerWithResult<E, T1> $$5 = BehaviorBuilder.<E, T1>get(p_259829_);
         TriggerWithResult<E, T2> $$6 = BehaviorBuilder.<E, T2>get(p_259314_);
         TriggerWithResult<E, T3> $$7 = BehaviorBuilder.<E, T3>get(p_260089_);
         TriggerWithResult<E, T4> $$8 = BehaviorBuilder.<E, T4>get(p_259136_);
         TriggerWithResult<E, Function4<T1, T2, T3, T4, R>> $$9 = BehaviorBuilder.<E, Function4<T1, T2, T3, T4, R>>get(p_259519_);
         return BehaviorBuilder.create(new TriggerWithResult<E, R>($$5, $$6, $$7, $$8, $$9) {
            // $FF: synthetic field
            final BehaviorBuilder.TriggerWithResult val$t1Trigger;
            // $FF: synthetic field
            final BehaviorBuilder.TriggerWithResult val$t2Trigger;
            // $FF: synthetic field
            final BehaviorBuilder.TriggerWithResult val$t3Trigger;
            // $FF: synthetic field
            final BehaviorBuilder.TriggerWithResult val$t4Trigger;
            // $FF: synthetic field
            final BehaviorBuilder.TriggerWithResult val$fTrigger;
            // $FF: synthetic field
            final BehaviorBuilder.Instance this$0;

            {
               this.this$0 = p_260116_;
               this.val$t1Trigger = var2;
               this.val$t2Trigger = var3;
               this.val$t3Trigger = var4;
               this.val$t4Trigger = var5;
               this.val$fTrigger = var6;
            }

            public R tryTrigger(ServerLevel p_259537_, E p_259581_, long p_259423_) {
               T1 $$3 = (T1)this.val$t1Trigger.tryTrigger(p_259537_, p_259581_, p_259423_);
               if ($$3 == null) {
                  return null;
               } else {
                  T2 $$4 = (T2)this.val$t2Trigger.tryTrigger(p_259537_, p_259581_, p_259423_);
                  if ($$4 == null) {
                     return null;
                  } else {
                     T3 $$5 = (T3)this.val$t3Trigger.tryTrigger(p_259537_, p_259581_, p_259423_);
                     if ($$5 == null) {
                        return null;
                     } else {
                        T4 $$6 = (T4)this.val$t4Trigger.tryTrigger(p_259537_, p_259581_, p_259423_);
                        if ($$6 == null) {
                           return null;
                        } else {
                           Function4<T1, T2, T3, T4, R> $$7 = (Function4)this.val$fTrigger.tryTrigger(p_259537_, p_259581_, p_259423_);
                           return (R)($$7 == null ? null : $$7.apply($$3, $$4, $$5, $$6));
                        }
                     }
                  }
               }
            }

            public String debugString() {
               String var10000 = this.val$fTrigger.debugString();
               return var10000 + " * " + this.val$t1Trigger.debugString() + " * " + this.val$t2Trigger.debugString() + " * " + this.val$t3Trigger.debugString() + " * " + this.val$t4Trigger.debugString();
            }

            public String toString() {
               return this.debugString();
            }
         });
      }

      // $FF: synthetic method
      public App ap4(App var1, App var2, App var3, App var4, App var5) {
         return this.ap4(var1, var2, var3, var4, var5);
      }

      // $FF: synthetic method
      public App ap3(App var1, App var2, App var3, App var4) {
         return this.ap3(var1, var2, var3, var4);
      }

      // $FF: synthetic method
      public App ap2(App var1, App var2, App var3) {
         return this.ap2(var1, var2, var3);
      }

      // $FF: synthetic method
      public App point(Object var1) {
         return this.point(var1);
      }

      // $FF: synthetic method
      public App map(Function var1, App var2) {
         return this.map(var1, var2);
      }

      static final class Mu<E extends LivingEntity> implements Applicative.Mu {
         private Mu() {
         }
      }
   }

   interface TriggerWithResult<E extends LivingEntity, R> {
      @Nullable
      R tryTrigger(ServerLevel var1, E var2, long var3);

      String debugString();
   }
}

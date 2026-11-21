package net.minecraft.client;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.AbstractOptionSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public final class OptionInstance<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Enum<Boolean> BOOLEAN_VALUES;
   public static final CaptionBasedToString<Boolean> BOOLEAN_TO_STRING;
   private final TooltipSupplier<T> tooltip;
   final Function<T, Component> toString;
   private final ValueSet<T> values;
   private final Codec<T> codec;
   private final T initialValue;
   private final Consumer<T> onValueUpdate;
   final Component caption;
   T value;

   public static OptionInstance<Boolean> createBoolean(String p_231529_, boolean p_231530_, Consumer<Boolean> p_231531_) {
      return createBoolean(p_231529_, noTooltip(), p_231530_, p_231531_);
   }

   public static OptionInstance<Boolean> createBoolean(String p_231526_, boolean p_231527_) {
      return createBoolean(p_231526_, noTooltip(), p_231527_, (p_231548_) -> {
      });
   }

   public static OptionInstance<Boolean> createBoolean(String p_259291_, TooltipSupplier<Boolean> p_260306_, boolean p_259985_) {
      return createBoolean(p_259291_, p_260306_, p_259985_, (p_231513_) -> {
      });
   }

   public static OptionInstance<Boolean> createBoolean(String p_259289_, TooltipSupplier<Boolean> p_260210_, boolean p_259359_, Consumer<Boolean> p_259975_) {
      return createBoolean(p_259289_, p_260210_, BOOLEAN_TO_STRING, p_259359_, p_259975_);
   }

   public static OptionInstance<Boolean> createBoolean(String p_262002_, TooltipSupplier<Boolean> p_261507_, CaptionBasedToString<Boolean> p_262099_, boolean p_262136_, Consumer<Boolean> p_261984_) {
      return new OptionInstance<Boolean>(p_262002_, p_261507_, p_262099_, BOOLEAN_VALUES, p_262136_, p_261984_);
   }

   public OptionInstance(String p_260248_, TooltipSupplier<T> p_259437_, CaptionBasedToString<T> p_259148_, ValueSet<T> p_259590_, T p_260067_, Consumer<T> p_259392_) {
      this(p_260248_, p_259437_, p_259148_, p_259590_, p_259590_.codec(), p_260067_, p_259392_);
   }

   public OptionInstance(String p_259964_, TooltipSupplier<T> p_260354_, CaptionBasedToString<T> p_259496_, ValueSet<T> p_259090_, Codec<T> p_259043_, T p_259396_, Consumer<T> p_260147_) {
      this.caption = Component.translatable(p_259964_);
      this.tooltip = p_260354_;
      this.toString = (p_231506_) -> p_259496_.toString(this.caption, p_231506_);
      this.values = p_259090_;
      this.codec = p_259043_;
      this.initialValue = p_259396_;
      this.onValueUpdate = p_260147_;
      this.value = this.initialValue;
   }

   public static <T> TooltipSupplier<T> noTooltip() {
      return (p_258114_) -> null;
   }

   public static <T> TooltipSupplier<T> cachedConstantTooltip(Component p_231536_) {
      return (p_258116_) -> Tooltip.create(p_231536_);
   }

   public static <T extends OptionEnum> CaptionBasedToString<T> forOptionEnum() {
      return (p_231538_, p_231539_) -> p_231539_.getCaption();
   }

   public AbstractWidget createButton(Options p_231508_, int p_231509_, int p_231510_, int p_231511_) {
      return this.createButton(p_231508_, p_231509_, p_231510_, p_231511_, (p_261336_) -> {
      });
   }

   public AbstractWidget createButton(Options p_261971_, int p_261486_, int p_261569_, int p_261677_, Consumer<T> p_261912_) {
      return (AbstractWidget)this.values.createButton(this.tooltip, p_261971_, p_261486_, p_261569_, p_261677_, p_261912_).apply(this);
   }

   public T get() {
      return this.value;
   }

   public Codec<T> codec() {
      return this.codec;
   }

   public String toString() {
      return this.caption.getString();
   }

   public void set(T p_231515_) {
      T $$1 = (T)this.values.validateValue(p_231515_).orElseGet(() -> {
         LOGGER.error("Illegal option value " + p_231515_ + " for " + this.caption);
         return this.initialValue;
      });
      if (!Minecraft.getInstance().isRunning()) {
         this.value = $$1;
      } else {
         if (!Objects.equals(this.value, $$1)) {
            this.value = $$1;
            this.onValueUpdate.accept(this.value);
         }

      }
   }

   public ValueSet<T> values() {
      return this.values;
   }

   static {
      BOOLEAN_VALUES = new Enum<Boolean>(ImmutableList.of(Boolean.TRUE, Boolean.FALSE), Codec.BOOL);
      BOOLEAN_TO_STRING = (p_231544_, p_231545_) -> p_231545_ ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
   }

   @OnlyIn(Dist.CLIENT)
   interface SliderableValueSet<T> extends ValueSet<T> {
      double toSliderValue(T var1);

      T fromSliderValue(double var1);

      default Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> p_261993_, Options p_262177_, int p_261706_, int p_261683_, int p_261573_, Consumer<T> p_261969_) {
         return (p_261355_) -> new OptionInstanceSliderButton(p_262177_, p_261706_, p_261683_, p_261573_, 20, p_261355_, this, p_261993_, p_261969_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   interface CycleableValueSet<T> extends ValueSet<T> {
      CycleButton.ValueListSupplier<T> valueListSupplier();

      default ValueSetter<T> valueSetter() {
         return OptionInstance::set;
      }

      default Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> p_261801_, Options p_261824_, int p_261649_, int p_262114_, int p_261536_, Consumer<T> p_261642_) {
         return (p_261343_) -> CycleButton.builder(p_261343_.toString).withValues(this.valueListSupplier()).withTooltip(p_261801_).withInitialValue(p_261343_.value).create(p_261649_, p_262114_, p_261536_, 20, p_261343_.caption, (p_261347_, p_261348_) -> {
               this.valueSetter().set(p_261343_, p_261348_);
               p_261824_.save();
               p_261642_.accept(p_261348_);
            });
      }

      @OnlyIn(Dist.CLIENT)
      public interface ValueSetter<T> {
         void set(OptionInstance<T> var1, T var2);
      }
   }

   @OnlyIn(Dist.CLIENT)
   interface SliderableOrCyclableValueSet<T> extends CycleableValueSet<T>, SliderableValueSet<T> {
      boolean createCycleButton();

      default Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> p_261786_, Options p_262030_, int p_261940_, int p_262149_, int p_261495_, Consumer<T> p_261881_) {
         return this.createCycleButton() ? OptionInstance.CycleableValueSet.super.createButton(p_261786_, p_262030_, p_261940_, p_262149_, p_261495_, p_261881_) : OptionInstance.SliderableValueSet.super.createButton(p_261786_, p_262030_, p_261940_, p_262149_, p_261495_, p_261881_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static record AltEnum<T>(List<T> values, List<T> altValues, BooleanSupplier altCondition, CycleableValueSet.ValueSetter<T> valueSetter, Codec<T> codec) implements CycleableValueSet<T> {
      public AltEnum(List<T> values, List<T> altValues, BooleanSupplier altCondition, CycleableValueSet.ValueSetter<T> valueSetter, Codec<T> codec) {
         this.values = values;
         this.altValues = altValues;
         this.altCondition = altCondition;
         this.valueSetter = valueSetter;
         this.codec = codec;
      }

      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.<T>create(this.altCondition, this.values, this.altValues);
      }

      public Optional<T> validateValue(T p_231570_) {
         return (this.altCondition.getAsBoolean() ? this.altValues : this.values).contains(p_231570_) ? Optional.of(p_231570_) : Optional.empty();
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static record Enum<T>(List<T> values, Codec<T> codec) implements CycleableValueSet<T> {
      public Enum(List<T> values, Codec<T> codec) {
         this.values = values;
         this.codec = codec;
      }

      public Optional<T> validateValue(T p_231632_) {
         return this.values.contains(p_231632_) ? Optional.of(p_231632_) : Optional.empty();
      }

      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.<T>create(this.values);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static record LazyEnum<T>(Supplier<List<T>> values, Function<T, Optional<T>> validateValue, Codec<T> codec) implements CycleableValueSet<T> {
      public LazyEnum(Supplier<List<T>> values, Function<T, Optional<T>> validateValue, Codec<T> codec) {
         this.values = values;
         this.validateValue = validateValue;
         this.codec = codec;
      }

      public Optional<T> validateValue(T p_231689_) {
         return (Optional)this.validateValue.apply(p_231689_);
      }

      public CycleButton.ValueListSupplier<T> valueListSupplier() {
         return CycleButton.ValueListSupplier.<T>create((Collection)this.values.get());
      }
   }

   @OnlyIn(Dist.CLIENT)
   static final class OptionInstanceSliderButton<N> extends AbstractOptionSliderButton {
      private final OptionInstance<N> instance;
      private final SliderableValueSet<N> values;
      private final TooltipSupplier<N> tooltipSupplier;
      private final Consumer<N> onValueChanged;

      OptionInstanceSliderButton(Options p_261713_, int p_261873_, int p_261656_, int p_261799_, int p_261893_, OptionInstance<N> p_262129_, SliderableValueSet<N> p_261995_, TooltipSupplier<N> p_261963_, Consumer<N> p_261829_) {
         super(p_261713_, p_261873_, p_261656_, p_261799_, p_261893_, p_261995_.toSliderValue(p_262129_.get()));
         this.instance = p_262129_;
         this.values = p_261995_;
         this.tooltipSupplier = p_261963_;
         this.onValueChanged = p_261829_;
         this.updateMessage();
      }

      protected void updateMessage() {
         this.setMessage((Component)this.instance.toString.apply(this.instance.get()));
         this.setTooltip(this.tooltipSupplier.apply(this.values.fromSliderValue(this.value)));
      }

      protected void applyValue() {
         this.instance.set(this.values.fromSliderValue(this.value));
         this.options.save();
         this.onValueChanged.accept(this.instance.get());
      }
   }

   @OnlyIn(Dist.CLIENT)
   interface IntRangeBase extends SliderableValueSet<Integer> {
      int minInclusive();

      int maxInclusive();

      default double toSliderValue(Integer p_231663_) {
         return (double)Mth.map((float)p_231663_, (float)this.minInclusive(), (float)this.maxInclusive(), 0.0F, 1.0F);
      }

      default Integer fromSliderValue(double p_231656_) {
         return Mth.floor(Mth.map(p_231656_, (double)0.0F, (double)1.0F, (double)this.minInclusive(), (double)this.maxInclusive()));
      }

      default <R> SliderableValueSet<R> xmap(IntFunction<? extends R> p_231658_, ToIntFunction<? super R> p_231659_) {
         return new SliderableValueSet<R>(p_231659_, p_231658_) {
            // $FF: synthetic field
            final ToIntFunction val$from;
            // $FF: synthetic field
            final IntFunction val$to;
            // $FF: synthetic field
            final OptionInstance.IntRangeBase this$0;

            {
               this.this$0 = p_231670_;
               this.val$from = var2;
               this.val$to = var3;
            }

            public Optional<R> validateValue(R p_231674_) {
               Optional var10000 = this.this$0.validateValue(this.val$from.applyAsInt(p_231674_));
               IntFunction var10001 = this.val$to;
               Objects.requireNonNull(var10001);
               return var10000.map(var10001::apply);
            }

            public double toSliderValue(R p_231678_) {
               return this.this$0.toSliderValue(this.val$from.applyAsInt(p_231678_));
            }

            public R fromSliderValue(double p_231676_) {
               return (R)this.val$to.apply(this.this$0.fromSliderValue(p_231676_));
            }

            public Codec<R> codec() {
               Codec var10000 = this.this$0.codec();
               IntFunction var10001 = this.val$to;
               Objects.requireNonNull(var10001);
               Function var1 = var10001::apply;
               ToIntFunction var10002 = this.val$from;
               Objects.requireNonNull(var10002);
               return var10000.xmap(var1, var10002::applyAsInt);
            }
         };
      }

      // $FF: synthetic method
      default Object fromSliderValue(double var1) {
         return this.fromSliderValue(var1);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static record IntRange(int minInclusive, int maxInclusive) implements IntRangeBase {
      public IntRange(int minInclusive, int maxInclusive) {
         this.minInclusive = minInclusive;
         this.maxInclusive = maxInclusive;
      }

      public Optional<Integer> validateValue(Integer p_231645_) {
         return p_231645_.compareTo(this.minInclusive()) >= 0 && p_231645_.compareTo(this.maxInclusive()) <= 0 ? Optional.of(p_231645_) : Optional.empty();
      }

      public Codec<Integer> codec() {
         return Codec.intRange(this.minInclusive, this.maxInclusive + 1);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static record ClampingLazyMaxIntRange(int minInclusive, IntSupplier maxSupplier, int encodableMaxInclusive) implements IntRangeBase, SliderableOrCyclableValueSet<Integer> {
      public ClampingLazyMaxIntRange(int minInclusive, IntSupplier maxSupplier, int encodableMaxInclusive) {
         this.minInclusive = minInclusive;
         this.maxSupplier = maxSupplier;
         this.encodableMaxInclusive = encodableMaxInclusive;
      }

      public Optional<Integer> validateValue(Integer p_231590_) {
         return Optional.of(Mth.clamp(p_231590_, this.minInclusive(), this.maxInclusive()));
      }

      public int maxInclusive() {
         return this.maxSupplier.getAsInt();
      }

      public Codec<Integer> codec() {
         return ExtraCodecs.validate(Codec.INT, (p_276098_) -> {
            int $$1 = this.encodableMaxInclusive + 1;
            return p_276098_.compareTo(this.minInclusive) >= 0 && p_276098_.compareTo($$1) <= 0 ? DataResult.success(p_276098_) : DataResult.error(() -> "Value " + p_276098_ + " outside of range [" + this.minInclusive + ":" + $$1 + "]", p_276098_);
         });
      }

      public boolean createCycleButton() {
         return true;
      }

      public CycleButton.ValueListSupplier<Integer> valueListSupplier() {
         return CycleButton.ValueListSupplier.<Integer>create(IntStream.range(this.minInclusive, this.maxInclusive() + 1).boxed().toList());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum UnitDouble implements SliderableValueSet<Double> {
      INSTANCE;

      public Optional<Double> validateValue(Double p_231747_) {
         return p_231747_ >= (double)0.0F && p_231747_ <= (double)1.0F ? Optional.of(p_231747_) : Optional.empty();
      }

      public double toSliderValue(Double p_231756_) {
         return p_231756_;
      }

      public Double fromSliderValue(double p_231741_) {
         return p_231741_;
      }

      public <R> SliderableValueSet<R> xmap(DoubleFunction<? extends R> p_231751_, ToDoubleFunction<? super R> p_231752_) {
         return new SliderableValueSet<R>(p_231752_, p_231751_) {
            // $FF: synthetic field
            final ToDoubleFunction val$from;
            // $FF: synthetic field
            final DoubleFunction val$to;
            // $FF: synthetic field
            final OptionInstance.UnitDouble this$0;

            {
               this.this$0 = p_231769_;
               this.val$from = var2;
               this.val$to = var3;
            }

            public Optional<R> validateValue(R p_231773_) {
               Optional var10000 = this.this$0.validateValue(this.val$from.applyAsDouble(p_231773_));
               DoubleFunction var10001 = this.val$to;
               Objects.requireNonNull(var10001);
               return var10000.map(var10001::apply);
            }

            public double toSliderValue(R p_231777_) {
               return this.this$0.toSliderValue(this.val$from.applyAsDouble(p_231777_));
            }

            public R fromSliderValue(double p_231775_) {
               return (R)this.val$to.apply(this.this$0.fromSliderValue(p_231775_));
            }

            public Codec<R> codec() {
               Codec var10000 = this.this$0.codec();
               DoubleFunction var10001 = this.val$to;
               Objects.requireNonNull(var10001);
               Function var1 = var10001::apply;
               ToDoubleFunction var10002 = this.val$from;
               Objects.requireNonNull(var10002);
               return var10000.xmap(var1, var10002::applyAsDouble);
            }
         };
      }

      public Codec<Double> codec() {
         return Codec.either(Codec.doubleRange((double)0.0F, (double)1.0F), Codec.BOOL).xmap((p_231743_) -> (Double)p_231743_.map((p_231760_) -> p_231760_, (p_231745_) -> p_231745_ ? (double)1.0F : (double)0.0F), Either::left);
      }

      // $FF: synthetic method
      public Object fromSliderValue(double var1) {
         return this.fromSliderValue(var1);
      }

      // $FF: synthetic method
      private static UnitDouble[] $values() {
         return new UnitDouble[]{INSTANCE};
      }
   }

   @OnlyIn(Dist.CLIENT)
   public interface CaptionBasedToString<T> {
      Component toString(Component var1, T var2);
   }

   @FunctionalInterface
   @OnlyIn(Dist.CLIENT)
   public interface TooltipSupplier<T> {
      @Nullable
      Tooltip apply(T var1);
   }

   @OnlyIn(Dist.CLIENT)
   interface ValueSet<T> {
      Function<OptionInstance<T>, AbstractWidget> createButton(TooltipSupplier<T> var1, Options var2, int var3, int var4, int var5, Consumer<T> var6);

      Optional<T> validateValue(T var1);

      Codec<T> codec();
   }
}

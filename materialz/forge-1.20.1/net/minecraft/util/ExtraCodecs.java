package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.HolderSet;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ExtraCodecs {
   public static final Codec<JsonElement> JSON;
   public static final Codec<Component> COMPONENT;
   public static final Codec<Component> FLAT_COMPONENT;
   public static final Codec<Vector3f> VECTOR3F;
   public static final Codec<Quaternionf> QUATERNIONF_COMPONENTS;
   public static final Codec<AxisAngle4f> AXISANGLE4F;
   public static final Codec<Quaternionf> QUATERNIONF;
   public static Codec<Matrix4f> MATRIX4F;
   public static final Codec<Integer> NON_NEGATIVE_INT;
   public static final Codec<Integer> POSITIVE_INT;
   public static final Codec<Float> POSITIVE_FLOAT;
   public static final Codec<Pattern> PATTERN;
   public static final Codec<Instant> INSTANT_ISO8601;
   public static final Codec<byte[]> BASE64_STRING;
   public static final Codec<TagOrElementLocation> TAG_OR_ELEMENT_ID;
   public static final Function<Optional<Long>, OptionalLong> toOptionalLong;
   public static final Function<OptionalLong, Optional<Long>> fromOptionalLong;
   public static final Codec<BitSet> BIT_SET;
   private static final Codec<Property> PROPERTY;
   @VisibleForTesting
   public static final Codec<PropertyMap> PROPERTY_MAP;
   public static final Codec<GameProfile> GAME_PROFILE;
   public static final Codec<String> NON_EMPTY_STRING;
   public static final Codec<Integer> CODEPOINT;

   public static <F, S> Codec<Either<F, S>> xor(Codec<F> p_144640_, Codec<S> p_144641_) {
      return new XorCodec(p_144640_, p_144641_);
   }

   public static <P, I> Codec<I> intervalCodec(Codec<P> p_184362_, String p_184363_, String p_184364_, BiFunction<P, P, DataResult<I>> p_184365_, Function<I, P> p_184366_, Function<I, P> p_184367_) {
      Codec<I> $$6 = Codec.list(p_184362_).comapFlatMap((p_184398_) -> Util.fixedSize((List)p_184398_, 2).flatMap((p_184445_) -> {
            P $$2 = (P)p_184445_.get(0);
            P $$3 = (P)p_184445_.get(1);
            return (DataResult)p_184365_.apply($$2, $$3);
         }), (p_184459_) -> ImmutableList.of(p_184366_.apply(p_184459_), p_184367_.apply(p_184459_)));
      Codec<I> $$7 = RecordCodecBuilder.create((p_184360_) -> p_184360_.group(p_184362_.fieldOf(p_184363_).forGetter(Pair::getFirst), p_184362_.fieldOf(p_184364_).forGetter(Pair::getSecond)).apply(p_184360_, Pair::of)).comapFlatMap((p_184392_) -> (DataResult)p_184365_.apply(p_184392_.getFirst(), p_184392_.getSecond()), (p_184449_) -> Pair.of(p_184366_.apply(p_184449_), p_184367_.apply(p_184449_)));
      Codec<I> $$8 = (new EitherCodec($$6, $$7)).xmap((p_184355_) -> p_184355_.map((p_184461_) -> p_184461_, (p_184455_) -> p_184455_), Either::left);
      return Codec.either(p_184362_, $$8).comapFlatMap((p_184389_) -> (DataResult)p_184389_.map((p_184395_) -> (DataResult)p_184365_.apply(p_184395_, p_184395_), DataResult::success), (p_184411_) -> {
         P $$3 = (P)p_184366_.apply(p_184411_);
         P $$4 = (P)p_184367_.apply(p_184411_);
         return Objects.equals($$3, $$4) ? Either.left($$3) : Either.right(p_184411_);
      });
   }

   public static <A> Codec.ResultFunction<A> orElsePartial(final A p_184382_) {
      return new Codec.ResultFunction<A>() {
         public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> p_184466_, T p_184467_, DataResult<Pair<A, T>> p_184468_) {
            MutableObject<String> $$3 = new MutableObject();
            Objects.requireNonNull($$3);
            Optional<Pair<A, T>> $$4 = p_184468_.resultOrPartial($$3::setValue);
            return $$4.isPresent() ? p_184468_ : DataResult.error(() -> "(" + (String)$$3.getValue() + " -> using default)", Pair.of(p_184382_, p_184467_));
         }

         public <T> DataResult<T> coApply(DynamicOps<T> p_184470_, A p_184471_, DataResult<T> p_184472_) {
            return p_184472_;
         }

         public String toString() {
            return "OrElsePartial[" + p_184382_ + "]";
         }
      };
   }

   public static <E> Codec<E> idResolverCodec(ToIntFunction<E> p_184422_, IntFunction<E> p_184423_, int p_184424_) {
      return Codec.INT.flatXmap((p_184414_) -> (DataResult)Optional.ofNullable(p_184423_.apply(p_184414_)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown element id: " + p_184414_)), (p_274850_) -> {
         int $$3 = p_184422_.applyAsInt(p_274850_);
         return $$3 == p_184424_ ? DataResult.error(() -> "Element with unknown id: " + p_274850_) : DataResult.success($$3);
      });
   }

   public static <E> Codec<E> stringResolverCodec(Function<E, String> p_184406_, Function<String, E> p_184407_) {
      return Codec.STRING.flatXmap((p_184404_) -> (DataResult)Optional.ofNullable(p_184407_.apply(p_184404_)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Unknown element name:" + p_184404_)), (p_184401_) -> (DataResult)Optional.ofNullable((String)p_184406_.apply(p_184401_)).map(DataResult::success).orElseGet(() -> DataResult.error(() -> "Element with unknown name: " + p_184401_)));
   }

   public static <E> Codec<E> orCompressed(final Codec<E> p_184426_, final Codec<E> p_184427_) {
      return new Codec<E>() {
         public <T> DataResult<T> encode(E p_184483_, DynamicOps<T> p_184484_, T p_184485_) {
            return p_184484_.compressMaps() ? p_184427_.encode(p_184483_, p_184484_, p_184485_) : p_184426_.encode(p_184483_, p_184484_, p_184485_);
         }

         public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> p_184480_, T p_184481_) {
            return p_184480_.compressMaps() ? p_184427_.decode(p_184480_, p_184481_) : p_184426_.decode(p_184480_, p_184481_);
         }

         public String toString() {
            return p_184426_ + " orCompressed " + p_184427_;
         }
      };
   }

   public static <E> Codec<E> overrideLifecycle(Codec<E> p_184369_, final Function<E, Lifecycle> p_184370_, final Function<E, Lifecycle> p_184371_) {
      return p_184369_.mapResult(new Codec.ResultFunction<E>() {
         public <T> DataResult<Pair<E, T>> apply(DynamicOps<T> p_184497_, T p_184498_, DataResult<Pair<E, T>> p_184499_) {
            return (DataResult)p_184499_.result().map((p_184495_) -> p_184499_.setLifecycle((Lifecycle)p_184370_.apply(p_184495_.getFirst()))).orElse(p_184499_);
         }

         public <T> DataResult<T> coApply(DynamicOps<T> p_184501_, E p_184502_, DataResult<T> p_184503_) {
            return p_184503_.setLifecycle((Lifecycle)p_184371_.apply(p_184502_));
         }

         public String toString() {
            return "WithLifecycle[" + p_184370_ + " " + p_184371_ + "]";
         }
      });
   }

   public static <T> Codec<T> validate(Codec<T> p_265690_, Function<T, DataResult<T>> p_265223_) {
      return p_265690_.flatXmap(p_265223_, p_265223_);
   }

   public static <T> MapCodec<T> validate(MapCodec<T> p_286613_, Function<T, DataResult<T>> p_286875_) {
      return p_286613_.flatXmap(p_286875_, p_286875_);
   }

   private static Codec<Integer> intRangeWithMessage(int p_144634_, int p_144635_, Function<Integer, String> p_144636_) {
      return validate(Codec.INT, (p_274889_) -> p_274889_.compareTo(p_144634_) >= 0 && p_274889_.compareTo(p_144635_) <= 0 ? DataResult.success(p_274889_) : DataResult.error(() -> (String)p_144636_.apply(p_274889_)));
   }

   public static Codec<Integer> intRange(int p_270883_, int p_270323_) {
      return intRangeWithMessage(p_270883_, p_270323_, (p_269784_) -> "Value must be within range [" + p_270883_ + ";" + p_270323_ + "]: " + p_269784_);
   }

   private static Codec<Float> floatRangeMinExclusiveWithMessage(float p_184351_, float p_184352_, Function<Float, String> p_184353_) {
      return validate(Codec.FLOAT, (p_274865_) -> p_274865_.compareTo(p_184351_) > 0 && p_274865_.compareTo(p_184352_) <= 0 ? DataResult.success(p_274865_) : DataResult.error(() -> (String)p_184353_.apply(p_274865_)));
   }

   public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> p_144638_) {
      return validate(p_144638_, (p_274853_) -> p_274853_.isEmpty() ? DataResult.error(() -> "List must have contents") : DataResult.success(p_274853_));
   }

   public static <T> Codec<HolderSet<T>> nonEmptyHolderSet(Codec<HolderSet<T>> p_203983_) {
      return validate(p_203983_, (p_274860_) -> p_274860_.unwrap().right().filter(List::isEmpty).isPresent() ? DataResult.error(() -> "List must have contents") : DataResult.success(p_274860_));
   }

   public static <A> Codec<A> lazyInitializedCodec(Supplier<Codec<A>> p_184416_) {
      return new LazyInitializedCodec<A>(p_184416_);
   }

   public static <E> MapCodec<E> retrieveContext(final Function<DynamicOps<?>, DataResult<E>> p_203977_) {
      class ContextRetrievalCodec extends MapCodec<E> {
         public <T> RecordBuilder<T> encode(E p_203993_, DynamicOps<T> p_203994_, RecordBuilder<T> p_203995_) {
            return p_203995_;
         }

         public <T> DataResult<E> decode(DynamicOps<T> p_203990_, MapLike<T> p_203991_) {
            return (DataResult)p_203977_.apply(p_203990_);
         }

         public String toString() {
            return "ContextRetrievalCodec[" + p_203977_ + "]";
         }

         public <T> Stream<T> keys(DynamicOps<T> p_203997_) {
            return Stream.empty();
         }
      }

      return new ContextRetrievalCodec();
   }

   public static <E, L extends Collection<E>, T> Function<L, DataResult<L>> ensureHomogenous(Function<E, T> p_203985_) {
      return (p_203980_) -> {
         Iterator<E> $$2 = p_203980_.iterator();
         if ($$2.hasNext()) {
            T $$3 = (T)p_203985_.apply($$2.next());

            while($$2.hasNext()) {
               E $$4 = (E)$$2.next();
               T $$5 = (T)p_203985_.apply($$4);
               if ($$5 != $$3) {
                  return DataResult.error(() -> "Mixed type list: element " + $$4 + " had type " + $$5 + ", but list is of type " + $$3);
               }
            }
         }

         return DataResult.success(p_203980_, Lifecycle.stable());
      };
   }

   public static <A> Codec<A> catchDecoderException(final Codec<A> p_216186_) {
      return Codec.of(p_216186_, new Decoder<A>() {
         public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> p_216193_, T p_216194_) {
            try {
               return p_216186_.decode(p_216193_, p_216194_);
            } catch (Exception $$2) {
               return DataResult.error(() -> "Caught exception decoding " + p_216194_ + ": " + $$2.getMessage());
            }
         }
      });
   }

   public static Codec<Instant> instantCodec(DateTimeFormatter p_216171_) {
      PrimitiveCodec var10000 = Codec.STRING;
      Function var10001 = (p_274881_) -> {
         try {
            return DataResult.success(Instant.from(p_216171_.parse(p_274881_)));
         } catch (Exception $$2) {
            Objects.requireNonNull($$2);
            return DataResult.error($$2::getMessage);
         }
      };
      Objects.requireNonNull(p_216171_);
      return var10000.comapFlatMap(var10001, p_216171_::format);
   }

   public static MapCodec<OptionalLong> asOptionalLong(MapCodec<Optional<Long>> p_216167_) {
      return p_216167_.xmap(toOptionalLong, fromOptionalLong);
   }

   private static DataResult<GameProfile> mapIdNameToGameProfile(Pair<Optional<UUID>, Optional<String>> p_253764_) {
      try {
         return DataResult.success(new GameProfile((UUID)((Optional)p_253764_.getFirst()).orElse((Object)null), (String)((Optional)p_253764_.getSecond()).orElse((Object)null)));
      } catch (Throwable $$1) {
         Objects.requireNonNull($$1);
         return DataResult.error($$1::getMessage);
      }
   }

   private static DataResult<Pair<Optional<UUID>, Optional<String>>> mapGameProfileToIdName(GameProfile p_254220_) {
      return DataResult.success(Pair.of(Optional.ofNullable(p_254220_.getId()), Optional.ofNullable(p_254220_.getName())));
   }

   public static Codec<String> sizeLimitedString(int p_265773_, int p_265217_) {
      return validate(Codec.STRING, (p_274879_) -> {
         int $$3 = p_274879_.length();
         if ($$3 < p_265773_) {
            return DataResult.error(() -> "String \"" + p_274879_ + "\" is too short: " + $$3 + ", expected range [" + p_265773_ + "-" + p_265217_ + "]");
         } else {
            return $$3 > p_265217_ ? DataResult.error(() -> "String \"" + p_274879_ + "\" is too long: " + $$3 + ", expected range [" + p_265773_ + "-" + p_265217_ + "]") : DataResult.success(p_274879_);
         }
      });
   }

   static {
      JSON = Codec.PASSTHROUGH.xmap((p_253507_) -> (JsonElement)p_253507_.convert(JsonOps.INSTANCE).getValue(), (p_253513_) -> new Dynamic(JsonOps.INSTANCE, p_253513_));
      COMPONENT = JSON.flatXmap((p_274861_) -> {
         try {
            return DataResult.success(Component.Serializer.fromJson(p_274861_));
         } catch (JsonParseException $$1) {
            Objects.requireNonNull($$1);
            return DataResult.error($$1::getMessage);
         }
      }, (p_274859_) -> {
         try {
            return DataResult.success(Component.Serializer.toJsonTree(p_274859_));
         } catch (IllegalArgumentException $$1) {
            Objects.requireNonNull($$1);
            return DataResult.error($$1::getMessage);
         }
      });
      FLAT_COMPONENT = Codec.STRING.flatXmap((p_277276_) -> {
         try {
            return DataResult.success(Component.Serializer.fromJson(p_277276_));
         } catch (JsonParseException $$1) {
            Objects.requireNonNull($$1);
            return DataResult.error($$1::getMessage);
         }
      }, (p_277277_) -> {
         try {
            return DataResult.success(Component.Serializer.toJson(p_277277_));
         } catch (IllegalArgumentException $$1) {
            Objects.requireNonNull($$1);
            return DataResult.error($$1::getMessage);
         }
      });
      VECTOR3F = Codec.FLOAT.listOf().comapFlatMap((p_253502_) -> Util.fixedSize((List)p_253502_, 3).map((p_253489_) -> new Vector3f((Float)p_253489_.get(0), (Float)p_253489_.get(1), (Float)p_253489_.get(2))), (p_269787_) -> List.of(p_269787_.x(), p_269787_.y(), p_269787_.z()));
      QUATERNIONF_COMPONENTS = Codec.FLOAT.listOf().comapFlatMap((p_269773_) -> Util.fixedSize((List)p_269773_, 4).map((p_269785_) -> new Quaternionf((Float)p_269785_.get(0), (Float)p_269785_.get(1), (Float)p_269785_.get(2), (Float)p_269785_.get(3))), (p_269780_) -> List.of(p_269780_.x, p_269780_.y, p_269780_.z, p_269780_.w));
      AXISANGLE4F = RecordCodecBuilder.create((p_269774_) -> p_269774_.group(Codec.FLOAT.fieldOf("angle").forGetter((p_269776_) -> p_269776_.angle), VECTOR3F.fieldOf("axis").forGetter((p_269778_) -> new Vector3f(p_269778_.x, p_269778_.y, p_269778_.z))).apply(p_269774_, AxisAngle4f::new));
      QUATERNIONF = Codec.either(QUATERNIONF_COMPONENTS, AXISANGLE4F.xmap(Quaternionf::new, AxisAngle4f::new)).xmap((p_269779_) -> (Quaternionf)p_269779_.map((p_269781_) -> p_269781_, (p_269786_) -> p_269786_), Either::left);
      MATRIX4F = Codec.FLOAT.listOf().comapFlatMap((p_269788_) -> Util.fixedSize((List)p_269788_, 16).map((p_269777_) -> {
            Matrix4f $$1 = new Matrix4f();

            for(int $$2 = 0; $$2 < p_269777_.size(); ++$$2) {
               $$1.setRowColumn($$2 >> 2, $$2 & 3, (Float)p_269777_.get($$2));
            }

            return $$1.determineProperties();
         }), (p_269775_) -> {
         FloatList $$1 = new FloatArrayList(16);

         for(int $$2 = 0; $$2 < 16; ++$$2) {
            $$1.add(p_269775_.getRowColumn($$2 >> 2, $$2 & 3));
         }

         return $$1;
      });
      NON_NEGATIVE_INT = intRangeWithMessage(0, Integer.MAX_VALUE, (p_275703_) -> "Value must be non-negative: " + p_275703_);
      POSITIVE_INT = intRangeWithMessage(1, Integer.MAX_VALUE, (p_274847_) -> "Value must be positive: " + p_274847_);
      POSITIVE_FLOAT = floatRangeMinExclusiveWithMessage(0.0F, Float.MAX_VALUE, (p_274876_) -> "Value must be positive: " + p_274876_);
      PATTERN = Codec.STRING.comapFlatMap((p_274857_) -> {
         try {
            return DataResult.success(Pattern.compile(p_274857_));
         } catch (PatternSyntaxException $$1) {
            return DataResult.error(() -> "Invalid regex pattern '" + p_274857_ + "': " + $$1.getMessage());
         }
      }, Pattern::pattern);
      INSTANT_ISO8601 = instantCodec(DateTimeFormatter.ISO_INSTANT);
      BASE64_STRING = Codec.STRING.comapFlatMap((p_274852_) -> {
         try {
            return DataResult.success(Base64.getDecoder().decode(p_274852_));
         } catch (IllegalArgumentException var2) {
            return DataResult.error(() -> "Malformed base64 string");
         }
      }, (p_216180_) -> Base64.getEncoder().encodeToString(p_216180_));
      TAG_OR_ELEMENT_ID = Codec.STRING.comapFlatMap((p_216169_) -> p_216169_.startsWith("#") ? ResourceLocation.read(p_216169_.substring(1)).map((p_216182_) -> new TagOrElementLocation(p_216182_, true)) : ResourceLocation.read(p_216169_).map((p_216165_) -> new TagOrElementLocation(p_216165_, false)), TagOrElementLocation::decoratedId);
      toOptionalLong = (p_216176_) -> (OptionalLong)p_216176_.map(OptionalLong::of).orElseGet(OptionalLong::empty);
      fromOptionalLong = (p_216178_) -> p_216178_.isPresent() ? Optional.of(p_216178_.getAsLong()) : Optional.empty();
      BIT_SET = Codec.LONG_STREAM.xmap((p_253514_) -> BitSet.valueOf(p_253514_.toArray()), (p_253493_) -> Arrays.stream(p_253493_.toLongArray()));
      PROPERTY = RecordCodecBuilder.create((p_253491_) -> p_253491_.group(Codec.STRING.fieldOf("name").forGetter(Property::getName), Codec.STRING.fieldOf("value").forGetter(Property::getValue), Codec.STRING.optionalFieldOf("signature").forGetter((p_253490_) -> Optional.ofNullable(p_253490_.getSignature()))).apply(p_253491_, (p_253494_, p_253495_, p_253496_) -> new Property(p_253494_, p_253495_, (String)p_253496_.orElse((Object)null))));
      PROPERTY_MAP = Codec.either(Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()), PROPERTY.listOf()).xmap((p_253515_) -> {
         PropertyMap $$1 = new PropertyMap();
         p_253515_.ifLeft((p_253506_) -> p_253506_.forEach((p_253500_, p_253501_) -> {
               for(String $$3 : p_253501_) {
                  $$1.put(p_253500_, new Property(p_253500_, $$3));
               }

            })).ifRight((p_253509_) -> {
            for(Property $$2 : p_253509_) {
               $$1.put($$2.getName(), $$2);
            }

         });
         return $$1;
      }, (p_253504_) -> Either.right(p_253504_.values().stream().toList()));
      GAME_PROFILE = RecordCodecBuilder.create((p_253497_) -> p_253497_.group(Codec.mapPair(UUIDUtil.AUTHLIB_CODEC.xmap(Optional::of, (p_253517_) -> (UUID)p_253517_.orElse((Object)null)).optionalFieldOf("id", Optional.empty()), Codec.STRING.xmap(Optional::of, (p_253492_) -> (String)p_253492_.orElse((Object)null)).optionalFieldOf("name", Optional.empty())).flatXmap(ExtraCodecs::mapIdNameToGameProfile, ExtraCodecs::mapGameProfileToIdName).forGetter(Function.identity()), PROPERTY_MAP.optionalFieldOf("properties", new PropertyMap()).forGetter(GameProfile::getProperties)).apply(p_253497_, (p_253518_, p_253519_) -> {
            p_253519_.forEach((p_253511_, p_253512_) -> p_253518_.getProperties().put(p_253511_, p_253512_));
            return p_253518_;
         }));
      NON_EMPTY_STRING = validate(Codec.STRING, (p_274858_) -> p_274858_.isEmpty() ? DataResult.error(() -> "Expected non-empty string") : DataResult.success(p_274858_));
      CODEPOINT = Codec.STRING.comapFlatMap((p_284688_) -> {
         int[] $$1 = p_284688_.codePoints().toArray();
         return $$1.length != 1 ? DataResult.error(() -> "Expected one codepoint, got: " + p_284688_) : DataResult.success($$1[0]);
      }, Character::toString);
   }

   public static final class EitherCodec<F, S> implements Codec<Either<F, S>> {
      private final Codec<F> first;
      private final Codec<S> second;

      public EitherCodec(Codec<F> p_184508_, Codec<S> p_184509_) {
         this.first = p_184508_;
         this.second = p_184509_;
      }

      public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> p_184530_, T p_184531_) {
         DataResult<Pair<Either<F, S>, T>> $$2 = this.first.decode(p_184530_, p_184531_).map((p_184524_) -> p_184524_.mapFirst(Either::left));
         if (!$$2.error().isPresent()) {
            return $$2;
         } else {
            DataResult<Pair<Either<F, S>, T>> $$3 = this.second.decode(p_184530_, p_184531_).map((p_184515_) -> p_184515_.mapFirst(Either::right));
            return !$$3.error().isPresent() ? $$3 : $$2.apply2((p_184517_, p_184518_) -> p_184518_, $$3);
         }
      }

      public <T> DataResult<T> encode(Either<F, S> p_184511_, DynamicOps<T> p_184512_, T p_184513_) {
         return (DataResult)p_184511_.map((p_184528_) -> this.first.encode(p_184528_, p_184512_, p_184513_), (p_184522_) -> this.second.encode(p_184522_, p_184512_, p_184513_));
      }

      public boolean equals(Object p_184537_) {
         if (this == p_184537_) {
            return true;
         } else if (p_184537_ != null && this.getClass() == p_184537_.getClass()) {
            EitherCodec<?, ?> $$1 = (EitherCodec)p_184537_;
            return Objects.equals(this.first, $$1.first) && Objects.equals(this.second, $$1.second);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.first, this.second});
      }

      public String toString() {
         return "EitherCodec[" + this.first + ", " + this.second + "]";
      }

      // $FF: synthetic method
      public DataResult encode(Object var1, DynamicOps var2, Object var3) {
         return this.encode((Either)var1, var2, var3);
      }
   }

   static final class XorCodec<F, S> implements Codec<Either<F, S>> {
      private final Codec<F> first;
      private final Codec<S> second;

      public XorCodec(Codec<F> p_144660_, Codec<S> p_144661_) {
         this.first = p_144660_;
         this.second = p_144661_;
      }

      public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> p_144679_, T p_144680_) {
         DataResult<Pair<Either<F, S>, T>> $$2 = this.first.decode(p_144679_, p_144680_).map((p_144673_) -> p_144673_.mapFirst(Either::left));
         DataResult<Pair<Either<F, S>, T>> $$3 = this.second.decode(p_144679_, p_144680_).map((p_144667_) -> p_144667_.mapFirst(Either::right));
         Optional<Pair<Either<F, S>, T>> $$4 = $$2.result();
         Optional<Pair<Either<F, S>, T>> $$5 = $$3.result();
         if ($$4.isPresent() && $$5.isPresent()) {
            return DataResult.error(() -> {
               Object var10000 = $$4.get();
               return "Both alternatives read successfully, can not pick the correct one; first: " + var10000 + " second: " + $$5.get();
            }, (Pair)$$4.get());
         } else {
            return $$4.isPresent() ? $$2 : $$3;
         }
      }

      public <T> DataResult<T> encode(Either<F, S> p_144663_, DynamicOps<T> p_144664_, T p_144665_) {
         return (DataResult)p_144663_.map((p_144677_) -> this.first.encode(p_144677_, p_144664_, p_144665_), (p_144671_) -> this.second.encode(p_144671_, p_144664_, p_144665_));
      }

      public boolean equals(Object p_144686_) {
         if (this == p_144686_) {
            return true;
         } else if (p_144686_ != null && this.getClass() == p_144686_.getClass()) {
            XorCodec<?, ?> $$1 = (XorCodec)p_144686_;
            return Objects.equals(this.first, $$1.first) && Objects.equals(this.second, $$1.second);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.first, this.second});
      }

      public String toString() {
         return "XorCodec[" + this.first + ", " + this.second + "]";
      }

      // $FF: synthetic method
      public DataResult encode(Object var1, DynamicOps var2, Object var3) {
         return this.encode((Either)var1, var2, var3);
      }
   }

   static record LazyInitializedCodec<A>(Supplier<Codec<A>> delegate) implements Codec<A> {
      LazyInitializedCodec(Supplier<Codec<A>> delegate) {
         Objects.requireNonNull(delegate);
         Supplier<Codec<A>> var2 = Suppliers.memoize(delegate::get);
         this.delegate = var2;
      }

      public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> p_184545_, T p_184546_) {
         return ((Codec)this.delegate.get()).decode(p_184545_, p_184546_);
      }

      public <T> DataResult<T> encode(A p_184548_, DynamicOps<T> p_184549_, T p_184550_) {
         return ((Codec)this.delegate.get()).encode(p_184548_, p_184549_, p_184550_);
      }
   }

   public static record TagOrElementLocation(ResourceLocation id, boolean tag) {
      public TagOrElementLocation(ResourceLocation id, boolean tag) {
         this.id = id;
         this.tag = tag;
      }

      public String toString() {
         return this.decoratedId();
      }

      private String decoratedId() {
         return this.tag ? "#" + this.id : this.id.toString();
      }
   }
}

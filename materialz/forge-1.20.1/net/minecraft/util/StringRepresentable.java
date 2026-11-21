package net.minecraft.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public interface StringRepresentable {
   int PRE_BUILT_MAP_THRESHOLD = 16;

   String getSerializedName();

   static <E extends Enum<E> & StringRepresentable> EnumCodec<E> fromEnum(Supplier<E[]> p_216440_) {
      return fromEnumWithMapping(p_216440_, (p_275327_) -> p_275327_);
   }

   static <E extends Enum<E> & StringRepresentable> EnumCodec<E> fromEnumWithMapping(Supplier<E[]> p_275615_, Function<String, String> p_275259_) {
      E[] $$2 = (Enum[])p_275615_.get();
      if ($$2.length > 16) {
         Map<String, E> $$3 = (Map)Arrays.stream($$2).collect(Collectors.toMap((p_274905_) -> (String)p_275259_.apply(((StringRepresentable)p_274905_).getSerializedName()), (p_274903_) -> p_274903_));
         return new EnumCodec<E>($$2, (p_216438_) -> p_216438_ == null ? null : (Enum)$$3.get(p_216438_));
      } else {
         return new EnumCodec<E>($$2, (p_274908_) -> {
            for(E $$3 : $$2) {
               if (((String)p_275259_.apply(((StringRepresentable)$$3).getSerializedName())).equals(p_274908_)) {
                  return $$3;
               }
            }

            return null;
         });
      }
   }

   static Keyable keys(final StringRepresentable[] p_14358_) {
      return new Keyable() {
         public <T> Stream<T> keys(DynamicOps<T> p_184758_) {
            Stream var10000 = Arrays.stream(p_14358_).map(StringRepresentable::getSerializedName);
            Objects.requireNonNull(p_184758_);
            return var10000.map(p_184758_::createString);
         }
      };
   }

   /** @deprecated */
   @Deprecated
   public static class EnumCodec<E extends Enum<E> & StringRepresentable> implements Codec<E> {
      private final Codec<E> codec;
      private final Function<String, E> resolver;

      public EnumCodec(E[] p_216447_, Function<String, E> p_216448_) {
         this.codec = ExtraCodecs.<E>orCompressed(ExtraCodecs.stringResolverCodec((p_216461_) -> ((StringRepresentable)p_216461_).getSerializedName(), p_216448_), ExtraCodecs.idResolverCodec((p_216454_) -> ((Enum)p_216454_).ordinal(), (p_216459_) -> p_216459_ >= 0 && p_216459_ < p_216447_.length ? p_216447_[p_216459_] : null, -1));
         this.resolver = p_216448_;
      }

      public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> p_216463_, T p_216464_) {
         return this.codec.decode(p_216463_, p_216464_);
      }

      public <T> DataResult<T> encode(E p_216450_, DynamicOps<T> p_216451_, T p_216452_) {
         return this.codec.encode(p_216450_, p_216451_, p_216452_);
      }

      @Nullable
      public E byName(@Nullable String p_216456_) {
         return (E)(this.resolver.apply(p_216456_));
      }

      public E byName(@Nullable String p_263077_, E p_263115_) {
         return (E)(Objects.requireNonNullElse(this.byName(p_263077_), p_263115_));
      }

      // $FF: synthetic method
      public DataResult encode(Object var1, DynamicOps var2, Object var3) {
         return this.encode((Enum)var1, var2, var3);
      }
   }
}

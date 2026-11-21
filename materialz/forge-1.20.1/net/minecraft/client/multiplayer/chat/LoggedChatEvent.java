package net.minecraft.client.multiplayer.chat;

import com.mojang.serialization.Codec;
import java.util.function.Supplier;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface LoggedChatEvent {
   Codec<LoggedChatEvent> CODEC = StringRepresentable.fromEnum(Type::values).dispatch(LoggedChatEvent::type, Type::codec);

   Type type();

   @OnlyIn(Dist.CLIENT)
   public static enum Type implements StringRepresentable {
      PLAYER("player", () -> LoggedChatMessage.Player.CODEC),
      SYSTEM("system", () -> LoggedChatMessage.System.CODEC);

      private final String serializedName;
      private final Supplier<Codec<? extends LoggedChatEvent>> codec;

      private Type(String p_254335_, Supplier<Codec<? extends LoggedChatEvent>> p_254115_) {
         this.serializedName = p_254335_;
         this.codec = p_254115_;
      }

      private Codec<? extends LoggedChatEvent> codec() {
         return (Codec)this.codec.get();
      }

      public String getSerializedName() {
         return this.serializedName;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{PLAYER, SYSTEM};
      }
   }
}

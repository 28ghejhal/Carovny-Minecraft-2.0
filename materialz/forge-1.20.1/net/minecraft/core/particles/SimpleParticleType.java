package net.minecraft.core.particles;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;

public class SimpleParticleType extends ParticleType<SimpleParticleType> implements ParticleOptions {
   private static final ParticleOptions.Deserializer<SimpleParticleType> DESERIALIZER = new ParticleOptions.Deserializer<SimpleParticleType>() {
      public SimpleParticleType fromCommand(ParticleType<SimpleParticleType> p_123846_, StringReader p_123847_) {
         return (SimpleParticleType)p_123846_;
      }

      public SimpleParticleType fromNetwork(ParticleType<SimpleParticleType> p_123849_, FriendlyByteBuf p_123850_) {
         return (SimpleParticleType)p_123849_;
      }

      // $FF: synthetic method
      public ParticleOptions fromNetwork(ParticleType var1, FriendlyByteBuf var2) {
         return this.fromNetwork(var1, var2);
      }

      // $FF: synthetic method
      public ParticleOptions fromCommand(ParticleType var1, StringReader var2) throws CommandSyntaxException {
         return this.fromCommand(var1, var2);
      }
   };
   private final Codec<SimpleParticleType> codec = Codec.unit(this::getType);

   public SimpleParticleType(boolean p_123837_) {
      super(p_123837_, DESERIALIZER);
   }

   public SimpleParticleType getType() {
      return this;
   }

   public Codec<SimpleParticleType> codec() {
      return this.codec;
   }

   public void writeToNetwork(FriendlyByteBuf p_123840_) {
   }

   public String writeToString() {
      return BuiltInRegistries.PARTICLE_TYPE.getKey(this).toString();
   }

   // $FF: synthetic method
   public ParticleType getType() {
      return this.getType();
   }
}
